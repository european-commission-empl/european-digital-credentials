import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { DateFormatService } from '@services/date-format.service';

export function dateRangeValidator(
    initialDateKey: string, finalDateKey: string, dateFormatService: DateFormatService
): ValidatorFn {
    return (group: FormGroup): ValidationErrors | null => {
        const initialDate = group.get(initialDateKey)?.value;
        const finalDate = group.get(finalDateKey)?.value;

        if ((initialDate && finalDate) && !dateFormatService.validateDates(initialDate, finalDate)) {
            return { dateRangeValidatorError : true };
        }

        return null;
    };
}
