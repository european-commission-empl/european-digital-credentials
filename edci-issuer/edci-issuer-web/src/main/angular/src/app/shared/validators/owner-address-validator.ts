import { FormGroup, ValidationErrors } from '@angular/forms';

export function ownerAddressValidator(
    ownerAddress: FormGroup
): ValidationErrors | null {
    const address = ownerAddress.get('address').value;
    const addressCountry = ownerAddress.get('addressCountry').value;
    let error = null;
    if (address && !addressCountry) {
        error = { ownerAddressError: true };
    }
    return error;
}
