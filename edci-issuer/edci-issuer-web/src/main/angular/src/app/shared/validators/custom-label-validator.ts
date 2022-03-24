import { FormGroup, ValidationErrors, FormArray } from '@angular/forms';

export function customLabelValidator(
    customLabel: FormGroup
): ValidationErrors | null {
    const keyLabel = customLabel.get('keyLabel').value;
    const hasMissingContent = contentCheck(customLabel.get('content').value);
    return hasError(hasMissingContent, !!keyLabel)
        ? { customLabelError: true }
        : null;
}

const contentCheck = (content): boolean => {
    let hasMissingContent = false;
    for (const language in content) {
        if (content.hasOwnProperty(language)) {
            if (!content[language]) {
                hasMissingContent = true;
            }
        }
    }
    return hasMissingContent;
};

const hasError = (hasMissingContent: boolean, hasTopic: boolean): boolean => {
    return (!hasMissingContent && !hasTopic) || (hasMissingContent && hasTopic);
};
