import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { UxLanguage, UxService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { MultilingualService } from '@services/multilingual.service';
import { Constants, Entities } from '@shared/constants';
import { SelectedItemList } from '@shared/models/selected-item-list.modal';
import { DocumentUpload } from '@shared/models/upload.model';
import {
    CodeDTView,
    ContactPointDCView,
    ContentDTView,
    IdentifierDTView,
    LegalIdentifierDTView,
    LocationDCView,
    NoteDTView,
    OrganizationSpecView,
    ResourceOrganizationSpecView,
    SubresourcesOids,
    TextDTView,
    V1Service,
} from '@shared/swagger';
import { identifierValidator } from '@shared/validators/identifier.validators';
import { legalIdentifierValidator } from '@shared/validators/legal-identifier.validators';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-organizations-modal',
    templateUrl: './organizations-modal.component.html',
    styleUrls: ['./organizations-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class OrganizationsModalComponent implements OnInit, OnDestroy {
    modalTitleBreadcrumb: string[];
    get legalAddress() {
        return this.formGroup.get('legalAddress') as FormGroup;
    }

    get homePage() {
        return this.formGroup.get('homePage');
    }

    get email() {
        return this.formGroup.get('email');
    }

    get taxIdentifier() {
        return this.formGroup.get('taxIdentifier') as FormGroup;
    }

    get taxIdentifierContent() {
        return this.formGroup.get('taxIdentifier.content');
    }

    get taxIdentifierSpatialId() {
        return this.formGroup.get('taxIdentifier.spatialId');
    }

    get legalIdentifier() {
        return this.formGroup.get('legalIdentifier') as FormGroup;
    }

    get legalIdentifierContent() {
        return this.formGroup.get('legalIdentifier.content');
    }

    get legalIdentifierSpatialId() {
        return this.formGroup.get('legalIdentifier.spatialId');
    }

    get vatIdentifier() {
        return this.formGroup.get('vatIdentifier') as FormGroup;
    }

    get vatIdentifierContent() {
        return this.formGroup.get('vatIdentifier.content');
    }

    get vatIdentifierSpatialId() {
        return this.formGroup.get('vatIdentifier.spatialId');
    }

    get identifiers() {
        return this.formGroup.get('identifiers') as FormArray;
    }

    get locationName() {
        return this.formGroup.get('locationName') as FormGroup;
    }

    get location() {
        return this.formGroup.get('location');
    }

    get country() {
        return this.formGroup.get('country') as FormGroup;
    }

    get preferredName() {
        return this.formGroup.get('preferredName') as FormGroup;
    }

    get alternativeName() {
        return this.formGroup.get('alternativeName') as FormGroup;
    }

    get parentOrganization() {
        return this.formGroup.get('parentOrganization');
    }

    get defaultTitle() {
        return this.formGroup.get('defaultTitle') as FormControl;
    }

    @Input() modalTitle: string;
    @Input() modalId: string = 'organizationModal';
    @Input() language: string;
    @Input() editOrganizationOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        title: string;
    }> = new EventEmitter();

    editOrganization: OrganizationSpecView;
    imageExtensions: string[] = ['jpeg', 'jpg', 'png'];
    logo: File;
    logoPreviewURL: string;
    isLogoNotAvailable: boolean = false;
    isLoading: boolean = true;
    hasFormBeenSubmitted: boolean = false;
    defaultLanguage: string;
    selectedLanguages: UxLanguage[] = [];
    availableLanguages: string[] = [];
    destroy$: Subject<boolean> = new Subject<boolean>();
    organizationBody: OrganizationSpecView;
    locationNUTS: CodeDTView[] = [];
    indexToNextTab: number;
    openEntityModal: {
        [key: string]: { modalId: string; isOpen: boolean; oid?: number };
    } = {};
    entityWillBeOpened: Entities;
    isNewEntityDisabled: boolean;
    isSaveDisabled: boolean = false;
    selectedEntitlements: SelectedItemList[] = [];
    selectedParentOrganization: ResourceOrganizationSpecView;
    extensionOfRequestBackground: string;
    base64FromRequest: string;
    isNewLogo: boolean;
    identifier = new FormGroup(
        {
            id: new FormControl(
                null,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
            ),
            identifierSchemeAgencyName: new FormControl(
                null,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
            ),
        },
        identifierValidator
    );
    formGroup = new FormGroup({
        defaultTitle: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        preferredName: new FormGroup({}),
        alternativeName: new FormGroup({}),
        legalIdentifier: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            Validators.required,
            noSpaceValidator,
        ]),
        vatIdentifier: new FormGroup(
            {
                content: new FormControl(null, [
                    Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                ]),
                spatialId: new FormControl(
                    null,
                    Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
                ),
            },
            legalIdentifierValidator
        ),
        taxIdentifier: new FormGroup(
            {
                content: new FormControl(
                    null,
                    Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
                ),
                spatialId: new FormControl(
                    null,
                    Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
                ),
            },
            legalIdentifierValidator
        ),
        identifiers: new FormArray([this.identifier]),
        country: new FormControl(null, Validators.required),
        locationName: new FormGroup({}),
        legalAddress: new FormGroup({}),
        email: new FormControl(null, [
            Validators.email,
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
        ]),
        homePage: new FormControl(null, [
            Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
        ]),
        parentOrganization: new FormControl(null),
        accreditation: new FormControl({ value: null, disabled: true }),
    });

    constructor(
        public uxService: UxService,
        public credentialBuilderService: CredentialBuilderService,
        private api: V1Service,
        private multilingualService: MultilingualService,
        private translateService: TranslateService,
        private notificationService: NotificationService
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editOrganizationOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.organizations-tab.editOrganization'
            );
            this.getOrganizationDetails();
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.organizations-tab.createOrganization'
            );
            this.language = this.language || this.translateService.currentLang;
            this.defaultLanguage = this.language;
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
            this.setOrganizationBody();
            this.isSaveDisabled = true;
            if (this.editOrganizationOid) {
                this.updateOrganization();
            } else {
                this.createOrganization();
            }
        }
    }

    onNewDocument(files: DocumentUpload[]) {
        if (files.length > 0 && files[0]) {
            this.isNewLogo = true;
            this.logo = files[0].file;
            const reader = new FileReader();
            reader.onloadend = () => {
                this.isLogoNotAvailable = false;
                this.logoPreviewURL = reader.result as string;
            };
            reader.readAsDataURL(files[0].file);
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
    }

    languageRemoved(language: string): void {
        if (this.language === language) {
            this.language = this.selectedLanguages[0].code.toLowerCase();
        }
        this.preferredName.removeControl(language);
        this.alternativeName.removeControl(language);
        this.locationName.removeControl(language);
        this.legalAddress.removeControl(language);
    }

    addIdentifierRow() {
        this.identifiers.push(
            new FormGroup(
                {
                    id: new FormControl(
                        null,
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
                    ),
                    identifierSchemeAgencyName: new FormControl(
                        null,
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
                    ),
                },
                identifierValidator
            )
        );
    }

    removeIdentifierRow(index: number) {
        this.identifiers.removeAt(index);
    }

    countrySelectionChange(country: CodeDTView): void {
        this.formGroup.patchValue({
            country: country,
        });
    }

    locationNUTSSelectionChange(locationNUTS: CodeDTView[]): void {
        this.locationNUTS = locationNUTS;
    }

    onParentOrganizationSelectionChange(oid: number): void {
        this.formGroup.patchValue({
            parentOrganization: oid,
        });
    }

    newEntityClicked(
        value: Entities,
        event = undefined,
        isMultiSelect: boolean = false
    ): void {
        if (event === undefined && !isMultiSelect) {
            this.entityWillBeOpened = value;
            this.uxService.openMessageBox('messageBoxNewEntityWarning');
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
        title?: string;
    }) {
        this.openEntityModal[this.entityWillBeOpened].isOpen = false;
        this.uxService.closeModal(
            this.credentialBuilderService.getIdFromLastModalAndRemove()
        );
        this.uxService.openModal(this.modalId);
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (closeInfo.oid) {
            let item: any = {
                oid: closeInfo.oid,
                defaultTitle: closeInfo.title,
                defaultLanguage: this.defaultLanguage,
            };
            switch (this.entityWillBeOpened) {
            case 'organization':
                this.selectedParentOrganization = item;
                break;
            }
        }
    }

    editEntityClicked(event: { oid: number; type: Entities }) {
        if (event) {
            this.entityWillBeOpened = event.type;
            this.gotoEntity(event.oid);
        }
    }

    deleteLogo(): void {
        this.logoPreviewURL = undefined;
    }

    private gotoEntity(oid: number = null) {
        this.uxService.closeMessageBox('messageBoxNewEntityWarning');
        const newEntityModalId =
            this.credentialBuilderService.generateNewIdModal(this.modalTitle);
        this.openEntityModal[this.entityWillBeOpened] = {
            isOpen: true,
            modalId: newEntityModalId,
            oid,
        };
        this.uxService.closeModal(this.modalId);
        this.uxService.openModal(newEntityModalId);
        this.setOrganizationBody();
    }

    private getOrganizationDetails(): void {
        this.api
            .getOrganization(
                this.editOrganizationOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((organization: OrganizationSpecView) => {
                this.editOrganization = organization;
                this.availableLanguages =
                    this.editOrganization.additionalInfo.languages;
                this.language = this.editOrganization.defaultLanguage;
                this.defaultLanguage = this.language;
                this.selectedLanguages =
                    this.multilingualService.setUsedLanguages(
                        this.editOrganization.additionalInfo.languages,
                        this.defaultLanguage
                    );
                this.extractIdentifiers();
                this.setForm();
                this.getLogo();
                this.extractParentOrganization();
            });
    }

    private getLogo(): void {
        if (this.editOrganization.logo && this.editOrganization.logo.content) {
            this.isLogoNotAvailable = false;
            this.logoPreviewURL =
                'data:image/png;base64,' + this.editOrganization.logo.content;
            this.base64FromRequest = this.editOrganization.logo.content;
            const contentTypeSplitted =
                this.editOrganization.logo.contentType.uri.split('/');
            this.extensionOfRequestBackground =
                contentTypeSplitted[contentTypeSplitted.length - 1];
        } else if (
            this.editOrganization.logo &&
            !this.editOrganization.logo.content &&
            this.editOrganization.logo.contentUrl
        ) {
            this.isLogoNotAvailable = true;
            this.logoPreviewURL = 'assets/images/image-not-found.svg';
        }
    }

    private addNewLanguageControl(language: string): void {
        this.addPreferredNameControl(language);
        this.addAlternativeNameControl(language);
        this.addGeographicNameControl(language);
        this.addFullAddressControl(language);
    }

    private addPreferredNameControl(
        language: string,
        value: string = null
    ): void {
        this.preferredName.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
                Validators.required,
                noSpaceValidator,
            ])
        );
    }

    private addAlternativeNameControl(
        language: string,
        value: string = null
    ): void {
        this.alternativeName.addControl(
            language,
            new FormControl(
                value,
                Validators.maxLength(Constants.MAX_LENGTH_LONG)
            )
        );
    }

    private addGeographicNameControl(
        language: string,
        value: string = ''
    ): void {
        this.locationName.addControl(
            language,
            new FormControl(
                value,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
            )
        );
    }

    private addFullAddressControl(language: string, value: string = ''): void {
        this.legalAddress.addControl(
            language,
            new FormControl(
                value,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)
            )
        );
    }

    private createOrganization(): void {
        this.api
            .createOrganization(
                this.organizationBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (organization: OrganizationSpecView) => {
                    this.addLogo(organization);
                },
                (err) => {
                    this.closeModal(false);
                    this.isLoading = false;
                }
            );
    }

    private updateOrganization(): void {
        this.organizationBody.oid = this.editOrganization.oid;
        this.api
            .updateOrganization(
                this.organizationBody,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (organization) => {
                    this.addLogo(organization);
                },
                (err) => {
                    this.closeModal(false);
                    this.isLoading = false;
                }
            );
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

    private addLogo(organization: ResourceOrganizationSpecView): any {
        if (this.logo || this.base64FromRequest) {
            if (!this.isNewLogo) {
                this.logo = this.b64toFile(
                    `data:image/${this.extensionOfRequestBackground};base64,${this.base64FromRequest}`,
                    `background-img.${this.extensionOfRequestBackground}`
                );
            }
            this.api
                .addLogo(organization.oid, this.logo)
                .pipe(takeUntil(this.destroy$))
                .subscribe(() => {
                    this.closeModal(
                        true,
                        organization.oid,
                        organization.defaultTitle
                    );
                    this.showNotification();
                });
        } else {
            this.closeModal(true, organization.oid, organization.defaultTitle);
        }
    }

    private setUnitOf(): SubresourcesOids {
        let relUnitOf: SubresourcesOids = null;
        if (this.parentOrganization.value) {
            relUnitOf = {
                oid: [
                    Array.isArray(this.parentOrganization.value)
                        ? this.parentOrganization.value[0]
                        : this.parentOrganization.value,
                ],
            };
        }
        return relUnitOf;
    }

    private showNotification() {
        if (this.editOrganization) {
            this.notificationService.showNotification({
                severity: 'success',
                summary: this.translateService.instant('common.edit'),
                detail: this.translateService.instant(
                    'credential-builder.operationSuccessful'
                ),
            });
        } else {
            this.notificationService.showNotification({
                severity: 'success',
                summary: this.translateService.instant('common.create'),
                detail: this.translateService.instant(
                    'credential-builder.operationSuccessful'
                ),
            });
        }
    }

    private extractIdentifiers(): void {
        if (this.editOrganization.identifier) {
            this.editOrganization.identifier.forEach(
                (identifier: IdentifierDTView) => {
                    this.identifiers.push(
                        new FormGroup(
                            {
                                id: new FormControl(
                                    _get(identifier, 'content', null),
                                    Validators.maxLength(
                                        Constants.MAX_LENGTH_DEFAULT
                                    )
                                ),
                                identifierSchemeAgencyName: new FormControl(
                                    _get(
                                        identifier,
                                        'identifierSchemeAgencyName',
                                        null
                                    ),
                                    Validators.maxLength(
                                        Constants.MAX_LENGTH_DEFAULT
                                    )
                                ),
                            },
                            identifierValidator
                        )
                    );
                }
            );
            if (this.editOrganization.identifier.length > 0) {
                this.identifiers.removeAt(0);
            }
        }
    }

    private extractParentOrganization(): void {
        this.api
            .getUnitOf(
                this.editOrganization.oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe((parentOrganization: ResourceOrganizationSpecView) => {
                this.selectedParentOrganization = parentOrganization;
            });
    }

    private setOrganizationBody(): void {
        this.organizationBody = {
            defaultTitle: this.defaultTitle.value,
            defaultLanguage: this.defaultLanguage,
            preferredName: this.getPreferredName(),
            alternativeName: this.getAlternativeName(),
            // REVIEW
            legalIdentifier: this.getIdentifier(this.legalIdentifier),
            vatIdentifier: this.getLegalIdentifierArray(this.vatIdentifier),
            taxIdentifier: this.getLegalIdentifierArray(this.taxIdentifier),
            identifier: this.getOtherIdentifiers(),
            homePage: this.credentialBuilderService.getHomePage(
                this.homePage.value
            ),
            contactPoint: this.getEmail(),
            relUnitOf: this.setUnitOf(),
            hasLocation: this.getLocation(),
            additionalInfo: {
                languages: this.multilingualService.getUsedLanguages(
                    this.selectedLanguages
                ),
            },
        };
    }

    private getPreferredName(): TextDTView {
        let preferredName: TextDTView;
        preferredName = {
            contents: this.multilingualService.formToView(
                this.preferredName.value
            ),
        };

        return preferredName;
    }

    private getAlternativeName(): Array<TextDTView> {
        let alternativeName: TextDTView[] = null;
        let alternativeNameContents: ContentDTView[] =
            this.multilingualService.formToView(this.alternativeName.value);
        if (alternativeNameContents.length > 0) {
            alternativeName = [
                {
                    contents: alternativeNameContents,
                },
            ];
        }
        return alternativeName;
    }

    private getIdentifier(identifier: FormGroup): IdentifierDTView {
        let id: LegalIdentifierDTView = null;
        if (identifier.value) {
            id = {
                content: identifier.value,
            };
        }
        return id;
    }

    private getLegalIdentifier(identifier: FormGroup): LegalIdentifierDTView {
        let legalIdentifier: LegalIdentifierDTView = null;
        const spatialId = identifier.controls.spatialId.value;
        const content = identifier.controls.content.value;
        if (identifier && spatialId && content) {
            legalIdentifier = {
                content: content,
                spatialId: spatialId,
            };
        }
        return legalIdentifier;
    }

    private getLegalIdentifierArray(identifier): Array<LegalIdentifierDTView> {
        const legalIdentifier = this.getLegalIdentifier(identifier);
        return legalIdentifier ? [legalIdentifier] : null;
    }

    private getOtherIdentifiers(): Array<IdentifierDTView> {
        const sentIdentifiers: Array<IdentifierDTView> = [];
        this.identifiers.value.forEach((identifier) => {
            if (identifier.identifierSchemeAgencyName || identifier.id) {
                sentIdentifiers.push({
                    content: _get(identifier, 'id', null),
                    identifierSchemeAgencyName: _get(
                        identifier,
                        'identifierSchemeAgencyName',
                        null
                    ),
                });
            }
        });
        return sentIdentifiers.length > 0 ? sentIdentifiers : null;
    }

    private getLocation(): Array<LocationDCView> {
        let location: LocationDCView[] = [
            {
                geographicName: this.getGeographicName(),
                spatialCode:
                    this.locationNUTS.length > 0 ? this.locationNUTS : null,
                hasAddress: [
                    {
                        countryCode: this.country.value,
                        fullAddress: this.getFullAddress(),
                    },
                ],
            },
        ];
        return location;
    }

    private getGeographicName(): TextDTView {
        let geographicName: TextDTView = null;
        let geographicNameContents: ContentDTView[] =
            this.multilingualService.formToView(this.locationName.value);
        if (geographicNameContents.length > 0) {
            geographicName = { contents: geographicNameContents };
        }
        return geographicName;
    }

    private getFullAddress(): NoteDTView {
        let fullAddress: NoteDTView = null;
        let fullAddressContents: ContentDTView[] =
            this.multilingualService.formToView(this.legalAddress.value);
        if (fullAddressContents.length > 0) {
            fullAddress = { contents: fullAddressContents };
        }
        return fullAddress;
    }

    private getEmail(): Array<ContactPointDCView> {
        let email: ContactPointDCView[] = null;
        if (this.email.value) {
            email = [
                {
                    email: [
                        {
                            id: this.email.value,
                        },
                    ],
                },
            ];
        }
        return email;
    }

    private setForm(): void {
        this.addControlsFromView();
        this.locationNUTS = _get(
            this.editOrganization,
            'hasLocation[0].spatialCode',
            null
        );
        this.formGroup.patchValue({
            defaultTitle: this.editOrganization.defaultTitle,
            // REVIEW
            legalIdentifier: _get(
                this.editOrganization,
                'legalIdentifier.content',
                null
            ),
            vatIdentifier: {
                content: _get(
                    this.editOrganization,
                    'vatIdentifier[0].content',
                    null
                ),
                spatialId: _get(
                    this.editOrganization,
                    'vatIdentifier[0].spatialId',
                    null
                ),
            },
            taxIdentifier: {
                content: _get(
                    this.editOrganization,
                    'taxIdentifier[0].content',
                    null
                ),
                spatialId: _get(
                    this.editOrganization,
                    'taxIdentifier[0].spatialId',
                    null
                ),
            },
            email: _get(
                this.editOrganization,
                'contactPoint[0].email[0].id',
                null
            ),
            homePage: _get(this.editOrganization, 'homePage[0].content', null),
            country: _get(
                this.editOrganization,
                'hasLocation[0].hasAddress[0].countryCode',
                null
            ),
        });
        this.isLoading = false;
    }

    private addControlsFromView(): void {
        this.availableLanguages.forEach((language: string) => {
            this.addPreferredNameControl(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    this.editOrganization.preferredName.contents
                )
            );
            this.addAlternativeNameControl(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editOrganization,
                        'alternativeName[0].contents',
                        []
                    )
                )
            );
            this.addFullAddressControl(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editOrganization,
                        'hasLocation[0].hasAddress[0].fullAddress.contents',
                        []
                    )
                )
            );
            this.addGeographicNameControl(
                language,
                this.multilingualService.getContentFromLanguage(
                    language,
                    _get(
                        this.editOrganization,
                        'hasLocation[0].geographicName.contents',
                        []
                    )
                )
            );
        });
    }
}
