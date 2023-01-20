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
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { ModalsService } from '@services/modals.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants, Entities, TIME_FORMAT } from '@shared/constants';
import {
    CodeDTView,
    EntitlementSpecLiteView,
    EntitlementSpecView,
    EntitlemSpecificationDCView,
    NoteDTView,
    OrganizationSpecLiteView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceEntitlementSpecView,
    SubresourcesOids,
    V1Service
} from '@shared/swagger';
import { dateValidator } from '@shared/validators/date-validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-entitlements-form',
    templateUrl: './entitlements-form.component.html',
    styleUrls: ['./entitlements-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [
        { provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT },
    ],
})
export class EntitlementsFormComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];

    @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
    @ViewChild('messageBoxFormError')
        messageBoxFormError: EuiMessageBoxComponent;
    @Input() modalTitle: string;
    @Input() modalId = 'entitlementModal';
    @Input() language: string;
    @Input() editEntitlementOid?: number;
    @Input() isModal: boolean;

    @Input() eventSave: Observable<void>;
    @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

    newEntity: EntitlementSpecView = null;

    isPrimaryLanguage = true;
    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    editEntitlement: EntitlementSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    entitlementBody: EntitlementSpecView;
    isLoading = true;
    specDisabled = true;
    additionalNoteView: NoteDTView[];
    additionalNoteSpecView: NoteDTView[];
    selectedValidWith: PagedResourcesOrganizationSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    validWithOidList: number[] = [];
    selectedSubEntitlements: PagedResourcesEntitlementSpecLiteView = {
        content: [],
        links: [],
        page: null,
    };
    subEntitlementsOidList: number[] = [];
    indexToNextTab: number;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};
    entityWillBeOpened: Entities | string;
    isNewEntityDisabled: boolean;
    unsavedEntitlements: EntitlementSpecLiteView[] = [];
    unsavedValidWithOrgs: OrganizationSpecLiteView[] = [];

    formGroup = new FormGroup({
        label: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_LABELS),
            noSpaceValidator,
        ]),
        title: new FormGroup({}),
        description: new FormGroup({}),
        issueDate: new FormControl(null, [dateValidator]),
        expiryDate: new FormControl(null, [dateValidator]),
        // Specification
        specifiedBy: new FormGroup({
            specificationTitle: new FormGroup({}),
            specificationDescription: new FormGroup({}),
            identifier: new FormControl(
                null,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
            ),
            entitlementType: new FormControl(null),
            status: new FormControl(null),
            limitOrganization: new FormControl(null), // Controlled List
            limitJurisdiction: new FormControl(null), // Controlled List
            limitOccupation: new FormControl([]), // Controlled List
            homePage: new FormControl('', [
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
            ]),
            otherWebDocuments: new FormArray([]),
        }),
    });
    issueDateValueInvalid: boolean;
    expiryDateValueInvalid: boolean;
    additionalNoteSpecification: NoteDTView[];
    additionalNote: NoteDTView[];
    removedLanguage: string;
    addedLanguage: string;
    isAdditionalNoteValid: boolean;
    isAdditionalNoteSpecificationValid: boolean;

    get label() {
        return this.formGroup.get('label') as FormControl;
    }

    get title() {
        return this.formGroup.get('title') as FormGroup;
    }

    get description() {
        return this.formGroup.get('description') as FormGroup;
    }

    get issueDate() {
        return this.formGroup.get('issueDate') as FormControl;
    }

    get expiryDate() {
        return this.formGroup.get('expiryDate') as FormControl;
    }

    get specifiedBy() {
        return this.formGroup.get('specifiedBy') as FormGroup;
    }

    get specificationTitle() {
        return this.specifiedBy.get('specificationTitle') as FormGroup;
    }

    get specificationTitleControl() {
        return this.specificationTitle.controls[this.language] as FormControl;
    }

    get specificationDescription() {
        return this.specifiedBy.get('specificationDescription') as FormGroup;
    }

    get specificationDescriptionControl() {
        return this.specificationDescription.controls[
            this.language
        ] as FormControl;
    }

    get identifier() {
        return this.specifiedBy.get('identifier') as FormControl;
    }

    get entitlementType() {
        return this.specifiedBy.get('entitlementType') as FormControl;
    }

    get status() {
        return this.specifiedBy.get('status') as FormControl;
    }

    get limitOrganization() {
        return this.specifiedBy.get('limitOrganization') as FormControl;
    }

    get limitJurisdiction() {
        return this.specifiedBy.get('limitJurisdiction') as FormControl;
    }

    get limitOccupation() {
        return this.specifiedBy.get('limitOccupation') as FormControl;
    }

    get homePage() {
        return this.specifiedBy.get('homePage') as FormControl;
    }

    get otherWebDocuments() {
        return this.specifiedBy.get('otherWebDocuments') as FormArray;
    }

    get titleControls(): FormControl {
        return this.title.controls[this.language] as FormControl;
    }

    get descriptionControls(): FormControl {
        return this.description.controls[this.language] as FormControl;
    }

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService,
        private route: ActivatedRoute,
        private router: Router,
        private modalsService: ModalsService
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.loadBreadcrumb();
            });
    }

    ngOnInit() {
        this.isPrimaryLanguage = true;
        if (this.isModal) {
            this.eventSave.pipe(takeUntil(this.destroy$)).subscribe((res) => {
                this.onSave();
            });
        } else {
            this.route.params.subscribe((params) => {
                if (params['id'] !== null || params['id'] !== undefined) {
                    this.editEntitlementOid = params['id'];
                }
            });
        }
        this.loadBreadcrumb();
        this.formGroup.get('issueDate').valueChanges.subscribe(() => {
            this.checkValidDate();
        });
        this.formGroup.get('expiryDate').valueChanges.subscribe(() => {
            this.checkValidDate();
        });
        this.specificationTitleValueChange();
        this.titleValueChangeAutocomplete();
        if (this.editEntitlementOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.entitlements-tab.editEntitlement'
            );
            this.getEntitlementDetails();
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.entitlements-tab.createEntitlement'
            );
            this.language = this.language || this.translateService.currentLang;
            this.defaultLanguage = this.translateService.currentLang;
            this.credentialBuilderService.addOtherDocumentRow(
                this.otherWebDocuments
            );
            this.selectedLanguages.push({
                code: this.language,
                label: this.language,
            });
            this.addNewLanguageControl(this.language);
            this.isLoading = false;
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        this.validateFormDatesValues();
        if (
            this.formGroup.invalid ||
            this.issueDateValueInvalid ||
            this.expiryDateValueInvalid ||
            !this.isAdditionalNoteValid ||
            !this.isAdditionalNoteSpecificationValid
        ) {
            this.formGroup.markAsUntouched();
            this.isLoading = false;
            this.messageBoxFormError.openMessageBox();
        } else {
            this.setEntitlementBody();
            if (this.editEntitlement) {
                this.updateEntitlement();
            } else {
                this.createEntitlement();
            }
        }
    }

    checkValidDate(): void {
        this.validateFormDatesValues();
        if (
            !this.dateFormatService.validateDates(
                this.issueDate.value,
                this.expiryDate.value
            )
        ) {
            this.issueDate.setErrors({ invalidDateError: true });
            this.expiryDate.setErrors({ invalidDateError: true });
        } else {
            this.issueDate.setErrors(null);
            this.expiryDate.setErrors(null);
        }
    }

    closeForm(): void {
        this.credentialBuilderService.setOcbTabSelected(6);
        this.router.navigateByUrl('credential-builder');
    }

    languageTabSelected(language: string) {
        if (this.language !== language) {
            this.language = language.toLowerCase();
        }
        this.isPrimaryLanguage = this.defaultLanguage === language;
    }

    languageAdded(language: string) {
        this.addedLanguage = language;
        this.addNewLanguageControl(language);
    }

    languageRemoved(language: string): void {
        this.removedLanguage = language;
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
        this.title.removeControl(language);
        this.description.removeControl(language);
        this.specificationDescription.removeControl(language);
        this.specificationTitle.removeControl(language);
        this.isPrimaryLanguage = this.defaultLanguage === this.language;
    }

    occupationSelectionChange(occupationsList: CodeDTView[]): void {
        this.specifiedBy.patchValue({
            limitOccupation: occupationsList,
        });
    }

    validWithinSelectionChange(countryList: CodeDTView[]): void {
        this.specifiedBy.patchValue({
            limitJurisdiction: countryList,
        });
    }

    deleteSelectionFromList(list: EntitlementSpecLiteView[], items: number[]) {
        const returnList: string[] = [];
        list.forEach((element) => {
            if (items.includes(element.oid)) {
                returnList.push(element.label);
            }
        });
        return returnList;
    }

    onValidWithSelectionChange(oids): void {
        this.validWithOidList = oids;
    }

    onSubEntitlementSelectionChange(oids): void {
        this.subEntitlementsOidList = oids;
    }

    titleValueChangeAutocomplete(): void {
        this.title.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
                if (
                    this.specificationTitle.controls[this.language] &&
                    this.specificationTitle.controls[this.language].pristine
                ) {
                    this.specificationTitle.controls[this.language].setValue(
                        value[this.language]
                    );
                }
            });
    }

    specificationTitleValueChange(): void {
        this.specificationTitle.valueChanges
            .pipe(takeUntil(this.destroy$))
            .subscribe((value) => {
                if (
                    this.credentialBuilderService.getDTView(
                        this.specificationTitle
                    )
                ) {
                    this.changeSpecifiedByAvailability(false);
                } else {
                    this.changeSpecifiedByAvailability(true);
                }
            });
    }

    additionalNoteValueChange(additionalNote: NoteDTView[]): void {
        this.additionalNote = additionalNote;
    }

    additionalNoteValidityChange(isValid: boolean) {
        this.isAdditionalNoteValid = isValid;
    }

    additionalNoteSpecificationValueChange(
        additionalNoteSpecification: NoteDTView[]
    ): void {
        this.additionalNoteSpecification = additionalNoteSpecification;
    }

    additionalNoteSpecificationValidityChange(isValid: boolean) {
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
            case 'entitlement':
                this.selectedSubEntitlements =
                        this.credentialBuilderService.fillMultipleInput(
                            this.selectedSubEntitlements,
                            this.subEntitlementsOidList,
                            item
                        );
                break;
            }
        }
    }

    editEntityClicked(event: { oid: number; type: string }) {
        if (event) {
            this.entityWillBeOpened = event.type;
            this.gotoEntity(event.oid);
        }
    }

    private gotoEntity(oid: number = null) {
        this.openEntityModal[this.entityWillBeOpened] =
            this.modalsService.openModal(this.modalTitle, oid);
        this.setEntitlementBody();
    }

    private getValidWith(): void {
        this.api
            .getValidWith(
                this.editEntitlementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((validWith: any) => {
                this.selectedValidWith = validWith;
            });
    }

    private setSubEntitlements(): SubresourcesOids {
        let relSubEntitlements: SubresourcesOids = null;
        if (this.subEntitlementsOidList.length > 0) {
            relSubEntitlements = {
                oid: this.subEntitlementsOidList,
            };
        }
        return relSubEntitlements;
    }

    private setValidWith(): SubresourcesOids {
        let relValidWith: SubresourcesOids = null;
        if (this.validWithOidList.length > 0) {
            relValidWith = {
                oid: this.validWithOidList,
            };
        }
        return relValidWith;
    }

    private getEntitlementDetails(): void {
        this.api
            .getEntitlement(
                this.editEntitlementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (entitlement: EntitlementSpecView) => {
                    this.editEntitlement = entitlement;
                    this.availableLanguages =
                        this.editEntitlement.additionalInfo.languages;
                    this.language = this.editEntitlement.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages =
                        this.multilingualService.setUsedLanguages(
                            this.editEntitlement.additionalInfo.languages,
                            this.defaultLanguage
                        );
                    this.credentialBuilderService.extractWebDocuments(
                        _get(
                            this.editEntitlement,
                            'specifiedBy.supplementaryDocument',
                            []
                        ),
                        this.otherWebDocuments
                    );
                    this.setForm();
                    this.getValidWith();
                    this.getSubEntitlements();
                    this.markSpecTitleAsDirty();
                },
                (err) => this.closeForm()
            );
    }

    private getSubEntitlements(): void {
        this.api
            .listHasEntPart(
                this.editEntitlementOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((hasPart: PagedResourcesEntitlementSpecLiteView) => {
                this.selectedSubEntitlements = hasPart;
            });
    }

    private addNewLanguageControl(language: string): void {
        this.addTitleControls(language);
        this.addDescriptionControls(language);
        this.addSpecificationDescriptionControls(language);
        this.addSpecificationTitleControls(language);
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

    private addDescriptionControls(
        language: string,
        value: string = null
    ): void {
        this.description.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private addSpecificationDescriptionControls(
        language: string,
        value: string = null
    ): void {
        this.specificationDescription.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private addSpecificationTitleControls(
        language: string,
        value: string = null
    ): void {
        this.specificationTitle.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private updateEntitlement(): void {
        this.entitlementBody.oid = this.editEntitlement.oid;
        this.api
            .updateEntitlement(
                this.entitlementBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (entitlement: ResourceEntitlementSpecView) => {
                    this.newEntity = entitlement;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                }
            }).add(() => this.handleSaveNavigation());
    }

    private handleSaveNavigation() {
        if (this.isModal) {
        // If it is a modal, just emit event when saving is done
            this.onSaveEvent.emit(this.newEntity);
        } else {
            this.credentialBuilderService.setOcbTabSelected(6);
            this.router.navigateByUrl('credential-builder');
        }
    }

    private createEntitlement(): void {
        this.api
            .createEntitlement(
                this.entitlementBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next: (entitlement: ResourceEntitlementSpecView) => {
                    this.newEntity = entitlement;
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                }
            }).add(() => this.handleSaveNavigation());
    }

    private setEntitlementBody(): void {
        this.entitlementBody = {
            label: this.label.value,
            defaultLanguage: this.defaultLanguage,
            title: this.credentialBuilderService.getDTView(this.title),
            description: this.credentialBuilderService.getDTView(
                this.description
            ),
            issuedDate: this.dateFormatService.dateToStringDate(
                this.issueDate.value
            ),
            expiryDate: this.dateFormatService.dateToStringDate(
                this.expiryDate.value
            ),
            additionalNote: this.additionalNote,
            specifiedBy: this.getEntitlementSpecification(),
            relHasPart: this.setSubEntitlements(),
            relValidWith: this.setValidWith(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
        };
    }

    private getEntitlementSpecification(): EntitlemSpecificationDCView {
        let specifiedBy: EntitlemSpecificationDCView = null;
        if (!this.specDisabled) {
            specifiedBy = {
                title: this.credentialBuilderService.getDTView(
                    this.specificationTitle
                ),
                description: this.credentialBuilderService.getDTView(
                    this.specificationDescription
                ),
                identifier: this.credentialBuilderService.getIdentifier(
                    this.identifier
                ),
                limitOccupation: this.limitOccupation.value,
                limitJurisdiction: this.limitJurisdiction.value,
                entitlementType: this.entitlementType.value,
                status: this.status.value,
                homePage: this.credentialBuilderService.getHomePage(
                    this.homePage.value
                ),
                supplementaryDocument:
                    this.credentialBuilderService.getOtherDocument(
                        this.otherWebDocuments,
                        this.defaultLanguage
                    ),
                additionalNote: this.additionalNoteSpecification,
            };
        }
        return specifiedBy;
    }

    private setForm(): void {
        this.additionalNoteView = _get(
            this.editEntitlement,
            'additionalNote',
            []
        );
        this.additionalNoteSpecView = _get(
            this.editEntitlement,
            'specifiedBy.additionalNote',
            []
        );
        this.addControlsFromView();

        this.formGroup.patchValue({
            label: this.editEntitlement.label,
            issueDate: this.getIssueDate(),
            expiryDate: this.getExpiryDateDate(),
        });

        this.specifiedBy.patchValue({
            entitlementType: _get(
                this.editEntitlement,
                'specifiedBy.entitlementType',
                null
            ),
            identifier: _get(
                this.editEntitlement,
                'specifiedBy.identifier[0].content',
                null
            ),
            homePage: _get(
                this.editEntitlement,
                'specifiedBy.homePage[0].content',
                null
            ),
            limitOccupation: _get(
                this.editEntitlement,
                'specifiedBy.limitOccupation',
                []
            ),
            limitJurisdiction: _get(
                this.editEntitlement,
                'specifiedBy.limitJurisdiction',
                []
            ),
            status: _get(this.editEntitlement, 'specifiedBy.status', null),
        });
        this.isLoading = false;
    }

    private getIssueDate(): Date | null {
        const date = _get(this.editEntitlement, 'issuedDate', null);
        return date ? new Date(date) : null;
    }

    private getExpiryDateDate(): Date | null {
        const date = _get(this.editEntitlement, 'expiryDate', null);
        return date ? new Date(date) : null;
    }

    private addControlsFromView(): void {
        this.availableLanguages.forEach((language: string) => {
            this.addTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    this.editEntitlement.title.contents
                )
            );
            this.addDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editEntitlement, 'description.contents', [])
                )
            );
            this.addSpecificationTitleControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(this.editEntitlement, 'specifiedBy.title.contents', [])
                )
            );
            this.addSpecificationDescriptionControls(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editEntitlement,
                        'specifiedBy.description.contents',
                        []
                    )
                )
            );
        });
        this.changeSpecifiedByAvailability(
            !this.credentialBuilderService.getDTView(this.specificationTitle)
        );
    }

    private changeSpecifiedByAvailability(isDisabled: boolean): void {
        if (isDisabled) {
            this.identifier.disable();
            this.homePage.disable();
            this.limitOrganization.disable();
            this.otherWebDocuments.disable();
            this.specificationDescription.disable();
        } else {
            this.specificationDescription.enable();
            this.identifier.enable();
            this.homePage.enable();
            this.limitOrganization.enable();
            this.otherWebDocuments.enable();
        }
        this.specificationRequiredFields(isDisabled);
        this.specDisabled = isDisabled;
    }

    private specificationRequiredFields(isDisabled: boolean): void {
        if (isDisabled) {
            this.specifiedBy.controls['entitlementType'].clearValidators();
            this.entitlementType.updateValueAndValidity();
            this.specifiedBy.controls['status'].clearValidators();
            this.status.updateValueAndValidity();
        } else {
            this.specifiedBy.controls['entitlementType'].setValidators(
                Validators.required
            );
            this.entitlementType.updateValueAndValidity();
            this.specifiedBy.controls['status'].setValidators([
                Validators.required,
            ]);
            this.status.updateValueAndValidity();
        }
    }

    private markSpecTitleAsDirty(): void {
        this.editEntitlement.additionalInfo.languages.forEach(
            (language: string) => {
                this.specificationTitle.controls[language].markAsDirty();
            }
        );
    }

    private validateFormDatesValues() {
        if (
            this.issueDate.value &&
            !this.dateFormatService.validateDate(this.issueDate.value)
        ) {
            this.issueDateValueInvalid = true;
            this.issueDate.reset();
        } else {
            this.issueDateValueInvalid = false;
        }

        if (
            this.expiryDate.value &&
            !this.dateFormatService.validateDate(this.expiryDate.value)
        ) {
            this.expiryDateValueInvalid = true;
            this.expiryDate.reset();
        } else {
            this.expiryDateValueInvalid = false;
        }
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
