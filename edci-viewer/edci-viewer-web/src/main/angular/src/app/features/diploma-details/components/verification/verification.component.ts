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
import { TranslateService } from '@ngx-translate/core';

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
    credentialOwner: CredentialSubjectTabView =
        this.shareDataService.credentialSubject;
    credId: string = sessionStorage.getItem('credId') || null;
    userId: string = sessionStorage.getItem('userId') || null;
    shareLink: string = sessionStorage.getItem('shareLink');
    language: string = this.shareDataService.toolbarLanguage;
    isPreview: boolean;
    steps: VerificationCheckView[] = this.shareDataService.verificationSteps;
    destroy$: Subject<boolean> = new Subject<boolean>();
    CONTROLLED_LIST_COLORS = {
        RED: 'http://data.europa.eu/snb/verification-status/9d26eb9a37',
        GREEN: 'http://data.europa.eu/snb/verification-status/9895008394',
        GREY: 'http://data.europa.eu/snb/verification-status/641f0c5e5d',
    };

    ignoredVerificationStepTypeLinks: string[] = [
        'http://data.europa.eu/snb/verification/271aef9eb4',
        'http://data.europa.eu/snb/verification/e2bbc86a28',
    ];

    ribbonLabel = {
        correct: '',
        incorrect: '',
        warning: '',
    };
    ribbonState: number = this.shareDataService.ribbonState;
    constructor(
        private apiService: V1Service,
        private router: Router,
        private shareDataService: ShareDataService,
        private translateService: TranslateService
    ) {
        /**
         * Reset steps since language will change.
         * No need to 'getVerificationData' since
         * the component will be destroyed and regenerated
         * thus triggering the onInit functions again
         */
        this.shareDataService.toolbarLanguageChange
            .takeUntil(this.destroy$)
            .subscribe((language) => {
                this.getRibbonTranslation(language);
            });
    }

    ngOnInit() {
        if (!this.language) {
            this.language = this.translateService.currentLang;
        }
        this.isPreview = !!sessionStorage.getItem('isPreview');
        if (!this.steps) {
            this.getVerificationData();
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    private getRibbonTranslation(language: string): void {
        this.translateService
            .getTranslation(language)
            .subscribe((translations) => {
                // REVIEW:  Waiting For translation  files to be filled
                this.ribbonLabel.correct =
                    translations['details.verification-tab.ribbon-correct'] ||
                    'Verified';
                this.ribbonLabel.incorrect =
                    translations['details.verification-tab.ribbon-incorrect'] ||
                    'Verification Failed';
                this.ribbonLabel.warning =
                    translations['details.verification-tab.ribbon-warning'] ||
                    'Partially Verified';
                this.steps[this.steps.length - 1].type.title =
                    this.ribbonState === 2
                        ? this.ribbonLabel.incorrect
                        : this.ribbonState === 1
                        ? this.ribbonLabel.warning
                        : this.ribbonLabel.correct;
            });
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
        if (steps) {
            this.shareDataService.setVerificationSteps(steps);
            this.getRibbonTranslation(this.language);
            // Ribbon state => 0 OK, 1 Grey, 2 KO
            let ribbonState: number = 0;
            for (const step of steps) {
                if (
                    !this.ignoredVerificationStepTypeLinks.find(
                        (str) => str === step.type.link
                    )
                ) {
                    if (step.status.link === this.CONTROLLED_LIST_COLORS.RED) {
                        ribbonState = 2;
                        break;
                    }
                    if (step.status.link === this.CONTROLLED_LIST_COLORS.GREY) {
                        ribbonState = 1;
                    }
                }
            }
            this.addRibbonStep(steps, ribbonState);
        }
    }

    private addRibbonStep(
        steps: VerificationCheckView[],
        ribbonState: number
    ): void {
        // Ribbon state => 0 OK, 1 Grey, 2 KO
        this.ribbonState = ribbonState;
        this.steps = steps.concat({
            status: {
                link:
                    ribbonState === 2
                        ? 'ribbon-failed'
                        : ribbonState === 1
                        ? 'ribbon-warning'
                        : 'ribbon',
                title:
                    ribbonState === 2
                        ? 'ribbon-failed'
                        : ribbonState === 1
                        ? 'ribbon-warning'
                        : 'ribbon',
            },
            type: {
                title:
                    ribbonState === 2
                        ? this.ribbonLabel.incorrect
                        : ribbonState === 1
                        ? this.ribbonLabel.warning
                        : this.ribbonLabel.correct,
            },
        });
        this.shareDataService.verificationSteps = this.steps;
        this.shareDataService.ribbonState = ribbonState;
        sessionStorage.setItem('verificationSteps', JSON.stringify(this.steps));
    }
}
