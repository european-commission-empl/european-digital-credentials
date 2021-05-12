import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { UxLanguage, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants } from '@shared/constants';
import { DocumentUpload } from '@shared/models/upload.model';
import {
    CodeDTView,
    ContentDTView,
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

interface FormArrayLangs {
    [key: string]: { key: string, value: string }[];
}

@Component({
    selector: 'edci-html-templates-modal',
    templateUrl: './html-templates-modal.component.html',
    styleUrls: ['./html-templates-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class HTMLTemplatesModalComponent implements OnInit, OnDestroy {

    @Input() modalId: string = 'htmlTemplateModal';
    @Input() language: string;
    @Input() modalTitle: string;
    @Input() editHtmlTemplateOid?: number;
    @Output() onCloseModal: EventEmitter<{isEdit: boolean, oid: number, title: string}> = new EventEmitter();

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
    imageExtensions: string[] = ['jpeg', 'jpg', 'png'];
    test: FormArrayLangs;
    listOfLangs: string[] = [];
    listOfFormArray: FormArrayLangs = {};
    backgroundFile: any;
    isLogoNotAvailable: boolean;
    backgroundPreviewURL: string;
    extensionOfRequestBackground: string;
    isNewLogo: boolean;

    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        html: new FormControl(null, [
            Validators.required
        ]),
        file: new FormControl({}),
    });

    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    get html() {
        return this.formGroup.get('html') as FormControl;
    }

    get file() {
        return this.formGroup.get('file') as FormControl;
    }

    get labels() {
        return this.formGroup.get('labels') as FormArray;
    }

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
    ) {}

    ngOnInit() {
        if (this.editHtmlTemplateOid) {
            this.modalTitle = this.translateService.instant('credential-builder.html-templates-tab.edit');
            this.getHTMLTemplateDetails();
        } else {
            this.modalTitle = this.translateService.instant('credential-builder.html-templates-tab.new');
            this.language = this.language || this.translateService.currentLang;
            this.listOfLangs.push(this.language);
            this.addNewLanguageControl(this.language);
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
            this.uxService.markControlsTouched(this.formGroup);
            this.isLoading = false;
            this.uxService.openMessageBox('messageBoxFormError');
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
            this.applyAllChangesAtLabels(this.language);
            this.language = language.toLowerCase();
        }
    }

    languageAdded(language: string) {
        this.listOfLangs.push(language);
        this.addNewLanguageControl(language);
    }

    languageRemoved(language: string): void {
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

    createRow(lang: string) {
        this.listOfFormArray[lang].push({ key: '', value: '' });
    }

    deleteRow(lang: string, index: number) {
        Object.keys(this.listOfFormArray).forEach((langOfArr: string) => {
            this.listOfFormArray[langOfArr].splice(index, 1);
        });
    }

    onNewBackgroundFile(files: DocumentUpload[]) {
        if (files.length > 0 && files[0]) {
            this.isNewLogo = true;
            this.backgroundFile = files[0].file;
            const reader = new FileReader();
            reader.onloadend = () => {
                this.isLogoNotAvailable = false;
                this.backgroundPreviewURL = reader.result as string;
            };
            reader.readAsDataURL(files[0].file);
        }
    }

    deleteLogo() {
        this.backgroundPreviewURL = undefined;
    }

    private b64toFile(dataurl, filename) {
        let arr = dataurl.split(','),
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
                .subscribe(() => {
                    this.isLoading = false;
                    this.closeModal(true, htmlTemplate.oid, htmlTemplate.defaultTitle);
                }, err => {
                    this.isLoading = false;
                    this.closeModal(true);
                });
        } else {
            this.isLoading = false;
            this.closeModal(true, htmlTemplate.oid, htmlTemplate.defaultTitle);
        }
    }

    private addNewLanguageControl(language: string): void {
        this.addLabelsControls(language);
        this.createRow(language);
    }

    private getHTMLTemplateDetails(): void {
        this.api
            .getDiploma_3(
                this.editHtmlTemplateOid,
                this.translateService.currentLang
            ).pipe(takeUntil(this.destroy$))
            .subscribe(
                (htmlTemplate: ResourceDiplomaSpecView) => {
                    this.editHTMLTemplate = htmlTemplate;
                    this.availableLanguages = this.editHTMLTemplate.additionalInfo.languages;
                    this.language = this.editHTMLTemplate.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages = this.multilingualService.setUsedLanguages(
                        this.editHTMLTemplate.additionalInfo.languages,
                        this.defaultLanguage
                    );
                    this.setForm();
                    this.getLogoFromRequest();
                    this.getLabelsFromRequest();
                },
                (err) => this.closeModal(false)
            );
    }

    private getLogoFromRequest() {
        if (this.editHTMLTemplate.background && this.editHTMLTemplate.background.content) {
            this.isLogoNotAvailable = false;
            this.backgroundPreviewURL =
                'data:image/png;base64,' + this.editHTMLTemplate.background.content;
            this.backgroundFile = this.editHTMLTemplate.background.content;
            const contentTypeSplitted = this.editHTMLTemplate.background.contentType.uri.split('/');
            this.extensionOfRequestBackground = contentTypeSplitted[contentTypeSplitted.length - 1];
        }
    }

    private getLabelsFromRequest() {
        this.editHTMLTemplate.labels.forEach((element) => {
            element.contents.forEach(content => {
                if (!this.listOfFormArray[content.language]) {
                    this.listOfFormArray[content.language] = [];
                }
                this.listOfFormArray[content.language].push({
                    key: element.key,
                    value: content.content
                });
            });
        });
        if (!this.listOfFormArray[this.language]) {
            this.editHTMLTemplate.additionalInfo.languages.forEach(lang => {
                this.listOfFormArray[lang] = [];
                this.createRow(lang);
            });
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
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
            labels: this.extractLabels()
        };
    }

    private extractLabels(): LabelDTView[] {
        const labelsToReturn: LabelDTView[] = [];
        const keys = this.listOfFormArray[this.defaultLanguage].map((e) => e.key);
        const langs = Object.keys(this.listOfFormArray);
        keys.forEach((key) => {
            if (key) {
                const content: LabelDTView = {
                    key,
                    contents: []
                };
                langs.forEach((lang) => {
                    this.listOfFormArray[lang].forEach((e) => {
                        if (e.key === key) {
                            content.contents.push({
                                language: lang,
                                content: e.value,
                                format: ''
                            });
                        }
                    });
                });
                labelsToReturn.push(content);
            }
        });
        return labelsToReturn;
    }

    private setForm(): void {
        this.formGroup.patchValue({
            defaultTitle: _get(this.editHTMLTemplate, 'defaultTitle', null),
            html: _get(this.editHTMLTemplate, 'html', null),
        });
        this.isLoading = false;
    }

    private addLabelsControls(language: string): void {
        this.listOfFormArray[language] = [];
    }

    private applyAllChangesAtLabels(previousLang: string) {
        const keysToCheckRepeated = {};
        this.listOfFormArray[previousLang].forEach((e, i) => {
            if (keysToCheckRepeated[e.key]) {
                this.listOfFormArray[previousLang].splice(i, 1);
            } else {
                keysToCheckRepeated[e.key] = true;
            }
        });
        const keysOfPreviousLangLang: string[] = this.listOfFormArray[previousLang].map( el => el.key);
        Object.keys(this.listOfFormArray).forEach((langOfArr: string) => {
            const keysOfIterationLang: string[] = this.listOfFormArray[langOfArr].map( el => el.key);
            this.listOfFormArray[previousLang].forEach( (el, i) => {
                if (el.key === '') {
                    if (this.listOfFormArray[previousLang].length > 1) {
                        this.listOfFormArray[previousLang].splice(i, 1);
                    }
                } else {
                    if (!keysOfIterationLang.includes(el.key)) {
                        this.listOfFormArray[langOfArr].splice(i, 0, { key: el.key, value: '' } );
                    }
                }
            });
            this.listOfFormArray[langOfArr].forEach( (el, i) => {
                if (el.key === '') {
                    if (this.listOfFormArray[langOfArr].length > 1) {
                        this.listOfFormArray[langOfArr].splice(i, 1);
                    }
                }
                if (!keysOfPreviousLangLang.includes(el.key)) {
                    this.listOfFormArray[langOfArr].splice(i, 1);
                }
            });
        });
    }

    private isLabelArrayValid(): boolean {
        let isValid = true;
        const numberOfValidRowsCurrentLang = this.numberValidRows(this.language);
        Object.keys(this.listOfFormArray).forEach((keyLang) => {
            if (isValid) {
                const keysToCheckRepeated = {};
                this.listOfFormArray[keyLang].forEach(row => {
                    if (numberOfValidRowsCurrentLang !== this.numberValidRows(keyLang)) {
                        isValid = false;
                    } else if (row.key && !row.value) {
                        isValid = false;
                    } else {
                        if (keysToCheckRepeated[row.key]) {
                            isValid = false;
                        } else if (row.key) {
                            keysToCheckRepeated[row.key] = true;
                        }
                    }
                });
            }
        });
        return isValid;
    }

    private numberValidRows(lang: string): number {
        return this.listOfFormArray[lang].filter(l => l.key !== '').length;
    }

    private isFormInvalid(): boolean {
        return (
            this.formGroup.invalid || !this.isLabelArrayValid()
        );
    }
}
