import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import {
    CredentialSubjectTabView,
    V1Service,
    VerificationCheckView,
} from 'src/app/shared/swagger';

@Component({
    selector: 'edci-viewer-verification',
    templateUrl: './verification.component.html',
    styleUrls: ['./verification.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class VerificationComponent implements OnInit, OnDestroy {
    XMLFile: Blob = new Blob([sessionStorage.getItem('diplomaXML')], {
        type: 'text/xml',
    });
    credentialOwner: CredentialSubjectTabView = this.shareDataService
        .credentialSubject;
    credId: string = sessionStorage.getItem('credId') || null;
    userId: string = sessionStorage.getItem('userId') || null;
    shareLink: string = sessionStorage.getItem('shareLink');
    language: string = this.shareDataService.toolbarLanguage;
    isPreview: boolean;
    steps: VerificationCheckView[] = this.shareDataService.verificationSteps;
    isVerified: boolean;
    destroy$: Subject<boolean> = new Subject<boolean>();
    CONTROLLEDLISTCOLORS = {
        RED: 'http://data.europa.eu/snb/verification-status/9d26eb9a37',
        GREEN: 'http://data.europa.eu/snb/verification-status/9895008394'
    };

    constructor(
        private apiService: V1Service,
        private router: Router,
        private shareDataService: ShareDataService
    ) {
        /**
         * Reset steps since language will change.
         * No need to 'getVerificationData' since
         * the component will be destroyed and regenerated
         * thus triggering the onInit functions again
         */
        this.shareDataService.toolbarLanguageChange
            .takeUntil(this.destroy$)
            .subscribe((res) => {
                this.saveSteps(null);
            });
    }

    ngOnInit() {
        this.isPreview = !!sessionStorage.getItem('isPreview');
        if (!this.steps) {
            this.getVerificationData();
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    private getVerificationData() {
        if (this.credId) {
            this.getCredentialVerificationByWalletAddressID();
        } else if (this.shareLink) {
            this.getCredentialVerificationByShareLink();
        } else {
            this.getCredentialVerificationByXML();
        }
    }

    private getCredentialVerificationByWalletAddressID(): void {
        this.apiService
            .getCredentialVerificationByWalletAddressID(
                this.userId,
                this.credId,
                this.language
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (steps: VerificationCheckView[]) => {
                    this.saveSteps(steps);
                },
                (response: HttpErrorResponse) => {
                    this.errorResponse(response);
                }
            );
    }

    private getCredentialVerificationByShareLink(): void {
        this.apiService
            .getShareLinkCredentialVerification(this.shareLink, this.language)
            .takeUntil(this.destroy$)
            .subscribe(
                (steps: VerificationCheckView[]) => {
                    this.saveSteps(steps);
                },
                (response: HttpErrorResponse) => {
                    this.errorResponse(response);
                }
            );
    }

    private getCredentialVerificationByXML(): void {
        this.apiService
            .getCredentialVerification(this.XMLFile, this.language)
            .subscribe(
                (steps: VerificationCheckView[]) => {
                    this.saveSteps(steps);
                },
                (response: HttpErrorResponse) => {
                    this.errorResponse(response);
                }
            );
    }

    private errorResponse(response: HttpErrorResponse) {
        this.router.navigate(['/diploma-details/diploma']);
    }

    private saveSteps(steps: VerificationCheckView[]) {
        this.steps = steps;
        this.shareDataService.verificationSteps = steps;
        sessionStorage.setItem('verificationSteps', JSON.stringify(steps));
        if (steps) {
            this.isVerified = !steps.find((value) => value.status.link !== this.CONTROLLEDLISTCOLORS.GREEN);
        }
    }
}
