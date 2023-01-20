import { Pipe, PipeTransform } from '@angular/core';
import { AddressFieldView } from '../swagger';

@Pipe({
    name: 'edciAddressJoin',
})
export class JoinAddressPipe implements PipeTransform {
    constructor() {}
    transform(input: Array<AddressFieldView>, sep = ','): string {
        let stringifiedAddress = '';
        for (const address of input) {
            if (stringifiedAddress !== '') {
                stringifiedAddress += ' | ';
            }
            if (address.fullAddress) {
                stringifiedAddress += address.fullAddress;
            }
            if (address.fullAddress && address.countryCode) {
                stringifiedAddress += ', ';
            }
            if (address.countryCode) {
                stringifiedAddress += address.countryCode;
            }
        }
        return stringifiedAddress;
    }
}
