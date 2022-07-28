import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation,
    ViewChild,
} from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { UxAppShellService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { DateFormatService } from '@services/date-format.service';
import { IssuerService } from '@services/issuer.service';
import { Constants } from '@shared/constants';
import {
    AssessmentSpecLiteView,
    CodeDTView,
    CredentialView,
    CredentialFileUploadResponseView,
    ResourceAssessmentsListIssueView,
    V1Service,
} from '@shared/swagger';
import { commaSepEmail } from '@shared/validators/email-comma.validator';
import { walletEmailValidator } from '@shared/validators/email-wallet.validator';
import { legalIdentifierValidator } from '@shared/validators/legal-identifier.validators';
import { noSpaceValidator } from '@shared/validators/no-space-validator';
import { ownerAddressValidator } from '@shared/validators/owner-address-validator';
import { Subject } from 'rxjs';
import { environment } from '@environments/environment';
import { takeUntil } from 'rxjs/operators';
import { EuiMessageBoxComponent } from '@eui/components/eui-message-box';

@Component({
    selector: 'edci-issue-modal',
    templateUrl: './issue-modal.component.html',
    styleUrls: ['./issue-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class IssueModalComponent implements OnInit, OnDestroy {
    @ViewChild('messageBoxFormError')
    messageBoxFormError: EuiMessageBoxComponent;
    @Input() issueCredentialOid: number;
    @Input() defaultLanguage: string;
    @Input() languages: { code: string; label: string }[];
    @Output() onCloseModal: EventEmitter<any> = new EventEmitter();
    language: string = this.translateService.currentLang;
    concentText: string = environment.concentText;
    recipientFormGroup: FormGroup = new FormGroup(
        {
            firstName: new FormControl('', [
                Validators.required,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                noSpaceValidator,
            ]),
            lastName: new FormControl('', [
                Validators.required,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                noSpaceValidator,
            ]),
            dateOfBirth: new FormControl(''),
            citizenshipCountry: new FormControl(null), // Controlled List
            nationalIdentifier: new FormGroup(
                {
                    content: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                    spatialId: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                },
                legalIdentifierValidator
            ),
            placeOfBirthCountry: new FormControl(null), // Controlled List
            ownerAddress: new FormGroup(
                {
                    address: new FormControl('', [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                    addressCountry: new FormControl(null), // Controlled List
                },
                ownerAddressValidator
            ),
            gender: new FormControl(null), // Controlled List
            emailAddress: new FormControl('', [
                commaSepEmail,
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                noSpaceValidator,
            ]),
            walletAddress: new FormControl('', [
                Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
            ]),
            grades: new FormGroup({}),
        },
        walletEmailValidator
    );

    consentForm: FormGroup = new FormGroup({
        concentCheckBox: new FormControl(false),
    });

    recipientsGroup: FormGroup = new FormGroup({
        recipients: new FormArray([this.recipientFormGroup]),
    });

    destroy$: Subject<boolean> = new Subject<boolean>();
    isLoading: boolean = true;
    isSaveDisabled: boolean = false;
    maxDateOfBirth: Date = new Date();
    assessments: any[] = [];

    get recipients(): FormArray {
        return this.recipientsGroup.get('recipients') as FormArray;
    }

    get concentCheckBox() {
        return this.consentForm.get('concentCheckBox') as FormControl;
    }

    constructor(
        public uxService: UxAppShellService,
        private api: V1Service,
        private issuerService: IssuerService,
        private translateService: TranslateService,
        private router: Router,
        private dateFormatService: DateFormatService
    ) {}

    ngOnInit() {
        this.getAssessments();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    closeModal(): void {
        this.onCloseModal.emit();
    }

    onIssue(): void {
        if (this.recipients.invalid || !this.concentCheckBox.value) {
            this.recipients.markAsUntouched();
            this.consentForm.markAsUntouched();
            this.isLoading = false;
            this.messageBoxFormError.openMessageBox();
        } else {
            this.isLoading = true;
            this.issuedCredential();
        }
    }

    checkValidDate(control: FormControl): void {
        if (
            !this.dateFormatService.validateDates(
                control.value,
                this.maxDateOfBirth
            )
        ) {
            control.setErrors({ invalidDateError: true });
        } else {
            if (control.value) {
                control.setErrors(null);
            }
        }
    }

    addRecipient(): void {
        this.recipients.push(
            new FormGroup(
                {
                    firstName: new FormControl('', [
                        Validators.required,
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                        noSpaceValidator,
                    ]),
                    lastName: new FormControl('', [
                        Validators.required,
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                        noSpaceValidator,
                    ]),
                    dateOfBirth: new FormControl(''),
                    citizenshipCountry: new FormControl(null), // Controlled List
                    nationalIdentifier: new FormGroup(
                        {
                            content: new FormControl(null, [
                                Validators.maxLength(
                                    Constants.MAX_LENGTH_DEFAULT
                                ),
                            ]),
                            spatialId: new FormControl(null, [
                                Validators.maxLength(
                                    Constants.MAX_LENGTH_DEFAULT
                                ),
                            ]),
                        },
                        legalIdentifierValidator
                    ),
                    placeOfBirthCountry: new FormControl(null), // Controlled List
                    ownerAddress: new FormGroup(
                        {
                            address: new FormControl('', [
                                Validators.maxLength(
                                    Constants.MAX_LENGTH_DEFAULT
                                ),
                            ]),
                            addressCountry: new FormControl(null), // Controlled List
                        },
                        ownerAddressValidator
                    ),
                    gender: new FormControl(null), // Controlled List
                    emailAddress: new FormControl('', [
                        commaSepEmail,
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                        noSpaceValidator,
                    ]),
                    walletAddress: new FormControl('', [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                    grades: new FormGroup({}),
                },
                walletEmailValidator
            )
        );
        this.addGradeControls();
    }

    removeRecipient(position: number): void {
        this.recipients.removeAt(position);
    }

    citizenshipCountrySelectionChange(
        citizenshipCountry: CodeDTView[],
        recipientIndex: number
    ): void {
        this.recipients.controls[recipientIndex].patchValue({
            citizenshipCountry: citizenshipCountry,
        });
    }

    placeOfBirthCountrySelectionChange(
        placeOfBirthCountry: CodeDTView,
        recipientIndex: number
    ): void {
        this.recipients.controls[recipientIndex].patchValue({
            placeOfBirthCountry: placeOfBirthCountry,
        });
    }

    addressCountrySelectionChange(
        addressCountry: CodeDTView,
        recipientIndex: number
    ): void {
        this.recipients.controls[recipientIndex][
            'controls'
        ].ownerAddress.patchValue({
            addressCountry: addressCountry,
        });
    }

    private getAssessments() {
        this.api
            .getIssuerAssessmentGrades(
                this.issueCredentialOid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (response: ResourceAssessmentsListIssueView) => {
                    const assessments = response.assessments;
                    if (assessments) {
                        Object.keys(assessments).forEach((oid, i) => {
                            this.assessments[i] = {
                                oid: oid,
                                defaultTitle: assessments[oid],
                            };
                        });

                        this.addGradeControls();
                    }
                    this.isLoading = false;
                },
                (err) => this.closeModal()
            );
    }

    private addGradeControls(): void {
        let grades = this.recipients.controls[
            this.recipients.controls.length - 1
        ].get('grades') as FormGroup;
        this.assessments.forEach((assessment) => {
            grades.addControl(
                assessment.oid.toString(),
                new FormControl('', [
                    Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    Validators.required,
                ])
            );
        });
    }

    private issuedCredential(): void {
        /*let body: IssueBuildCredentialView = this.getIssueCredentialBody();
        this.api
            .issueCredential(body, this.defaultLanguage)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                (issuedCredential: CredentialFileUploadResponseView) => {
                    if (issuedCredential.valid) {
                        this.issuerService.setCredentials(
                            <CredentialView[]>issuedCredential.credentials
                        );
                        this.router.navigate(['/create/overview']);
                    }
                },
                (err) => this.closeModal()
            );*/
    }

    /*private getIssueCredentialBody(): IssueBuildCredentialView {
        return {
            credential: this.issueCredentialOid,
            recipients: this.getRecipients(),
        };
    }*/

    private getAssessmentGrades(recipient: FormGroup): any {
        let grades = {};
        this.assessments.forEach((assessment: AssessmentSpecLiteView) => {
            grades[assessment.oid.toString()] =
                recipient.value.grades[assessment.oid.toString()];
        });
        return grades;
    }
}
