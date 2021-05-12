import {
    Component,
    OnInit,
    ViewEncapsulation,
    OnDestroy,
    Input,
    OnChanges,
    SimpleChanges,
} from '@angular/core';
import { UxService } from '@eui/core';
import { TranslateService, LangChangeEvent } from '@ngx-translate/core';
import {
    V1Service,
    LearningAchievementSpecView,
    LearningAchievementSpecLiteView,
    PagedResourcesLearningAchievementSpecLiteView,
    AssessmentSpecView,
    LearningActivitySpecLiteView,
    EntitlementSpecLiteView,
    OrganizationSpecLiteView,
    PagedResourcesLearningOutcomeSpecLiteView,
    LearningOutcomeSpecLiteView,
    PagedResourcesLearningActivitySpecLiteView,
    PagedResourcesEntitlementSpecLiteView,
    PagedResourcesOrganizationSpecLiteView,
    ResourceAssessmentSpecView,
} from '@shared/swagger';
import { Subject, Observable, forkJoin } from 'rxjs';
import { NotificationService } from '@services/error.service';
import { get as _get } from 'lodash';
import { CredentialBuilderService } from '@services/credential-builder.service';

@Component({
    selector: 'edci-achievements',
    templateUrl: './achievements.component.html',
    styleUrls: ['./achievements.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class AchievementsComponent implements OnInit, OnDestroy, OnChanges {
    achievements: Array<LearningAchievementSpecLiteView> = [];
    activeAchievement: LearningAchievementSpecLiteView;
    achievementDetails: LearningAchievementSpecView;
    learningOutcomes: LearningOutcomeSpecLiteView[] = [];
    provenBy: AssessmentSpecView;
    influencedBy: LearningActivitySpecLiteView[];
    entitlesTo: EntitlementSpecLiteView;
    awardingBody: OrganizationSpecLiteView;
    subAchievements: LearningAchievementSpecLiteView[] = [];
    editAchievementOid: number;
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
    selectedLanguage: string = this.translateService.currentLang;
    availableLanguages: string[];
    firstLoad: boolean = true;
    loadingDetails: boolean = false;
    destroy$: Subject<boolean> = new Subject<boolean>();
    stopResources$: Subject<boolean> = new Subject<boolean>();

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
            this.newAchievement();
        }
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
        this.api
            .deleteLearningAchievement(oid, this.translateService.currentLang)
            .takeUntil(this.destroy$)
            .subscribe(() => {
                this.loading = true;
                this.getAchievementList(this.totalItems - 1);
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
        this.editAchievementOid = oid;
        this.openModal = true;
        this.modalTitle = this.translateService.instant(
            'credential-builder.achievements-tab.editAchievement'
        );
        this.uxService.openModal('achievementModal');
    }

    onDuplicate(oid: number): void {
        this.loading = true;
        this.api
            .duplicateLearningAchievement(
                oid,
                this.translateService.currentLang
            )
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
        this.getAchievementDetails(oid);
    }

    onPage(event): void {
        this.loading = true;
        this.page = event.first / this.perPage;
        this.getAchievementList();
    }

    onSort(event): void {
        const order = event.order === 1 ? 'ASC' : 'DESC';
        if (event.field !== this.sort || order !== this.direction) {
            this.loading = true;
            this.sort = event.field;
            this.direction = order;
            this.getAchievementList();
        }
    }

    newAchievement(): void {
        this.editAchievementOid = null;
        this.modalTitle = this.translateService.instant(
            'credential-builder.achievements-tab.createAchievement'
        );
        this.openModal = true;
        this.uxService.openModal('achievementModal');
    }

    closeModal(closeInfo: {isEdit: boolean, oid?: string}): void {
        this.uxService.closeModal('achievementModal');
        this.openModal = false;
        // If it's true, move to page 1 and trigger onPageEvent.
        if (closeInfo.isEdit) {
            this.modalEdited = true;
            if (!this.achievements.length) {
                this.getAchievementList();
            } else {
                this.moveToFirstPage();
            }
        }
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
        this.listAchievements();
    }

    private listAchievements(): void {
        this.api
            .listLearningAchievement(
                this.sort,
                this.direction,
                this.page,
                this.perPage,
                '',
                this.translateService.currentLang
            )
            .takeUntil(this.destroy$)
            .subscribe(
                (data: PagedResourcesLearningAchievementSpecLiteView) => {
                    this.achievements = data.content;
                    this.totalItems = data.page.totalElements;
                    this.activeAchievement = this.achievements[0];
                    this.loading = false;
                    this.firstLoad = false;
                },
                () => {
                    this.loading = false;
                    this.firstLoad = false;
                }
            );
    }

    private getAchievementDetails(oid: number): void {
        if (
            this.modalEdited ||
            _get(this.achievementDetails, 'oid', null) !== oid
        ) {
            this.resetDetails();
            this.modalEdited = false;
            this.loadingDetails = true;
            this.api
                .getLearningAchievement(oid, this.translateService.currentLang)
                .takeUntil(this.destroy$)
                .subscribe(
                    (achievement: LearningAchievementSpecView) => {
                        this.achievementDetails = achievement;
                        this.availableLanguages =
                            achievement.additionalInfo.languages;
                        this.setLanguage(this.translateService.currentLang);
                        this.stopResources$.next(true);
                        this.getAchievementsResources(oid);
                    },
                    () => {}
                );
        }
    }

    private getAchievementsResources(oid: number): void {
        forkJoin({
            subAchievements: this.getSubAchievements(),
            provenBy: this.getProvenBy(),
            influencedBy: this.getInfluencedBy(),
            entitlesTo: this.getEntitlesTo(),
            learningOutcomes: this.getLearningOutcome(),
            awardingBody: this.getAwardingBody(),
        })
            .takeUntil(this.stopResources$)
            .subscribe((resources) => {
                this.subAchievements = resources.subAchievements.content;
                this.provenBy = resources.provenBy;
                this.entitlesTo = resources.entitlesTo.content[0];
                this.influencedBy = resources.influencedBy.content;
                this.learningOutcomes = resources.learningOutcomes.content;
                this.awardingBody = resources.awardingBody.content[0];
                this.loadingDetails = false;
            });
    }

    private getProvenBy(): Observable<ResourceAssessmentSpecView> {
        return this.api.getProvenBy(
            this.achievementDetails.oid,
            this.translateService.currentLang
        );
    }

    private getLearningOutcome(): Observable<PagedResourcesLearningOutcomeSpecLiteView> {
        return this.api.listLearningOutcomes(
            this.achievementDetails.oid,
            this.translateService.currentLang
        );
    }

    private getInfluencedBy(): Observable<PagedResourcesLearningActivitySpecLiteView> {
        return this.api.listInfluencedBy(
            this.achievementDetails.oid,
            this.translateService.currentLang
        );
    }

    private getEntitlesTo(): Observable<PagedResourcesEntitlementSpecLiteView> {
        return this.api.listEntitlesTo(
            this.achievementDetails.oid,
            this.translateService.currentLang
        );
    }

    private getAwardingBody(): Observable<PagedResourcesOrganizationSpecLiteView> {
        return this.api.listAwardingBodies(
            this.achievementDetails.oid,
            this.translateService.currentLang
        );
    }

    private getSubAchievements(): Observable<PagedResourcesLearningAchievementSpecLiteView> {
        return this.api.listSubAchievements(
            this.achievementDetails.oid,
            this.translateService.currentLang
        );
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

    private resetDetails(): void {
        this.achievementDetails = null;
        this.subAchievements = null;
        this.provenBy = null;
        this.entitlesTo = null;
        this.influencedBy = null;
        this.learningOutcomes = null;
        this.awardingBody = null;
    }
}
