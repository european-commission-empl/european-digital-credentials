import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'appOcbModalBreadcrumb',
})
export class OcbModalBreadcrumbPipe implements PipeTransform {
    transform(titleList: string[], lastTitle: string): string {
        return `${titleList.join(' > ')} > ${lastTitle}`;
    }
}
