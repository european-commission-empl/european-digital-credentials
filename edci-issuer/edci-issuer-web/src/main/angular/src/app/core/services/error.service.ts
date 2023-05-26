import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';

export interface OKNotification {
    severity: 'info' | 'success' | 'warn' | 'error';
    summary: string;
    detail?: string;
}

@Injectable({
    providedIn: 'root',
})
export class NotificationService {
    constructor(
        private messageService: MessageService,
        private translateService: TranslateService
    ) {}

    showNotification(notification: HttpErrorResponse | OKNotification) {
        console.log(notification);
    /* Error */
        if (notification instanceof HttpErrorResponse) {
            this.messageService.add({
                severity: 'error',
                summary: this.translateService.instant('common.error'),
                detail: notification.error.message
                    ? notification.error.message
                    : notification.message,
                life: 5000,
            });
        } else {
            /* Info / Success */
            this.messageService.add({
                severity: notification.severity,
                summary: notification.summary,
                detail: notification.detail,
                life: 5000,
            });
        }
    }

    showNotificationText(
        summary = '',
        notification: string,
        error = false
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
