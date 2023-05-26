import { AbstractControl, ValidationErrors } from '@angular/forms';

export function walletEmailValidator(
    control: AbstractControl
): ValidationErrors | null {
    const emailAddress = control.get('emailAddress').value.trim();
    const walletAddress = control.get('walletAddress').value.trim();
    return !emailAddress && !walletAddress ? { missingAddress: true } : null;
}
