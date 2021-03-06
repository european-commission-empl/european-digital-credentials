import { Component, OnInit, OnDestroy, Input, OnChanges, SimpleChanges } from '@angular/core';
import { UxService } from '@eui/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    V1Service,
    AssessmentSpecLiteView,
    PagedResourcesDiplomaSpecLiteView,
    DiplomaSpecLiteView,
    ResourceDiplomaSpecView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { get as _get } from 'lodash';
import { NotificationService } from '@services/error.service';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-html-templates',
    templateUrl: './html-templates.component.html',
    styleUrls: ['./html-templates.component.scss'],
})
export class HTMLTemplatesComponent implements OnInit, OnDestroy, OnChanges {
    htmlTemplates: DiplomaSpecLiteView[] = [];
    activeHTMLTemplate: AssessmentSpecLiteView;
    htmlTemplateDetails: ResourceDiplomaSpecView;
    htmlTemplateToEditOid: number;
    modalTitle: string;
    @Input() openModal: boolean = false;
    modalEdited: boolean = false;
    loading: boolean = true;
    totalItems: number = 0;
    perPage: number = 7;
    page: number = 0;
    sort: string = 'auditDAO.updateDate';
    direction: string = 'DESC';
    selectedRowIndex: number = 0;
    availableLanguages: string[];
    selectedLanguage: string = this.translateService.currentLang;
    firstLoad: boolean = true;
    loadingDetails: boolean = false;
    stopResources$: Subject<boolean> = new Subject<boolean>();
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private uxService: UxService,
        private translateService: TranslateService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService
    ) {
        this.translateService.onLangChange
            .takeUntil(this.destroy$)
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
        this.api
            .deleteDiploma_1(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
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
            });
    }

    onEdit(oid: number): void {
        this.htmlTemplateToEditOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.html-templates-tab.edit'
        );
        this.uxService.openModal('htmlTemplateModal');
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateDiploma(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(
                () => {
                    this.moveToFirstPage();
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary: this.translateService.instant(
                            'common.duplicate'
                        ),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                },
                () => (this.loading = false)
            );
    }

    onSelect(oid: number): void {
        this.getHTMLTemplateDetails(oid);
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.first / this.perPage;
        this.getHTMLTemplateList();
    }
    onSort(event): void {
        const order = event.order === 1 ? 'ASC' : 'DESC';
        if (event.field !== this.sort || order !== this.direction) {
            this.loading = true;
            this.sort = event.field;
            this.direction = order;
            this.getHTMLTemplateList();
        }
    }

    newHTMLTemplates(): void {
        this.modalTitle = this.translateService.instant(
            'credential-builder.html-templates-tab.new'
        );
        this.htmlTemplateToEditOid = null;
        this.openModal = true;
        this.uxService.openModal('htmlTemplateModal');
    }

    closeModal(closeInfo: {isEdit: boolean, oid?: string}): void {
        this.uxService.closeModal('htmlTemplateModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (closeInfo.isEdit) {
            this.modalEdited = true;
            if (!this.htmlTemplates.length) {
                this.getHTMLTemplateList();
            } else {
                this.moveToFirstPage();
            }
        }
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
        this.listHTMLTemplates();
    }

    private listHTMLTemplates(): void {
        this.api
            .listDiploma(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: PagedResourcesDiplomaSpecLiteView) => {
                    this.htmlTemplates = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeHTMLTemplate = this.htmlTemplates[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => {
                    this.loading = false;
                    this.firstLoad = false;
                }
            );
    }

    private getHTMLTemplateDetails(oid: number): void {
        if (
            this.modalEdited ||
            _get(this.htmlTemplateDetails, 'oid', null) !== oid
        ) {
            this.resetDetails();
            this.modalEdited = false;
            this.loadingDetails = true;
            this.api
                .getDiploma_3(oid, this.translateService.currentLang)
                .takeUntil(this.destroy$)
                .subscribe(
                    (htmlTemplate: ResourceDiplomaSpecView) => {
                        this.stopResources$.next(true);
                        this.htmlTemplateDetails = htmlTemplate;
                        this.availableLanguages =
                            htmlTemplate.additionalInfo.languages;
                        this.setLanguage(this.translateService.currentLang);
                        this.loadingDetails = false;
                    },
                    () => {}
                );
        }
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

    private resetDetails(): void {
        this.htmlTemplateDetails = null;
    }
}
