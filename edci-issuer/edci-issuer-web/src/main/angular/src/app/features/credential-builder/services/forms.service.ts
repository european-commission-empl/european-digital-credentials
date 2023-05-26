import { Injectable } from '@angular/core';
import { AddressDCView, LocationDCView, NoteDTView } from '@shared/swagger';
import {
    FormArray,
    FormControl,
    FormGroup,
    Validators,
    FormBuilder,
} from '@angular/forms';
import { get as _get } from 'lodash';

@Injectable({
    providedIn: 'root'
})
export class FormsService {

    public getDateByFieldNameFromObject(fieldName: string, object: any): Date | null {
        const date = _get(object, fieldName, null);
        return date ? new Date(date) : null;
    }

    public hasRequiredValidator(formControl: FormControl): boolean {
        return formControl ? formControl.hasValidator(Validators.required) : false;
    }

    public getFormGroupControl(formGroup: FormGroup, name: string): FormControl {
        return formGroup.get(name) ? formGroup.get(name) as FormControl : null;
    }

    public getFormGroupMultiLanguageControl(formGroup: FormGroup, name: string , language: string): FormControl {
        return (formGroup.get(name) as FormGroup).get(language) as FormControl;
    }

    public getLocationDCView(formArray: FormArray): LocationDCView {
        const location = this.getLocationDCViews(formArray);
        return location && location.length > 0 ? location[0] : null;
    }

    public getLocationDCViews(formArray: FormArray): LocationDCView[] {
        const locations: LocationDCView[] = [];
        type NewType = FormGroup;

        formArray.controls.forEach((element: NewType) => {
            const descriptionGroup = element.get('description') as FormGroup;
            const description: NoteDTView = { contents : [] };

            Object.keys(descriptionGroup.controls).forEach(languageKey => {
                if (descriptionGroup.get(languageKey).value) {
                    description.contents.push({
                        content : descriptionGroup.get(languageKey).value,
                        language : languageKey
                    });
                }
            });

            const fullAddress: NoteDTView = { contents : [] };
            const addressGroup = element.get('address') as FormGroup;

            Object.keys(addressGroup.controls).forEach(languageKey => {
                if (addressGroup.get(languageKey).value) {
                    fullAddress.contents.push({
                        content : addressGroup.get(languageKey).value,
                        language : languageKey
                    });
                }
            });

            const addresses: AddressDCView[] = [{
                fullAddress : fullAddress?.contents?.length > 0 ? fullAddress : null,
                countryCode : element.get('country').value
            }];

            const location: LocationDCView = {
                spatialCode: element.get('area').value ? [element.get('area').value] : [],
                description : description,
                address : addresses
            };

            if (
                element.get('country').value
            ) {
                locations.push(location);
            }
        });
        return locations;
    }
}
