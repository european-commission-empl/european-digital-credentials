import {
    AbstractControl,
    ValidationErrors
} from '@angular/forms';

export function noSpaceValidator(
    control: AbstractControl
): ValidationErrors | null {
    return control.value && control.value.trim() === ''
        ? { onlySpaceError: true }
        : null;
}
