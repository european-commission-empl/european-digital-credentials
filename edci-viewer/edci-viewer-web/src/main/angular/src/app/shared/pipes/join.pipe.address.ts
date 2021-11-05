import { Pipe, PipeTransform } from '@angular/core';
import { AddressFieldView } from '../swagger';

@Pipe({
    name: 'edciAdressJoin',
})
export class JoinPipeAddress implements PipeTransform {
    constructor() {}
    transform(input: Array<AddressFieldView>, sep = ','): string {
        let stringifiedAddress: string = '';
        for (let address of input) {
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
