import {
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { environment } from '@environments/environment';
import {
    UxButtonConfig,
    UxDynamicModalConfig,
    UxDynamicModalService,
} from '@eui/core';
import { TranslateService } from '@ngx-translate/core';

@Component({
    selector: 'edci-upload',
    templateUrl: 'upload.component.html',
    styleUrls: ['./upload.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class UploadComponent implements OnInit, OnChanges {
    @ViewChild('inputFile') inputFile: ElementRef;
    @Input() label: string = 'Upload';
    @Input() isIconOnly: boolean = false;
    @Input() wrapperClass: string = '';
    @Input() styleClass: string = '';
    @Input() allowedFiles = ['.xls', '.xlsx', '.xlsm'];
    @Input() documents: File[];
    @Input() isMultiple = false;
    @Input() required = false;
    @Input() touched = false;
    @Input() hasDate: boolean;
    @Input() isXML: boolean = false;
    @Input() formatMessage = 'file-upload.accepted-formats';
    @Input() percentUploadedDone: number | null = null;
    @Input() needsConfirmation: boolean = true;
    @Input() isSecondary: boolean = false;
    @Output() newDocuments: EventEmitter<File[] | Blob[]> = new EventEmitter();
    @Output() deleted = new EventEmitter();

    error = false;
    errorMessage = '';
    deleteWarning = 'organizer.upload.confirm.delete';
    downloadUrl = null;
    extensionList = [];
    maxUploadSizeMB = environment.maxUploadSizeMB;

    uploads: Array<File | Blob> = []; // List of documents to save
    deletedList: Array<File> = []; // List of deleted documents, to remove from the API

    constructor(
        private translateService: TranslateService,
        private uxDynamicModalService: UxDynamicModalService
    ) {}

    ngOnInit() {
        this.allowedFiles.map((extension) => {
            this.allowedFiles.push(extension.toLocaleUpperCase());
            this.extensionList.push(' ' + extension.toLocaleUpperCase());
        });
    }

    ngOnChanges(changes) {
        if (changes.documents) {
            this.uploads = this.documents || [];
            this.deletedList = [];
        }
        if (changes.touched && this.touched) {
            this.validateRequired();
        }
    }

    public openDialog() {
        this.inputFile.nativeElement.value = null;
        return this.inputFile.nativeElement.click();
    }

    readFile(event: FileList) {
        if (event.length === 0) {
            return false;
        }

        this.resetErrors();
        const file = event[0];

        if (this.validateFile(file)) {
            this.pushFile(file);
            this.inputFile.nativeElement.value = '';
        }
    }

    readMultiFile(event: FileList): void {
        if (event.length === 0) {
            return;
        }
        let totalSize: number = 0;
        this.uploads = [];
        this.resetErrors();
        for (let index = 0; index < event.length; index++) {
            if (this.validateFile(event[index])) {
                this.uploads.push(event[index]);
                totalSize = totalSize + event[index].size;
            } else {
                break;
            }
        }
        if (this.uploads.length > 0 && this.isValidTotalSize(totalSize)) {
            this.onNewDocuments();
        }
    }

    validateFile(file: File): boolean {
        return this.isValidateFileExtension(file) && this.isValidSize(file);
    }

    pushFile(file: File) {
        this.uploads = [];
        this.uploads.push(file);
        this.onNewDocuments();
        this.touched = true;
    }

    isValidateFileExtension(file: File): boolean {
        let isValid: boolean = true;
        const extension = `.${file.name.split('.').pop()}`;
        // If no file extension match the valid extensions returns error
        if (!this.allowedFiles.some((item) => item === extension)) {
            this.error = true;
            this.errorMessage = this.translateService.instant(
                'file-upload.wrongExtension'
            );
            isValid = false;
        }
        return isValid;
    }

    isValidSize(file: File): boolean {
        let isValid: boolean = true;
        // maxUploadSizeMB in MB to Bytes
        if (file.size > environment.maxUploadSizeMB * 1024 * 1024) {
            this.error = true;
            this.errorMessage = `${this.translateService.instant(
                'file-upload.no-more-than'
            )} ${environment.maxUploadSizeMB} MB`;
            isValid = false;
        }
        return isValid;
    }

    isValidTotalSize(uploadSize: number): boolean {
        let isValid: boolean = true;
        // maxUploadSizeMB in MB to Bytes
        if (uploadSize > environment.maxUploadSizeMB * 1024 * 1024) {
            this.error = true;
            this.errorMessage = `${this.translateService.instant(
                'file-upload.no-more-than'
            )} ${environment.maxUploadSizeMB} MB`;

            isValid = false;
        }
        return isValid;
    }

    validateRequired() {
        if (this.required && this.touched && this.uploads.length === 0) {
            this.error = true;
            this.errorMessage = 'organizer.upload.validation.document.required';
        }
    }

    resetErrors() {
        this.error = false;
        this.errorMessage = '';
    }

    onUploadFile(): void {
        if (this.needsConfirmation) {
            this.openDynamicModal();
        } else {
            this.openDialog();
        }
    }

    onNewDocuments() {
        this.newDocuments.emit(this.uploads);
        this.deleted.emit(this.deletedList);
    }

    openDynamicModal(): void {
        const config = new UxDynamicModalConfig({
            id: 'concentModal',
            content: `<p>${this.translateService.instant(
                'common.upload-concent-message'
            )}<p>`,
            titleLabel: this.isXML
                ? this.translateService.instant('common.upload-xml')
                : this.translateService.instant('common.upload-xls'),
            isSizeSmall: true,
            customFooterContent: {
                right: {
                    buttons: [
                        new UxButtonConfig({
                            label: this.translateService.instant(
                                'common.cancel'
                            ),
                            typeClass: 'secondary',
                            styleClass: 'mr-3',
                            onClick: (portalHostRef, portalRef) => {
                                this.uxDynamicModalService.closeModal(
                                    portalHostRef,
                                    portalRef
                                );
                            },
                        }),
                        new UxButtonConfig({
                            label: this.translateService.instant(
                                'common.upload'
                            ),
                            typeClass: 'primary',
                            onClick: (portalHostRef, portalRef) => {
                                this.openDialog();
                                this.uxDynamicModalService.closeModal(
                                    portalHostRef,
                                    portalRef
                                );
                            },
                        }),
                    ],
                },
            },
        });
        this.uxDynamicModalService.openModal(config);
    }

    private formatDate(date: Date): string {
        let day = date.getDate().toString();
        if (day.length === 1) {
            day = '0' + day;
        }
        let month = (date.getMonth() + 1).toString();
        if (month.length === 1) {
            month = '0' + month;
        }
        const year = date.getFullYear();

        return day + '/' + month + '/' + year;
    }
}
