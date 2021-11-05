import {
    HttpBackend,
    HttpClient,
    HttpErrorResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
    UxButtonConfig,
    UxDynamicModalConfig,
    UxDynamicModalService,
} from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import {
    CredentialView,
    DSSTimestampDTO,
    CredentialFileUploadResponseView,
    SignatureBytesView,
    SignatureNexuView,
    SignatureParametersView,
    V1Service,
} from '@shared/swagger';
import { concat, empty, Observable, Subject, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { NotificationService } from './error.service';
import { IssuerService } from './issuer.service';

const API = 'http://127.0.0.1:9795';

export interface MessageEmitter {
    status: NEXU_STATUS;
    message: string | null;
    data?: any;
}

export enum MESSAGE_INFO {
    CHECK_CONNECTION = 'seal-credential.steps.check-connection',
    CONNECTION_READY = 'seal-credential.steps.check-connection',
    READ_CERTIFICATES = 'seal-credential.steps.read-certificates',
    VALIDATING_CERTIFICATE = 'seal-credential.steps.validating-certificate',
    SIGNED = 'seal-credential.steps.signed',
}

export enum NEXU_STATUS {
    LOADING = 'loading',
    COMPLETED = 'completed',
    FAILED = 'failed',
}

@Injectable({
    providedIn: 'root',
})
export class NexUService {
    testHashView: any;
    statusMessage: MessageEmitter[] = [];
    testUUID: string;
    testCredential;

    private listSubject = new Subject();
    private dateSignatureBytesViewTest: Date;
    private dssTimestampDTOArrayTest: DSSTimestampDTO;
    private dateSignatureBytesView: Date[] = [];
    private encryptionAlgorithm = null;
    private iteration: number = 0;
    private dssTimestampDTOArray: DSSTimestampDTO[] = [];
    private signatureNexuViewList: SignatureNexuView[] = [];
    private credentialHashView = null;
    private uuids: Array<string>;

    constructor(
        private http: HttpClient,
        private api: V1Service,
        private translate: TranslateService,
        private issuerService: IssuerService,
        private notifService: NotificationService,
        private handler: HttpBackend,
        private uxDynamicModalService: UxDynamicModalService
    ) {}

    resetSteps() {
        this.statusMessage = [];
    }

    getTestSteps() {
        return this.listSubject;
    }

    /**
     * Send credentials to email
     */
    toSend(credentials: CredentialView[]) {
        return this.api.sendCredentials(
            credentials,
            this.translate.currentLang
        );
    }

    testSeal() {
        this.checkConnection()
            .do(() =>
                this.addAndUpdateList({
                    status: NEXU_STATUS.LOADING,
                    message: this.translate.instant(
                        MESSAGE_INFO.CONNECTION_READY
                    ),
                })
            )
            .pipe(
                switchMap(() => this.uploadTestCredential()),
                // To step 2 (Get certificate)
                switchMap((uploadedCredentials) => {
                    this.testUUID = uploadedCredentials.credentials[0].uuid;
                    this.testCredential = uploadedCredentials.credentials[0];
                    return this.getCertificates().do(() =>
                        this.addAndUpdateList({
                            status: NEXU_STATUS.LOADING,
                            message: this.translate.instant(
                                MESSAGE_INFO.READ_CERTIFICATES
                            ),
                        })
                    );
                }),
                // To step 3 (Get Signature Bytes)
                switchMap((signatureParams: SignatureParametersView) =>
                    this.getSignatureBytesTest(signatureParams).do(() =>
                        this.addAndUpdateList({
                            status: NEXU_STATUS.LOADING,
                            message: this.translate.instant(
                                MESSAGE_INFO.VALIDATING_CERTIFICATE
                            ),
                        })
                    )
                ),
                // To step 4 (Sign Credentials)
                switchMap((signatureBytes: SignatureBytesView[]) =>
                    this.signCredentialsTest(signatureBytes)
                ),
                // To step 5 (Seal Credentials)
                switchMap((signedCredential) =>
                    this.sealCredentialsTest(signedCredential).do(() =>
                        this.addAndUpdateList({
                            status: NEXU_STATUS.COMPLETED,
                            message: this.translate.instant(
                                MESSAGE_INFO.SIGNED
                            ),
                        })
                    )
                ),
                catchError((error: HttpErrorResponse) => {
                    if (error.url.includes(API)) {
                        this.notifService.showNotification(error);
                    }
                    return throwError(error);
                })
            )
            .subscribe();
    }

    /**
     * Seal credentials
     */
    toSeal(uuids: Array<string>) {
        this.iteration = 0;
        this.uuids = uuids;
        this.signatureNexuViewList = [];
        this.dateSignatureBytesView = [];
        this.dssTimestampDTOArray = [];

        // Step 1
        return this.checkConnection().pipe(
            // To step 2
            switchMap(() => this.getCertificates()),
            // To step 3
            switchMap((signatureParams: SignatureParametersView) =>
                this.getSignatureBytes(signatureParams)
            ),
            // Display confirmation modal
            switchMap(
                (signatureBytesResponse: SignatureBytesView[], index: number) =>
                    this.openConfirmationModal(signatureBytesResponse, index)
            ),
            // To step 4
            switchMap((signatureBytes: SignatureBytesView[]) =>
                this.signCredential(signatureBytes)
            ),
            // To step 5
            switchMap((signedCredentials) => this.toXaDes(signedCredentials)),
            catchError((error: HttpErrorResponse) => {
                if (error.status === 0) {
                    this.notifService.showNotification(error);
                }
                return throwError(error);
            })
        );
    }

    private getSignatureBytes(signatureParams: SignatureParametersView) {
        this.encryptionAlgorithm = signatureParams.response.encryptionAlgorithm;
        this.credentialHashView = {
            signaturePacking: signatureParams.response.keyId,
            certificateResponse: signatureParams.response.certificate,
            digestAlgorithm: signatureParams.response.encryptionAlgorithm,
            signatureLevel: signatureParams.response.tokenId.id,
        };
        signatureParams.uuids = this.uuids;
        signatureParams.presentation = this.issuerService.presentation;
        return this.api.getSignatureBytes(
            signatureParams,
            this.translate.currentLang
        );
    }

    private signCredential(signatures: SignatureBytesView[]) {
        let signedCredentials = [];
        signatures.forEach((signature: SignatureBytesView) => {
            const params = {
                toBeSigned: {
                    bytes: signature.bytes,
                },
                digestAlgorithm: 'SHA256',
                tokenId: {
                    id: this.credentialHashView.signatureLevel,
                },
                keyId: this.credentialHashView.signaturePacking,
            };
            signedCredentials.push(this.toSign(params));
            this.dateSignatureBytesView.push(signature.date);
            this.dssTimestampDTOArray.push(signature.dssTimestampDTO);
        });
        return concat(...signedCredentials);
    }

    private toXaDes(signedCredentials) {
        signedCredentials.response.encryptionAlgorithm = this.encryptionAlgorithm;
        const credential = this.findCredential(this.uuids[this.iteration]);
        const date = this.dateSignatureBytesView[this.iteration];
        const dssTimestampDTO = this.dssTimestampDTOArray[this.iteration];
        this.signatureNexuViewList.push({
            uuid: this.uuids[this.iteration],
            success: signedCredentials.success,
            response: signedCredentials.response,
            feedback: signedCredentials.feedback,
            credential: credential,
            date: date,
            dssTimestampDTO: dssTimestampDTO,
            presentation: this.issuerService.presentation,
        });
        this.iteration++;

        if (this.iteration === this.uuids.length) {
            return this.api.sealCredentials(
                this.signatureNexuViewList,
                this.translate.currentLang
            );
        } else {
            return empty();
        }
    }

    private findCredential(uuid: string) {
        const credentials = this.issuerService.getCredentials();
        return credentials.find((cred) => cred.uuid === uuid);
    }

    private openConfirmationModal(
        signatureBytesResponse: SignatureBytesView[],
        index: number
    ) {
        const modalResolution: Subject<SignatureBytesView[]> = new Subject<
            SignatureBytesView[]
        >();
        this.removeInvalidCredential(signatureBytesResponse);
        this.openDynamicModal(signatureBytesResponse, modalResolution, index);
        return modalResolution;
    }

    private removeInvalidCredential(
        signatureBytesResponse: SignatureBytesView[]
    ): void {
        let signatureBytesErrors: any[] = [];
        signatureBytesResponse.forEach(
            (signatureBytes: SignatureBytesView, index: number) => {
                if (!signatureBytes.valid) {
                    const error = {
                        uuid: signatureBytes.uuid,
                        errorMessage: signatureBytes.errorMessage,
                    };
                    signatureBytesErrors.push(error);
                    signatureBytesResponse.splice(index, 1);
                    this.uuids.splice(index, 1);
                }
            }
        );
        this.issuerService.signatureBytesErrors = signatureBytesErrors;
    }

    /* Send message status */
    private addAndUpdateList(step: MessageEmitter) {
        this.statusMessage = this.statusMessage
            .map((message: MessageEmitter) => {
                message.status = NEXU_STATUS.COMPLETED;
                return message;
            })
            .slice();

        const currentMessage: MessageEmitter = this.statusMessage.find(
            (message: MessageEmitter) => message.message === step.message
        );
        if (currentMessage) {
            currentMessage.status = step.status;
        } else {
            this.statusMessage.push(step);
        }
        this.listSubject.next(this.statusMessage);
    }

    // Nexu API calls
    private checkConnection(): Observable<any> {
        // Skipping all interceptors to prevent CORS problems (REVIEW)
        this.http = new HttpClient(this.handler);

        return this.http.get<any>(`${API}/nexu-info`).pipe(
            catchError((err) => {
                this.addAndUpdateList({
                    status: NEXU_STATUS.FAILED,
                    message: this.translate.instant(
                        MESSAGE_INFO.CHECK_CONNECTION
                    ),
                });
                return Observable.throwError(
                    Object.assign(err, {
                        message: this.translate.instant(
                            'seal-credential.error.connection-error'
                        ),
                    })
                );
            })
        );
    }

    private getCertificates(): Observable<SignatureParametersView> {
        return this.http.get<any>(`${API}/rest/certificates`).pipe(
            catchError((err) => {
                this.addAndUpdateList({
                    status: NEXU_STATUS.FAILED,
                    message: this.translate.instant(
                        MESSAGE_INFO.READ_CERTIFICATES
                    ),
                });
                return Observable.throwError(
                    Object.assign(err, {
                        message: this.translate.instant(
                            'seal-credential.error.certificates-error'
                        ),
                    })
                );
            })
        );
    }

    private toSign(params: any): Observable<any> {
        return this.http.post<any>(`${API}/rest/sign`, params).pipe(
            catchError((err) => {
                this.addAndUpdateList({
                    status: NEXU_STATUS.FAILED,
                    message: this.translate.instant(MESSAGE_INFO.SIGNED),
                });
                return Observable.throwError(
                    Object.assign(err, {
                        message: this.translate.instant(
                            'seal-credential.error.sign-error'
                        ),
                    })
                );
            })
        );
    }

    private getSignatureBytesTest(
        signatureParams: SignatureParametersView
    ): Observable<SignatureBytesView[]> {
        signatureParams.uuids = [this.testUUID];
        signatureParams.presentation = 'EC';
        this.testHashView = {
            signaturePacking: signatureParams.response.keyId,
            certificateResponse: signatureParams.response.certificate,
            digestAlgorithm: signatureParams.response.encryptionAlgorithm,
            signatureLevel: signatureParams.response.tokenId.id,
        };
        return this.api
            .getSignatureBytes(signatureParams, this.translate.currentLang)
            .pipe(
                catchError((err) => {
                    this.addAndUpdateList({
                        status: NEXU_STATUS.FAILED,
                        message: this.translate.instant(
                            MESSAGE_INFO.VALIDATING_CERTIFICATE
                        ),
                    });
                    return Observable.throwError(
                        Object.assign(err, {
                            message: this.translate.instant(
                                'seal-credential.error.sign-error'
                            ),
                        })
                    );
                })
            );
    }

    private signCredentialsTest(
        signature: SignatureBytesView[]
    ): Observable<{}> {
        const params = {
            toBeSigned: {
                bytes: signature[0].bytes,
            },
            digestAlgorithm: 'SHA256',
            tokenId: {
                id: this.testHashView.signatureLevel,
            },
            keyId: this.testHashView.signaturePacking,
        };
        this.dateSignatureBytesViewTest = signature[0].date;
        this.dssTimestampDTOArrayTest = signature[0].dssTimestampDTO;
        return this.toSign(params);
    }

    private sealCredentialsTest(
        signedCredential
    ): Observable<CredentialView[]> {
        const signatureNexuViewList: SignatureNexuView[] = [
            {
                uuid: this.testUUID,
                success: signedCredential.success,
                response: signedCredential.response,
                feedback: signedCredential.feedback,
                credential: this.testCredential,
                date: this.dateSignatureBytesViewTest,
                dssTimestampDTO: this.dssTimestampDTOArrayTest,
                presentation: 'EC',
            },
        ];
        return this.api
            .sealCredentials(signatureNexuViewList, this.translate.currentLang)
            .pipe(
                catchError((err) => {
                    this.addAndUpdateList({
                        status: NEXU_STATUS.FAILED,
                        message: this.translate.instant(MESSAGE_INFO.SIGNED),
                    });
                    return Observable.throwError(
                        Object.assign(err, {
                            message: this.translate.instant(
                                'seal-credential.error.sign-error'
                            ),
                        })
                    );
                })
            );
    }

    private uploadTestCredential(): Observable<CredentialFileUploadResponseView> {
        return this.api
            .getTestCredential()
            .switchMap((testXml) =>
                this.api.addCredentials(testXml, this.translate.currentLang)
            );
    }

    private openDynamicModal(
        signatureBytesResponse: SignatureBytesView[],
        modalResolution: Subject<SignatureBytesView[]>,
        index: number
    ): void {
        const config = new UxDynamicModalConfig({
            id: 'signConfirmationModal',
            content: signatureBytesResponse[index].warningMsg,
            customWidth: '30%',
            styleClass: 'signature-modal',
            isShowCloseButton: false,
            isCloseModalOnEscape: false,
            customFooterContent: {
                right: {
                    buttons: [
                        new UxButtonConfig({
                            label: this.translate.instant('common.cancel'),
                            typeClass: 'secondary',
                            styleClass: 'mr-3',
                            onClick: (portalHostRef, portalRef) => {
                                this.uxDynamicModalService.closeModal(
                                    portalHostRef,
                                    portalRef
                                );
                                modalResolution.next(null);
                            },
                        }),
                        new UxButtonConfig({
                            label: this.translate.instant('common.proceed'),
                            typeClass: 'primary',
                            onClick: (portalHostRef, portalRef) => {
                                this.uxDynamicModalService.closeModal(
                                    portalHostRef,
                                    portalRef
                                );
                                modalResolution.next(signatureBytesResponse);
                            },
                        }),
                    ],
                },
            },
        });
        if (signatureBytesResponse.length > 0) {
            this.uxDynamicModalService.openModal(config);
        } else {
            modalResolution.next(null);
        }
    }
}
