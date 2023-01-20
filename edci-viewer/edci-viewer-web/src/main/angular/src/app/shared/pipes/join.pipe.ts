import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'edciJoin',
})
export class JoinPipe implements PipeTransform {
    constructor() {}
    transform(input: Array<string | number>, sep = ','): string {
        input = input.filter((n) => n); // Removes empty elements
        return input.join(sep);
    }
}
