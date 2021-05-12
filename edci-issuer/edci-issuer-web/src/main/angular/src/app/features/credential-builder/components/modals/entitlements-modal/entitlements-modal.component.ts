import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { MoreInformationComponent } from '@shared/components/more-information/more-information.component';
import { Constants, Entities } from '@shared/constants';
import {
    CodeDTView,
    EntitlementSpecView,
    EntitlemSpecificationDCView,
    NoteDTView,
    PagedResourcesEntitlementSpecLiteView,
    ResourceEntitlementSpecView,
    ResourceOrganizationSpecView,
    SubresourcesOids,
    V1Service,
} from '@shared/swagger';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-entitlements-modal',
    templateUrl: './entitlements-modal.component.html',
    styleUrls: ['./entitlements-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class EntitlementsModalComponent implements OnInit, OnDestroy {

    @Input() modalTitle: string;
    @Input() modalId: string = 'entitlementModal';
    @Input() language: string;
    @Input() editEntitlementOid?: number;
    @Output() onCloseModal: EventEmitter<{isEdit: boolean, oid: number, title: string}> = new EventEmitter();
    @ViewChild('additionalNoteSpecification')
    additionalNoteSpecification: MoreInformationComponent;
    @ViewChild('additionalNote')
    additionalNote: MoreInformationComponent;

    defaultLanguage: string;
    availableLanguages: string[] = [];
    selectedLanguages: UxLanguage[] = [];
    editEntitlement: EntitlementSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    entitlementBody: EntitlementSpecView;
    isSaveDisabled: boolean = false;
    isLoading: boolean = true;
    specDisabled: boolean = true;
    additionalNoteView: NoteDTView[];
    additionalNoteSpecView: NoteDTView[];
    selectedValidWith: ResourceOrganizationSpecView;
    selectedSubEntitlements: PagedResourcesEntitlementSpecLiteView;
    subEntitlementsOidList: number[] = [];
    indexToNextTab: number;
    openEntityModal: { [key: string]: { modalId: string, isOpen: boolean, oid?: number } } = {};
    entityWillBeOpened: Entities;
    isNewEntityDisabled: boolean;

    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        title: new FormGroup({}),
        description: new FormGroup({}),
        issueDate: new FormControl(null),
        expiryDate: new FormControl(null),
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
            moreInformation: new FormGroup({}),
        }),
    });

    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
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

    get specificationDescription() {
        return this.specifiedBy.get('specificationDescription') as FormGroup;
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

    get moreInformation() {
        return this.specifiedBy.get('moreInformation') as FormArray;
    }

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private translateService: TranslateService,
        private notificationService: NotificationService,
        private multilingualService: MultilingualService,
        private dateFormatService: DateFormatService
    ) {}
    ngOnInit() {
        this.specificationTitleValueChange();
        this.titleValueChangeAutocomplete();
        if (this.editEntitlementOid) {
            this.modalTitle = this.translateService.instant('credential-builder.entitlements-tab.editEntitlement');
            this.getEntitlementDetails();
        } else {
            this.modalTitle = this.translateService.instant('credential-builder.entitlements-tab.createEntitlement');
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
        if (this.formGroup.invalid) {
            this.uxService.markControlsTouched(this.formGroup);
            this.isLoading = false;
            this.uxService.openMessageBox('messageBoxFormError');
        } else {
            this.isSaveDisabled = true;
            this.setEntitlementBody();
            if (this.editEntitlement) {
                this.updateEntitlement();
            } else {
                this.createEntitlement();
            }
        }
    }

    checkValidDate(): void {
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
        this.addNewLanguageControl(language);
        this.moreInformationAddLanguage(language);
    }

    languageRemoved(language: string): void {
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
        this.moreInformationRemoveLanguage(language);
        this.title.removeControl(language);
        this.description.removeControl(language);
        this.specificationDescription.removeControl(language);
        this.specificationTitle.removeControl(language);
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

    onValidWithSelectionChange(oid: number): void {
        this.specifiedBy.patchValue({
            limitOrganization: oid,
        });
    }

    onSubEntitlementSelectionChange(oids: number[]): void {
        this.subEntitlementsOidList = oids;
    }

    titleValueChangeAutocomplete(): void {
        this.title.valueChanges.takeUntil(this.destroy$).subscribe((value) => {
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
            .takeUntil(this.destroy$)
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

    moreInformationValueChange(moreInformation: FormGroup): void {
        this.specifiedBy.patchValue({
            moreInformation: moreInformation,
        });
    }

    newEntityClicked(value: Entities, event = undefined): void {
        if (event === undefined) {
            this.entityWillBeOpened = value;
            this.uxService.openMessageBox('messageBoxNewEntityWarning');
        } else {
            if (event) {
                this.gotoEntity();
            }
        }
    }

    closeNewEntityModal(closeInfo: {isEdit: boolean, oid?: number, title?: string}) {
        this.openEntityModal[this.entityWillBeOpened].isOpen = false;
        this.uxService.closeModal(this.credentialBuilderService.getIdFromLastModalAndRemove());
        this.uxService.openModal(this.modalId);
        if (closeInfo.oid) {
            let item: any = {
                oid: closeInfo.oid,
                defaultTitle: closeInfo.title,
                defaultLanguage: this.defaultLanguage
            };
            switch (this.entityWillBeOpened) {
            case 'entitlement':
                this.selectedSubEntitlements =
                    this.credentialBuilderService.fillMultipleInput(this.selectedSubEntitlements, this.subEntitlementsOidList, item);
                break;
            }
        }
    }

    editEntityClicked(event: { oid: number, type: Entities }) {
        if (event) {
            this.entityWillBeOpened = event.type;
            this.gotoEntity(event.oid);
        }
    }

    private gotoEntity(oid: number = null) {
        this.uxService.closeMessageBox('messageBoxNewEntityWarning');
        const newEntityModalId = this.credentialBuilderService.generateNewIdModal();
        this.openEntityModal[this.entityWillBeOpened] = {
            isOpen: true,
            modalId: newEntityModalId,
            oid
        };
        this.uxService.closeModal(this.modalId);
        this.uxService.openModal(newEntityModalId);
        this.setEntitlementBody();
    }

    private getValidWith(): void {
        this.api
            .getValidWith(
                this.editEntitlementOid,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe((validWith: ResourceOrganizationSpecView) => {
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
        if (this.limitOrganization.value && !this.specDisabled) {
            relValidWith = {
                oid: [this.limitOrganization.value],
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
            .takeUntil(this.destroy$)
            .subscribe(
                (entitlement: EntitlementSpecView) => {
                    this.editEntitlement = entitlement;
                    this.availableLanguages = this.editEntitlement.additionalInfo.languages;
                    this.language = this.editEntitlement.defaultLanguage;
                    this.defaultLanguage = this.language;
                    this.selectedLanguages = this.multilingualService.setUsedLanguages(
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
                (err) => this.closeModal(false)
            );
    }

    private getSubEntitlements(): void {
        this.api
            .listHasEntPart(
                this.editEntitlementOid,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (hasPart: PagedResourcesEntitlementSpecLiteView) => {
                    this.selectedSubEntitlements = hasPart;
                },
                (err) => {}
            );
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
            .takeUntil(this.destroy$)
            .subscribe(
                (entitlement: ResourceEntitlementSpecView) => {
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.edit'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true, entitlement.oid, entitlement.defaultTitle);
                },
                (err) => {
                    this.closeModal(false);
                    this.isLoading = false;
                }
            );
    }

    private createEntitlement(): void {
        this.api
            .createEntitlement(
                this.entitlementBody,
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (entitlement: ResourceEntitlementSpecView) => {
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant('common.create'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                    this.isLoading = false;
                    this.closeModal(true, entitlement.oid, entitlement.defaultTitle);
                },
                (err) => {
                    this.closeModal(false);
                    this.isLoading = false;
                }
            );
    }

    private setEntitlementBody(): void {
        this.entitlementBody = {
            defaultTitle: this.defaultTitle.value,
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
            additionalNote: this.additionalNote.getAdditionalNotes(),
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
                supplementaryDocument: this.credentialBuilderService.getOtherDocument(
                    this.otherWebDocuments,
                    this.defaultLanguage
                ),
                additionalNote: this.additionalNoteSpecification.getAdditionalNotes(),
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
            defaultTitle: this.editEntitlement.defaultTitle,
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

    private moreInformationRemoveLanguage(language: string): void {
        this.additionalNote.languageRemoved(language);
        this.additionalNoteSpecification.languageRemoved(language);
    }

    private moreInformationAddLanguage(language: string): void {
        this.additionalNote.languageAdded(language);
        this.additionalNoteSpecification.languageAdded(language);
    }

    private changeSpecifiedByAvailability(isDisabled: boolean): void {
        if (isDisabled) {
            this.identifier.disable();
            this.homePage.disable();
            this.limitOrganization.disable();
            this.otherWebDocuments.disable();
            this.moreInformation.disable();
            this.specificationDescription.disable();
        } else {
            this.specificationDescription.enable();
            this.identifier.enable();
            this.homePage.enable();
            this.limitOrganization.enable();
            this.otherWebDocuments.enable();
            this.moreInformation.enable();
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
}
