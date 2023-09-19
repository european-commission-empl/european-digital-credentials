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
import { openCloseAnimation } from '@shared/animations/openClose';
import { multipleFieldsBoundValidator } from '@shared/validators/multiple-fields-bound-validator';
import { atLeastOneRequiredValidator } from '@shared/validators/at-least-one-required-validator';

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

export class CustomValidations {
    static readonly MANDATORYIF = { validationName: 'MandatoryIf', funct: multipleFieldsBoundValidator };
    static readonly MANDATORYIFNOT = { validationName: 'MandatoryIfNot', funct: atLeastOneRequiredValidator };
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
    encapsulation: ViewEncapsulation.None,
    animations: [
        openCloseAnimation
    ]
})
export class FillFormCustomizeDataComponent implements OnInit, OnDestroy {

    get recipients(): FormArray {
        return this.recipientsGroup.get('recipients') as FormArray;
    }

    get consentCheckBox() {
        return this.consentForm.get('consentCheckBox');
    }
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
    expanded = {};

    uploads: Array<File> = [];
    error: boolean;
    errorMessage: string;
    errorOfValidation: boolean;
    errorOfValidationMessage: string;
    inputFileIsEnabled: boolean;
    isLoadingRecipientTemplate: boolean;
    deletedList: Array<File> = [];

    destroy$: Subject<boolean> = new Subject<boolean>();

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

    setControlErrors(control: FormControl, errors: any) {
        if (errors.length > 0) {
            errors.forEach(error => {
                control.setErrors(error);
            });
        } else {
            control.setErrors(null);
        }
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
        const recipient: FormControl =
            this.recipients.controls[index]['controls']['entry-' + block.frontId].controls[block.frontId + '-' + field.frontId];
        if (recipient !== undefined) {
            recipient.patchValue(value ? value.uri : '');
        }
        this.recipients.controls[index]['controls']['entry-' + block.frontId].updateValueAndValidity();
    }

    public getGroup(obj: CustomizableInstanceFieldView) {
        let result = '';
        Object.values(CustomValidations).forEach(customValidation => {
            if (!result) {
                result = obj.validation.split(',').find(f =>
                    f.toLocaleLowerCase().includes(customValidation.validationName.toLocaleLowerCase()))?.split('(')[1]?.split(')')[0];
            }
        });

        return result;
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
                    this.loading = false;
                },
                    () => (this.loading = false));
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

    public toggleExpanded(index: number) {
        if (this.expanded[index] != null) {
            this.expanded[index] = !this.expanded[index];
        } else {
            this.expanded[index] = false;
        }
    }

    public isExpanded(index: number): boolean {
        return this.expanded[index] != null ? this.expanded[index] : true;
    }

    private checkValidForm() {
        this.recipients.markAllAsTouched();
        this.recipients.updateValueAndValidity();
        return this.recipients.valid;
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
                entry.relations.sort(function (a, b) {
                    return a.position !== b.position ? a.position - b.position : a.label.localeCompare(b.label);
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
        this.customizedView.customizableInstanceViews.sort(function (a, b) {
            return a.position - b.position;
        });
        this.customizedView.customizableInstanceViews.forEach(entity => {
            entity.fields?.sort(function (a, b) {
                return a.position !== b.position ? a.position - b.position : a.label.localeCompare(b.label);
            });
            entity.relations?.sort(function (a, b) {
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

    private addValidator(validator, controlGroup: FormControl, formGroup: FormGroup, field?: CustomizableInstanceFieldView, entity?) {
        switch (validator) {
            case ValidationsEnum.LENGTH255:
                controlGroup.addValidators(Validators.maxLength(255));
                break;
            case ValidationsEnum.LENGTH4000:
                controlGroup.addValidators(Validators.maxLength(4000));
                break;
            case ValidationsEnum.EMAIL:
                controlGroup.addValidators(Validators.email);
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
            default:
                break;
        }
    }

    private addNewControl(field: CustomizableInstanceFieldView, formgroup: FormGroup, entity?, isRelation = false) {
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
                this.addValidator(validator, control, formgroup, field, entity);
            });
        }
        return control;
    }

    private addNewGroup(entry: CustomizableInstanceView) {
        const group = this.fb.group([]);
        entry.fields.forEach((field: CustomizableInstanceFieldView) => {
            const label = `${entry.frontId}-${field.frontId}`;
            group.addControl(label, this.addNewControl(field, group, entry));
            this.fieldsFormControl[entry.frontId].push(`${field.frontId}`);
        });
        entry.relations.forEach((relation: CustomizableInstanceRelationView) => {
            const relationGroup = this.relationsGroups[`entry-${entry.frontId}`].find(f => f.groupId === relation.groupId);
            if (relationGroup !== undefined) {
                const relationAdded = relationGroup.relations.find(f => f.relPath === relation.relPath);
                if (relationAdded !== undefined) {
                    const label = `${entry.frontId}-${relation.frontId}`;
                    group.addControl(label, this.addNewControl(relationAdded, group, true));
                }
            }
        });

        this.addGroupValidations(entry, group);

        return group;
    }

    private addGroupValidations(entry: CustomizableInstanceView, group: FormGroup) {
        Object.values(CustomValidations).forEach(customValidation => {
            this.addValidation(entry, group, customValidation.validationName.toLocaleLowerCase(), customValidation.funct);
        });
    }

    private addValidation(entry: CustomizableInstanceView, group: FormGroup, validation: string, validationFunction: Function) {
        let validations = entry.fields.filter(f => f.validation.split(',').find(v => v.toLocaleLowerCase()?.split('(')[0] === validation));

        if (validations?.length > 0) {
            let validationsMap = new Map<string, string>();
            validations.forEach((obj) => {
                let groupName = obj.validation.split(',').find(f => f.toLocaleLowerCase().includes(validation)).split('(')[1].split(')')[0];
                let separator = validationsMap.get(groupName) != null ? validationsMap.get(groupName) + ',' : '';
                validationsMap.set(groupName, separator + entry.frontId + '-' + obj.frontId);
            });

            validationsMap.forEach(function(value, key) { group.addValidators(validationFunction(value.split(','), key)); });
        }

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
