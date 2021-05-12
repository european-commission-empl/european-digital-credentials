import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';

@Injectable()
export class DisplayErrorService {
    isGrowlSticky: boolean = false;
    isGrowlMultiple: boolean = true;

    constructor(
        private translateService: TranslateService,
        private messageService: MessageService
    ) {}

    displayRequestErrorGrowl(response: HttpErrorResponse): void {
        this.messageService.add({
            severity: 'error',
            summary: this.translateService.instant('error'),
            detail: response.error.message,
            life: 5000,
        });
    }

    showNotificationText(
        summary: string = '',
        notification: string,
        error: boolean = false
    ) {
        /* Error */
        if (error) {
            this.messageService.add({
                severity: 'error',
                summary: summary,
                detail: notification,
                life: 5000,
            });
        } else {
            /* Success */
            this.messageService.add({
                severity: 'success',
                summary: summary,
                detail: notification,
                life: 5000,
            });
        }
    }
}
