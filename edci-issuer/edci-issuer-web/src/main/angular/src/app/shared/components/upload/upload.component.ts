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
import {
    UxButtonConfig,
    UxDynamicModalConfig,
    UxDynamicModalService,
} from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { DocumentUpload } from '@shared/models/upload.model';

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
    @Input() allowedFiles = ['xls', 'xlsx', 'xlsm'];
    @Input() documents: DocumentUpload[];
    @Input() isMultiple = false;
    @Input() required = false;
    @Input() touched = false;
    @Input() hasDate: boolean;
    @Input() size = '5MB';
    @Input() isXML: boolean = false;
    @Input() formatMessage = 'file-upload.accepted-formats';
    @Input() percentUploadedDone: number | null = null;
    @Input() needsConfirmation: boolean = true;
    @Input() isSecondary: boolean = false;
    @Output() newDocuments = new EventEmitter();
    @Output() deleted = new EventEmitter();

    error = false;
    errorMessage = '';
    deleteWarning = 'organizer.upload.confirm.delete';
    downloadUrl = null;
    extensionList = [];

    uploads: Array<DocumentUpload> = []; // List of documents to save
    deletedList: Array<DocumentUpload> = []; // List of deleted documents, to remove from the API

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

    hasFile(file) {
        return this.uploads.some(
            (item) =>
                item.fileName === file.name && item.file.size === file.size
        );
    }

    validateFile(file: File): boolean {
        if (!this.validateSize(file)) {
            this.error = true;
            this.errorMessage =
                this.translateService.instant('file-upload.no-more-than') +
                ' ' +
                this.size;
            return false;
        }

        if (!this.validateFileExtension(file)) {
            this.error = true;
            this.errorMessage = this.translateService.instant(
                'file-upload.wrongExtension'
            );
            return false;
        }
        return true;
    }

    pushFile(file: File) {
        if (this.uploads.length > 0) {
            this.removeFile(0);
        }

        this.uploads.push(this.createDocumentUpload(file));
        this.onNewDocuments();
        this.touched = true;
    }

    /**
     * Move file to trash
     */
    removeFile(index: number) {
        const items = this.uploads.splice(index, 1);
        const item = items[0];

        if (item.id) {
            item.action = 2;
            this.deletedList.push(item);
        }
        this.touched = true;
    }

    createDocumentUpload(file: File): DocumentUpload {
        const doc = {
            id: null,
            fileName: file.name,
            date: this.formatDate(new Date()),
            status: 'Pending approval',
            visible: true,
            action: 1,
            file: file,
        };
        return doc;
    }

    validateFileExtension(file): boolean {
        const extension = file.name.split('.').pop();

        if (this.allowedFiles.some((item) => item === extension)) {
            return true;
        }
        return false;
    }

    validateSize(file): boolean {
        if (file.size < 5242880) {
            return true;
        }
        return false;
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

    confirmDelete(index: number) {
        this.removeFile(index);
        this.validateRequired();
        this.onNewDocuments();
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
