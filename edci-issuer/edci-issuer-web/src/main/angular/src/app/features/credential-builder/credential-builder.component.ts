import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { StepInterface } from '@core/models/step-status-bar.model';
import { EuiTabComponent } from '@eui/components/eui-tabs';
import { UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { IssuerService } from '@services/issuer.service';
import { StatusBarService } from '@services/status-bar.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

export interface TabElement {
    label: string;
    index: number;
}

@Component({
    templateUrl: './credential-builder.component.html',
    styleUrls: ['./credential-builder.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CredentialBuilderComponent implements OnInit, OnDestroy {
    parts: UxLink[] = [];
    urlActivated: string;
    selectedLanguage: string = this.translateService.currentLang;
    activeTagIndex = 0;
    openModalByRedirection = false;
    stepsStatusBar: StepInterface[] = [];
    tabElements: TabElement[] = [];
    activeTagElementIndex = 0;

    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private issuerService: IssuerService,
        private translateService: TranslateService,
        private credentialBuilderService: CredentialBuilderService,
        private statusBarService: StatusBarService,
    ) {
        this.loadTabElements();
        this.credentialBuilderService.ocbTabSelectedObservable.pipe(takeUntil(this.destroy$)).subscribe(res => {
            if (res && typeof res === 'number') {
                if (res !== 0) {
                    this.onChangeTagIndex(this.tabElements.find(tab => tab.index === res));
                    this.onActiveTabChange(1);
                } else {
                    this.onActiveTabChange(0);
                }
            }
        });
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
        this.statusBarService.setStepStatusBarActive(0);
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    loadTabElements() {
        this.tabElements = [
            {
                label: 'credential-builder.achievements',
                index: 1
            }, {
                label: 'credential-builder.learningOutcomes',
                index: 2
            }, {
                label: 'credential-builder.activities',
                index: 3
            }, {
                label: 'credential-builder.assessments',
                index: 4
            }, {
                label: 'credential-builder.organizations',
                index: 5
            }, {
                label: 'credential-builder.entitlements',
                index: 6
            }, {
                label: 'credential-builder.HTMLTemplates',
                index: 7
            }, {
                label: 'credential-builder.accreditation',
                index: 8
            },
        ];
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

    onChangeTagIndex(element: TabElement) {
        this.activeTagIndex = 1;
        this.activeTagElementIndex = element.index;
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
