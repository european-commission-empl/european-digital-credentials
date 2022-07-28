import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { EuiTabComponent } from '@eui/components/eui-tabs';
import { UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { IssuerService } from '@services/issuer.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    templateUrl: './credential-builder.component.html',
    styleUrls: ['./credential-builder.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CredentialBuilderComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];
    urlActivated: string;
    selectedLanguage: string = this.translateService.currentLang;
    activeTagIndex: number = 0;
    openModalByRedirection = false;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private issuerService: IssuerService,
        private translateService: TranslateService
    ) {
        this.issuerService.breadcrumbSubject
            .pipe(takeUntil(this.destroy$))
            .subscribe((url: string) => {
                this.urlActivated = url;
                this.loadBreadcrumb();
            });
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.selectedLanguage = event.lang;
                this.loadBreadcrumb();
            });
    }

    ngOnInit() {
        this.loadBreadcrumb();
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onActiveTabChange(activeTagIndex: number) {
        if (this.activeTagIndex !== activeTagIndex) {
            this.openModalByRedirection = false;
            this.activeTagIndex = activeTagIndex;
        }
    }

    onTabSelected(event: { index: number; tab: EuiTabComponent }) {
        this.activeTagIndex = event.index;
    }

    private loadBreadcrumb() {
        this.parts = [
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.digitallySealedCredentials'
                ),
                url: '/home',
            }),
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.credentialBuilder'
                ),
            }),
        ];
    }
}
