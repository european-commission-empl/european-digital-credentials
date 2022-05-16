import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { UxLink } from '@eui/core';
import { IssuerService } from '@services/issuer.service';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs/Subject';
import { takeUntil } from 'rxjs/operators';

@Component({
    templateUrl: './create.component.html',
    styleUrls: ['./create.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class CreateComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];
    urlActivated: string;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private issuerService: IssuerService,
        private translateService: TranslateService
    ) {
        this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe((event: LangChangeEvent) => {
            this.loadBreadcrumb();
        });
        this.issuerService.breadcrumbSubject.pipe(takeUntil(this.destroy$)).subscribe((url: string) => {
            this.urlActivated = url;
            this.loadBreadcrumb();
        });
    }

    ngOnInit() {
        this.loadBreadcrumb();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    private loadBreadcrumb() {
        this.parts = [
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.digitallySealedCredentials'
                ),
                url: '/home'
            })
        ];

        if (this.urlActivated === '/create/overview') {
            this.parts.push(
                new UxLink({
                    label: this.translateService.instant('common.check'),
                    url: '/create/overview'
                })
            );
        }
    }
}
