import { FormGroup, ValidationErrors } from '@angular/forms';

export function webDocumentValidator(
    webDocument: FormGroup
): ValidationErrors | null {
    let error = null;
    const title = webDocument.get('webDocumentTitle');
    const content = webDocument.get('webDocumentContent');
    if (webDocument && title && content) {
        if (!content.value && title.value) {
            error = { webDocumentError: true };
        }
    }
    return error;
}
