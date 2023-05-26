import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export function multipleFieldsBoundValidator(
    keys: string[], groupname: string = ''
): ValidatorFn {
    return (control: FormGroup): ValidationErrors | null => {
        const filledControl = keys.find(key => control.get(key)?.value);
        if (filledControl) {
            const unFilledControl = keys.find(key => !control.get(key).value);
            if (unFilledControl) {
                return { ['multipleFieldsBoundError' + groupname] : true };
            }
        }

        return null;
    };
}
