import { AbstractControl, ValidationErrors } from '@angular/forms';
import * as moment from 'moment';

export function dateValidator(control: AbstractControl): ValidationErrors | null {
    let error = null;
    if (control.value && !moment(control.value).isValid()) {
        error = { invalidDate: true };
    }
    return error;
}
