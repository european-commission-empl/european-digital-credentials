import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Subject } from 'rxjs';
import { Router } from '@angular/router';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import { takeUntil, take } from 'rxjs/operators';
import { AssessmentSpecLiteView, DiplomaSpecLiteView, V1Service } from '@shared/swagger';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-accreditation',
    templateUrl: './accreditation.component.html',
    styleUrls: ['./accreditation.component.scss'],
})
export class AccreditationComponent implements OnInit, OnDestroy {
    @Input() openModal = false;
    destroy$: Subject<boolean> = new Subject<boolean>();
    loading: boolean;

    accreditations: DiplomaSpecLiteView[] = [];
    selectedAccreditation: AssessmentSpecLiteView;
    noAccreditationsAddedYet = false;

    /* lang defaults */
    language: string[];
    selectedLanguage: string = this.translateService.currentLang;

    /* table defaults */
    totalItems = 0;
    perPage = 7;
    page = 0;
    sort = 'auditDAO.updateDate';
    direction = 'DESC';

    /* filter input default value */
    inputSearchText = '';

    constructor(
      private readonly translateService: TranslateService,
      private api: V1Service,
      private notificationService: NotificationService,
      private readonly router: Router,
      private credentialBuilderService: CredentialBuilderService,
    ) {}

    ngOnInit(): void {
        this.getAccreditations();

        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                if (this.language) {
                    this.setLanguage(event.lang);
                }
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    getAccreditations() {
        this.loading = true;
        this.api.listAccreditation(
            this.sort,
            this.direction,
            this.page,
            this.perPage,
            this.inputSearchText,
            this.translateService.currentLang
        ).subscribe((v) => {
            this.accreditations = v.content;
            this.totalItems = v.page.totalElements;
            this.selectedAccreditation = this.accreditations[0];
            if (this.inputSearchText === '' && v.content?.length === 0 ) {
                this.noAccreditationsAddedYet = true;
            }
            this.loading = false;
        });
    }

    getLabelTranslations() {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                if (this.language) {
                    this.setLanguage(event.lang);
                }
            });
    }

    createAccreditation(): void {
        this.router.navigateByUrl('credential-builder/accreditation');
    }

    onEmittedOCBQueryChange(value: string): void {
        this.inputSearchText = value;
        this.getAccreditations();
    }

    onDelete(oid: number): void {
        this.loading = true;
        this.api.deleteAccreditation(oid, this.translateService.currentLang).subscribe({
            next: (v) => {
                this.getAccreditationList(this.totalItems - 1);
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.delete'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });
            },
            error: (e) => {
                console.error(e);
                this.loading = false;
            },
            complete: () => this.loading = false
        });
        /* observable into getAccreditation to refresh table */
    }

    onEdit(oid: number): void {
        this.router.navigateByUrl('credential-builder/accreditation/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api.duplicateAccreditation(oid, this.translateService.currentLang).pipe(take(1)).subscribe({
            next: (v) => {
                this.credentialBuilderService.redirectToPage.next(0);
                this.notificationService.showNotification({
                    severity: 'success',
                    summary:
                        this.translateService.instant('common.duplicate'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });
            },
            error: (e) => {
                console.error(e);
                this.loading = false;
            },
            complete: () => {
                this.loading = false;
            }
        });
    }

    onPage(event): void {
        this.page = event.page;
        this.perPage = event.pageSize;
        this.getAccreditations();
    }

    onSort(event) {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getAccreditations();
            }
        }
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.selectedAccreditation.defaultLanguage;
    }

    private getAccreditationList(itemsLeft?: number) {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        if (!this.noAccreditationsAddedYet) { this.getAccreditations(); }
    }
}
