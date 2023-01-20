import {
    Component,
    OnInit,
    OnDestroy,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    V1Service,
    AssessmentSpecLiteView,
    PagedResourcesDiplomaSpecLiteView,
    DiplomaSpecLiteView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';

@Component({
    selector: 'edci-html-templates',
    templateUrl: './html-templates.component.html',
    styleUrls: ['./html-templates.component.scss'],
})
export class HTMLTemplatesComponent implements OnInit, OnDestroy, OnChanges {
    htmlTemplates: DiplomaSpecLiteView[] = [];
    activeHTMLTemplate: AssessmentSpecLiteView;
    htmlTemplateToEditOid: number;
    modalTitle: string;
    @Input() openModal = false;
    modalEdited = false;
    loading = true;
    totalItems = 0;
    perPage = 7;
    page = 0;
    sort = 'auditDAO.updateDate';
    direction = 'DESC';
    inputSearchText = '';
    noHtmlTemplatesAddedYet = false;
    selectedRowIndex = 0;
    availableLanguages: string[];
    selectedLanguage: string = this.translateService.currentLang;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private translateService: TranslateService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService,
        private router: Router
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                if (this.availableLanguages) {
                    this.setLanguage(event.lang);
                }
            });
    }

    ngOnInit() {
        if (this.openModal) {
            this.newHTMLTemplates();
        }
        this.getHTMLTemplateList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.openModal && changes.openModal.currentValue) {
            this.newHTMLTemplates();
        }
    }

    onDelete(oid: number): void {
        this.loading = true;
        this.api
            .deleteDiploma_1(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.loading = true;
                this.getHTMLTemplateList(this.totalItems - 1);
                this.notificationService.showNotification({
                    severity: 'success',
                    summary: this.translateService.instant('common.delete'),
                    detail: this.translateService.instant(
                        'credential-builder.operationSuccessful'
                    ),
                });
            }).add(() => this.loading = false);
    }

    onEdit(oid: number): void {
        this.router.navigateByUrl('credential-builder/html-templates/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateDiploma(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                () => {
                    this.moveToFirstPage();
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary:
                            this.translateService.instant('common.duplicate'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                },
                () => (this.loading = false)
            );
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.page;
        this.perPage = event.pageSize;
        this.getHTMLTemplateList();
    }

    onSort(event): void {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.loading = true;
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getHTMLTemplateList();
            }
        }
    }

    newHTMLTemplates(): void {
        this.router.navigateByUrl('credential-builder/html-templates');
    }

    onEmittedOCBQueryChange(value: string): void {
        this.inputSearchText = value;
        this.getHTMLTemplateList();
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private getHTMLTemplateList(itemsLeft?: number): void {
        // Prevents asking for the items of an empty page
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        if (!this.noHtmlTemplatesAddedYet) { this.listHTMLTemplates(); }
    }

    private listHTMLTemplates(): void {
        this.loading = true;
        this.api
            .listDiploma(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                this.inputSearchText,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next : (data: PagedResourcesDiplomaSpecLiteView) => {
                    this.htmlTemplates = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeHTMLTemplate = this.htmlTemplates[0];
                    if (this.inputSearchText === '' && data.content?.length === 0) {
                        this.noHtmlTemplatesAddedYet = true;
                    }
                }
            }).add(() => this.loading = false);
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeHTMLTemplate.defaultLanguage;
        /* let isLanguageAvailable: boolean = false;
        this.availableLanguages.forEach((language: string) => {
            if (lang === language) {
                this.selectedLanguage = lang;
                isLanguageAvailable = true;
            }
        });
        if (!isLanguageAvailable) {
            this.selectedLanguage = this.activeLearningOutcome.defaultLanguage;
        } */
    }

}
