import {
    HttpClient,
    HttpErrorResponse,
    HttpHeaders,
    HttpResponse,
    HttpParams,
} from '@angular/common/http';
import {
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    Renderer2,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UxEuLanguages, UxLink, UxService } from '@eui/core';
import { EclLanguage } from '@eui/ecl-core';
import { TranslateService } from '@ngx-translate/core';
import * as moment from 'moment';
import { Subject, Subscription } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { ViewerService } from 'src/app/core/services/viewer.service';
import { environment } from 'src/environments/environment';
import { ShareLinkView, V1Service } from '../../swagger';

@Component({
    selector: 'edci-viewer-diploma-toolbar',
    templateUrl: './diploma-toolbar.component.html',
    styleUrls: ['./diploma-toolbar.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class DiplomaToolbarComponent implements OnInit, OnDestroy {
    private _userId: string;
    private _credId: string;
    private _availableLanguages: string[] = [];
    private _primaryLanguage: string;
    private _isXMLDisabled: boolean = false;
    private _downloadOriginal: boolean = environment.displayDownloadOriginal;

    @ViewChild('inputFile') inputFile: ElementRef;

    @Input() isDetailDisabled: boolean = true;
    @Input()
    set isXMLDisabled(value: boolean) {
        this._isXMLDisabled = value;
    }
    get isXMLDisabled(): boolean {
        return this._isXMLDisabled;
    }

    @Input()
    set availableLanguages(languages: string[]) {
        this._availableLanguages = languages;
        this.setLanguageDropdown();
    }
    get availableLanguages(): string[] {
        return this._availableLanguages;
    }

    @Input()
    set primaryLanguage(language: string) {
        const primaryLanguage = this.languageList.find(
            (lang) => lang.code === language
        );
        if (primaryLanguage) {
            this.selectedLanguage = primaryLanguage.label;
            this.locale = primaryLanguage.code;
        } else {
            this.locale = 'en';
            this.selectedLanguage = 'English';
        }
        this._primaryLanguage = this.selectedLanguage;
    }
    get primaryLanguage(): string {
        return this._primaryLanguage;
    }

    @Input()
    set userId(value: string) {
        this._userId = value;
    }
    get userId(): string {
        return this._userId;
    }

    @Input()
    set credId(value: string) {
        this._credId = value;
    }
    get credId(): string {
        return this._credId;
    }

    get downloadOriginal(): boolean {
        return this._downloadOriginal;
    }

    @Output() onLanguageChange: EventEmitter<string> =
        new EventEmitter<string>();
    @Output() onUploadNewCredential: EventEmitter<string> =
        new EventEmitter<string>();

    environment = environment;
    pdfType: string;
    sharedFromURL: string;
    XMLFile: string;
    isPreview: boolean;
    minDate: Date = new Date(new Date().getTime() + 0 * 60 * 60 * 1000);
    shareLink: string = this.shareDataService.shareLink;
    feedback: 'danger' | 'success';
    generateLinkDisabled: boolean = true;
    basePath: string = environment.apiBaseUrl;
    downloadCredBasePath: string = environment.downloadCredentialUrl;
    downloadSharedCredBasePath: string =
        environment.downloadSharedCredentialUrl;
    dateChange: boolean = true;
    loading: boolean = false;
    isDownloading: boolean = false;
    getLinkRequest: Subscription;
    destroy$: Subject<boolean> = new Subject<boolean>();
    languages: UxLink[] = [];
    locale: string;
    isCopyToClipBoardClicked: boolean;
    selectedLanguage: string;
    sharedForm: FormGroup = new FormGroup({
        date: new FormControl('', [Validators.required]),
    });
    downloadForm: FormGroup = new FormGroup({
        date: new FormControl('', [Validators.required]),
    });
    languageList: EclLanguage[] = this.viewerService.addMissingLanguages(
        UxEuLanguages.getLanguages()
    );

    get shareDate(): Date {
        return this.sharedForm.get('date').value as Date;
    }

    get downloadDate(): any {
        return this.downloadForm.get('date').value;
    }

    constructor(
        public uxService: UxService,
        private viewerService: ViewerService,
        public shareDataService: ShareDataService,
        private router: Router,
        private http: HttpClient,
        private renderer: Renderer2,
        private translateService: TranslateService,
        private apiService: V1Service
    ) {
        this.minDate.setHours(23, 59, 59, 999);
        this.sharedFromURL = sessionStorage.getItem('shareLink');
    }

    ngOnInit() {
        this.isPreview = !!sessionStorage.getItem('isPreview');
        this.setLanguageDropdown();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    uploadAnotherCredential(): void {
        if (!this.isPreview) {
            return this.inputFile.nativeElement.click();
        }
    }

    languageChange(language: UxLink): void {
        this.onLanguageChange.emit(language.id);
        this.locale = language.id;
    }

    navigateHome(): void {
        this.router.navigate(['home']);
    }

    /**
     * Reads the content of an uploaded XML file and stores it as string.
     */
    readFile(event: FileList): void {
        const reader = new FileReader();
        reader.readAsText(event[0], 'utf-8');
        reader.onloadend = () => {
            sessionStorage.clear();
            this.resetVerificationSteps();
            sessionStorage.setItem('diplomaXML', reader.result as string);
            this.onUploadNewCredential.emit(reader.result as string);
        };
    }

    /**
     * Validates if the date on the date picker is correct.
     * - If the datePicker is empty and touched, will display an error
     * - If the date is after the min date, it will clean the date picker
     *   and display an error.
     * To access this method the model (input) of the datePicker must have
     * changed, no need to check that in here.
     */
    checkValidDate(date): void {
        if (date) {
            if (typeof date === 'string') {
                if (moment(this.minDate).isAfter(date)) {
                    this.feedback = 'danger';
                    this.generateLinkDisabled = true;
                    this.sharedForm.patchValue({ date: '' });
                }
            } else {
                this.feedback = 'success';
                this.generateLinkDisabled = false;
                this.dateChange = true;
            }
        } else {
            this.feedback = 'danger';
            this.generateLinkDisabled = true;
        }
    }

    /**
     * Blocks "generate link" button to prevent multiple request (dateChange)
     * and starts spinner (loading).
     * Makes the request to generate the link. If successful stops spinner
     * If error closes modal and shows error.
     */
    generateLink(): void {
        this.dateChange = false;
        this.loading = true;
        const dateTime: string = moment(this.shareDate)
            .set({
                hour: 23,
                minute: 59,
                second: 59,
                millisecond: 999,
            })
            .format('YYYY-MM-DDTHH:mm:ssZ');
        const shareLinkView: ShareLinkView = { expirationDate: dateTime };
        this.apiService
            .createShareLink(
                this.userId,
                this.credId,
                shareLinkView,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data) => {
                    this.shareLink = this.getViewLinkFromResourceLinks(data);
                    this.loading = false;
                },
                (response: HttpErrorResponse) => {
                    this.closeShareModal();
                }
            );
    }

    closeShareModal() {
        this.uxService.closeModal('shareCredentialModal');
        this.resetModal();
        this.isCopyToClipBoardClicked = false;
        if (this.getLinkRequest) {
            this.getLinkRequest.unsubscribe();
        }
    }

    closeDownloadModal() {
        this.uxService.closeModal('pdfDownloadModal');
        this.resetModal();
    }

    downloadSharePDF(): void {
        const httpMethod: string = this.credId ? `POST` : `GET`;
        const headers: HttpHeaders = new HttpHeaders({
            Accept: 'application/pdf',
        });
        this.downloadPDF(
            headers,
            httpMethod,
            this.getDownloadURL(),
            this.pdfType
        );
    }

    downloadOriginalXML(): void {
        if (!this.isPreview) {
            this.isDownloading = true;
            if (this.userId && this.credId) {
                this.downloadOriginalFromWallet();
            } else if (this.sharedFromURL) {
                this.downloadOriginalFromShareLink();
            } else {
                this.isDownloading = false;
            }
        }
    }

    downloadVP(exportedFileType: string, pdfType?: string): void {
        if (!this.isPreview) {
            this.pdfType = null;
            const httpMethod: string = this.credId ? `POST` : `GET`;
            if (exportedFileType === 'PDF') {
                this.pdfType = pdfType;
                const headers: HttpHeaders = new HttpHeaders({
                    Accept: 'application/pdf',
                });
                this.credId
                    ? this.uxService.openModal('pdfDownloadModal')
                    : this.downloadPDF(
                          headers,
                          httpMethod,
                          this.getDownloadURL(),
                          this.pdfType
                      );
            } else if (exportedFileType === 'XML') {
                const headers: HttpHeaders = new HttpHeaders({
                    Accept: 'application/octet-stream',
                });
                this.downloadXML(headers, httpMethod, this.getDownloadURL());
            }
        }
    }

    private downloadOriginalFromWallet(): void {
        const headers: HttpHeaders = new HttpHeaders({
            Accept: 'application/octet-stream',
        });
        let url = this.downloadCredBasePath.replace(
            environment.walletAddressParameter,
            this.userId
        );
        url = url.replace('verifiable', 'credential');
        this.http
            .post(
                `${this.basePath}${url}`,
            {
                uuid: this.credId,
            },
            {
                observe : 'response',
                responseType : 'blob',
                headers: headers,
            }
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (originalXML: any) => {
                    this.downloadFile(originalXML);
                    this.isDownloading = false;
                },
                (response: HttpErrorResponse) => {
                    this.isDownloading = false;
                }
            );
    }

    private downloadOriginalFromShareLink(): void {
        const headers: HttpHeaders = new HttpHeaders({
            Accept: 'application/octet-stream',
        });
        let url = this.downloadSharedCredBasePath.replace(
            environment.shareHashParameter,
            this.sharedFromURL
        );
        url = url.replace('presentation', 'credential');
        this.http
            .get(`${this.basePath}${url}`, {
                observe : 'response',
                responseType : 'blob',
                headers: headers,
            })
            .takeUntil(this.destroy$)
            .subscribe(
                (originalXML: HttpResponse<Blob>) => {
                    this.downloadFile(originalXML);
                    this.isDownloading = false;
                },
                (response: HttpErrorResponse) => {
                    this.isDownloading = false;
                }
            );
    }

    private resetVerificationSteps(): void {
        sessionStorage.setItem('verificationSteps', null);
        this.shareDataService.verificationSteps = null;
        this.shareDataService.setVerificationSteps(null);
    }

    private setLanguageDropdown(): void {
        if (this.availableLanguages) {
            this.setLanguageDropdownAvailableOnly();
        } else {
            this.setLanguageDropdownAll();
        }
    }

    private setLanguageDropdownAll(): void {
        this.languages = [];
        this.languageList.forEach((lang) => {
            this.languages.push(
                new UxLink({
                    id: lang.code,
                    label: lang.label,
                })
            );
        });
    }

    private setLanguageDropdownAvailableOnly(): void {
        this.languages = [];
        this.languageList.forEach((lang) => {
            if (this.availableLanguages.includes(lang.code)) {
                this.languages.push(
                    new UxLink({
                        id: lang.code,
                        label: lang.label,
                    })
                );
            }
        });
    }

    private getViewLinkFromResourceLinks(resourceView: any) {
        let viewLink: string = '';
        if (resourceView.links && resourceView.links.length > 0) {
            viewLink = resourceView.links.find(
                (element) => element.rel === 'view'
            ).href;
        }
        return viewLink;
    }

    private downloadXML(
        headers: HttpHeaders,
        httpMethod: string,
        url: string
    ): void {
        if (url) {
            this.isDownloading = true;
            this.downloadVerifiablePresentation(
                httpMethod,
                headers,
                { uuid: this.credId },
                url,
                'XML'
            );
        } else {
            this.downloadVerifiablePresentationFromFile(headers);
        }
    }

    private downloadPDF(
        headers: HttpHeaders,
        httpMethod: string,
        url: string,
        pdfType: string
    ): void {
        if (url) {
            this.isDownloading = true;
            this.downloadVerifiablePresentation(
                httpMethod,
                headers,
                { uuid: this.credId },
                url,
                pdfType
            );
        } else {
            this.downloadVerifiablePresentationFromFile(headers, pdfType);
        }
    }

    private downloadVerifiablePresentation(
        httpMethod: string,
        headers: HttpHeaders,
        body: any,
        url: string,
        pdfType?: string
    ): void {
        let params = { locale: this.locale, pdfType };
        if (this.credId && pdfType !== 'XML') {
            params['expirationDate'] = this.downloadDate.format('YYYY-MM-DD');
        }
        this.http
            .request(httpMethod, `${this.basePath}${url}`, {
                body: body,
                headers: headers,
                params: params,
                responseType: 'blob',
                observe: 'response',
                withCredentials: false,
            })
            .takeUntil(this.destroy$)
            .subscribe(
                (response: HttpResponse<Blob>) => {
                    this.downloadFile(response);
                    this.isDownloading = false;
                },
                (response: HttpErrorResponse) => {
                    this.isDownloading = false;
                }
            );
    }

    private downloadVerifiablePresentationFromFile(
        headers: HttpHeaders,
        pdfType?: string
    ): void {
        let params = { locale: this.locale };
        if (pdfType) {
            params['pdfType'] = pdfType;
        }
        this.XMLFile = sessionStorage.getItem('diplomaXML');
        const xmlBlob: Blob = new Blob([this.XMLFile], {
            type: 'text/xml',
        });
        let body: { append(param: string, value: any): any };
        body = new FormData();
        body = body.append('file', <any>xmlBlob) || body;
        this.isDownloading = true;
        this.http
            .post(`${this.basePath}/v1/credentials/presentation`, body, {
                params: params,
                headers: headers,
                responseType: 'blob',
                observe: 'response',
                withCredentials: false,
            })
            .takeUntil(this.destroy$)
            .subscribe(
                (response: HttpResponse<Blob>) => {
                    this.downloadFile(response);
                    this.isDownloading = false;
                },
                (response: HttpErrorResponse) => {
                    this.isDownloading = false;
                }
            );
    }

    private downloadFile(response: HttpResponse<Blob>) {
        // Get only the filename
        const fileName: string = response.headers
            .get('content-disposition')
            .match(/filename="(.+)"/)[1];
        const file: Blob = response.body;
        /**
         *  IF: IE10+ & EDGE
         *  ELSE: Chrome, FF and EDGE 13+
         *  NOTE: Edge 13+ is compliant with both these standards
         */
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
            window.navigator.msSaveBlob(file, fileName);
        } else {
            const a = this.renderer.createElement('a');
            this.renderer.setStyle(a, 'display', 'none');
            this.renderer.appendChild(document.body, a);
            const url = window.URL.createObjectURL(file);
            this.renderer.setAttribute(a, 'href', url);
            a.download = fileName;
            a.click();
            window.URL.revokeObjectURL(url);
        }
    }

    private resetModal() {
        this.generateLinkDisabled = true;
        this.loading = false;
        this.dateChange = true;
        this.sharedForm.reset();
        this.downloadForm.reset();
        this.feedback = undefined;
        this.shareLink = undefined;
    }

    private getDownloadURL(): string {
        let url: string = null;
        if (this.credId || this.sharedFromURL) {
            url = this.credId
                ? this.downloadCredBasePath.replace(
                      environment.walletAddressParameter,
                      this.userId
                  )
                : this.downloadSharedCredBasePath.replace(
                      environment.shareHashParameter,
                      this.sharedFromURL
                  );
        }
        return url;
    }
}
