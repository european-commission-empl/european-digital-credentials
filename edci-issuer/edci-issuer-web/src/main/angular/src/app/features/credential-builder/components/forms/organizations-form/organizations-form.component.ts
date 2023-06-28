import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators, ValidatorFn, ValidationErrors, FormBuilder } from '@angular/forms';
import { MAT_DATE_FORMATS } from '@angular/material/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { FormsService } from '@features/credential-builder/services/forms.service';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { DateFormatService } from '@services/date-format.service';
import { NotificationService } from '@services/error.service';
import { ModalsService } from '@services/modals.service';
import { MultilingualService } from '@services/multilingual.service';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { Constants, Entities, TIME_FORMAT } from '@shared/constants';
import {
    CodeDTView,
    ContactPointDCView,
    ContentDTView,
    GroupDCView,
    IdentifierDTView,
    LegalIdentifierDTView,
    LocationDCView,
    OrganizationSpecView,
    TextDTView,
    V1Service,
} from '@shared/swagger';
import { ContactPointValidator } from '@shared/validators/contactPoint-email-validator';
import { identifierValidator } from '@shared/validators/identifier.validators';
import { legalIdentifierValidator } from '@shared/validators/legal-identifier.validators';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { get as _get } from 'lodash';
import { Observable, Subject } from 'rxjs';
import { takeUntil, take, switchMap } from 'rxjs/operators';
@Component({
    selector: 'edci-organizations-form',
    templateUrl: './organizations-form.component.html',
    styleUrls: ['./organizations-form.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [{ provide: MAT_DATE_FORMATS, useValue: TIME_FORMAT }],
})
export class OrganizationsFormComponent implements OnInit, OnDestroy {
    get legalAddress() {
        return this.formGroup.get('legalAddress') as FormGroup;
    }

    get legalAddressControl() {
        return this.legalAddress.controls[this.language] as FormControl;
    }

    get homePage() {
        return this.formGroup.get('homePage');
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

    get eIDAS() {
        return this.formGroup.get('eIDAS') as FormGroup;
    }

    get eIDASContent() {
        return this.formGroup.get('eIDAS.content');
    }

    get eIDASSpatialId() {
        return this.formGroup.get('eIDAS.spatialId');
    }

    get otherLegalIdentifier() {
        return this.formGroup.get('otherLegalIdentifier') as FormGroup;
    }

    get otherLegalIdentifierContent() {
        return this.formGroup.get('otherLegalIdentifier.content');
    }

    get otherLegalIdentifierSpatialId() {
        return this.formGroup.get('otherLegalIdentifier.spatialId');
    }

    /* change to form group? */
    get legalIdentifier() {
        return this.formGroup.get('legalIdentifier') as FormGroup;
    }

    get legalIdentifierContent() {
        return this.formGroup.get('legalIdentifier.content');
    }

    get legalIdentifierSpatialId() {
        return this.formGroup.get('legalIdentifier.spatialId');
    }

    /*     get legalIdentifier() {
        return this.formGroup.get("legalIdentifier") as FormControl;
    } */

    /*     get legalIdentifierName() {
        return this.formGroup.get("legalIdentifierName") as FormControl;
    } */
    /*  */

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

    get preferredName() {
        return this.formGroup.get('preferredName') as FormGroup;
    }

    get preferredNameControl() {
        return this.preferredName.controls[this.language] as FormControl;
    }

    get alternativeName() {
        return this.formGroup.get('alternativeName') as FormGroup;
    }

    get alternativeNameControl() {
        return this.alternativeName.controls[this.language] as FormControl;
    }

    get label() {
        return this.formGroup.get('label') as FormControl;
    }

    get lastUpdateDate() {
        return this.formGroup.get('lastUpdateDate') as FormControl;
    }

    get contactAddress() {
        return this.formGroup.controls['contactAddress'] as FormArray;
    }

    get cAddress() {
        return (<FormArray>this.formGroup.get('contactAddress')).controls;
    }

    get memberGroups() {
        return this.formGroup.controls['memberGroups'] as FormArray;
    }

    get memberGroupsCtrls(): FormGroup[] {
        return this.memberGroups.controls as FormGroup[];
    }

    get additionalInformation() {
        return this.formGroup.get('additionalInformation') as FormGroup;
    }

    get location() {
        return this.formGroup.get('location') as FormArray;
    }

    get contactPoints() {
        return this.formGroup.get('contactPoints') as FormArray;
    }

    get additionalInformationControl() {
        return this.additionalInformation.controls[this.language] as FormControl;
    }
    parts: UxLink[] = [];

  @ViewChild('messageBoxNewEntityWarning') messageBox: EuiMessageBoxComponent;
  @ViewChild('messageBoxFormError')
      messageBoxFormError: EuiMessageBoxComponent;
  saveClicked: boolean;

  newEntity: OrganizationSpecView = null;

  /*     get additionalInformation() {
        return this.formGroup.get('additionalInformation') as FormControl;
    } */

  @Input() modalTitle: string;
  @Input() modalId = 'organizationModal';
  @Input() language: string;
  @Input() editOrganizationOid?: number;
  @Input() isModal: boolean;
  @Input() modalData: any;
  @Input() eventSave: Observable<void>;
  @Output() onSaveEvent: EventEmitter<any> = new EventEmitter();

  organizationsListContent: [];
  addressInterface = {
      description: new FormGroup({}),
      address: new FormGroup({}),
      country: [null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]],
      area: [null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]],
  };
  isPrimaryLanguage = true;
  editOrganization: OrganizationSpecView;
  imageExtensions: string[] = ['.jpeg', '.jpg', '.png'];
  logo: File;
  logoPreviewURL: string;
  isLogoNotAvailable = false;
  isLoading = true;
  hasFormBeenSubmitted = false;
  defaultLanguage: string;
  selectedLanguages: UxLanguage[] = [];
  languages: string[] = [];
  destroy$: Subject<boolean> = new Subject<boolean>();
  organizationBody: OrganizationSpecView;
  locationNUTS: CodeDTView[] = [];
  indexToNextTab: number;
  openEntityModal: {
    [key: string]: { modalId: string; isOpen: boolean; oid?: number };
  } = {};
  entityWillBeOpened: Entities | string;
  isNewEntityDisabled: boolean;
  isSaveDisabled = false;
  extensionOfRequestBackground: string;
  base64FromRequest: string;
  isNewLogo: boolean;
  identifier = new FormGroup(
      {
          id: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
          identifierSchemeAgencyName: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
      },
      identifierValidator
  );
  formGroup = new FormGroup({
      label: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_LABELS), noSpaceValidator]),
      preferredName: new FormGroup({}),
      alternativeName: new FormGroup({}),
      legalIdentifier: new FormGroup(
          {
              content: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT), Validators.required, noSpaceValidator]),
              spatialId: new FormControl(null, [Validators.required, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)], ),
          },
          legalIdentifierValidator
      ),
      vatIdentifier: new FormGroup(
          {
              content: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
              spatialId: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
          },
          legalIdentifierValidator
      ),
      taxIdentifier: new FormGroup(
          {
              content: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
              spatialId: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
          },
          legalIdentifierValidator
      ),
      eIDAS: new FormGroup(
          {
              content: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
              spatialId: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
          },
          legalIdentifierValidator
      ),
      otherLegalIdentifier: new FormGroup({
          content: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
          spatialId: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
      }),
      identifiers: new FormArray([this.identifier]),
      locationName: new FormGroup({}),
      legalAddress: new FormGroup({}),
      contactAddress: new FormArray([
          new FormGroup({
              description: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
              address: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
              country: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
          }),
      ]),
      memberGroups: this.fb.array([]),
      homePage: new FormControl(null, [Validators.pattern(Constants.URL_REGULAR_EXPRESSION), Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
      lastUpdateDate: new FormControl(null, []),

      additionalInformation: new FormGroup({}),
      location: this.fb.array([], [this.createLocationRequiredValidator()]),
      contactPoints: this.fb.array([
          this.fb.group(
              {
                  description: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
                  address: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
                  country: new FormControl(null),
                  email: new FormControl(null, [Validators.email, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
                  contactForm: new FormControl(null,
                      [Validators.pattern(Constants.URL_REGULAR_EXPRESSION), Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
                  phone: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
              },
              {
                  validators: [this.contactPointValidator.emailValidator(), this.contactPointValidator.countryValidator()],
                  updateOn: 'blur',
              }
          ),
      ]),
      /*         additionalInformation: new FormControl(null, [
            Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
        ]), */
  });

  constructor(
    public credentialBuilderService: CredentialBuilderService,
    private api: V1Service,
    private multilingualService: MultilingualService,
    private translateService: TranslateService,
    private notificationService: NotificationService,
    private route: ActivatedRoute,
    private router: Router,
    private modalsService: ModalsService,
    private fb: FormBuilder,
    private dateFormatService: DateFormatService,
    private contactPointValidator: ContactPointValidator,
    private readonly pageLoadingSpinnerService: PageLoadingSpinnerService,
    public formsService: FormsService
  ) {
      this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe(() => {
          this.loadBreadcrumb();
      });
  }

  getContactAddress(index) {
      return this.contactAddress.at(index) as FormGroup;
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
                  this.setEditOrganizationFormData(this.modalData);
              } else {
                  this.setNewOrganizationFormdata();
              }
              return;
          }

          if (data.organizationDetails) {
              this.setEditOrganizationFormData(data.organizationDetails);
              return;
          } else {
              this.setNewOrganizationFormdata();
          }
      });
      this.loadBreadcrumb();
      this.setIdentifierErrorsObservable();
  }

  ngOnDestroy() {
      this.destroy$.next(true);
      this.destroy$.unsubscribe();
  }

  onSave(): void {
      this.isLoading = true;
      this.saveClicked = true;
      this.saveForm();
  }

  onNewDocument(files: File[]) {
      if (files.length > 0) {
          this.isNewLogo = true;
          this.logo = files[0];
          const reader = new FileReader();
          reader.onloadend = () => {
              this.isLogoNotAvailable = false;
              this.logoPreviewURL = reader.result as string;
          };
          reader.readAsDataURL(files[0]);
      }
  }

  closeForm(): void {
      this.credentialBuilderService.setOcbTabSelected(5);
      this.router.navigateByUrl('credential-builder');
  }

  languageTabSelected(language: string) {
      if (this.language !== language) {
          this.language = language.toLowerCase();
      }
      this.isPrimaryLanguage = this.defaultLanguage === language;
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
      this.legalAddress.removeControl(language);
      this.additionalInformation.removeControl(language);
      this.isPrimaryLanguage = this.defaultLanguage === this.language;
      this.removeLocationControls(language);
  }

  addIdentifierRow() {
      this.identifiers.push(
          new FormGroup(
              {
                  id: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
                  identifierSchemeAgencyName: new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
              },
              identifierValidator
          )
      );
  }

  addNewMemberGroupRow() {
      this.memberGroups.push(this.createMemberGroupRow(this.languages));
  }

  addLanguageToExistingMemberGroupRows(language: string) {
      this.memberGroupsCtrls.forEach((controls) => {
          const groups = controls.get('name') as FormGroup;
          groups.addControl(language, new FormControl(null, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)));
      });
  }

  removeMemberGroupRow(index: number) {
      this.memberGroups.removeAt(index);
  }

  removeIdentifierRow(index: number) {
      this.identifiers.removeAt(index);
  }

  addContactPoint() {
      this.contactPoints.push(
          new FormGroup({
              description: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
              address: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
              country: new FormControl(null),
              email: new FormControl(null, [Validators.email, Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
              contactForm: new FormControl(null, [Validators.pattern(Constants.URL_REGULAR_EXPRESSION), Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
              phone: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
          })
      );
  }

  deleteContactPoint(index: number) {
      this.contactPoints.removeAt(index);
  }

  countrySelectionChange(country: CodeDTView): void {
      this.formGroup.patchValue({
          country: country,
      });
  }

  onSpatialIdSelection(country: CodeDTView, fieldToUpdate: string): void {
      switch (fieldToUpdate) {
      case 'legalId':
          this.legalIdentifierSpatialId.patchValue(country);
          break;

      case 'eIDAS':
          this.eIDASSpatialId.patchValue(country);
          break;

      case 'vat':
          this.vatIdentifierSpatialId.patchValue(country);
          break;

      case 'tax':
          this.taxIdentifierSpatialId.patchValue(country);
          break;

      default:
          break;
      }
  }

  contactAddressCountrySelectionChange(country: CodeDTView, index): void {
      const contactAddress = this.getContactAddress(index);
      contactAddress.get('country').patchValue(country);
  }

  locationNUTSSelectionChange(locationNUTS: CodeDTView[]): void {
      this.locationNUTS = locationNUTS;
  }

  onOrganizationCharge($event) {
      this.organizationsListContent = $event.content;
  }

  newEntityClicked(value: Entities | string, event = undefined, isMultiSelect = false): void {
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

  deleteLogo(): void {
      this.logoPreviewURL = null;
      this.logo = null;
      this.base64FromRequest = null;
  }

  /** LOCATION  **/
  /* LOCATION */
  getLocationById(index) {
      return this.location.at(index) as FormGroup;
  }

  addLocationRow(language: string) {
      /* TODO bugfix - addressInterface description and address are sharing control */
      this.location.push(this.createLocationFormGroup(language));
  }

  addLocationsLanguageControls(language: string) {
      this.location.controls.forEach((location) => {
          this.addLocationGroupLanguageControls(language, location as FormGroup);
      });
  }

  addLocationGroupLanguageControls(language: string, locationGroup: FormGroup) {
      const descCtrl = locationGroup.get('description') as FormGroup;
      descCtrl.addControl(language, new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_LONG)]));

      const addCtrl = locationGroup.get('address') as FormGroup;
      addCtrl.addControl(language, new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_LONG)]));
  }

  createLocationFormGroup(language: string): FormGroup {
      const locationGroup = this.generateBaseLocationGroup();
      this.addLocationGroupLanguageControls(language, locationGroup);
      return locationGroup;
  }

  deleteLocationRow(i) {
      this.location.removeAt(i);
  }

  locationCountrySelectionChange(country: CodeDTView, index): void {
      const address = this.getLocationById(index);
      address.get('country').patchValue(country);
  }

  locationAreaSelectionChange(area: CodeDTView[], index): void {
      const address = this.getLocationById(index);
      address.get('area').patchValue(area);
  }

  removeLocationControls(language) {
      this.removeLocationAddressControl(language);
      this.removeLocationDescriptionControl(language);
  }
  removeLocationAddressControl(language) {
      const location = this.location.controls;
      location.forEach((element) => {
          const address = element.get('address') as FormGroup;
          address.removeControl(language);
      });
  }

  removeLocationDescriptionControl(language) {
      const location = this.location.controls;
      location.forEach((element) => {
          const address = element.get('description') as FormGroup;
          address.removeControl(language);
      });
  }

  onAddLocationRow() {
      this.addLocationRow(this.language);
  }

  onDeleteLocationRow(index) {
      this.deleteLocationRow(index);
  }

  onContactPointCountrySelectionChange(country, index: number) {
      this.getContactPointCountry(index).patchValue(country);
  }

  getContactPointCountry(index) {
      return this.contactPoints.at(index).get('country');
  }

  setContactPointForm() {
      const contactPoint = this.editOrganization?.contactPoint;

      contactPoint.forEach((element, index) => {
          if (index > 0) {
              this.addContactPoint();
          }

          /* multilang */
          if (element?.description?.contents[0]?.content) {
              this.contactPoints.at(index).get('description').patchValue(element?.description?.contents[0]?.content);
          }
          /* multilang */
          if (element?.address[0]?.fullAddress?.contents[0]?.content) {
              this.contactPoints.at(index).get('address').patchValue(element?.address[0].fullAddress.contents[0].content);
          }

          if (element?.address[0]?.countryCode) {
              this.contactPoints.at(index).get('country').patchValue(element?.address[0].countryCode);
          }

          if (element?.emailAddress[0]?.id) {
              this.contactPoints.at(index).get('email').patchValue(element?.emailAddress[0]?.id);
          }
          if (element?.contactForm[0]?.contentUrl) {
              this.contactPoints.at(index).get('contactForm').patchValue(element?.contactForm[0]?.contentUrl);
          }
          if (element?.phone[0]?.phoneNumber) {
              this.contactPoints.at(index).get('phone').patchValue(element?.phone[0]?.phoneNumber);
          }
      });
  }

  createLocationRequiredValidator(): ValidatorFn {
      return (formArray: FormArray): ValidationErrors | null => {
          const locations = this.formsService.getLocationDCView(formArray);
          if (locations) {
              return null;
          } else {
              return { required: true };
          }
      };
  }

  setEditOrganizationFormData(data) {
      this.modalTitle = this.translateService.instant('credential-builder.organizations-tab.editOrganization');
      this.editOrganization = data;
      this.editOrganizationOid = data.oid;
      this.languages = this.editOrganization.additionalInfo.languages;
      this.language = this.editOrganization.defaultLanguage;
      this.defaultLanguage = this.language;
      this.selectedLanguages = this.multilingualService.setUsedLanguages(this.editOrganization.additionalInfo.languages, this.defaultLanguage);
      this.extractIdentifiers();
      this.setForm();
      this.getLogo();
      this.extractGroups();
      this.setLastUpdateDate(data);
      this.setAdditionalInformation(data);
      /* this.setLegalIdentifiers(data); */
  }

  setNewOrganizationFormdata() {
      this.modalTitle = this.translateService.instant('credential-builder.organizations-tab.createOrganization');
      this.language = this.language || this.translateService.currentLang;
      this.defaultLanguage = this.language;
      this.selectedLanguages.push({
          code: this.language,
          label: this.language,
      });
      this.addLocationRow(this.language);
      this.languages = [this.language];
      this.addNewMemberGroupRow();
      this.addNewLanguageControl(this.language);
  }

  updateOrganizationAndLogo() {
      if (!this.isNewLogo) {
          this.logo = this.b64toFile(
              `data:image/${this.extensionOfRequestBackground};base64,${this.base64FromRequest}`,
              `background-img.${this.extensionOfRequestBackground}`
          );
      }
      this.organizationBody.oid = this.editOrganization.oid;
      this.isLoading = true;

      const template$ = this.api.updateOrganization(this.organizationBody, this.translateService.currentLang).pipe(take(1));
      const background$ = template$.pipe(switchMap((templateDetails) => this.api.addLogo(templateDetails.oid, this.logo).pipe(take(1))));

      background$.subscribe({
          next: (organization) => {
              this.newEntity = organization;
              this.handleSaveNavigation();
              this.showNotification();
              this.isLoading = false;
          },
          error: () => {
              if (this.isModal) {
                  this.onSaveEvent.emit(null);
              } else {
                  this.closeForm();
              }
              this.isLoading = false;
          },
      });
  }

  createOrganizationAndLogo() {
      if (!this.isNewLogo) {
          this.logo = this.b64toFile(
              `data:image/${this.extensionOfRequestBackground};base64,${this.base64FromRequest}`,
              `background-img.${this.extensionOfRequestBackground}`
          );
      }

      this.isLoading = true;

      const template$ = this.api.createOrganization(this.organizationBody, this.translateService.currentLang).pipe(take(1));
      const background$ = template$.pipe(switchMap((templateDetails) => this.api.addLogo(templateDetails.oid, this.logo).pipe(take(1))));

      background$.subscribe({
          next: (organization) => {
              this.newEntity = organization;
              this.handleSaveNavigation();
              this.showNotification();
              this.isLoading = false;
          },
          error: () => {
              if (this.isModal) {
                  this.onSaveEvent.emit(null);
              } else {
                  this.closeForm();
              }
              this.isLoading = false;
          },
      });
  }

  public doCheckGroupNameRequired(formGroup: FormGroup) {
      const nameGroup = formGroup.get('name') as FormGroup;
      if (formGroup.get('email').value) {
          Object.keys(nameGroup.controls).forEach((lang) => {
              nameGroup.get(lang).addValidators([Validators.required]);
              nameGroup.get(lang).updateValueAndValidity();
          });
      } else {
          Object.keys(nameGroup.controls).forEach((lang) => {
              nameGroup.get(lang).removeValidators([Validators.required]);
              nameGroup.get(lang).updateValueAndValidity();
          });
      }
  }

  private createMemberGroupRow(languages: string[]): FormGroup {
      const nameGroup = new FormGroup({});
      languages.forEach((lang) => {
          nameGroup.addControl(lang, new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT), noSpaceValidator]));
      });

      const newGroupRow = new FormGroup({
          email: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT), Validators.email, noSpaceValidator]),
          name: nameGroup,
      });
      return newGroupRow;
  }

  private setIdentifierErrorsObservable() {
      this.vatIdentifier.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((res) => {
          if (res.content !== null && res.spatialId === null) {
              this.vatIdentifierSpatialId.markAsTouched();
          }
          if (res.content === null && res.spatialId !== null) {
              this.vatIdentifierContent.markAsTouched();
          }
      });
      this.taxIdentifier.valueChanges.pipe(takeUntil(this.destroy$)).subscribe((res) => {
          if (res.content !== null && res.spatialId === null) {
              this.taxIdentifierSpatialId.markAsTouched();
          }
          if (res.content === null && res.spatialId !== null) {
              this.taxIdentifierContent.markAsTouched();
          }
      });
  }

  private saveForm(): void {
      if (this.formGroup.invalid) {
          this.formGroup.markAllAsTouched();
          this.isLoading = false;
          this.messageBoxFormError.openMessageBox();
      } else {
          this.setOrganizationBody();
          this.isSaveDisabled = true;

          if (this.editOrganizationOid && (this.logo || this.base64FromRequest)) {
              this.updateOrganizationAndLogo();
              return;
          }

          if (this.editOrganizationOid) {
              this.updateOrganization();
              return;
          }

          if (!this.editOrganizationOid && (this.logo || this.base64FromRequest)) {
              this.createOrganizationAndLogo();
              return;
          }

          if (!this.editOrganizationOid) {
              this.createOrganization();
          }
      }
  }

  private gotoEntity(oid: number = null) {
      this.openEntityModal[this.entityWillBeOpened] = this.modalsService.openModal(this.modalTitle, oid);
      this.setOrganizationBody();
  }

  private getLogo(): void {
      if (this.editOrganization.logo && this.editOrganization.logo.content) {
          this.isLogoNotAvailable = false;
          this.logoPreviewURL = 'data:image/png;base64,' + this.editOrganization.logo.content;
          this.base64FromRequest = this.editOrganization.logo.content;
          const contentTypeSplitted = this.editOrganization.logo.contentType.uri.split('/');
          this.extensionOfRequestBackground = contentTypeSplitted[contentTypeSplitted.length - 1];
      } else if (this.editOrganization.logo && !this.editOrganization.logo.content && this.editOrganization.logo.contentUrl) {
          this.isLogoNotAvailable = true;
          this.logoPreviewURL = 'assets/images/image-not-found.svg';
      }
  }

  private addNewLanguageControl(language: string): void {
      this.addPreferredNameControl(language);
      this.addAlternativeNameControl(language);
      this.addAdditionalInformationControl(language);
      this.addLocationsLanguageControls(language);
      this.addLanguageToExistingMemberGroupRows(language);
  }

  private addPreferredNameControl(language: string, value: string = null): void {
      this.preferredName.addControl(
          language,
          new FormControl(value, [Validators.maxLength(Constants.MAX_LENGTH_LONG), Validators.required, noSpaceValidator])
      );
  }

  private addAlternativeNameControl(language: string, value: string = null): void {
      this.alternativeName.addControl(language, new FormControl(value, Validators.maxLength(Constants.MAX_LENGTH_LONG)));
  }

  private addAdditionalInformationControl(language: string, value = ''): void {
      this.additionalInformation.addControl(language, new FormControl(value, Validators.maxLength(Constants.MAX_LENGTH_LONG)));
  }

  private createOrganization(): void {
      this.isLoading = true;
      this.api
          .createOrganization(this.organizationBody, this.translateService.currentLang)
          .pipe(take(1))
          .subscribe({
              next: (organization: OrganizationSpecView) => {
                  this.newEntity = organization;
                  this.handleSaveNavigation();
                  this.showNotification();
                  this.isLoading = false;
              },
              error: () => {
                  if (this.isModal) {
                      this.onSaveEvent.emit(null);
                  } else {
                      this.closeForm();
                  }
                  this.isLoading = false;
              },
          });
  }

  private updateOrganization(): void {
      this.organizationBody.oid = this.editOrganization.oid;
      this.isLoading = true;

      this.api
          .updateOrganization(this.organizationBody, this.translateService.currentLang)
          .pipe(take(1))
          .subscribe({
              next: (organization) => {
                  this.newEntity = organization;
                  this.handleSaveNavigation();
                  this.showNotification();
                  this.isLoading = false;
              },
              error: () => {
                  if (this.isModal) {
                      this.onSaveEvent.emit(null);
                  } else {
                      this.closeForm();
                  }
                  this.isLoading = false;
              },
          });
  }

  private handleSaveNavigation() {
      if (this.isModal) {
      // If it is a modal, just emit event when saving is done
          this.onSaveEvent.emit(this.newEntity);
      } else {
          this.credentialBuilderService.setOcbTabSelected(5);
          this.router.navigateByUrl('credential-builder');
      }
  }

  private b64toFile(dataurl, filename) {
      const arr = dataurl.split(','),
          mime = arr[0].match(/:(.*?);/)[1],
          bstr = atob(arr[1]);
      let n = bstr.length;
      const u8arr = new Uint8Array(n);
      while (n--) {
          u8arr[n] = bstr.charCodeAt(n);
      }
      return new File([u8arr], filename, { type: mime });
  }

  private showNotification() {
      if (this.editOrganization) {
          this.notificationService.showNotification({
              severity: 'success',
              summary: this.translateService.instant('common.edit'),
              detail: this.translateService.instant('credential-builder.operationSuccessful'),
          });
      } else {
          this.notificationService.showNotification({
              severity: 'success',
              summary: this.translateService.instant('common.create'),
              detail: this.translateService.instant('credential-builder.operationSuccessful'),
          });
      }
  }

  private extractIdentifiers(): void {
      if (this.editOrganization.identifier) {
          this.editOrganization.identifier.forEach((identifier: IdentifierDTView) => {
              this.identifiers.push(
                  new FormGroup(
                      {
                          id: new FormControl(_get(identifier, 'notation', null), Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
                          identifierSchemeAgencyName: new FormControl(_get(identifier, 'schemeName', null), Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)),
                      },
                      identifierValidator
                  )
              );
          });
          if (this.editOrganization.identifier.length > 0) {
              this.identifiers.removeAt(0);
          }
      }
  }

  private extractGroups(): void {
      const groups = this.editOrganization.groupMemberOf;
      if (groups && groups.length > 0) {
          this.memberGroups.clear();
          groups.forEach((identifier: GroupDCView) => {
              const email = identifier.contactPoint[0]?.emailAddress[0]?.id;
              const nameContents: ContentDTView[] = identifier.prefLabel.contents;

              const nameGroup = new FormGroup({});
              nameContents.forEach((content) =>
                  nameGroup.addControl(
                      content.language,
                      new FormControl(content.content, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT), Validators.required, noSpaceValidator])
                  )
              );
              this.memberGroups.push(
                  new FormGroup({
                      name: nameGroup,
                      email: new FormControl(email, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT), Validators.email]),
                  })
              );
          });
      } else {
          this.addNewMemberGroupRow();
      }
  }

  private setOrganizationBody(): void {
      this.organizationBody = {
          label: this.label.value,
          defaultLanguage: this.defaultLanguage,
          legalName: this.getPreferredName(),
          altLabel: this.getAlternativeName(),
          registration: this.getLegalIdentifier(this.legalIdentifier),
          vatIdentifier: this.getLegalIdentifierArray(this.vatIdentifier),
          taxIdentifier: this.getLegalIdentifierArray(this.taxIdentifier),
          identifier: this.getOtherIdentifiers(),
          homePage: this.credentialBuilderService.getHomePage(this.homePage.value),
          contactPoint: this.setContactPointPayload(),
          location: this.formsService.getLocationDCView(this.location) ? [this.formsService.getLocationDCView(this.location)] : [],
          additionalInfo: {
              languages: this.multilingualService.getUsedLanguages(this.selectedLanguages),
          },
          additionalNote: this.setPayloadLabel(this.additionalInformation.value),
          groupMemberOf: this.setGroupsPayload(this.memberGroups.value),
          eidasIdentifier: this.getLegalIdentifier(this.eIDAS),
          modified: this.setModifiedDatePayload(),
      };
  }

  private setModifiedDatePayload(): string {
      const date = this.dateFormatService.dateToStringDateTime(this.lastUpdateDate.value);
      return date ?? null;
  }

  private setGroupsPayload(groups): GroupDCView[] {
      const finalGroups: GroupDCView[] = [];
      groups.forEach((element) => {
          const nameContents: ContentDTView[] = [];
          Object.keys(element.name).forEach((lang) => {
              const localizedContent = element.name[lang];
              const content: ContentDTView = {};
              if (localizedContent) {
                  content.content = localizedContent;
                  content.language = lang;
                  nameContents.push(content);
              }
          });
          const group: GroupDCView = {
              prefLabel: {
                  contents: nameContents,
              },
              contactPoint: [
                  {
                      emailAddress: [{ id: element.email }],
                  },
              ],
          };
          if (nameContents.length > 0) {
              finalGroups.push(group);
          }
      });
      return finalGroups;
  }

  private setContactPointPayload(): Array<ContactPointDCView> {
      const contactPoints: ContactPointDCView[] = [];
      const formContactPoints = this.contactPoints.value;

      formContactPoints.forEach((element) => {
          const contactPoint: ContactPointDCView = {};

          const email = element.email ?? null;
          const phone = element.phone ?? null;
          const contactFormUrl = element.contactForm ?? null;
          const addressDescription = element.description ?? null;
          const address = element.address ?? null;
          const country = element.country ?? null;

          if (email) {
              contactPoint.emailAddress = [{ id: email }];
          }

          if (phone) {
              contactPoint.phone = [{ phoneNumber: phone }];
          }

          if (contactFormUrl) {
              contactPoint.contactForm = [{ contentUrl: contactFormUrl }];
          }

          if (addressDescription) {
              contactPoint.description = {
                  contents: [
                      {
                          content: addressDescription,
                          language: this.language,
                          format: 'string',
                      },
                  ],
              };
          }

          if (address && country) {
              const tempAddress = {
                  content: address,
                  language: this.language,
                  format: 'string',
              };
              const addressPayload = {
                  /* fullAddress: this.getFullAddress(address), */
                  fullAddress: {
                      contents: [tempAddress],
                  },
                  countryCode: country,
              };
              contactPoint.address = [addressPayload];
          }

          if (email || phone || contactFormUrl || addressDescription || (address && country)) {
              contactPoints.push(contactPoint);
          }
      });

      return contactPoints;
  }

  private getPreferredName(): TextDTView {
      const preferredName = {
          contents: this.multilingualService.formToView(this.preferredName.value),
      };
      return preferredName;
  }

  private setPayloadLabel(value): TextDTView[] {
      const valueContent: ContentDTView[] = this.multilingualService.formToView(value);

      if (valueContent && valueContent.length > 0) {
          return [
              {
                  contents: valueContent,
              },
          ];
      } else {
          return [];
      }
  }

  private getAlternativeName(): TextDTView {
      let alternativeName: TextDTView[] = null;
      const alternativeNameContents: ContentDTView[] = this.multilingualService.formToView(this.alternativeName.value);
      if (alternativeNameContents.length > 0) {
          alternativeName = [
              {
                  contents: alternativeNameContents,
              },
          ];
          return alternativeName.pop();
      } else {
          return null;
      }
  }

  private getLegalIdentifier(identifier: FormGroup): LegalIdentifierDTView {
      let legalIdentifier: LegalIdentifierDTView = null;
      const spatialId = identifier.controls.spatialId.value;
      const content = identifier.controls.content.value;
      if (identifier && spatialId && content) {
          legalIdentifier = {
              notation: content,
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
                  notation: _get(identifier, 'id', null),
                  schemeName: _get(identifier, 'identifierSchemeAgencyName', null),
              });
          }
      });
      return sentIdentifiers.length > 0 ? sentIdentifiers : null;
  }

  private setForm(): void {
      this.addControlsFromView();
      this.locationNUTS = _get(this.editOrganization, 'location[0].spatialCode', null);
      this.formGroup.patchValue({
          label: this.editOrganization.label,

          legalIdentifier: {
              content: _get(this.editOrganization, 'registration.notation', null),
              spatialId: _get(this.editOrganization, 'registration.spatialId', null),
          },
          eIDAS: {
              content: _get(this.editOrganization, 'eidasIdentifier.notation', null),
              spatialId: _get(this.editOrganization, 'eidasIdentifier.spatialId', null),
          },
          vatIdentifier: {
              content: _get(this.editOrganization, 'vatIdentifier[0].notation', null),
              spatialId: _get(this.editOrganization, 'vatIdentifier[0].spatialId', null),
          },
          taxIdentifier: {
              content: _get(this.editOrganization, 'taxIdentifier[0].notation', null),
              spatialId: _get(this.editOrganization, 'taxIdentifier[0].spatialId', null),
          },
          email: _get(this.editOrganization, 'contactPoint[0].emailAddress[0].id', null),
          phone: _get(this.editOrganization, 'contactPoint[0].phone[0].phoneNumber', null),
          contactForm: _get(this.editOrganization, 'contactPoint[0].contactForm[0].contentUrl', null),
          homePage: _get(this.editOrganization, 'homePage[0].contentUrl', null),
      });

      this.setContactPointForm();
      this.addLocationControls(_get(this.editOrganization, 'location[0]', null));
      this.isLoading = false;
  }

  private generateBaseLocationGroup(): FormGroup {
      return this.fb.group({
          description: new FormGroup({}),
          address: new FormGroup({}),
          country: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
          area: new FormControl(null, [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]),
      });
  }

  private addLocationControls(location: LocationDCView): void {
      if (location) {
          const locationGroup = this.generateBaseLocationGroup();
          const descCtrl = locationGroup.get('description') as FormGroup;
          const addrCtrl = locationGroup.get('address') as FormGroup;

          const fullAddressContents = location.address[0]?.fullAddress?.contents;
          const descriptionContents = location.description?.contents;

          this.selectedLanguages.forEach((lang) => {
              const langCode = lang.code.toLowerCase();
              const fullAddressLangContent = fullAddressContents ? this.multilingualService.getContentFromLanguage(langCode, fullAddressContents) : null;
              const descriptionLangContent = descriptionContents ? this.multilingualService.getContentFromLanguage(langCode, descriptionContents) : null;
              addrCtrl.addControl(langCode, new FormControl(fullAddressLangContent, [Validators.maxLength(Constants.MAX_LENGTH_LONG)]));
              descCtrl.addControl(langCode, new FormControl(descriptionLangContent, [Validators.maxLength(Constants.MAX_LENGTH_LONG)]));
          });

          locationGroup.get('area').patchValue(location.spatialCode[0]);
          locationGroup.get('country').patchValue(location.address[0]?.countryCode);

          this.location.push(locationGroup);
      } else {
          this.addLocationRow(this.language);
      }
  }

  private addControlsFromView(): void {
      this.languages.forEach((language: string) => {
          this.addPreferredNameControl(
              language,
              this.multilingualService.getContentFromLanguage(language, _get(this.editOrganization, 'legalName.contents', []))
          );

          this.addAlternativeNameControl(
              language,
              this.multilingualService.getContentFromLanguage(language, _get(this.editOrganization, 'altLabel.contents', []))
          );

          this.addAdditionalInformationControl(
              language,
              this.multilingualService.getContentFromLanguage(language, _get(this.editOrganization, 'additionalNote[0].contents', []))
          );
      });
  }

  private loadBreadcrumb() {
      this.parts = [
          new UxLink({
              label: this.translateService.instant('breadcrumb.digitallySealedCredentials'),
              url: '/home',
          }),
          new UxLink({
              label: this.translateService.instant('breadcrumb.credentialBuilder'),
              url: '/credential-builder',
          }),
      ];
  }

  private setLastUpdateDate(organization: OrganizationSpecView) {
      const updateDate = organization?.modified;
      if (updateDate && updateDate !== '') {
          const date = new Date(updateDate);
          this.lastUpdateDate.patchValue(date);
      }
  }

  private setAdditionalInformation(organization) {
      const note = organization?.note;
      if (note && note !== '') {
          this.additionalInformation.patchValue(note);
      }
  }
}
