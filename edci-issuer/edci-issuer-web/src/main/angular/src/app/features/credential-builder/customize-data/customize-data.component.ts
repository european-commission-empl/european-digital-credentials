import {
    Component,
    ElementRef,
    OnInit,
    ViewChild,
    Output,
    OnDestroy,
    EventEmitter,
    ViewEncapsulation
} from '@angular/core';
import { EuiDialogComponent } from '@eui/components/eui-dialog';
import { Router } from '@angular/router';
import { FormArray, FormControl, FormGroup, Validators, FormBuilder } from '@angular/forms';
import { UxLink } from '@eui/core';
import { IssuerService } from '@services/issuer.service';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    CodeDTView,
    CredentialView,
    CustomizableFieldView,
    CustomizableInstanceFieldView,
    CustomizableInstanceRelationView,
    CustomizableInstanceSpecView,
    CustomizableInstanceView,
    CustomizableSpecView,
    CustomizedEntityView,
    CustomizedFieldView,
    CustomizedRecipientView,
    CustomizedRelationView,
    V1Service
} from '@shared/swagger';
import { environment } from '@environments/environment';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { UxAppShellService, UxLanguage } from '@eui/core';
import { Location } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { commaSepEmail } from '@shared/validators/email-comma.validator';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { Constants } from '@shared/constants';
import { CustomizedRecipientsView } from '@shared/swagger/model/customizedRecipientsView';
import { dateValidator } from '@shared/validators/date-validator';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { NotificationService, OKNotification } from '@services/error.service';

export enum ValidationsEnum {
    LENGTH255 = 'maxLength(255)',
    LENGTH4000 = 'maxLength(4000)',
    DATEFORMAT = 'dateValidator',
    DATELOCALFORMAT = 'dateValidator',
    MANDATORY = 'required',
    MANDATORYIF = 'MandatoryIf',
    MANDATORYIFNOT = 'MandatoryIfNot',
    EMAIL = 'commaSepEmail',
    NUMERIC = 'pattern("^[0-9]*$")'
}

export interface ValidationsMandatoryErrors {
    validatorType: string;
    validation: string;
    error: number;
}

export interface ValidationIncluded {
    validation: string;
    fieldId: string;
    entityId: string;
    validationGroup: string;
}
export interface MandatoryValidations {
    mandatory: string;
    validations: ValidationIncluded[];
}

export interface RelationsGroups {
    entry: string;
    groupId: number;
    groupLabel: string;
    relations: CustomizableInstanceRelationView[];
}

