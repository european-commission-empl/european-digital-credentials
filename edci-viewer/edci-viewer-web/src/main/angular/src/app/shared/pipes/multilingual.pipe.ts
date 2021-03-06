import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'edciTranslate',
})
export class MultilingualPipe implements PipeTransform {
    constructor() {}
    transform(
        input: { [key: string]: string },
        activeLanguage: string
    ): string {
        let label: string;
        for (let language in input) {
            if (language === activeLanguage) {
                label = input[language];
                break;
            }
        }
        return label;
    }
}
