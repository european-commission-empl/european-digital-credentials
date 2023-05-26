import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export function atLeastOneRequiredValidator(
    keys: string[], groupname: string = ''
): ValidatorFn {
    return (control: FormGroup): ValidationErrors | null => {
        const filledControl = keys.find(key => control.get(key)?.value);

        /*const touchedControl = keys.find(key => control.get(key)?.touched);

        if (touchedControl) {
            const untouchedControl = control.get(keys.find(key => !control.get(key)?.touched));

            if (untouchedControl) {
                untouchedControl.markAsTouched();
            }
        }*/

        if (!filledControl) {
            return { ['atLeastOneRequiredError' + groupname] : true };
        }

        return null;
    };
}
