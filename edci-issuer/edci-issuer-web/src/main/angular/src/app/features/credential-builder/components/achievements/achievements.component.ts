import {
    Component, Input,
    OnChanges, OnDestroy, OnInit, SimpleChanges, ViewEncapsulation
} from '@angular/core';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { NotificationService } from '@services/error.service';
import {
    LearningAchievementSpecLiteView, LearningOutcomeSpecLiteView, PagedResourcesLearningAchievementSpecLiteView, V1Service
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-achievements',
    templateUrl: './achievements.component.html',
    styleUrls: ['./achievements.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AchievementsComponent implements OnInit, OnDestroy, OnChanges {
    achievements: Array<LearningAchievementSpecLiteView> = [];
    activeAchievement: LearningAchievementSpecLiteView;
    learningOutcomes: LearningOutcomeSpecLiteView[] = [];
    editAchievementOid: number;
    modalTitle: string;
    @Input() openModal = false;
    modalEdited = false;
    loading = true;
    totalItems = 0;
    perPage = 7;
    page = 0;
    sort = 'auditDAO.updateDate';
    noAchievementsAddedYet = false;
    direction = 'DESC';
    inputSearchText = '';
    selectedRowIndex = 0;
    selectedLanguage: string = this.translateService.currentLang;
    availableLanguages: string[];
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private translateService: TranslateService,
        private api: V1Service,
        private notificationService: NotificationService,
        private credentialBuilderService: CredentialBuilderService,
        private router: Router,
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
        this.getAchievementList();
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.openModal && changes.openModal.currentValue) {
            this.newAchievement();
        }
    }

    onDelete(oid: number): void {
        this.loading = true;
        this.api
            .deleteLearningAchievement(oid, this.translateService.currentLang)
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.getAchievementList(this.totalItems - 1);
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
        this.router.navigateByUrl('credential-builder/achievements/' + oid);
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateLearningAchievement(
                oid,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe(
                {next : () => {
                    this.moveToFirstPage();
                    this.notificationService.showNotification({
                        severity: 'success',
                        summary:
                            this.translateService.instant('common.duplicate'),
                        detail: this.translateService.instant(
                            'credential-builder.operationSuccessful'
                        ),
                    });
                }
                }).add(() => (this.loading = false));
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.page;
        this.perPage = event.pageSize;
        this.getAchievementList();
    }

    onSort(event): void {
        if (event.sort !== null && event.order !== null) {
            if (event.sort !== this.sort || event.order.toUpperCase() !== this.direction) {
                this.loading = true;
                this.sort = event.sort;
                this.direction = event.order.toUpperCase();
                this.getAchievementList();
            }
        }
    }

    newAchievement(): void {
        this.router.navigateByUrl('credential-builder/achievements');
    }

    onEmittedOCBQueryChange(value: string): void {
        this.inputSearchText = value;
        this.getAchievementList();
    }

    private moveToFirstPage() {
        this.credentialBuilderService.redirectToPage.next(0);
    }

    private getAchievementList(itemsLeft?: number): void {
        if (
            !!itemsLeft &&
            itemsLeft / this.perPage <= this.page &&
            this.page !== 0
        ) {
            this.page = this.page - 1;
        }
        if (!this.noAchievementsAddedYet) { this.listAchievements(); }
    }

    private listAchievements(): void {
        this.loading = true;
        this.api
            .listLearningAchievement(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                this.inputSearchText,
                this.translateService.currentLang
            )
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next : (data: PagedResourcesLearningAchievementSpecLiteView) => {
                    this.achievements = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeAchievement = this.achievements[0];
                    if (this.inputSearchText === '' && data.content?.length === 0) {
                        this.noAchievementsAddedYet = true;
                    }
                },
            }).add(() => this.loading = false);
    }

    private setLanguage(lang: string): void {
        this.selectedLanguage = this.activeAchievement.defaultLanguage;
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
