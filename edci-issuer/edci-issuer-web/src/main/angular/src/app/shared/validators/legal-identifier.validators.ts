import { FormGroup, ValidationErrors } from '@angular/forms';

export function legalIdentifierValidator(
    legalIdentifier: FormGroup
): ValidationErrors | null {
    const content = legalIdentifier.get('content').value;
    const spatialId = legalIdentifier.get('spatialId').value;
    let error = null;
    if ((content && !spatialId) || (!content && spatialId)) {
        error = { legalIdentifierError: true };
    }
    return error;
}
