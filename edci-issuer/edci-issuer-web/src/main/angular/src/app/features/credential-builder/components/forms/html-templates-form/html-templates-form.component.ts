import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from '@environments/environment';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { Constants, TIME_FORMAT } from '@shared/constants';
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
import { Observable, Subject } from 'rxjs';
import { takeUntil, take, switchMap } from 'rxjs/operators';

@Component({
    selector: 'edci-html-templates-form',
    templateUrl: './html-templates-form.component.html',
    styleUrls: ['./html-templates-form.component.scss'],
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class HTMLTemplatesFormComponent implements OnInit, OnDestroy {

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
    @Input() modalData: any;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: ResourceDiplomaSpecView = null;
    learnLink = environment.htmlLearnLink;
    defaultLanguage: string;
    editHTMLTemplate: ResourceDiplomaSpecView;
    assessmentLanguage: CodeDTView[] = [];
    selectedLanguages: UxLanguage[] = [];
    languages: string[] = [];
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
    isNewLogo: boolean;
    selectFormatTemplate: string[] = ['HTML', 'THYMELEAF'];
    formGroup = new FormGroup({
        label: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_LABELS),
            Validators.required,
            noSpaceValidator,
        ]),
        html: new FormControl('', [Validators.required, noSpaceValidator]),
        format: new FormControl('HTML', [Validators.required]),
        file: new FormControl({}),
    });
    removedLanguage: string;
    addedLanguage: string;
    isLabelArrayValid: boolean;
    customLabels: LabelDTView[];
    customLabelsEdit: LabelDTView[];
    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private route: ActivatedRoute,
        private router: Router,
        private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.loadBreadcrumb();
            });
    }

    ngOnInit() {
        this.route.data.pipe(takeUntil(this.destroy$)).subscribe((data) => {
            this.pageLoadingSpinnerService.stopPageLoader();
            this.isLoading = false;

            if (this.isModal) {
                this.eventSave.pipe(takeUntil(this.destroy$)).subscribe(() => {
                    this.onSave();
                });
                if (this.modalData) {
                    this.setEditTemplateData(this.modalData);
                } else {
                    this.setNewTemplateData();
                }
                return;
            }

            if (data.htmlTemplateDetails) {
                this.setEditTemplateData(data.htmlTemplateDetails);
            } else {
                this.setNewTemplateData();
            }
        });

        this.format.setValue(this.selectFormatTemplate[0], { onlySelf: true });
        this.loadBreadcrumb();
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
                if (this.backgroundFile) {
                    this.updateTemplateAndBackground();
                } else {
                    this.updateHTMLTemplate();
                }
            } else {
                if (this.backgroundFile) {
                    this.createHTMLTemplateWithBackground();
                } else {
                    this.createHTMLTemplate();
                }
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

    setEditTemplateData(data) {
        this.editHTMLTemplate = data;
        this.editHtmlTemplateOid = data.oid;
        this.languages = this.editHTMLTemplate.additionalInfo.languages;
        this.language = this.editHTMLTemplate.defaultLanguage;
        this.defaultLanguage = this.language;
        this.selectedLanguages = this.multilingualService.setUsedLanguages(
            this.editHTMLTemplate.additionalInfo.languages,
            this.defaultLanguage
        );
        this.setForm();
        this.getLogoFromRequest();
        this.customLabelsEdit = this.editHTMLTemplate.labels;

        this.modalTitle = this.translateService.instant(
            'credential-builder.html-templates-tab.edit'
        );
    }

    setNewTemplateData() {
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
    }

    updateTemplateAndBackground() {
        this.htmlTemplateBody.oid = this.editHTMLTemplate.oid;
        if (!this.isNewLogo) {
            this.backgroundFile = this.b64toFile(
                `data:image/${this.extensionOfRequestBackground};base64,${this.backgroundFile}`,
                `background-img.${this.extensionOfRequestBackground}`
            );
        }
        this.isLoading = true;

        const template$ = this.api.updateDiploma(this.htmlTemplateBody, this.translateService.currentLang).pipe(take(1));
        const background$ = template$.pipe(
            switchMap(templateDetails => this.api.addBackground(templateDetails.oid, this.backgroundFile).pipe(take(1)))
        );

        background$.subscribe({
            next: (data) => {
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.edit'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });

                if (this.isModal) {
                    this.onSaveEvent.emit(data);
                } else {
                    this.credentialBuilderService.setOcbTabSelected(7);
                    this.router.navigateByUrl('credential-builder');
                }
                this.isLoading = false;
            },
            error: () => {
                if (this.isModal) {
                    this.onSaveEvent.emit(null);
                } else {
                    this.credentialBuilderService.setOcbTabSelected(7);
                    this.router.navigateByUrl('credential-builder');
                }
                this.isLoading = false;
            }
        });

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
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (htmlTemplate: ResourceDiplomaSpecView) => {
                    this.newEntity = htmlTemplate;
                    this.handleSaveNavigation();
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                },
                error: () => {
                    if (this.isModal) {
                        this.onSaveEvent.emit(null);
                    } else {
                        this.closeForm();
                    }
                    this.isLoading = false;
                }
            });
    }

    private handleSaveNavigation() {
        if (this.isModal) {
            // If it is a modal, just emit event when saving is done
            this.onSaveEvent.emit(this.newEntity);
        } else {
            if (this.backgroundFile) {
                return;
            } else {
                this.credentialBuilderService.setOcbTabSelected(7);
                this.router.navigateByUrl('credential-builder');
            }

        }
    }

    private createHTMLTemplate(): void {
        this.isLoading = true;
        this.api
            .createDiploma(
                this.htmlTemplateBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (htmlTemplate: ResourceDiplomaSpecView) => {
                    this.newEntity = htmlTemplate;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.handleSaveNavigation();
                    this.isLoading = false;
                },
                error: () => {
                    if (this.isModal) {
                        this.onSaveEvent.emit(null);
                    } else {
                        this.closeForm();
                    }
                    this.isLoading = false;
                }
            });
    }

    private createHTMLTemplateWithBackground(): void {
        this.isLoading = true;

        if (!this.isNewLogo) {
            this.backgroundFile = this.b64toFile(
                `data:image/${this.extensionOfRequestBackground};base64,${this.backgroundFile}`,
                `background-img.${this.extensionOfRequestBackground}`
            );
        }

        const template$ = this.api
            .createDiploma(
                this.htmlTemplateBody,
                this.translateService.currentLang
            );

        const background$ = template$.pipe(
            switchMap(templateDetails => this.api.addBackground(templateDetails.oid, this.backgroundFile).pipe(take(1)))
        );

        background$.subscribe({
            next: (data) => {
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.edit'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });

                if (this.isModal) {
                    this.onSaveEvent.emit(data);
                } else {
                    this.credentialBuilderService.setOcbTabSelected(7);
                    this.router.navigateByUrl('credential-builder');
                }
                this.isLoading = false;
            },
            error: () => {
                if (this.isModal) {
                    this.onSaveEvent.emit(null);
                } else {
                    this.credentialBuilderService.setOcbTabSelected(7);
                    this.router.navigateByUrl('credential-builder');
                }
                this.isLoading = false;
            }
        });

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
