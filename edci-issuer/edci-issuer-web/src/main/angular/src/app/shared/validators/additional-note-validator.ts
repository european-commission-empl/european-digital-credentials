import { FormGroup, ValidationErrors } from '@angular/forms';

export function additionalNoteValidator(
    additionalNote: FormGroup
): ValidationErrors | null {
    const topic = additionalNote.get('topic').value;
    const hasContent = contentCheck(additionalNote.get('content').value);
    return hasError(hasContent, !!topic) ? { additionalNoteError: true } : null;
}

const contentCheck = (content): boolean => {
    let hasContent = false;
    for (const language in content) {
        if (content.hasOwnProperty(language)) {
            if (content[language]) {
                hasContent = true;
            }
        }
    }
    return hasContent;
};

const hasError = (hasContent: boolean, hasTopic: boolean): boolean => {
    return (hasContent && !hasTopic) || (!hasContent && hasTopic);
};
