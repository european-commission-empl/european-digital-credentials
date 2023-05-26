import {
    AbstractControl,
    ValidationErrors
} from '@angular/forms';

export function requiredListValidator(
    control: AbstractControl
): ValidationErrors | null {
    const result = control.value && control.value.length ? null : {
        requiredList: true
    };
    return result;
}
