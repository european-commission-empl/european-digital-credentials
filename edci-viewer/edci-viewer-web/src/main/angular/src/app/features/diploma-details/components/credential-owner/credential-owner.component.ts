import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { CredentialSubjectTabView } from 'src/app/shared/swagger/model/credentialSubjectTabView';

@Component({
    selector: 'edci-viewer-credential-owner',
    templateUrl: './credential-owner.component.html',
    styleUrls: ['./credential-owner.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CredentialOwnerComponent implements OnInit, OnDestroy {
    subject: CredentialSubjectTabView = this.shareDataService.activeEntity;

    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private shareDataService: ShareDataService) {}

    ngOnInit(): void {
        this.shareDataService
            .changeEntitySelection()
            .takeUntil(this.destroy$)
            .subscribe((subject) => {
                this.subject = subject;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
    existContactInfo(): boolean {
        for (let i = 0; i < this.subject.contactPoint.length; i++) {
            if (
                this.subject.contactPoint[i].address ||
                this.subject.contactPoint[i].phone ||
                this.subject.contactPoint[i].email
            ) {
                return true;
            }
        }
        return false;
    }
}
