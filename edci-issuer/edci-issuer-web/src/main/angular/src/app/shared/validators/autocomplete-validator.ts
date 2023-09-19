import { ValidationErrors } from '@angular/forms';
import { get as _get } from 'lodash';

export function autocompleteValidator(item): ValidationErrors | null {
    return hasItem(item) ? null : { missingContent: true };
}

const hasItem = (item): boolean => {
    return (
        (_get(item, 'value', false) && _get(item, 'value[0].id', false)) ||
        _get(item, 'value.id', false)
    );
};
