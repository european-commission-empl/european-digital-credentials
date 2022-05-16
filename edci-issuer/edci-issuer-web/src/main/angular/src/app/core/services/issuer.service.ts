import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material';
import { BatchStatusDTO } from '@core/models/DTO/batchStatus.dto';
import { EclLanguage } from '@eui/ecl-core/lib/model/ecl-language.model';
import { SpinnerDialogComponent } from '@shared/components/spinner-dialog/spinner-dialog.component';
import { CredentialView, SignatureNexuView, UserDetailsView, V1Service } from '@shared/swagger';
import { Observable, Subject } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class IssuerService {
    private _files: File[] = [];
    private credentials: CredentialView[] = [];
    private spinnerDialog: MatDialogRef<SpinnerDialogComponent>;
    breadcrumbSubject: Subject<string> = new Subject<string>();
    isExcelDownloadedSubject: Subject<boolean> = new Subject<boolean>();
    private _signatureBytesErrors: any[];
    private _presentation: string;
    private setting = {
        element: {
            dynamicDownload: null as HTMLElement,
        },
    };

    userDetails = new Subject();

    constructor(
        private http: HttpClient,
        private api: V1Service,
        public dialog: MatDialog
    ) {}

    get files(): File[] {
        return this._files;
    }
    set files(files: File[]) {
        this._files = files;
    }

    get presentation(): string {
        return this._presentation;
    }
    set presentation(presentation: string) {
        this._presentation = presentation;
    }

    get userInfo(): UserDetailsView {
        return JSON.parse(localStorage.getItem('userInfo'));
    }

    set userInfo(userDetails: UserDetailsView) {
        this.userDetails.next(userDetails);
        localStorage.setItem('userInfo', JSON.stringify(userDetails));
    }

    get emailRegex() {
        return new RegExp(
            // tslint:disable-next-line: max-line-length
            /(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|"(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21\x23-\x5b\x5d-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])*")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\x01-\x08\x0b\x0c\x0e-\x1f\x21-\x5a\x53-\x7f]|\\[\x01-\x09\x0b\x0c\x0e-\x7f])+)\])/
        );
    }

    public get signatureBytesErrors(): any[] {
        return this._signatureBytesErrors;
    }

    public set signatureBytesErrors(value: any[]) {
        this._signatureBytesErrors = value;
    }

    /**
     *  Download template XLS
     *
     * @param {string} type the type of document to be download
     */
    public downloadTemplate(type: string) {
        this.api
            .getCredentialTemplate(type, null, 'response')
            .subscribe((x) => {
                const contentDispositionHeader = x.headers.get(
                    'content-disposition'
                );
                const fileName = contentDispositionHeader
                    ? contentDispositionHeader
                          .split(';')[1]
                          .trim()
                          .split('=')[1]
                          .replace(/\//g, '')
                          .replace(/['"]+/g, '')
                    : 'template.xls';
                // It is necessary to create a new blob object with mime-type explicitly set
                // otherwise only Chrome works like it should
                const newBlob = new Blob([x.body], { type: x.body.type });

                this.generateBlobLink(newBlob, fileName);
            });
    }

    public downloadRecipientTemplate(oid: number, locale: string) {
        this.api.getRecipientTemplate(oid, locale, 'response').subscribe(
            (x) => {
                this.isExcelDownloadedSubject.next(true);
                const contentDispositionHeader = x.headers.get(
                    'content-disposition'
                );
                const fileName = contentDispositionHeader
                    ? contentDispositionHeader
                          .split(';')[1]
                          .trim()
                          .split('=')[1]
                          .replace(/\//g, '')
                          .replace(/['"]+/g, '')
                    : 'recipient-template.xls';
                // It is necessary to create a new blob object with mime-type explicitly set
                // otherwise only Chrome works like it should
                const newBlob = new Blob([x.body], { type: 'xml' });
                this.generateBlobLink(newBlob, fileName);
            },
            (err) => {
                this.isExcelDownloadedSubject.next(false);
            }
        );
    }

    public openSpinnerDialog() {
        this.spinnerDialog = this.dialog.open(SpinnerDialogComponent, {
            backdropClass: 'blueBackdrop',
            maxWidth: '100vw',
            minWidth: '100vw',
            maxHeight: '100vh',
            minHeight: '100vh',
        });
    }

    public closeSpinnerDialog() {
        this.spinnerDialog.close();
    }

    /**
     * Seal credentials
     */
    public sealCredentials(
        credentialListView: SignatureNexuView[]
    ): Observable<any> {
        return this.api.sealCredentials(credentialListView).pipe(delay(3000));
    }

    /** Download credential */
    public downloadCredential(uuid: string): Observable<any> {
        return this.api.downloadCredential(uuid).pipe(delay(1000));
    }

    /** Delete credential */
    public deleteCredential(uuid: string): Observable<any> {
        return this.api.deleteCredentials(uuid);
    }

    getCredentials(): CredentialView[] {
        return this.credentials.slice();
    }

    setCredentials(credentials: CredentialView[]) {
        this.credentials = credentials;
    }

    getBatchStatus(): Observable<BatchStatusDTO> {
        return this.http.get<BatchStatusDTO>('/api/batch/status');
    }

    // Adding missing languages on eUI list
    addMissingLanguages(languages: EclLanguage[]): EclLanguage[] {
        const missingLanguages = [
            {
                code: 'sr',
                label: 'srpski',
            },
        ];
        missingLanguages.forEach((missingLanguage) => {
            languages.push(missingLanguage);
        });
        return languages;
    }

    generateCSVfromJSON(json: any): any {
        const items = json;
        const replacer = (key, value) => (value === null ? ' ' : value);
        const header = Object.keys(items[0]);
        return [
            header.join(';'),
            ...items.map((row) =>
                header
                    .map((fieldName) =>
                        JSON.stringify(row[fieldName], replacer)
                    )
                    .join(';')
            ),
        ].join('\r\n');
    }

    downloadCSV(arg: { fileName: string; text: string }) {
        if (!this.setting.element.dynamicDownload) {
            this.setting.element.dynamicDownload = document.createElement('a');
        }
        const element = this.setting.element.dynamicDownload;
        const fileType = 'text/csv';
        element.setAttribute(
            'href',
            `data:${fileType};charset=UTF-8,%EF%BB%BF${encodeURIComponent(
                arg.text
            )}`
        );
        element.setAttribute('download', arg.fileName);
        const event = new MouseEvent('click');
        element.dispatchEvent(event);
    }

    generateUTCDate(date: Date, time: boolean): string {
        date.setDate(date.getDate() + 20);

        const dateToReturn =
            ('0' + date.getDate()).slice(-2) +
            '-' +
            ('0' + (date.getMonth() + 1)).slice(-2) +
            '-' +
            date.getFullYear();
        if (!time) {
            return dateToReturn;
        }
        return (
            dateToReturn +
            `T${date.getUTCHours()}:${date.getUTCMinutes()}:${date.getUTCSeconds()}`
        );
    }

    replacer(str: string, char: string, charSubstitution: string): string {
        return str.split(char).join(charSubstitution);
    }

    /**
     * Generate link to download Blob
     * @param blob
     */
    private generateBlobLink(blob: Blob, fileName: string): void {
        // IE doesn't allow using a blob object directly as link href
        // instead it is necessary to use msSaveOrOpenBlob
        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(blob);
            return;
        }

        // For other browsers:
        // Create a link pointing to the ObjectURL containing the blob.
        const data = window.URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.href = data;
        link.download = fileName;
        // this is necessary as link.click() does not work on the latest firefox
        link.dispatchEvent(
            new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
                view: window,
            })
        );

        setTimeout(function () {
            // For Firefox it is necessary to delay revoking the ObjectURL
            window.URL.revokeObjectURL(data);
            link.remove();
        }, 100);
    }
}