@Component({
    selector: 'edci-customize-data',
    templateUrl: './customize-data.component.html',
    styleUrls: ['./customize-data.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class CustomizeDataComponent implements OnInit, OnDestroy {
    @ViewChild('inputFile') inputFile: ElementRef;
    @ViewChild('issuingDialogModal') issuingDialogModal: EuiDialogComponent;
    @ViewChild('uploadDialog') uploadDialog: EuiDialogComponent;
    @ViewChild('testDialog') testDialog: EuiDialogComponent;
    @ViewChild('messageBoxFormError') messageBoxFormError: EuiMessageBoxComponent;

    @Output() onBack: EventEmitter<boolean> = new EventEmitter();
    @Output() onUpload: EventEmitter<boolean> = new EventEmitter();

    languages: UxLanguage[] = [];

    parts: UxLink[] = [];
    urlActivated: string;
    selectedLanguage: string = this.translateService.currentLang;
    concentText: string = environment.concentText;
    customizableDataArchitect: CustomizableSpecView;
    customizedData: CustomizableSpecView = {
        customizableEntityViews: []
    };

    issuingModeModal: boolean;

    headerTitlte: string = '';
    customizedView: CustomizableInstanceSpecView;
    issuingOid: number = -1;
    modalState: string = 'showCustomizableData';
    consentForm: FormGroup = new FormGroup({
        consentCheckBox: new FormControl(false, Validators.required),
    });

    loading: boolean = false;

    recipientFormGroup: FormGroup;
    recipientsGroup: FormGroup = new FormGroup({
        recipients: new FormArray([]),
    });
    fieldsFormControl = [];
    relationsFormControl = [];
    relationsGroups: RelationsGroups[] = [];
    sendData = [];
    cerrado: boolean = false;

    uploads: Array<File> = [];
    error: boolean;
    errorMessage: string;
    errorOfValidation: boolean;
    errorOfValidationMessage: string;
    inputFileIsEnabled: boolean;
    isLoadingRecipientTemplate: boolean;
    deletedList: Array<File> = [];

    mandatoryValidations: MandatoryValidations[] = [];
    mandatoryValidationsErrors: ValidationsMandatoryErrors[] = [];

    destroy$: Subject<boolean> = new Subject<boolean>();

    get recipients(): FormArray {
        return this.recipientsGroup.get('recipients') as FormArray;
    }

    get consentCheckBox() {
        return this.consentForm.get('consentCheckBox');
    }

    constructor(
        private issuerService: IssuerService,
        private translateService: TranslateService,
        private api: V1Service,
        private uxService: UxAppShellService,
        private credentialBuilderService: CredentialBuilderService,
        private fb: FormBuilder,
        private notifService: NotificationService,
        private router: Router
    ) {
        /*this.issuerService.breadcrumbSubject.subscribe((url: string) => {
            this.urlActivated = url;
            this.loadBreadcrumb();
        });*/
        this.translateService.onLangChange.subscribe((event: LangChangeEvent) => {
            this.selectedLanguage = event.lang;
            // this.loadBreadcrumb();
        });

        this.credentialBuilderService.recipientAddObservable.pipe(takeUntil(this.destroy$)).subscribe(res => {
            this.mandatoryValidations.forEach(validation => {
                validation.validations.forEach(v => {
                    this.setSubscriber(v);
                });
            });
        });
    }

    setSubscriber(validation) {
        let recipientIndex = 0;
        this.recipients.controls.forEach((recipient: FormGroup) => {
            let control: FormControl =
            recipient.controls['entry-' + validation.entityId]['controls'][validation.entityId + '-' + validation.fieldId];
            control.valueChanges.subscribe(res1 => {
                if (validation.validationGroup.toLocaleLowerCase() === 'mandatoryif') {
                    this.checkMandatoryIf(recipientIndex, validation);
                } else {
                    switch (validation.validation.toLocaleLowerCase()) {
                        case 'contactpoint':
                            this.setControlEmailAndWalletError(control, this.checkValidationEmail(recipientIndex, validation));
                            break;
                        default:
                            break;
                    }
                }
            });
            recipientIndex++;
        });
    }

    setControlErrors(control: FormControl, errors: any) {
        if (errors.length > 0) {
            errors.forEach(error => {
                control.setErrors(error);
            });
        } else {
            control.setErrors(null);
        }
    }

    checkMandatoryIf(recipientId, validation) {
        let controlPrimary: FormControl =
        this.recipients.controls[recipientId - 1]['controls']['entry-' + validation.entityId]
        .controls[validation.entityId + '-' + validation.fieldId];
        let controlSecondary: FormControl =
        this.getOtherControl(this.recipients.controls[recipientId - 1]['controls'], validation, controlPrimary);
        controlPrimary.markAsTouched();
        controlSecondary.markAsTouched();
        if (controlPrimary.value === '' && controlSecondary.value === '') {
            this.setControlErrors(controlPrimary, []);
            this.setControlErrors(controlSecondary, []);
        } else if (controlPrimary.value === '' && controlSecondary.value !== '') {
            this.setControlErrors(controlPrimary, [{ required: true }]);
        } else if (controlPrimary.value !== '' && controlSecondary.value === '') {
            this.setControlErrors(controlSecondary, [{ required: true }]);
        }
    }

    setControlEmailAndWalletError(control: FormControl, emailAndWalletError) {
        if (emailAndWalletError) {
            if (control.hasError('email')) {
                this.setControlErrors(control, [{ email: true }, { emailWalletError: true }]);
            } else {
                this.setControlErrors(control, [{ emailWalletError: true }]);
            }
        } else {
            if (control.hasError('email')) {
                this.setControlErrors(control, [{ email: true }]);
            } else {
                this.setControlErrors(control, []);
            }
        }
    }

    getOtherControl(controls, validation, controlPrimary) {
        let validationFound = this.mandatoryValidations
        .find(mandatoryValidation => mandatoryValidation.mandatory === validation.validationGroup)
        .validations.filter(mv => {
            return mv.validationGroup === validation.validationGroup &&
                mv.entityId === validation.entityId &&
                mv.validation === validation.validation;
        });
        let controlsArray = [];
        validationFound.forEach(v => {
            let controlFound = controls['entry-' + v.entityId].controls[v.entityId + '-' + v.fieldId];
            controlsArray.push(controlFound);
        });
        return controlsArray.filter(c => c !== controlPrimary)[0];
    }

    checkValidationEmail(recipientId, validation) {
        let error: boolean = false;
        let controlPrimary: FormControl =
        this.recipients.controls[recipientId - 1]['controls']['entry-' + validation.entityId]
        .controls[validation.entityId + '-' + validation.fieldId];
        let controlSecondary: FormControl =
        this.getOtherControl(this.recipients.controls[recipientId - 1]['controls'], validation, controlPrimary);
        if (controlPrimary.value === '') {
            if (controlSecondary.value === '') {
                this.setControlEmailAndWalletError(controlSecondary, true);
                error = true;
            } else {
                error = false;
            }
        }
        if (!error) {
            this.setControlEmailAndWalletError(controlSecondary, false);
        }
        return error;
    }

    ngOnInit() {
        this.loading = true;
        this.modalState = '';
        this.credentialBuilderService.issuingModalStateObservable.pipe(takeUntil(this.destroy$)).subscribe((res) => {
            if (res === 'loading') {
                this.loading = true;
            } else {
                this.modalState = res;
                this.loading = false;
            }
        });
        this.credentialBuilderService.oidSelectedObservable.pipe(takeUntil(this.destroy$)).subscribe((oid: any) => {
            this.issuingOid = oid;
        });
        this.credentialBuilderService.closeIssuingModalObservable.pipe(takeUntil(this.destroy$)).subscribe((close: any) => {
            if (close === 'close' || close === 'back') {
                this.resetComponent();
                this.credentialBuilderService.setIssuingModalState('showCustomizableData');
                this.credentialBuilderService.setIssuingModalHeaderTitle(this.translateService.instant('credential-builder.customise-data.title'));
            } else if (close === 'sendForm') {
                this.loading = true;
                this.sendForm();
            }
        });
        this.loadJSON();
        this.loadBreadcrumb();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    public closeIssuingModeModal(byError: boolean = false): void {
        if (!this.isLoadingRecipientTemplate || byError) {
            this.isLoadingRecipientTemplate = false;
            this.issuingModeModal = false;
            this.modalState = 'showCustomizableData';
            this.uxService.closeModal('issuingModeModal');
        }
    }

    public openDynamicModal(): void {
        this.inputFileIsEnabled = true;
        this.onUpload.emit(true);
    }

    public onAccept(dialog: string): void {
        this[dialog].closeDialog();
        this.openDialog();
    }

    public openIssuingModeModal(): void {
        this.isLoadingRecipientTemplate = false;
        this.issuingModeModal = true;
        this.uxService.openModal('issuingModeModal');
    }

    public openDialog() {
        this.openIssuingModeModal();
        return this.inputFile.nativeElement.click();
    }

    public onDismiss(dialogClosed): void {
        this[dialogClosed].closeDialog();
    }

    public checkRelated(field: CustomizableFieldView, position) {
        const findRelation = (relation) => field.fieldPath === relation.relPath;
        this.addCheck(position, this.customizableDataArchitect.customizableEntityViews[position].relations.findIndex(findRelation), true);
    }

    public addCheck(positionField, i, isRelation: boolean = false) {
        if (!isRelation) {
            let field = this.customizableDataArchitect.customizableEntityViews[positionField].fields[i];
            if (this.customizedData.customizableEntityViews[positionField].fields.find((f) => f.label === field.label) === undefined) {
                this.customizedData.customizableEntityViews[positionField].fields.push(field);
                this.sortFields('customizedData', positionField);
            } else {
                let fieldsAux = this.customizedData.customizableEntityViews[positionField].fields.filter(f => {
                    return f.label !== field.label;
                });
                this.customizedData.customizableEntityViews[positionField].fields = fieldsAux;
            }
            if (field.relationDependant) {
                this.checkRelated(field, positionField);
            }
        } else {
            let relation = this.customizableDataArchitect.customizableEntityViews[positionField].relations[i];
            let entityFound = this.customizedData.customizableEntityViews[positionField].relations.find((f) => f.label === relation.label);
            if (entityFound === undefined) {
                this.customizedData.customizableEntityViews[positionField].relations.push(relation);
                this.sortFields('customizedData', positionField, false);
            } else {
                let relationAux = this.customizedData.customizableEntityViews[positionField].relations.filter(f => {
                    return f.label !== relation.label;
                });
                this.customizedData.customizableEntityViews[positionField].relations = relationAux;
            }
        }
    }

    public resetComponent() {
        this.concentText = environment.concentText;
        this.customizedData = {
            customizableEntityViews: []
        };
        this.customizableDataArchitect = {};
        this.customizedView = {};
        this.issuingOid = -1;
        this.modalState = 'showCustomizableData';
        this.loading = false;
        this.consentForm = new FormGroup({
            consentCheckBox: new FormControl(false),
        });
        this.recipientFormGroup = new FormGroup({});
        this.recipientsGroup = new FormGroup({});
        this.fieldsFormControl = [];
        this.relationsFormControl = [];
        this.sendData = [];
        this.credentialBuilderService.oidSelectedObservable.subscribe((oid: any) => {
            this.issuingOid = oid;
        });
        this.mandatoryValidations = [];
        this.mandatoryValidationsErrors = [];
        this.loadJSON();
        this.loadBreadcrumb();
    }

    public dataBrowser() {
        this.loading = true;
        this.getForm();
    }

    public downloadExcel() {
        this.loading = true;
        this.issuerService.isExcelDownloadedSubject.subscribe(() => {
            this.modalState = 'showCustomizableData';
            this.loading = false;
        });
        this.issuerService.downloadCustomizableTemplate(this.issuingOid, this.selectedLanguage, this.customizedData);
    }

    public addRecipient(): void {
        this.recipients.controls.push(this.createVoidRecipient());
        this.credentialBuilderService.setRecipientAdd(true);
    }

    public removeRecipient(position: number): void {
        this.recipients.removeAt(position);
    }

    public addRelation(recipient, block, field) {
        let relationLabel = `${block.frontId}-${field.frontId}`;
        let estado =
        this.recipients.controls[recipient]['controls'][`entry-${block.frontId}`].controls[relationLabel].value;
        this.recipients.controls[recipient]['controls'][`entry-${block.frontId}`].controls[relationLabel].setValue(!estado);
    }

    public controlledListSelected(block: CustomizableInstanceView, field: CustomizableInstanceFieldView, index: number, value: any) {
        if (value !== null) {
            let recipient: FormControl =
            this.recipients.controls[index]['controls']['entry-' + block.frontId].controls[block.frontId + '-' + field.frontId];
            if (recipient !== undefined) {
                recipient.setValue(value.uri);
            }
        }
    }

    public sendForm() {
        let validForm = this.checkValidForm();
        if (validForm && this.consentForm.controls['consentCheckBox'].value) {
            let recipientsData: CustomizedRecipientsView = {
                recipients: []
            };
            this.recipients.controls.forEach((recipient: FormGroup) => {
                let recipientData: CustomizedRecipientView = {
                    entities: []
                };
                Object.keys(recipient.controls).forEach((entry: any) => {
                    let entityData: CustomizedEntityView = {
                        fields: [],
                        relations: []
                    };
                    Object.keys(recipient.controls[entry]['controls']).forEach(field => {
                        let ids = field.split('-');
                        let entityFrontId = ids[0];
                        let fieldFrontId = ids[1];
                        let foundCamp = this.customizedView.customizableInstanceViews.find(f => f.frontId === entityFrontId);
                        if (foundCamp !== undefined) {
                            if (foundCamp.relations !== undefined && foundCamp.relations.length > 0) {
                                let relation = foundCamp.relations.find(f => f.frontId === fieldFrontId);
                                if (relation !== undefined) {
                                    let relationData: CustomizedRelationView = {
                                        relPathIdentifier: relation.relPath,
                                        included: recipient.controls[entry]['controls'][field].value
                                    };
                                    entityData.relations.push(relationData);
                                }
                            }
                            if (foundCamp.fields !== undefined && foundCamp.fields.length > 0) {
                                if (recipient.controls[entry]['controls'][field].value !== '') {
                                    let fieldFound = foundCamp.fields.find(f => f.frontId === fieldFrontId);
                                    if (fieldFound !== undefined) {
                                        let fieldData: CustomizedFieldView = {
                                            fieldPathIdentifier: fieldFound.fieldPath,
                                            value: recipient.controls[entry]['controls'][field].value
                                        };
                                        entityData.fields.push(fieldData);
                                    }
                                }
                            }
                        }
                    });
                    recipientData.entities.push(entityData);
                });
                recipientsData.recipients.push(recipientData);
            });
            this.api.issueCredentialsFromRecipientsForm(recipientsData, this.selectedLanguage)
            .pipe(takeUntil(this.destroy$))
            .subscribe((res) => {
                if (res.valid) {
                    this.issuerService.setCredentials(
                        <CredentialView[]>res.credentials
                    );
                    this.router.navigate(['/create/overview']);
                    this.credentialBuilderService.setCloseIssuingModal('close');
                    this.loading = false;
                }
            });
        } else if (validForm === false || !this.consentForm.controls['consentCheckBox'].value) {
            this.messageBoxFormError.openMessageBox();
            this.loading = false;
        }
    }

    public closeMessage(message: string) {
        this.messageBoxFormError.closeMessageBox();
    }

    private checkValidForm() {
        let valids = 0;
        this.mandatoryValidationsErrors = [];
        this.mandatoryValidator();
        if (this.recipients !== null) {
            this.recipients.controls.forEach((control: FormGroup) => {
                if (control.valid) {
                    valids++;
                }
            });
        }
        if (this.recipients !== null) {
            if (this.checkMandatoryValidator() && valids === this.recipients.controls.length) {
                return true;
            } else {
                return false;
            }
        }
        return 'pasa';
    }

    private createFieldsControlMatrix() {
        this.fieldsFormControl = [];
        this.relationsFormControl = [];
        this.relationsGroups = [];
        this.customizedView.customizableInstanceViews.forEach(entry => {
            this.fieldsFormControl[entry.frontId] = [];
            this.relationsFormControl[entry.position] = [];
            this.relationsGroups[`entry-${entry.frontId}`] = [];
            if (entry.relations.length !== 0) {
                entry.relations.sort(function(a, b) {
                    return a.position - b.position;
                });
                entry.relations.forEach(relation => {
                    let index = this.relationsGroups[`entry-${entry.frontId}`].findIndex(f => f.groupId === relation.groupId && f.entry === `entry-${entry.frontId}`);
                    if (index === -1) {
                        let relationAux: RelationsGroups = {
                            entry: 'entry-' + entry.frontId,
                            groupId: relation.groupId,
                            groupLabel: relation.groupLabel,
                            relations: [relation]
                        };
                        this.relationsGroups[`entry-${entry.frontId}`].push(relationAux);
                    } else {
                        this.relationsGroups[`entry-${entry.frontId}`].at(index).relations.push(relation);
                    }
                });
            }
        });
    }

    private getForm() {
        this.api.getCredentialSpec(this.issuingOid, this.selectedLanguage, this.customizedData)
        .pipe(takeUntil(this.destroy$))
        .subscribe((respuesta: any) => {
            this.credentialBuilderService.setIssuingModalHeaderTitle(this.translateService.instant('credential-builder.customise-data.input-form.title'));
            this.customizedView = respuesta;
            this.createFieldsControlMatrix();
            this.sortFields('customizedView');
            this.recipientFormGroup = this.createVoidRecipient();
            this.recipientsGroup = new FormGroup({
                recipients: new FormArray([this.recipientFormGroup]),
            });
            this.credentialBuilderService.setRecipientAdd(true);
            this.credentialBuilderService.setIssuingModalState('showForm');
            this.loading = false;
        });
    }

    private generateVoidCustomizedData() {
        this.customizedData.customizableEntityViews = [];
        this.customizableDataArchitect.customizableEntityViews.forEach(entity => {
            let entityAux = JSON.parse(JSON.stringify(entity));
            entityAux.fields = entityAux.fields.filter(f => { return f.mandatory === true; });
            entityAux.relations = entityAux.relations.filter(f => { return f.mandatory === true; });
            this.customizedData.customizableEntityViews.push(entityAux);
        });
        this.modalState = 'showCustomizableData';
        this.loading = false;
    }

    private loadJSON() {
        this.api.getFullCustomizableSpec()
        .pipe(takeUntil(this.destroy$))
        .subscribe((respuesta: any) => {
            this.customizableDataArchitect = respuesta;
            this.prepareData();
        });
    }

    private sortFields(arrayToSort: string, position?, field: boolean = true) {
        if (arrayToSort === 'customizedData' && position !== undefined) {
            this[arrayToSort].customizableEntityViews[position].fields.sort(function(a, b) {
                return a.position - b.position;
            });
            this[arrayToSort].customizableEntityViews[position].relations.sort(function(a, b) {
                return a.position - b.position;
            });
        } else if (arrayToSort === 'customizedView' && field) {
            this[arrayToSort].customizableInstanceViews.sort(function(a, b) {
                return a.position - b.position;
            });
            this[arrayToSort].customizableInstanceViews.forEach(entity => {
                entity.fields?.sort(function(a, b) {
                    return a.position - b.position;
                });
                entity.relations?.sort(function(a, b) {
                    return a.position - b.position;
                });
            });
        } else if (arrayToSort !== 'customizableDataArchitect' && field) {
            this[arrayToSort].customizableEntityViews.sort(function(a, b) {
                return a.position - b.position;
            });
            this[arrayToSort].customizableEntityViews.forEach(entity => {
                entity.fields.sort(function(a, b) {
                    return a.position - b.position;
                });
            });
        } else if (arrayToSort === 'customizableDataArchitect') {
            this[arrayToSort].customizableEntityViews.sort(function(a, b) {
                return a.position - b.position;
            });
            this[arrayToSort].customizableEntityViews.forEach(entity => {
                entity.fields.sort(function(a, b) {
                    return a.position - b.position;
                });
                entity.relations.sort(function(a, b) {
                    return a.position - b.position;
                });
            });
        } else {
            this[arrayToSort].customizableEntityViews.forEach(entity => {
                entity.relations.sort(function(a, b) {
                    return a.position - b.position;
                });
            });
        }
    }

    private prepareData() {
        this.sortFields('customizableDataArchitect');
        this.generateVoidCustomizedData();
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
                    'Credential Builder'
                ),
                url: '/credential-builder',
            }),
            new UxLink({
                label: this.translateService.instant(
                    'Customise data'
                )
            }),
        ];
    }

    private showErrorNotification(error?: string) {
        const message: OKNotification = {
            severity: 'error',
            summary: error
        };
        this.notifService.showNotification(message);
        this.credentialBuilderService.setCloseIssuingModal('close');
    }

    private checkValidators(validators): String[] {
        let validatorsArray = [];
        if (validators !== undefined) {
            validatorsArray = validators.split(',');
        }
        if (validatorsArray.length > 0) {
            let validatorsResponse = [];
            validatorsArray.forEach(validator => {
                validator = validator.toUpperCase();
                if (validator.includes('(')) {
                    if (validator.includes('MANDATORYIF')) {
                        let mandatoryValidations = validator.split('(');
                        validator = mandatoryValidations[0];
                        let validatorValue = mandatoryValidations[1].split(')')[0];
                    } else {
                        validator = validator.replace('(', '');
                        validator = validator.replace(')', '');
                    }
                }
                validatorsResponse.push(ValidationsEnum[validator]);
            });
            return validatorsResponse;
        } else {
            return [];
        }
    }

    private addErrorMandatoryValidation(validation, validator) {
        let errorFound = this.mandatoryValidationsErrors.find(f => f.validation === validation.validation);
        if (errorFound !== undefined) {
            errorFound.error++;
        } else {
            let error: ValidationsMandatoryErrors = {
                validatorType: validator,
                validation: validation.validation,
                error: 1
            };
            this.mandatoryValidationsErrors.push(error);
        }
    }

    private checkMandatoryValidator() {
        let errors = 0;
        this.mandatoryValidationsErrors.forEach(error => {
            if (error.validatorType === 'MandatoryIfNot' && error.error === 2) {
                errors++;
            }
            if (error.validatorType === 'MandatoryIf' && error.error > 0) {
                errors++;
            }
        });
        return errors > 0 ? false : true;
    }

    private mandatoryValidator() {
        this.mandatoryValidations.forEach((validations: MandatoryValidations) => {
            validations.validations.forEach((validation: ValidationIncluded) => {
                this.recipients.controls.forEach(recipient => {
                    let fieldFound =
                    recipient['controls']['entry-' + validation.entityId]['controls'][validation.entityId + '-' + validation.fieldId];
                    if (fieldFound.value === '') {
                        this.addErrorMandatoryValidation(validation, validations.mandatory);
                    }
                });
            });
        });
    }

    private checkMandatory(field: CustomizableInstanceFieldView, entity, mandatoryType) {
        let validation = '';
        if (mandatoryType === 'MandatoryIf') {
            validation = field.validation.split(',').find(f => f.toLocaleLowerCase().includes('mandatoryif')).split('(')[1].split(')')[0];
        } else if (mandatoryType === 'MandatoryIfNot') {
            validation =
            field.validation.split(',').find(f => f.toLocaleLowerCase().includes('mandatoryifnot')).split('(')[1].split(')')[0];
        }
        let found = this.mandatoryValidations.find(f => f.mandatory === mandatoryType);
        let validationIncluded: ValidationIncluded = {
            validation: validation,
            fieldId: field.frontId,
            entityId: entity.frontId,
            validationGroup: mandatoryType,
        };
        if (found !== undefined) {
            found.validations.push(validationIncluded);
        } else {
            let mandatoryValidation: MandatoryValidations = {
                mandatory: mandatoryType,
                validations: [validationIncluded]
            };
            this.mandatoryValidations.push(mandatoryValidation);
        }
    }

    private addValidator(validator, controlGroup: FormControl, field?: CustomizableInstanceFieldView, entity?) {
        switch (validator) {
            case ValidationsEnum.LENGTH255:
                controlGroup.addValidators(Validators.maxLength(255));
                break;
            case ValidationsEnum.LENGTH4000:
                controlGroup.addValidators(Validators.maxLength(4000));
                break;
            case ValidationsEnum.EMAIL:
                controlGroup.addValidators(Validators.email);
                // controlGroup.addValidators(commaSepEmail);
                break;
            case ValidationsEnum.NUMERIC:
                controlGroup.addValidators(Validators.pattern('^[0-9]*$'));
                break;
            case ValidationsEnum.DATEFORMAT:
                controlGroup.addValidators(dateValidator);
                break;
            case ValidationsEnum.DATELOCALFORMAT:
                controlGroup.addValidators(dateValidator);
                break;
            case ValidationsEnum.MANDATORY:
                controlGroup.addValidators(Validators.required);
                break;
            case ValidationsEnum.MANDATORYIF:
                this.checkMandatory(field, entity, validator);
                break;
            case ValidationsEnum.MANDATORYIFNOT:
                this.checkMandatory(field, entity, validator);
                break;
            default:
                break;
        }
    }

    private addNewControl(field: CustomizableInstanceFieldView, entity?, isRelation = false) {
        let control = this.fb.control('');
        if (isRelation) {
            if (field.mandatory) {
                control.setValue(true);
            } else {
                control.setValue(false);
            }
        } else {
            let validators = this.checkValidators(field.validation);
            validators.forEach(validator => {
                this.addValidator(validator, control, field, entity);
            });
            /*if (field.mandatory) {
                control.addValidators(Validators.required);
            }*/
        }
        return control;
    }

    private addNewGroup(entry: CustomizableInstanceView) {
        let group = this.fb.group([]);
        entry.fields.forEach((field: CustomizableInstanceFieldView) => {
            let label = `${entry.frontId}-${field.frontId}`;
            group.addControl(label, this.addNewControl(field, entry));
            this.fieldsFormControl[entry.frontId].push(`${field.frontId}`);
        });
        entry.relations.forEach((relation: CustomizableInstanceRelationView) => {
            let relationGroup = this.relationsGroups[`entry-${entry.frontId}`].find(f => f.groupId === relation.groupId);
            if (relationGroup !== undefined) {
                let relationAdded = relationGroup.relations.find(f => f.relPath === relation.relPath);
                if (relationAdded !== undefined) {
                    let label = `${entry.frontId}-${relation.frontId}`;
                    group.addControl(label, this.addNewControl(relationAdded, true));
                }
            }
        });
        return group;
    }

    private addNewRecipient() {
        let recipient = this.fb.group([]);
        this.customizedView.customizableInstanceViews.forEach((entry: CustomizableInstanceView) => {
            recipient.addControl(`entry-${entry.frontId}`, this.addNewGroup(entry));
        });
        return recipient;
    }

    private createVoidRecipient() {
        let arrayReturn = this.addNewRecipient();
        return arrayReturn;
    }

}
