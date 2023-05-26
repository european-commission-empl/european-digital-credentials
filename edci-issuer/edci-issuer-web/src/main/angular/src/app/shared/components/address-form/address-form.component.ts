import { Component, Input, OnChanges, SimpleChanges, Output, EventEmitter } from '@angular/core';
import {
    FormControl,
    FormGroup,
    Validators,
} from '@angular/forms';
import { UxLanguage } from '@eui/core';
import { Constants } from '@shared/constants';
import { CodeDTView } from '@shared/swagger';

@Component({
    selector: 'edci-address-form',
    templateUrl: './address-form.component.html',
    styleUrls: ['./address-form.component.scss'],
})
export class AddressFormComponent implements OnChanges {
    @Input() formGroup: FormGroup;
    @Input() language: string;
    @Input() isPrimaryLanguage: boolean;
    @Input() selectedLanguages: UxLanguage[] = [];
    @Input() isFirstGroupMember: boolean;
    @Input() itemIndex: number;
    @Input() showAddBtn = false;

    @Output() deleteRow: EventEmitter<number> = new EventEmitter();
    @Output() addRow: EventEmitter<boolean> = new EventEmitter();
    @Output() onClose: EventEmitter<boolean> = new EventEmitter();

    ngOnChanges(changes: SimpleChanges) {
        if (changes.language && !changes.language.firstChange) {
        // only logged upon a change after rendering

            this.addLanguageControls(this.language);
        }
    }

    doCheckCountryRequired() {
        const description = this.getLocationDescriptionCtrl(this.language).value;
        const address = this.getLocationAddressCtrl(this.language).value;
        if (description || address) {
            this.getCountrySelectedCtrl().addValidators([Validators.required]);
        } else {
            this.getCountrySelectedCtrl().removeValidators([Validators.required]);
        }
        this.getCountrySelectedCtrl().updateValueAndValidity();
    }

    getLocationDescriptionGrp(): FormGroup {
        return this.formGroup.get('description') as FormGroup;
    }

    getLocationDescriptionCtrl(language: string): FormControl {
        return this.getLocationDescriptionGrp().controls[language] as FormControl;
    }

    getAreaSelectedCtrl(): FormControl {
        return this.formGroup.get('area') as FormControl;
    }

    isCountryOrAreaRequired(): boolean {
        return this.getCountrySelectedCtrl().hasValidator(Validators.required) || this.getAreaSelectedCtrl().hasValidator(Validators.required);
    }

    getCountrySelectedCtrl(): FormControl {
        return this.formGroup.get('country') as FormControl;
    }

    countrySelectionChange(country: CodeDTView): void {
        this.getCountrySelectedCtrl().patchValue(country);
        this.doCheckCountryRequired();
    }

    getLocationAddressGrp(): FormGroup {
        return this.formGroup.get('address') as FormGroup;
    }

    getLocationAddressCtrl(language: string): FormControl {
        return this.getLocationAddressGrp().controls[language] as FormControl;
    }

    areaSelectionChange(area: CodeDTView): void {
        this.getAreaSelectedCtrl().patchValue(area);
        this.doCheckCountryRequired();
    }

    closeForm() {
        this.onClose.emit(true);
    }

    addLanguageControls(language: string) {
        this.addFullAddressControls(language);
        this.addAddressDescriptionControls(language);
    }

    deleteAddress() {
        this.deleteRow.emit(this.itemIndex);
    }

    addAddress() {
        this.addRow.emit();
    }

    private addFullAddressControls(
        language: string,
        value: string = null
    ): void {
        const fullAddress = this.formGroup?.get(
            'address'
        ) as FormGroup;
        fullAddress.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }

    private addAddressDescriptionControls(
        language: string,
        value: string = null
    ): void {
        const desc = this.formGroup?.get(
            'description'
        ) as FormGroup;
        desc.addControl(
            language,
            new FormControl(value, [
                Validators.maxLength(Constants.MAX_LENGTH_LONG),
            ])
        );
    }
}
