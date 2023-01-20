import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { OrganizationTabView, VerificationCheckView } from '../../swagger';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-viewer-organization-detail',
    templateUrl: './organization-detail.component.html',
    styleUrls: ['./organization-detail.component.scss'],
})
export class OrganizationDetailComponent implements OnInit, OnDestroy {
    private _organisation: OrganizationTabView;
    @Input()
    set organisation(value: OrganizationTabView) {
        this._organisation = value;
        this.isTabExpanded = true;
    }
    get organisation(): OrganizationTabView {
        return this._organisation;
    }
    @Input() titleSection: string;
    @Input() showVP: boolean;
    isTabExpanded = true;
    sealLongDescription: { [key: string]: string };
    language: string = this.shareDataService.toolbarLanguage;
    verificationSteps: VerificationCheckView[] = this.shareDataService
        .verificationSteps
        ? this.shareDataService.verificationSteps
        : JSON.parse(sessionStorage.getItem('verificationSteps'));
    destroy$: Subject<boolean> = new Subject<boolean>();
    constructor(
        private entityLinkService: EntityLinkService,
        private shareDataService: ShareDataService
    ) {
        this.shareDataService.toolbarLanguageChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((newLanguage) => {
                this.language = newLanguage;
            });

        this.shareDataService
            .getVerificationSteps()
            .pipe(takeUntil(this.destroy$))
            .subscribe((verificationSteps) => {
                this.verificationSteps = verificationSteps;
                this.sealLongDescription = this.getSealLongDescription();
            });
    }

    ngOnInit() {
        this.shareDataService.toolbarLanguageObservable.subscribe(language => {
            this.language = language;
        });
        this.sealLongDescription = this.getSealLongDescription();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    changeSelection(id: string): void {
        this.entityLinkService.changeSelection(id);
    }

    private getSealLongDescription(): { [key: string]: string } {
        let longDescription: { [key: string]: string } = null;
        if (this.verificationSteps) {
            this.verificationSteps.forEach((step) => {
                if (
                    step.type.link ===
                    'http://data.europa.eu/snb/verification/f9c2016fe9'
                ) {
                    longDescription = step.longDescrAvailableLangs;
                }
            });
        }
        return longDescription;
    }
}
