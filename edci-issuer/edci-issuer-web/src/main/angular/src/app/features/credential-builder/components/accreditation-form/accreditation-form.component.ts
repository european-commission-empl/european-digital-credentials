import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TranslateService } from '@ngx-translate/core';
import { UxLanguage, UxLink } from '@eui/core';

import { Subject } from 'rxjs';
import { takeUntil, take } from 'rxjs/operators';
import {
    FormArray,
    FormControl,
    FormGroup,
    Validators,
} from '@angular/forms';
import { Constants, Entities } from '@shared/constants';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { CredentialBuilderService } from '@services/credential-builder.service';
import {
    AccreditationSpecView,
    OrganizationSpecLiteView,
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

@Component({
    selector: 'edci-accreditation-form',
    templateUrl: './accreditation-form.component.html',
    styleUrls: ['./accreditation-form.component.scss'],
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

    get accreditingAgent() {
        return this.formGroup.get('accreditingAgent') as FormControl;
    }

    get accreditationStartDate() {
        return this.formGroup.get('accreditationStartDate') as FormControl;
    }

    get accreditationExpiryDate() {
        return this.formGroup.get('accreditationExpiryDate') as FormControl;
    }

    get accreditationReport() {
        return this.formGroup.get('accreditationReport') as FormControl;
    }

    get relatedDocuments() {
        return this.formGroup.get('relatedDocuments') as FormArray;
    }

    get accreditedOrganization() {
        return this.formGroup.get('accreditedOrganization') as FormControl;
    }

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;

    pageTitle: string;
    isLoading = false;
    /* needed? */
    isModal = false;

    /* lang */
    language: string;
    defaultLanguage: string;
    isPrimaryLanguage = true;
    removedLanguage: string;
    addedLanguage: string;
    selectedLanguages: UxLanguage[] = [];
    languages: string[] = [];

    /* breadcrumbs */
    breadcrumbs: UxLink[] = [];

    selectedAccreditingAgent: OrganizationSpecLiteView;
    selectedAccreditedOrganization: OrganizationSpecLiteView;
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
        accreditingAgent: new FormControl(null, [Validators.required]),
        accreditedOrganization: new FormControl(null, [Validators.required]),
        accreditationStartDate: new FormControl(null, [dateValidator]),
        accreditationExpiryDate: new FormControl(null, [dateValidator]),
        accreditationType: new FormControl(null, [Validators.required]),
        accreditationStatus: new FormControl(null),
        accreditationJurisdiction: new FormControl(null),
        accreditationReport: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
        ]),
        accreditationValidEqf: new FormControl(null),
        accreditationValidThematicArea: new FormControl(null),
        relatedDocuments: new FormArray([]),
    });

    private accreditationOid: number;
    private accreditationData: ResourceAccreditationSpecView;

    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private route: ActivatedRoute,
        private translateService: TranslateService,
        public credentialBuilderService: CredentialBuilderService,
        private router: Router,
        private modalsService: ModalsService,
        private api: V1Service,
        private dateFormatService: DateFormatService,
        private multilingualService: MultilingualService,
        private notificationService: NotificationService,
        private readonly pageLoadingSpinnerService: PageLoadingSpinnerService
    ) {}

    ngOnInit(): void {
        this.route.data.pipe(takeUntil(this.destroy$)).subscribe((data) => {
            this.isLoading = false;
            this.pageLoadingSpinnerService.stopPageLoader();
            if (data.accreditationDetails) {
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
        this.credentialBuilderService.setOcbTabSelected(8);
        this.router.navigateByUrl('credential-builder');
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

    onAccreditedOrganizationSelectionChange(oids): void {
        this.accreditedOrganization.patchValue(oids);
    }

    checkValidDate() {
        this.validateFormDatesValues();
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
        this.languages = this.accreditationData.additionalInfo.languages;
        this.language = this.accreditationData.defaultLanguage;
        this.defaultLanguage = this.language;
        this.selectedLanguages = this.multilingualService.setUsedLanguages(
            this.accreditationData.additionalInfo.languages,
            this.defaultLanguage
        );

        this.addTitleControls(this.language, this.accreditationData?.title?.contents[0].content);
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
            accreditationStartDate: this.accreditationData?.dateIssued,
            accreditationExpiryDate: this.accreditationData?.expiryDate,
            accreditationType: this.accreditationData?.dcType,
            accreditationStatus: this.accreditationData?.status,
            accreditationJurisdiction: this.accreditationData?.limitJurisdiction[0],
            accreditationReport: this.accreditationData?.report?.contentUrl,
            accreditationValidEqf: this.accreditationData?.limitEQFLevel[0],
            accreditationValidThematicArea: this.accreditationData?.limitField,

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

            relOrganisation: this.accreditedOrganization.value
                ? {
                    oid: [this.accreditedOrganization.value],
                }
                : null,

            dcType: this.formGroup.get('accreditationType').value ?? null,
            status: this.formGroup.get('accreditationStatus').value ?? null,
            limitJurisdiction: this.formGroup.get('accreditationJurisdiction')
                .value
                ? [this.formGroup.get('accreditationJurisdiction').value]
                : this.formGroup.get('accreditationJurisdiction').value,
            report: this.accreditationReport.value
                ? {
                    contentUrl: this.accreditationReport.value,
                }
                : null,
            limitEQFLevel: this.formGroup.get('accreditationValidEqf').value
                ? [this.formGroup.get('accreditationValidEqf').value]
                : null,
            limitField: this.setLimitFieldPayload(),
            dateIssued: this.setDateIssuedPayload(),
            expiryDate: this.setExpiryDatePayload(),
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

    private setLimitFieldPayload() {
        const limit = this.formGroup.get('accreditationValidThematicArea').value;
        if (!limit) {
            return null;
        }

        if (limit && Array.isArray(limit) && limit?.length === 0) {
            return null;
        }

        return [this.formGroup.get('accreditationValidThematicArea').value];
    }

    private setDateIssuedPayload() {
        if (this.accreditationData) {
            return this.accreditationStartDate.value;
        } else {
            return this.dateFormatService.dateToStringDateTime(
                this.accreditationStartDate.value
            );
        }
    }

    private setExpiryDatePayload() {
        if (this.accreditationData) {
            return this.accreditationExpiryDate.value;
        } else {
            return this.dateFormatService.dateToStringDateTime(
                this.accreditationExpiryDate.value
            );
        }
    }

    private createAccreditation(payload) {
        this.isLoading = true;
        this.api
            .createAccreditation(payload, this.translateService.currentLang)
            .pipe(take(1))
            .subscribe({
                next: (v) => {

                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.closeForm();
                },
                error: (e) => {
                    console.error(e);
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
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.closeForm();
                },
                error: (e) => {
                    console.error(e);
                    this.isLoading = false;
                },
                complete: () => {
                    this.isLoading = false;
                },
            });
    }

    private addTitleControls(language: string, value: string = null): void {
        this.title.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
                Validators.required,
                noSpaceValidator,
            ])
        );
    }

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.pageTitle, oid);
        /* this.setAssessmentBody(); */
    }

    private validateFormDatesValues() {
        /*  if (
            this.accreditationExpiryDate.value &&
            !this.dateFormatService.validateDate(this.assessmentDate.value)
        ) {
            this.assessmentDate.reset();
        } */
    }
}
