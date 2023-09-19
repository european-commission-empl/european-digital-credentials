import { Component, OnInit, Input, Output, OnDestroy, ViewChild, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { UxLanguage, UxLink } from '@eui/core';

import { Subject, Observable } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';
import {
    FormArray,
    FormControl,
    FormGroup,
    Validators,
} from '@angular/forms';
import { Constants, Entities, TIME_FORMAT } from '@shared/constants';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { CredentialBuilderService } from '@services/credential-builder.service';
import {
    AccreditationSpecView,
    CodeDTView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesAccreditationSpecLiteView,
    ResourceAccreditationSpecView,
    V1Service,
} from '@shared/swagger';
import { ModalsService } from '@services/modals.service';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { dateValidator } from '@shared/validators/date-validator';
import { DateFormatService } from '@services/date-format.service';
import { MultilingualService } from '@services/multilingual.service';
import { NotificationService } from '@services/error.service';
import { get as _get } from 'lodash';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { FormsService } from '@features/credential-builder/services/forms.service';
import { dateRangeValidator } from '@shared/validators/date-range-validator';

@Component({
    selector: 'edci-accreditation-form',
    templateUrl: './accreditation-form.component.html',
    styleUrls: ['./accreditation-form.component.scss'],
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class AccreditationFormComponent implements OnInit, OnDestroy {

    get label() {
        return this.formGroup.get('label') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get titleControl() {
        return this.title?.controls[this.language] as FormControl;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
    }

    get descriptionControl() {
        return this.description?.controls[this.language] as FormControl;
    }

    get accreditationType() {
        return this.formGroup.get('accreditationType') as FormControl;
    }

    get accreditationDecision() {
        return this.formGroup.get('accreditationDecision') as FormControl;
    }

    get accreditingAgent() {
        return this.formGroup.get('accreditingAgent') as FormControl;
    }

    get accreditationStartDate() {
        return this.formGroup.get('accreditationStartDate') as FormControl;
    }

    get accreditationExpiryDate() {
        return this.formGroup.get('accreditationExpiryDate') as FormControl;
    }

    get accreditationReviewDate() {
        return this.formGroup.get('accreditationReviewDate') as FormControl;
    }

    get accreditationReport() {
        return this.formGroup.get('accreditationReport') as FormControl;
    }

    get homePage() {
        return this.formGroup.get('homePage') as FormControl;
    }

    get relatedDocuments() {
        return this.formGroup.get('relatedDocuments') as FormArray;
    }

    get accreditationJurisdiction() {
        return this.formGroup.get('accreditationJurisdiction') as FormArray;
    }

    get accreditationValidEqf() {
        return this.formGroup.get('accreditationValidEqf') as FormArray;
    }

    get accreditationValidThematicArea() {
        return this.formGroup.get('accreditationValidThematicArea') as FormArray;
    }

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;

    pageTitle: string;
    isLoading = false;

    /* lang */
    language: string;
    defaultLanguage: string;
    isPrimaryLanguage = true;
    removedLanguage: string;
    addedLanguage: string;
    selectedLanguages: UxLanguage[] = [];
    languages: string[] = [];
    accreditationBody: AccreditationSpecView;

    /* breadcrumbs */
    breadcrumbs: UxLink[] = [];

    @Input() modalId = 'credentialModal';
    @Input() modalTitle: string;
    @Input() editCredentialOid?: number;
    @Input() isModal: boolean;
    @Input() modalData: any;

    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: AccreditationSpecView = null;
    selectedAccreditingAgent: OrganizationSpecLiteView;
    entityWillBeOpened: Entities | string;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};

    /* form */
    formGroup = new FormGroup({
        label: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_LABELS),
            noSpaceValidator,
        ]),
        title: new FormGroup({}),
        description: new FormGroup({}),
        accreditingAgent: new FormControl(null, [Validators.required]),
        accreditationStartDate: new FormControl(null, [dateValidator]),
        accreditationExpiryDate: new FormControl(null, [dateValidator]),
        accreditationReviewDate: new FormControl(null, [dateValidator]),
        accreditationType: new FormControl(null, [Validators.required]),
        accreditationDecision: new FormControl(null),
        accreditationJurisdiction: new FormControl(null),
        accreditationReport: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
        ]),
        accreditationValidEqf: new FormControl(null),
        accreditationValidThematicArea: new FormControl(null),
        homePage: new FormControl(null, [Validators.pattern(Constants.URL_REGULAR_EXPRESSION), Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
        relatedDocuments: new FormArray([]),
    },
        [dateRangeValidator('accreditationStartDate', 'accreditationExpiryDate', this.dateFormatService)]
    );

    additionalNoteSpecView: NoteDTView[];
    additionalNoteSpecification: NoteDTView[];
    isAdditionalNoteSpecificationValid: boolean;
    private accreditationOid: number;
    private accreditationData: ResourceAccreditationSpecView;

    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private route: ActivatedRoute,
        private translateService: TranslateService,
        public credentialBuilderService: CredentialBuilderService,
        private router: Router,
        private modalsService: ModalsService,
        private formsService: FormsService,
        private api: V1Service,
        private dateFormatService: DateFormatService,
        private multilingualService: MultilingualService,
        private notificationService: NotificationService,
        private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) { }

    ngOnInit(): void {
        this.route.data.pipe(takeUntil(this.destroy$)).subscribe((data) => {
            this.isLoading = false;
            this.pageLoadingSpinnerService.stopPageLoader();
            if (this.isModal) {
                this.eventSave.pipe(takeUntil(this.destroy$)).subscribe(() => {
                    this.onSave();
                });

                if (this.modalData?.accreditationDetails) {
                    this.setEditAccreditation(this.modalData.accreditationDetails);

                    if (this.modalData?.accreditationAccreditingAgent) {
                        this.selectedAccreditingAgent = this.modalData?.accreditationAccreditingAgent;
                        this.accreditingAgent.patchValue(this.modalData?.accreditationAccreditingAgent);
                    }
                } else {
                    this.setNewAccreditation();
                }

            } else if (data.accreditationDetails) {
                this.setEditAccreditation(data.accreditationDetails);

                if (data?.accreditationAccreditingAgent) {
                    this.selectedAccreditingAgent = data?.accreditationAccreditingAgent;
                    this.accreditingAgent.patchValue(data?.accreditationAccreditingAgent);
                }
            } else {
                this.setNewAccreditation();
            }
        });

        this.getLangChange();
        this.loadBreadcrumbs();

    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave() {

        /* checkValidDate */

        if (this.formGroup.invalid) {
            this.formGroup.markAllAsTouched();
            return;
        }

        const payload: AccreditationSpecView = this.setAssessmentPayload();

        if (this.accreditationData) {
            this.updateAccreditation(payload);
        } else {
            this.createAccreditation(payload);
        }

    }

    closeForm(): void {

        if (this.isModal) {
            // If it is a modal, just emit event when saving is done
            this.onSaveEvent.emit(this.newEntity);
        } else {
            this.credentialBuilderService.setOcbTabSelected(8);
            this.router.navigateByUrl('credential-builder');
        }
    }
    /* language */
    languageTabSelected(language: string) {
        if (this.language !== language) {
            this.language = language.toLowerCase();
        }
        this.isPrimaryLanguage = this.defaultLanguage === language;
    }

    languageAdded(language: string) {
        this.addedLanguage = language;
        /* add controls for langs */
        this.addTitleControls(language);
    }

    languageRemoved(language: string): void {
        this.removedLanguage = language;
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
        /* remove controls for langs */
        this.title.removeControl(language);
        this.description.removeControl(language);
    }

    additionalNoteValueChange(
        additionalNoteSpecification: NoteDTView[]
    ): void {
        this.additionalNoteSpecification = additionalNoteSpecification;
    }

    setAdditionalNote() {
        if (this.additionalNoteSpecification && this.additionalNoteSpecification.length > 0 && this.additionalNoteSpecification[0].contents.length > 0) {
            return this.additionalNoteSpecification;
        } else {
            return null;
        }
    }

    additionalNoteValidityChange(isValid: boolean) {
        this.isAdditionalNoteSpecificationValid = isValid;
    }

    newEntityClicked(
        value: Entities | string,
        event = undefined,
        isMultiSelect = false
    ): void {
        if (event === undefined && !isMultiSelect) {
            this.entityWillBeOpened = value;
            this.messageBox.openMessageBox();
        } else {
            if (isMultiSelect) {
                this.entityWillBeOpened = value;
                this.gotoEntity();
            } else if (event) {
                this.gotoEntity();
            }
        }
    }

    editEntityClicked(event: { oid: number; type: string }) {
        if (event) {
            this.entityWillBeOpened = event.type;
            this.gotoEntity(event.oid);
        }
    }

    onAccreditingAgentSelectionChange(oids): void {
        this.accreditingAgent.patchValue(oids);
    }

    accreditationJurisdictionChange(countryList: CodeDTView[]): void {
        this.accreditationJurisdiction.patchValue(countryList);
    }

    accreditationValidEqfChange(countryList: CodeDTView[]): void {
        this.accreditationValidEqf.patchValue(countryList);
    }

    accreditationValidThematicAreaChange(countryList: CodeDTView[]): void {
        this.accreditationValidThematicArea.patchValue(countryList);
    }

    closeNewEntityModal(closeInfo: {
        isEdit: boolean;
        oid?: number;
        displayName?: string;
    }) {
        this.openEntityModal[this.entityWillBeOpened].isOpen =
            this.modalsService.closeModal();
        if (closeInfo.oid) {
            const item: any = {
                oid: closeInfo.oid,
                displayName: closeInfo.displayName,
                defaultLanguage: this.defaultLanguage,
            };
            switch (this.entityWillBeOpened) {
                case 'organization':
                    this.selectedAccreditingAgent = item;
                    break;
            }
        }
    }

    private getLangChange() {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {

                this.loadBreadcrumbs();
            });
    }

    private setNewAccreditation() {
        this.setPageTitle('new');
        this.language = this.language || this.translateService.currentLang;
        this.defaultLanguage = this.language;
        this.selectedLanguages.push({
            code: this.language,
            label: this.language,
        });

        this.addTitleControls(this.language);
        this.credentialBuilderService.addOtherDocumentRow(
            this.relatedDocuments
        );
    }

    private setEditAccreditation(data) {
        this.setPageTitle('edit');
        this.accreditationData = data;
        this.setAccreditationData();
    }

    private setAccreditationData() {
        this.accreditationOid = this.accreditationData.oid;
        this.languages = this.accreditationData.additionalInfo.languages;
        this.language = this.accreditationData.defaultLanguage;
        this.defaultLanguage = this.language;
        this.additionalNoteSpecView = _get(this.accreditationData, 'additionalNote', []);
        this.selectedLanguages = this.multilingualService.setUsedLanguages(
            this.accreditationData.additionalInfo.languages,
            this.defaultLanguage
        );

        this.addTitleControls(this.language, this.accreditationData?.title?.contents[0].content, this.accreditationData?.description?.contents[0].content);
        /*         this.credentialBuilderService.addOtherDocumentRow(
            this.relatedDocuments
        ); */
        this.credentialBuilderService.extractWebDocuments(
            _get(
                this.accreditationData,
                'supplementaryDocument',
                []
            ),
            this.relatedDocuments
        );

        this.formGroup.patchValue({
            label: this.accreditationData?.label,
            accreditationStartDate: this.formsService.getDateByFieldNameFromObject('dateIssued', this.accreditationData),
            accreditationExpiryDate: this.formsService.getDateByFieldNameFromObject('expiryDate', this.accreditationData),
            accreditationReviewDate: this.formsService.getDateByFieldNameFromObject('reviewDate', this.accreditationData),
            accreditationType: this.accreditationData?.dcType,
            accreditationDecision: this.accreditationData?.decision,
            accreditationJurisdiction: this.accreditationData?.limitJurisdiction,
            accreditationReport: this.accreditationData?.report?.contentUrl,
            accreditationValidEqf: this.accreditationData?.limitEQFLevel,
            accreditationValidThematicArea: this.accreditationData?.limitField,
            homePage: _get(
                this.accreditationData,
                'homepage[0].contentUrl',
                null
            ),
        });
    }

    private setPageTitle(titleType) {
        switch (titleType) {
            case 'new':
                this.pageTitle = this.translateService.instant(
                    'credential-builder.accreditation-form.title-new'
                );
                break;

            case 'edit':
                this.pageTitle = this.translateService.instant(
                    'credential-builder.accreditation-form.title-edit'
                );
                break;

            case 'modal':
                this.pageTitle = '';
                break;

            default:
                break;
        }
    }

    private loadBreadcrumbs() {
        this.breadcrumbs = [
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

    private setAssessmentPayload(): AccreditationSpecView {
        return {
            label: this.label.value ?? null,
            defaultLanguage: this.defaultLanguage,
            title: this.title
                ? this.credentialBuilderService.getDTView(this.title)
                : null,
            relAccreditingAgent: this.accreditingAgent.value
                ? {
                    oid: [this.accreditingAgent.value],
                }
                : null,
            dcType: this.formGroup.get('accreditationType').value ?? null,
            description: this.description
                ? this.credentialBuilderService.getDTView(this.description)
                : null,
            decision: this.formGroup.get('accreditationDecision').value ?? null,
            limitJurisdiction: this.formGroup.get('accreditationJurisdiction').value,
            report: this.accreditationReport.value
                ? {
                    contentUrl: this.accreditationReport.value,
                }
                : null,
            homepage: this.credentialBuilderService.getHomePage(this.homePage.value),
            limitEQFLevel: this.formGroup.get('accreditationValidEqf').value,
            limitField: this.formGroup.get('accreditationValidThematicArea').value,
            dateIssued: this.dateFormatService.dateToStringDateTime(this.accreditationStartDate.value),
            expiryDate: this.dateFormatService.dateToStringDateTime(this.accreditationExpiryDate.value),
            reviewDate: this.dateFormatService.dateToStringDateTime(this.accreditationReviewDate.value),
            additionalNote: this.setAdditionalNote(),
            supplementaryDocument: this.credentialBuilderService.getOtherDocument(
                this.relatedDocuments,
                this.defaultLanguage
            ),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
            oid: this.accreditationOid ?? null
        };
    }

    /* private setDateIssuedPayload() {
        if (this.accreditationData) {
            return this.accreditationStartDate.value;
        } else {
            return this.dateFormatService.dateToStringDateTime(
                this.accreditationStartDate.value
            );
        }
    } */

    private createAccreditation(payload) {
        this.isLoading = true;
        this.api
            .createAccreditation(payload, this.translateService.currentLang)
            .pipe(take(1))
            .subscribe({
                next: (v) => {
                    this.newEntity = v;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.closeForm();
                },
                error: () => {
                    if (this.isModal) {
                        this.onSaveEvent.emit(null);
                    } else {
                        this.closeForm();
                    }
                    this.isLoading = false;
                },
                complete: () => {
                    this.isLoading = false;
                },
            });
    }

    private updateAccreditation(payload) {
        this.isLoading = true;
        this.api
            .updateAccreditation(payload, this.translateService.currentLang)
            .pipe(take(1))
            .subscribe({
                next: (v) => {
                    this.newEntity = v;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.closeForm();
                },
                error: () => {
                    if (this.isModal) {
                        this.onSaveEvent.emit(null);
                    } else {
                        this.closeForm();
                    }
                    this.isLoading = false;
                },
                complete: () => {
                    this.isLoading = false;
                },
            });
    }

    private addTitleControls(language: string, value: string = null, descValue: string = null): void {
        this.title.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
                Validators.required,
                noSpaceValidator,
            ])
        );

        this.description.addControl(
            language,
            new FormControl(descValue, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
                noSpaceValidator,
            ])
        );
    }

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.modalTitle, oid);
        this.accreditationBody = this.setAssessmentPayload();
    }
}
