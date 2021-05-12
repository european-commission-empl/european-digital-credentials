import {
    HttpClient,
    HttpErrorResponse,
    HttpHeaders,
    HttpResponse,
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
    ChangeDetectorRef,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UxEuLanguages, UxLink, UxService } from '@eui/core';
import { EclLanguage } from '@eui/ecl-core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
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
    private _availableLanguages: string[] = [];
    private _primaryLanguage: string;
    @ViewChild('inputFile') inputFile: ElementRef;
    @Input() isDetailDisabled: boolean = true;
    @Input() isDetails: boolean = false;
    @Input() set availableLanguages(languages: string[]) {
        this._availableLanguages = languages;
        this.setLanguageDropdown();
    }
    @Input() set primaryLanguage(language: string) {
        const primaryLanguage = this.languageList.find(
            (l) => l.code === language
        );
        if (primaryLanguage) {
            this.selectedLanguage = primaryLanguage.label;
            this.locale = primaryLanguage.code;
        } else {
            this.locale = 'en';
            this.selectedLanguage = 'English';
            this._primaryLanguage = 'en';
        }
        this._primaryLanguage = this.selectedLanguage;
    }
    @Output()
    onLanguageChange: EventEmitter<string> = new EventEmitter<string>();
    @Output()
    onNewCredentialUpload: EventEmitter<boolean> = new EventEmitter<boolean>();
    sharedFromURL: string;
    userId: string = sessionStorage.getItem('userId');
    credId: string = sessionStorage.getItem('credId');
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
    exportLinks: UxLink[];
    languages: UxLink[] = [];
    locale: string;
    isCopyToClipBoardClicked: boolean;
    selectedLanguage: string;
    sharedForm: FormGroup = new FormGroup({
        date: new FormControl('', [Validators.required]),
    });
    languageList: EclLanguage[] = this.viewerService.addMissingLanguages(
        UxEuLanguages.getLanguages()
    );

    get date(): Date {
        return this.sharedForm.get('date').value as Date;
    }

    get availableLanguages(): string[] {
        return this._availableLanguages;
    }

    get primaryLanguage(): string {
        return this._primaryLanguage;
    }

    constructor(
        public uxService: UxService,
        private viewerService: ViewerService,
        public shareDataService: ShareDataService,
        private router: Router,
        private http: HttpClient,
        private renderer: Renderer2,
        private translateService: TranslateService,
        private apiService: V1Service,
        private changeDetectorRef: ChangeDetectorRef
    ) {
        this.minDate.setHours(23, 59, 59, 999);
        this.translateService.onLangChange
            .takeUntil(this.destroy$)
            .subscribe((event: LangChangeEvent) => {
                this.setExportDropdown();
            });
    }

    ngOnInit() {
        this.setLanguageDropdown();
        this.setInitialState();
        this.setExportDropdown();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    viewDetails(): void {
        this.router.navigate(['diploma-details']);
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

    viewDiploma(): void {
        this.router.navigate(['diploma']);
    }

    /**
     * Reads the content of an uploaded XML file and stores it as string.
     */
    readFile(event: FileList): void {
        const reader = new FileReader();
        reader.readAsText(event[0], 'utf-8');
        reader.onloadend = () => {
            sessionStorage.removeItem('isPreview');
            sessionStorage.removeItem('credId');
            sessionStorage.removeItem('userId');
            sessionStorage.removeItem('shareLink');
            this.shareDataService.uploadedXML = reader.result as string;
            this.shareDataService.toolbarLanguage = null;
            if (this.router.url === '/diploma') {
                this.onNewCredentialUpload.emit(true);
            }
            this.router.navigate(['diploma']);
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
    checkValidDate(): void {
        if (this.date) {
            if (typeof this.date === 'string') {
                if (moment(this.minDate).isAfter(this.date)) {
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
        const dateTime: string = moment(this.date).set({
                hour: 23,
                minute: 59,
                second: 59,
                millisecond: 999
            }).format('YYYY-MM-DDTHH:mm:ssZ');
        this.userId = this.shareDataService.userId;
        this.credId = this.shareDataService.credentialId;
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
                    this.closeModal();
                }
            );
    }

    closeModal() {
        this.uxService.closeModal('shareCredentialModal');
        this.resetModal();
        this.isCopyToClipBoardClicked = false;
        if (this.getLinkRequest) {
            this.getLinkRequest.unsubscribe();
        }
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

    private downloadVP(exportedFileType: string): void {
        this.sharedFromURL = sessionStorage.getItem('shareLink');
        this.credId = sessionStorage.getItem('credId');
        this.userId = sessionStorage.getItem('userId');
        const httpMethod: string = this.credId ? `POST` : `GET`;
        if (exportedFileType === 'PDF') {
            const headers: HttpHeaders = new HttpHeaders({
                Accept: 'application/pdf',
            });
            this.downloadPDF(headers, httpMethod, this.getDownloadURL());
        } else if (exportedFileType === 'XML') {
            const headers: HttpHeaders = new HttpHeaders({
                Accept: 'application/octet-stream',
            });
            this.downloadXML(headers, httpMethod, this.getDownloadURL());
        }
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
                url
            );
        } else {
            this.downloadVerifiablePresentationFromFile(headers);
        }
    }

    private downloadPDF(
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
                url
            );
        } else {
            this.downloadVerifiablePresentationFromFile(headers);
        }
    }

    private downloadVerifiablePresentation(
        httpMethod: string,
        headers: HttpHeaders,
        body: any,
        url: string
    ): void {
        this.http
            .request(httpMethod, `${this.basePath}${url}`, {
                body: body,
                headers: headers,
                params: { locale: this.locale },
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

    private downloadVerifiablePresentationFromFile(headers: HttpHeaders): void {
        this.XMLFile = sessionStorage.getItem('diplomaXML');
        const xmlBlob: Blob = new Blob([this.XMLFile], {
            type: 'text/xml',
        });
        let body: { append(param: string, value: any): any };
        body = new FormData();
        body = body.append('file', <any>xmlBlob) || body;
        this.isDownloading = true;
        this.http
            .post(`${this.basePath}/api/v1/credentials/presentation`, body, {
                params: { locale: this.locale },
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

    private setExportDropdown(): void {
        this.exportLinks = null;
        this.changeDetectorRef.detectChanges();
        this.exportLinks = [
            new UxLink({
                id: '0',
                label: this.translateService.instant('downloadXml'),
                iconClass: 'fa fa-file-code-o',
                tooltipLabel: this.translateService.instant('details.download'),
                disabled: this.isPreview,
                command: () => this.downloadVP('XML'),
            }),
            new UxLink({
                id: '1',
                label: this.translateService.instant('downloadPdf'),
                iconClass: 'fa fa-file-pdf-o',
                tooltipLabel: this.translateService.instant('details.download'),
                disabled: this.isPreview,
                command: () => this.downloadVP('PDF'),
            }),
        ];
    }

    private setInitialState(): void {
        this.isPreview = !!sessionStorage.getItem('isPreview');
        this.userId = sessionStorage.getItem('userId');
        this.credId = sessionStorage.getItem('credId');
        this.shareDataService.credentialId = this.shareDataService.credentialId
            ? this.shareDataService.credentialId
            : sessionStorage.getItem('credId');
        this.shareDataService.userId = this.shareDataService.userId
            ? this.shareDataService.userId
            : sessionStorage.getItem('userId');
    }
}
