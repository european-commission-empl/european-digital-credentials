import { Injectable } from '@angular/core';
import { UxAppShellService } from '@eui/core';
import { TranslateService } from '@ngx-translate/core';
import { MODAL_LIMITS } from '@shared/constants';
import { BehaviorSubject } from 'rxjs';
import { CredentialBuilderService } from './credential-builder.service';
import { NotificationService } from './error.service';

@Injectable({
    providedIn: 'root',
})
export class ModalsService {
    private modalsOpen$ = new BehaviorSubject<number>(0);
    modalsOpenObservable = this.modalsOpen$.asObservable();

    constructor(
        public uxService: UxAppShellService,
        private credentialBuilderService: CredentialBuilderService,
        private notificationService: NotificationService,
        private translateService: TranslateService
    ) {}

    openModal(modalTitle: any, oid: any) {
        if (this.modalsOpen$.value < MODAL_LIMITS) {
            const newEntityModalId =
                this.credentialBuilderService.generateNewIdModal(modalTitle);
            this.uxService.openModal(newEntityModalId);
            this.modalsOpen$.next(this.modalsOpen$.value + 1);
            const modal = {
                isOpen: true,
                modalId: newEntityModalId,
                oid,
            };
            return modal;
        } else {
            /* this.notificationService.showNotification({
                severity: "error",
                summary: this.translateService.instant("common.delete"),
                detail: this.translateService.instant(
                    "credential-builder.operationSuccessful"
                ),
            }); */
            const modal = {
                isOpen: false,
                modalId: null,
                oid,
            };
            return modal;
        }
    }

    closeModal() {
        if (this.modalsOpen$.value > 0) {
            this.uxService.closeModal(
                this.credentialBuilderService.getIdFromLastModalAndRemove()
            );
            this.modalsOpen$.next(this.modalsOpen$.value - 1);
            return false;
        } else {
            return true;
        }
    }
}
