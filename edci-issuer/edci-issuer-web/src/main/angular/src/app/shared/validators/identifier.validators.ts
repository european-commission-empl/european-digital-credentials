import { FormGroup, ValidationErrors } from '@angular/forms';

export function identifierValidator(
    legalIdentifier: FormGroup
): ValidationErrors | null {
    let error = null;
    const content = legalIdentifier.get('id');
    const identifierSchemeAgencyName = legalIdentifier.get(
        'identifierSchemeAgencyName'
    );
    if (legalIdentifier && content && identifierSchemeAgencyName) {
        if (!content.value && identifierSchemeAgencyName.value) {
            error = { identifierError: true };
        }
    }
    return error;
}
