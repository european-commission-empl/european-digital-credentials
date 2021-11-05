import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export function specifiedByValidator(requiredValues: any[]): ValidatorFn {
    return (formGroup: FormGroup): ValidationErrors => {
        return isError(formGroup, requiredValues);
    };
}

const isError = (formGroup: FormGroup, requiredFields: any[]) => {
    let hasError: boolean = false;
    // Check if required values are empty
    if (!requiredFields.some(hasValue)) {
        // If required values are empty, check if the form is empty
        hasError = isFormGroupEmpty(formGroup) ? false : true;
    }
    return { specifiedByError: hasError };
};
const hasValue = (value: any) => {
    return value ? true : false;
};

const isFormGroupEmpty = (formGroup: FormGroup) => {
    let isEmpty: boolean = true;
    Object.keys(formGroup.controls).forEach((controlName) => {
        if (formGroup.controls[controlName].value) {
            isEmpty = false;
        }
    });
    return isEmpty;
};
