import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants, TIME_FORMAT } from '@shared/constants';
import {
    CodeDTView,
    DiplomaSpecView,
    LabelDTView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesAssessmentSpecLiteView,
    ResourceDiplomaSpecView,
    V1Service
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject, of, pipe } from 'rxjs';
import { takeUntil, switchMap, filter } from 'rxjs/operators';

interface FormArrayLangs {
    [key: string]: { key: string; value: string }[];
}

@Component({
    selector: 'edci-html-templates-form',
    templateUrl: './html-templates-form.component.html',
    styleUrls: ['./html-templates-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [
        { provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT },
    ],
})
export class HTMLTemplatesFormComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;
    @Input() modalId = 'htmlTemplateModal';
    @Input() language: string;
    @Input() modalTitle: string;
    @Input() editHtmlTemplateOid?: number;
    @Input() isModal: boolean;

    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: ResourceDiplomaSpecView = null;

    defaultLanguage: string;
    editHTMLTemplate: ResourceDiplomaSpecView;
    assessmentLanguage: CodeDTView[] = [];
    selectedLanguages: UxLanguage[] = [];
    availableLanguages: string[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    htmlTemplateBody: DiplomaSpecView;
    isLoading = true;
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
    isNewLogo = false;
    selectFormatTemplate: string[] = ['HTML', 'THYMELEAF'];
    formGroup = new FormGroup({
        label: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_LABELS),
            Validators.required,
            noSpaceValidator,
        ]),
        html: new FormControl('', [Validators.required]),
        format: new FormControl('HTML', [Validators.required]),
        file: new FormControl({}),
    });
    removedLanguage: string;
    addedLanguage: string;
    isLabelArrayValid: boolean;
    customLabels: LabelDTView[];
    customLabelsEdit: LabelDTView[];

    get label() {
        return this.formGroup.get('label') as FormControl;
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
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadBreadcrumb();
            });
    }

    ngOnInit() {
        this.format.setValue(this.selectFormatTemplate[0], { onlySelf: true });
        if (this.isModal) {
            this.eventSave.pipe(takeUntil(this.destroy$)).subscribe((res) => {
                this.onSave();
            });
        } else {
            this.route.params.subscribe((params) => {
                if (params['id'] !== null || params['id'] !== undefined) {
                    this.editHtmlTemplateOid = params['id'];
                }
            });
        }
        this.loadBreadcrumb();
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
            this.setHTMLTemplateBody();
            if (this.editHTMLTemplate) {
                this.updateHTMLTemplate();
            } else {
                this.createHTMLTemplate();
            }
        }
    }

    closeForm(): void {
        this.credentialBuilderService.setOcbTabSelected(7);
        this.router.navigateByUrl('credential-builder');
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
                (err) => this.closeForm()
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
        this.isLoading = true;
        this.api
            .updateDiploma(
                this.htmlTemplateBody,
                this.translateService.currentLang
            )
            .pipe(
                takeUntil(this.destroy$),
                this.handleNewOrUpdatedTemplate.bind(this)
            )
            .subscribe()
            .add(() => {
                this.isLoading = false;
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.edit'),
                    detail: this.translateService.instant( 'credential-builder.operationSuccessful'),
                });
                this.handleSaveNavigation();
            });
    }

    private createHTMLTemplate(): void {
        this.isLoading = true;
        this.api
            .createDiploma(
                this.htmlTemplateBody,
                this.translateService.currentLang
            )
            .pipe(
                takeUntil(this.destroy$),
                this.handleNewOrUpdatedTemplate.bind(this)
            )
            .subscribe()
            .add(() => {
                this.isLoading = false;
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.create'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });
                this.handleSaveNavigation();
            });
    }

    private handleNewOrUpdatedTemplate(source: Observable<ResourceDiplomaSpecView>): Observable<any>{
        return source.pipe(
            takeUntil(this.destroy$),
            switchMap(htmlTemplate => {
                this.newEntity = htmlTemplate;
                if (!this.isNewLogo && this.backgroundFile != null){
                    this.backgroundFile = this.b64toFile(
                        `data:image/${this.extensionOfRequestBackground};base64,${this.backgroundFile}`,
                        `background-img.${this.extensionOfRequestBackground}`
                    );
                }
                return of(htmlTemplate);
            }),
            filter(() => this.backgroundFile != null),
            switchMap(htmlTemplate =>this.api.addBackground(htmlTemplate.oid,this.backgroundFile))
        );
    }

    private b64toFile(dataURL, filename) {
        const arr = dataURL.split(','),
            mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]);
        let n = bstr.length;
        const u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new File([u8arr], filename, { type: mime });
    }

    private handleSaveNavigation() {
        if (this.isModal) {
        // If it is a modal, just emit event when saving is done
            this.onSaveEvent.emit(this.newEntity);
        } else {
            this.credentialBuilderService.setOcbTabSelected(7);
            this.router.navigateByUrl('credential-builder');
        }
    }

    private setHTMLTemplateBody(): void {
        this.htmlTemplateBody = {
            label: this.label.value,
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
            label: _get(this.editHTMLTemplate, 'label', null),
            html: _get(this.editHTMLTemplate, 'html', null),
            format: _get(this.editHTMLTemplate, 'format', 'HTML'),
        });
        this.isLoading = false;
    }

    private isFormInvalid(): boolean {
        return this.formGroup.invalid || !this.isLabelArrayValid;
    }

    private loadBreadcrumb() {
        this.parts = [
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.digitallySealedCredentials'
                ),
                url: '/home',
            }),
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.credentialBuilder'
                ),
                url: '/credential-builder',
            }),
        ];
    }
}

