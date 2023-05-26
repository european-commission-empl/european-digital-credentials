import { AbstractControl, ValidationErrors } from '@angular/forms';
import { Constants } from '@shared/constants';
import moment from 'moment';

export function dateValidator(control: AbstractControl): ValidationErrors | null {
    let error = null;
    if (control.value && !moment(control.value, Constants.MEDIUM_DATE).isValid()) {
        error = { invalidDate: true };
    }
    return error;
}
