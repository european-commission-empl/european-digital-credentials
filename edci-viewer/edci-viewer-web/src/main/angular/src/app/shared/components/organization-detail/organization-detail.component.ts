import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { EntityLinkService } from 'src/app/core/services/entity-link.service';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { GroupFieldView, OrganizationTabView, VerificationCheckView } from '../../swagger';
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

    mailAddress: string[];

    // Download file
    fileName: string;
    fileUrl: string;

    @Input() titleSection: string;
    @Input() showVP: boolean;
    isTabExpanded = true;
    sealLongDescription: string;
    language: string = this.shareDataService.toolbarLanguage;
    verificationSteps: VerificationCheckView[] = this.shareDataService
        .verificationSteps
        ? this.shareDataService.verificationSteps
        : JSON.parse(sessionStorage.getItem('verificationSteps'));
    destroy$: Subject<boolean> = new Subject<boolean>();
    constructor(
        private entityLinkService: EntityLinkService,
        private shareDataService: ShareDataService,
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
        if (this.organisation.mandateEvidence && this.organisation.mandateEvidence.embeddedEvidence[0]) {
            this.fileName = this.organisation.mandateEvidence.embeddedEvidence[0].title;

            let fileType = this.organisation?.mandateEvidence?.embeddedEvidence[0].contentType;
            let fileContent = this.organisation?.mandateEvidence?.embeddedEvidence[0].content.split(',')[1];
            let file = this.base64ToBlob(fileContent, fileType);

            this.fileUrl = window.URL.createObjectURL(file);
        }

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

    getEmailList(group: GroupFieldView) {
        return group.contactPoint.flatMap(cP => cP.emailAddress.map(eA => eA.id));
    }

    private getSealLongDescription(): string {
        let longDescription = null;
        if (this.verificationSteps) {
            this.verificationSteps.forEach((step) => {
                if (
                    step.type.link ===
                    'http://data.europa.eu/snb/verification/f9c2016fe9' &&
                    Object.keys(step.longDescrAvailableLangs).length > 0
                ) {
                    longDescription = step.longDescrAvailableLangs;
                }
            });
        }
        return longDescription;
    }

    private base64ToBlob(b64Data, fileType, sliceSize = 512) {
        b64Data = b64Data.replace(/\s/g, '');
        let byteCharacters = window.atob(b64Data);
        let byteArrays = [];
        for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
            let slice = byteCharacters.slice(offset, offset + sliceSize);

            let byteNumbers = new Array(slice.length);
            for (let i = 0; i < slice.length; i++) {
                byteNumbers[i] = slice.charCodeAt(i);
            }
            let byteArray = new Uint8Array(byteNumbers);
            byteArrays.push(byteArray);
        }
        return new Blob(byteArrays, { type: fileType });
    }
}
