import {
    Component, OnDestroy, OnInit,
    ViewChild, ViewEncapsulation
} from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { environment } from '@environments/environment';
import { EuiDialogComponent } from '@eui/components/eui-dialog';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';
import { UxLanguage, UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import { IssuerService } from '@services/issuer.service';
import { StatusBarService } from '@services/status-bar.service';
import {
    CredentialView,
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
import { CustomizedRecipientsView } from '@shared/swagger/model/customizedRecipientsView';
import { dateValidator } from '@shared/validators/date-validator';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

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
    selector: 'edci-fill-form-customize-data',
    templateUrl: './fill-form-customize-data.component.html',
    styleUrls: ['./fill-form-customize-data.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class FillFormCustomizeDataComponent implements OnInit, OnDestroy {
    @ViewChild('issuingDialogModal') issuingDialogModal: EuiDialogComponent;
    @ViewChild('messageBoxFormError') messageBoxFormError: EuiMessageBoxComponent;

    languages: UxLanguage[] = [];
    parts: UxLink[] = [];
    urlActivated: string;
    selectedLanguage: string = this.translateService.currentLang;
    concentText: string = environment.concentText;
    customizedData: CustomizableSpecView = {
        customizableEntityViews: []
    };

    issuingModeModal: boolean;

    customizedView: CustomizableInstanceSpecView;
    issuingOid = -1;
    modalState = 'showCustomizableData';
    consentForm: FormGroup = new FormGroup({
        consentCheckBox: new FormControl(false, Validators.required),
    });

    loading = false;

    recipientFormGroup: FormGroup;
    recipientsGroup: FormGroup = new FormGroup({
        recipients: new FormArray([]),
    });
    fieldsFormControl = [];
    relationsFormControl = [];
    relationsGroups: RelationsGroups[] = [];
    sendData = [];
    cerrado = false;

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
        private credentialBuilderService: CredentialBuilderService,
        private fb: FormBuilder,
        private notifService: NotificationService,
        private router: Router,
        private route: ActivatedRoute,
        private statusBarService: StatusBarService,
    ) {
        this.issuerService.breadcrumbSubject.pipe(takeUntil(this.destroy$)).subscribe((url: string) => {
            this.urlActivated = url;
            this.loadBreadcrumb();
        });
        this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((params: ParamMap) => {
            this.issuingOid = parseInt(params.get('id'));
        });
        this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe((event: LangChangeEvent) => {
            this.selectedLanguage = event.lang;
            this.loadBreadcrumb();
        });
        this.credentialBuilderService.recipientAddObservable.pipe(takeUntil(this.destroy$)).subscribe(res => {
            this.mandatoryValidations.forEach(validation => {
                validation.validations.forEach(v => {
                    this.setSubscriber(v);
                });
            });
        });
    }

    ngOnInit() {
        this.loading = true;
        this.modalState = '';
        this.getForm();
        this.loadBreadcrumb();
        this.statusBarService.setStepStatusBarActive(1);
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    setSubscriber(validation) {
        let recipientIndex = 0;
        this.recipients.controls.forEach((recipient: FormGroup) => {
            const control: FormControl =
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
        const controlPrimary: FormControl =
        this.recipients.controls[recipientId - 1]['controls']['entry-' + validation.entityId]
            .controls[validation.entityId + '-' + validation.fieldId];
        const controlSecondary: FormControl =
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
        const validationFound = this.mandatoryValidations
            .find(mandatoryValidation => mandatoryValidation.mandatory === validation.validationGroup)
            .validations.filter(mv => {
                return mv.validationGroup === validation.validationGroup &&
                mv.entityId === validation.entityId &&
                mv.validation === validation.validation;
            });
        const controlsArray = [];
        validationFound.forEach(v => {
            const controlFound = controls['entry-' + v.entityId].controls[v.entityId + '-' + v.fieldId];
            controlsArray.push(controlFound);
        });
        return controlsArray.filter(c => c !== controlPrimary)[0];
    }

    checkValidationEmail(recipientId, validation) {
        let error = false;
        const controlPrimary: FormControl =
        this.recipients.controls[recipientId - 1]['controls']['entry-' + validation.entityId]
            .controls[validation.entityId + '-' + validation.fieldId];
        const controlSecondary: FormControl =
        this.getOtherControl(this.recipients.controls[recipientId - 1]['controls'], validation, controlPrimary);
        if (controlPrimary.value === '' && controlPrimary.touched) {
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

    public addRecipient(): void {
        this.recipients.controls.push(this.createVoidRecipient());
        this.credentialBuilderService.setRecipientAdd(true);
    }

    public removeRecipient(position: number): void {
        this.recipients.removeAt(position);
    }

    public addRelation(recipient, block, field) {
        const relationLabel = `${block.frontId}-${field.frontId}`;
        const estado =
        this.recipients.controls[recipient]['controls'][`entry-${block.frontId}`].controls[relationLabel].value;
        this.recipients.controls[recipient]['controls'][`entry-${block.frontId}`].controls[relationLabel].setValue(!estado);
    }

    public controlledListSelected(block: CustomizableInstanceView, field: CustomizableInstanceFieldView, index: number, value: any) {
        if (value !== null) {
            const recipient: FormControl =
            this.recipients.controls[index]['controls']['entry-' + block.frontId].controls[block.frontId + '-' + field.frontId];
            if (recipient !== undefined) {
                recipient.setValue(value.uri);
            }
        }
    }

    public sendForm() {
        this.loading = true;
        const validForm = this.checkValidForm();
        if (validForm && this.consentForm.controls['consentCheckBox'].value) {
            const recipientsData: CustomizedRecipientsView = {
                recipients: []
            };
            this.recipients.controls.forEach((recipient: FormGroup) => {
                const recipientData: CustomizedRecipientView = {
                    entities: []
                };
                Object.keys(recipient.controls).forEach((entry: any) => {
                    const entityData: CustomizedEntityView = {
                        fields: [],
                        relations: []
                    };
                    Object.keys(recipient.controls[entry]['controls']).forEach(field => {
                        const ids = field.split('-');
                        const entityFrontId = ids[0];
                        const fieldFrontId = ids[1];
                        const foundCamp = this.customizedView.customizableInstanceViews.find(f => f.frontId === entityFrontId);
                        if (foundCamp !== undefined) {
                            if (foundCamp.relations !== undefined && foundCamp.relations.length > 0) {
                                const relation = foundCamp.relations.find(f => f.frontId === fieldFrontId);
                                if (relation !== undefined) {
                                    const relationData: CustomizedRelationView = {
                                        relPathIdentifier: relation.relPath,
                                        included: recipient.controls[entry]['controls'][field].value
                                    };
                                    entityData.relations.push(relationData);
                                }
                            }
                            if (foundCamp.fields !== undefined && foundCamp.fields.length > 0) {
                                if (recipient.controls[entry]['controls'][field].value !== '') {
                                    const fieldFound = foundCamp.fields.find(f => f.frontId === fieldFrontId);
                                    if (fieldFound !== undefined) {
                                        const fieldData: CustomizedFieldView = {
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

    public backToFields() {
        this.router.navigateByUrl('/credential-builder/issue/fields/' + this.issuingOid);
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
                    const index = this.relationsGroups[`entry-${entry.frontId}`].findIndex(f =>
                        f.groupId === relation.groupId && f.entry === `entry-${entry.frontId}`);
                    if (index === -1) {
                        const relationAux: RelationsGroups = {
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
        this.credentialBuilderService.customizedViewRequestDataObservable.pipe(takeUntil(this.destroy$)).subscribe(data => {
            this.customizedData = data;
            if (this.customizedData === null || this.customizedData === undefined) {
                this.router.navigateByUrl('credential-builder/issue/fields/' + this.issuingOid);
            }
            this.api.getCredentialSpec(this.issuingOid, this.selectedLanguage, this.customizedData)
                .pipe(takeUntil(this.destroy$))
                .subscribe((respuesta: any) => {
                    this.customizedView = respuesta;
                    this.createFieldsControlMatrix();
                    this.sortFields();
                    this.recipientFormGroup = this.createVoidRecipient();
                    this.recipientsGroup = new FormGroup({
                        recipients: new FormArray([this.recipientFormGroup]),
                    });
                    this.credentialBuilderService.setRecipientAdd(true);
                    this.loading = false;
                });
        },
        (err) => {
            this.router.navigateByUrl('credential-builder/issue/fields/' + this.issuingOid);
        });
    }

    private sortFields() {
        this.customizedView.customizableInstanceViews.sort(function(a, b) {
            return a.position - b.position;
        });
        this.customizedView.customizableInstanceViews.forEach(entity => {
            entity.fields?.sort(function(a, b) {
                return a.position - b.position;
            });
            entity.relations?.sort(function(a, b) {
                return a.position - b.position;
            });
        });
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
                    'Fields Selection'
                ),
                url: '/credential-builder/issue/fields/' + this.issuingOid,
            }),
            new UxLink({
                label: this.translateService.instant(
                    'Issuing Data'
                ),
                url: '/credential-builder/issue/form/' + this.issuingOid,
            }),
        ];
    }

    private checkValidators(validators): string[] {
        let validatorsArray = [];
        if (validators !== undefined) {
            validatorsArray = validators.split(',');
        }
        if (validatorsArray.length > 0) {
            const validatorsResponse = [];
            validatorsArray.forEach(validator => {
                validator = validator.toUpperCase();
                if (validator.includes('(')) {
                    if (validator.includes('MANDATORYIF')) {
                        const mandatoryValidations = validator.split('(');
                        validator = mandatoryValidations[0];
                        const validatorValue = mandatoryValidations[1].split(')')[0];
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
        const errorFound = this.mandatoryValidationsErrors.find(f => f.validation === validation.validation);
        if (errorFound !== undefined) {
            errorFound.error++;
        } else {
            const error: ValidationsMandatoryErrors = {
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
                let recipientIndex = 1;
                this.recipients.controls.forEach(recipient => {
                    recipient.markAllAsTouched();
                    if (validation.validation.includes('contactPoint')) {
                        this.checkValidationEmail(recipientIndex, validation);
                    }
                    const fieldFound =
                    recipient['controls']['entry-' + validation.entityId]['controls'][validation.entityId + '-' + validation.fieldId];
                    if (fieldFound.value === '') {
                        this.addErrorMandatoryValidation(validation, validations.mandatory);
                    }
                    recipientIndex++;
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
        const found = this.mandatoryValidations.find(f => f.mandatory === mandatoryType);
        const validationIncluded: ValidationIncluded = {
            validation: validation,
            fieldId: field.frontId,
            entityId: entity.frontId,
            validationGroup: mandatoryType,
        };
        if (found !== undefined) {
            found.validations.push(validationIncluded);
        } else {
            const mandatoryValidation: MandatoryValidations = {
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
        const control = this.fb.control('');
        if (isRelation) {
            if (field.mandatory) {
                control.setValue(true);
            } else {
                control.setValue(false);
            }
        } else {
            const validators = this.checkValidators(field.validation);
            validators.forEach(validator => {
                this.addValidator(validator, control, field, entity);
            });
        /* if (field.mandatory) {
                control.addValidators(Validators.required);
            } */
        }
        return control;
    }

    private addNewGroup(entry: CustomizableInstanceView) {
        const group = this.fb.group([]);
        entry.fields.forEach((field: CustomizableInstanceFieldView) => {
            const label = `${entry.frontId}-${field.frontId}`;
            group.addControl(label, this.addNewControl(field, entry));
            this.fieldsFormControl[entry.frontId].push(`${field.frontId}`);
        });
        entry.relations.forEach((relation: CustomizableInstanceRelationView) => {
            const relationGroup = this.relationsGroups[`entry-${entry.frontId}`].find(f => f.groupId === relation.groupId);
            if (relationGroup !== undefined) {
                const relationAdded = relationGroup.relations.find(f => f.relPath === relation.relPath);
                if (relationAdded !== undefined) {
                    const label = `${entry.frontId}-${relation.frontId}`;
                    group.addControl(label, this.addNewControl(relationAdded, true));
                }
            }
        });
        return group;
    }

    private addNewRecipient() {
        const recipient = this.fb.group([]);
        this.customizedView.customizableInstanceViews.forEach((entry: CustomizableInstanceView) => {
            recipient.addControl(`entry-${entry.frontId}`, this.addNewGroup(entry));
        });
        return recipient;
    }

    private createVoidRecipient() {
        const arrayReturn = this.addNewRecipient();
        return arrayReturn;
    }

}
