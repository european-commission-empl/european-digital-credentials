import { FormGroup, ValidatorFn } from '@angular/forms';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ContactPointValidator {
    public emailValidator(): ValidatorFn {
        return (formGroup: FormGroup) => {
            const emailCtrl = formGroup.get('email');
            const phoneCtrl = formGroup.get('phone');
            const contactFormCtrl = formGroup.get('contactForm');
            const addressDescriptionCtrl = formGroup.get('description');
            const addressCtrl = formGroup.get('address');
            const countryCtrl = formGroup.get('country');

            if (emailCtrl.value) {
                return null;
            }

            if ((
                phoneCtrl.value
                || contactFormCtrl.value
                || addressDescriptionCtrl.value
                || addressCtrl.value
                || countryCtrl.value)
                && !emailCtrl.value
            ) {
                return {
                    emailRequired: true
                };
            }

        };
    }

    public countryValidator(): ValidatorFn {
        return (formGroup: FormGroup) => {
            const addressDescriptionCtrl = formGroup.get('description');
            const addressCtrl = formGroup.get('address');
            const countryCtrl = formGroup.get('country');

            if (countryCtrl.value) {
                return null;
            }

            if ((addressCtrl.value || addressDescriptionCtrl.value)
                && !countryCtrl.value
            ) {
                return {
                    countryRequired: true
                };
            }

        };
    }
}
