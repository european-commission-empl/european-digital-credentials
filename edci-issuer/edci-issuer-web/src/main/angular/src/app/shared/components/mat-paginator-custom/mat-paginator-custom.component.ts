import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslateService } from '@ngx-translate/core';
import { take } from 'rxjs/operators';

export class MatPaginatorCustomComponent extends MatPaginatorIntl {

    itemsPerPageLabel = '';
    nextPageLabel = '';
    previousPageLabel = '';
    firstPageLabel = '';
    lastPageLabel = '';

    constructor(private translateService: TranslateService) {
        super();
    }

    injectTranslateService(translate: TranslateService) {
        this.translateService = translate;

        this.translateService.onLangChange.subscribe(() => {
            this.translateLabels();
        });

        this.translateLabels();
    }

    translateLabels() {
        this.translateService.setDefaultLang('en');
        this.translateService.get('paginator.items-per-page').pipe(take(1)).subscribe((translated: string) => {
            this.itemsPerPageLabel = translated;
            this.changes.next();
        });
        this.translateService.get('paginator.next-page').pipe(take(1)).subscribe((translated: string) => {
            this.nextPageLabel = translated;
            this.changes.next();
        });
        this.translateService.get('paginator.previous-page').pipe(take(1)).subscribe((translated: string) => {
            this.previousPageLabel = translated;
            this.changes.next();
        });
        this.translateService.get('paginator.first-page').pipe(take(1)).subscribe((translated: string) => {
            this.firstPageLabel = translated;
            this.changes.next();
        });
        this.translateService.get('paginator.last-page').pipe(take(1)).subscribe((translated: string) => {
            this.lastPageLabel = translated;
            this.changes.next();
        });
    }

}
