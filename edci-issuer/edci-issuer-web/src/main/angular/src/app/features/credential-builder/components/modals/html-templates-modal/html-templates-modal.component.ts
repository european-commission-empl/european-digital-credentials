import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
    ViewChild,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage, UxAppShellService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants } from '@shared/constants';
import {
    CodeDTView,
    DiplomaSpecView,
    LabelDTView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
    ResourceDiplomaSpecView,
    V1Service,
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';

interface FormArrayLangs {
    [key: string]: { key: string; value: string }[];
}

@Component({
    selector: 'edci-html-templates-modal',
    templateUrl: './html-templates-modal.component.html',
    styleUrls: ['./html-templates-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class HTMLTemplatesModalComponent implements OnInit, OnDestroy {
    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
    messageBoxFormError: EuiMessageBoxComponent;
    @Input() modalId: string = 'htmlTemplateModal';
    @Input() language: string;
    @Input() modalTitle: string;
    @Input() editHtmlTemplateOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        title: string;
    }> = new EventEmitter();

    defaultLanguage: string;
    editHTMLTemplate: ResourceDiplomaSpecView;
    assessmentLanguage: CodeDTView[] = [];
    selectedLanguages: UxLanguage[] = [];
    availableLanguages: string[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    htmlTemplateBody: DiplomaSpecView;
    isSaveDisabled: boolean = false;
    isLoading: boolean = true;
    additionalNoteView: NoteDTView[];
    additionalNoteSpecView: NoteDTView[];
    selectedAssessedBy: OrganizationSpecLiteView;
    selectedSubAssessments: PagedResourcesAssessmentSpecLiteView;
    subAssessmentsOidList: number[] = [];
    indexToNextTab: number;
    imageExtensions: string[] = ['.jpeg', '.jpg', '.png'];
    listOfLangs: string[] = [];
    backgroundFile: any;
    isLogoNotAvailable: boolean;
    backgroundPreviewURL: string;
    extensionOfRequestBackground: string;
    isNewLogo: boolean;
    selectFormatTemplate: string[] = ['HTML', 'THYMELEAF'];
    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        html: new FormControl('', [Validators.required]),
        format: new FormControl('HTML', [Validators.required]),
        file: new FormControl({}),
    });
    modalTitleBreadcrumb: string[];
    removedLanguage: string;
    addedLanguage: string;
    isLabelArrayValid: boolean;
    customLabels: LabelDTView[];
    customLabelsEdit: LabelDTView[];

    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    get html() {
        return this.formGroup.get('html') as FormControl;
    }

    get format() {
        return this.formGroup.get('format') as FormControl;
    }

    get file() {
        return this.formGroup.get('file') as FormControl;
    }

    get labels() {
        return this.formGroup.get('labels') as FormArray;
    }

    constructor(
        public uxService: UxAppShellService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService
    ) {}

    ngOnInit() {
        this.format.setValue(this.selectFormatTemplate[0], { onlySelf: true });
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editHtmlTemplateOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.html-templates-tab.edit'
            );
            this.getHTMLTemplateDetails();
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.html-templates-tab.new'
            );
            this.language = this.language || this.translateService.currentLang;
            this.listOfLangs.push(this.language);
            this.defaultLanguage = this.language;
            this.selectedLanguages.push({
                code: this.language,
                label: this.language,
            });
            this.isLoading = false;
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        if (this.isFormInvalid()) {
            this.formGroup.markAsUntouched();
            this.isLoading = false;
            this.messageBoxFormError.openMessageBox();
        } else {
            this.isSaveDisabled = true;
            this.setHTMLTemplateBody();
            if (this.editHTMLTemplate) {
                this.updateHTMLTemplate();
            } else {
                this.createHTMLTemplate();
            }
        }
    }

    closeModal(isEdit: boolean, oid?: number, title?: string): void {
        this.onCloseModal.emit({ isEdit, oid, title });
        this.formGroup.reset();
        this.isSaveDisabled = false;
    }

    languageTabSelected(language: string) {
        if (this.language !== language) {
            this.language = language.toLowerCase();
        }
    }

    languageAdded(language: string) {
        this.addedLanguage = language;
        this.listOfLangs.push(language);
    }

    languageRemoved(language: string): void {
        this.removedLanguage = language;
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
    }

    assessmentLanguageSelectionChange(assessmentLanguage: CodeDTView[]): void {
        this.assessmentLanguage = assessmentLanguage;
    }

    onSubAssessmentSelectionChange(oids: number[]): void {
        this.subAssessmentsOidList = oids;
    }

    onAssessedBySelectionChange(oid: number): void {
        this.formGroup.patchValue({ assessedBy: oid });
    }

    onNewBackgroundFile(files: File[] | Blob[]) {
        if (files.length > 0) {
            this.isNewLogo = true;
            this.backgroundFile = files[0];
            const reader = new FileReader();
            reader.onloadend = () => {
                this.isLogoNotAvailable = false;
                this.backgroundPreviewURL = reader.result as string;
            };
            reader.readAsDataURL(files[0]);
        }
    }

    deleteBackground() {
        this.backgroundPreviewURL = null;
        this.backgroundFile = null;
    }

    labelValueChange(value): void {
        this.customLabels = value;
    }

    labelValidityChange(value: boolean): void {
        this.isLabelArrayValid = value;
    }

    private b64toFile(dataURL, filename) {
        let arr = dataURL.split(','),
            mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]),
            n = bstr.length,
            u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new File([u8arr], filename, { type: mime });
    }

    private sendBackground(htmlTemplate: ResourceDiplomaSpecView) {
        if (this.backgroundFile) {
            if (!this.isNewLogo) {
                this.backgroundFile = this.b64toFile(
                    `data:image/${this.extensionOfRequestBackground};base64,${this.backgroundFile}`,
                    `background-img.${this.extensionOfRequestBackground}`
                );
            }
            this.api
                .addBackground(htmlTemplate.oid, this.backgroundFile)
                .pipe(takeUntil(this.destroy$))
                .subscribe(
                    () => {
                        this.isLoading = false;
                        this.closeModal(
                            true,
                            htmlTemplate.oid,
                            htmlTemplate.defaultTitle
                        );
                    },
                    (err) => {
                        this.isLoading = false;
                        this.closeModal(true);
                    }
                );
        } else {
            this.isLoading = false;
            this.closeModal(true, htmlTemplate.oid, htmlTemplate.defaultTitle);
        }
    }

    private getHTMLTemplateDetails(): void {
        this.api
            .getDiplomaSpec(
                this.editHtmlTemplateOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (htmlTemplate: ResourceDiplomaSpecView) => {
                    this.editHTMLTemplate = htmlTemplate;
                    this.availableLanguages =
                        this.editHTMLTemplate.additionalInfo.languages;
                    this.language = this.editHTMLTemplate.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editHTMLTemplate.additionalInfo.languages,
                            this.defaultLanguage
                        );
                    this.setForm();
                    this.getLogoFromRequest();
                    this.customLabelsEdit = this.editHTMLTemplate.labels;
                },
                (err) => this.closeModal(false)
            );
    }

    private getLogoFromRequest() {
        if (
            this.editHTMLTemplate.background &&
            this.editHTMLTemplate.background.content
        ) {
            this.isLogoNotAvailable = false;
            this.backgroundPreviewURL =
                'data:image/png;base64,' +
                this.editHTMLTemplate.background.content;
            this.backgroundFile = this.editHTMLTemplate.background.content;
            const contentTypeSplitted =
                this.editHTMLTemplate.background.contentType.uri.split('/');
            this.extensionOfRequestBackground =
                contentTypeSplitted[contentTypeSplitted.length - 1];
        }
    }

    private updateHTMLTemplate(): void {
        this.htmlTemplateBody.oid = this.editHTMLTemplate.oid;
        this.api
            .updateDiploma(
                this.htmlTemplateBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (htmlTemplate: ResourceDiplomaSpecView) => {
                    this.isLoading = false;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.sendBackground(htmlTemplate);
                },
                (err) => {
                    this.isLoading = false;
                    this.closeModal(false);
                }
            );
    }

    private createHTMLTemplate(): void {
        this.api
            .createDiploma(
                this.htmlTemplateBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (htmlTemplate: ResourceDiplomaSpecView) => {
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.sendBackground(htmlTemplate);
                },
                (err) => {
                    this.isLoading = false;
                    this.closeModal(false);
                }
            );
    }

    private setHTMLTemplateBody(): void {
        this.htmlTemplateBody = {
            defaultTitle: this.defaultTitle.value,
            defaultLanguage: this.defaultLanguage,
            html: this.html.value,
            format: this.format.value,
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
            labels: this.customLabels,
        };
    }

    private setForm(): void {
        this.formGroup.patchValue({
            defaultTitle: _get(this.editHTMLTemplate, 'defaultTitle', null),
            html: _get(this.editHTMLTemplate, 'html', null),
            format: _get(this.editHTMLTemplate, 'format', 'HTML'),
        });
        this.isLoading = false;
    }

    private isFormInvalid(): boolean {
        return this.formGroup.invalid || !this.isLabelArrayValid;
    }
}
