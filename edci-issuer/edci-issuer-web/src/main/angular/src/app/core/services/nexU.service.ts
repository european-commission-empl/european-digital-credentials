import {
    HttpBackend,
    HttpClient,
    HttpErrorResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import {
    CredentialView,
    DSSTimestampDTO,
    CredentialFileUploadResponseView,
    SignatureBytesView,
    SignatureNexuView,
    AttachmentView,
    V1Service,
} from '@shared/swagger';
import {
    SignatureParametersView,
} from '@shared/swagger/model/signatureParametersView';
import { concat, empty, Observable, Subject, throwError } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';
import { NotificationService } from './error.service';
import { IssuerService } from './issuer.service';
import { EuiDialogConfig, EuiDialogService } from '@eui/components/eui-dialog';

import { FooterComponent } from '@core/components/app-shell/footer/footer.component';
import { ApiService } from './api.service';

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
    private iteration = 0;
    private dssTimestampDTOArray: DSSTimestampDTO[] = [];
    private signatureNexuViewList: SignatureNexuView[] = [];
    private credentialHashView = null;
    private uuids: Array<string>;

    constructor(
        private http: HttpClient,
        private api: V1Service,
        private apiSwaggerService: ApiService,
        private translate: TranslateService,
        private issuerService: IssuerService,
        private notifService: NotificationService,
        private handler: HttpBackend,
        private euiDialogService: EuiDialogService
    ) { }

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
        this.checkConnection().pipe(
            tap(() =>
                this.addAndUpdateList({
                    status: NEXU_STATUS.LOADING,
                    message: this.translate.instant(
                        MESSAGE_INFO.CONNECTION_READY
                    ),
                })
            ))
            .pipe(
                switchMap(() => this.uploadTestCredential()),
                // To step 2 (Get certificate)
                switchMap((uploadedCredentials: any) => {
                    this.testUUID = uploadedCredentials.credentials[0].uuid;
                    this.testCredential = uploadedCredentials.credentials[0];
                    return this.getCertificates().pipe(tap(() =>
                        this.addAndUpdateList({
                            status: NEXU_STATUS.LOADING,
                            message: this.translate.instant(
                                MESSAGE_INFO.READ_CERTIFICATES
                            ),
                        })
                    ));
                }),
                // To step 3 (Get Signature Bytes)
                switchMap((signatureParams: SignatureParametersView) =>
                    this.getSignatureBytesTest(signatureParams).pipe(tap(() =>
                        this.addAndUpdateList({
                            status: NEXU_STATUS.LOADING,
                            message: this.translate.instant(
                                MESSAGE_INFO.VALIDATING_CERTIFICATE
                            ),
                        })
                    ))
                ),
                // To step 4 (Sign Credentials)
                switchMap((signatureBytes: SignatureBytesView[]) =>
                    this.signCredentialsTest(signatureBytes)
                ),
                // To step 5 (Seal Credentials)
                switchMap((signedCredential) =>
                    this.sealCredentialsTest(signedCredential).pipe(tap(() =>
                        this.addAndUpdateList({
                            status: NEXU_STATUS.COMPLETED,
                            message: this.translate.instant(
                                MESSAGE_INFO.SIGNED
                            ),
                        })
                    ))
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
    toSeal(uuids: Array<string>, mandatedIssue: AttachmentView) {
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
                this.getSignatureBytes(signatureParams, mandatedIssue)
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
            }),
        );
    }

    private getSignatureBytes(signatureParams: SignatureParametersView, mandatedIssue: AttachmentView) {
        this.encryptionAlgorithm = signatureParams.response.encryptionAlgorithm;
        this.credentialHashView = {
            signaturePacking: signatureParams.response.keyId,
            certificateResponse: signatureParams.response.certificate,
            digestAlgorithm: signatureParams.response.encryptionAlgorithm,
            signatureLevel: signatureParams.response.tokenId.id,
        };
        signatureParams.uuids = this.uuids;
        signatureParams.presentation = this.issuerService.presentation;
        if (Object.keys(mandatedIssue).length > 0) {
            signatureParams.mandatedIssue = mandatedIssue;
        }

        return this.api.getSignatureBytes(
            signatureParams,
            this.translate.currentLang
        );

    }

    private signCredential(signatures: SignatureBytesView[]) {
        const signedCredentials = [];
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
        const signatureBytesErrors: any[] = [];
        const correctSignatureBytes: any[] = [];
        signatureBytesResponse.forEach(
            (signatureBytes: SignatureBytesView, index: number) => {
                if (!signatureBytes.valid) {
                    const error = {
                        uuid: signatureBytes.uuid,
                        errorMessage: signatureBytes.errorMessage,
                    };
                    signatureBytesErrors.push(error);
                    /* signatureBytesResponse.splice(index, 1);
                    this.uuids.splice(index, 1); */
                } else {
                    correctSignatureBytes.push(signatureBytes);
                }
            }
        );
        /*
        * workaround to show a growl message when signature bytes has issues (ie: malformed subjects),
         remove when refactoring sealing pipe.
        */
        signatureBytesErrors.forEach(signatureBytesError => {
            const errorUuid = signatureBytesError.uuid;
            const errorIndex = signatureBytesErrors.findIndex(signatureBytes => signatureBytes.uuid === errorUuid);
            signatureBytesResponse.splice(errorIndex, 1);
            const uuidsIndex = this.uuids.indexOf(errorUuid);
            this.uuids.splice(uuidsIndex, 1);
        });
        if (signatureBytesResponse.length === 0) {
            const summary = this.translate.instant('seal-credential.message.seal-ko');
            const notification = signatureBytesErrors.map(error => {
                return error.errorMessage;
            }).join('\n');
            this.notifService.showNotificationText(summary, notification, true);
        }
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
                return throwError(
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
                return throwError(
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
                return throwError(
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
                    return throwError(
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
                    return throwError(
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
        return this.api.getTestCredential().pipe(
            switchMap((testXml) => this.apiSwaggerService.addCredentials([testXml], this.translate.currentLang))
        );
    }

    private openDynamicModal(
        signatureBytesResponse: SignatureBytesView[],
        modalResolution: Subject<SignatureBytesView[]>,
        index: number
    ): void {
        const config = new EuiDialogConfig({
            title: 'signConfirmationModal',
            content: signatureBytesResponse[index].warningMsg,
            width: '30rem',
            typeClass: 'primary',
            hasCloseButton: false,
            isClosedOnEscape: false,
            dismissLabel: this.translate.instant('common.cancel'),
            acceptLabel: this.translate.instant('common.proceed'),
            accept: () => {
                this.euiDialogService.closeDialog();
                modalResolution.next(signatureBytesResponse);
            },
            dismiss: () => {
                this.euiDialogService.closeDialog();
                modalResolution.next(null);
            }
        });
        if (signatureBytesResponse.length > 0) {
            this.euiDialogService.openDialog(config);
        } else {
            modalResolution.next(null);
        }
    }
}
