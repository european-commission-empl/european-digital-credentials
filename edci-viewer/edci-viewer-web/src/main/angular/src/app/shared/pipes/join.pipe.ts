import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'edciJoin',
})
export class JoinPipe implements PipeTransform {
    constructor() {}
    transform(input: Array<string | number | Array<string | number>>, sep = ','): string {
        const aux = [];
        input.forEach(item => {
            if (Array.isArray(item)) {
                aux.push(...item);
            } else {
                aux.push(item);
            }
        });
        return aux.filter((n) => n).join(sep);
    }
}
