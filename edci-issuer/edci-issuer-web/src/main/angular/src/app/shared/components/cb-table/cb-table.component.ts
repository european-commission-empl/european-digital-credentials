import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    Output,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { environment } from '@environments/environment';
import {
    EuiMessageBoxComponent,
    EuiMessageBoxService,
} from '@eui/components/eui-message-box';
import { PaginationEvent } from '@eui/components/eui-table';
import { CredentialBuilderService } from '@services/credential-builder.service';
import {
    AssessmentSpecView,
    EntitlementSpecView,
    EuropassCredentialSpecView,
    LearningAchievementSpecView,
    LearningActivitySpecView,
    LearningOutcomeSpecView,
    OrganizationSpecView,
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-cb-table',
    templateUrl: './cb-table.component.html',
    styleUrls: ['./cb-table.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CbTableComponent implements OnDestroy {
    @ViewChild('messageBoxDelete') messageBoxDelete: EuiMessageBoxComponent;

    first: number = null;
    @Input() items: Array<
        | LearningAchievementSpecView
        | LearningOutcomeSpecView
        | LearningActivitySpecView
        | AssessmentSpecView
        | OrganizationSpecView
        | EntitlementSpecView
        | EuropassCredentialSpecView
    > = [];
    @Input() perPage = 7;
    @Input() pagesShown = 5;
    @Input() totalItems = 0;
    @Input() loading = true;
    @Input() set selectedItem(value) {
        if (value) { this.onRowSelect(value.oid, 0); }
    }
    @Input() sortField;
    @Input() isCredential?: boolean = false;
    @Output() onSelectItem: EventEmitter<number> = new EventEmitter();
    @Output() onEdit: EventEmitter<number> = new EventEmitter();
    @Output() onDelete: EventEmitter<number> = new EventEmitter();
    @Output() onDuplicate: EventEmitter<number> = new EventEmitter();
    @Output() onIssue: EventEmitter<number> = new EventEmitter();
    @Output() onPage: EventEmitter<any> = new EventEmitter();
    @Output() onSort: EventEmitter<any> = new EventEmitter();
    item:
        | LearningAchievementSpecView
        | LearningOutcomeSpecView
        | LearningActivitySpecView
        | AssessmentSpecView
        | OrganizationSpecView
        | EntitlementSpecView
        | EuropassCredentialSpecView;
    selectedRowIndex = 0;
    destroy$: Subject<boolean> = new Subject<boolean>();
    limitDate: Date = environment.limitDate;

    constructor(
        private credentialBuilderService: CredentialBuilderService,
        private euiMessageBoxService: EuiMessageBoxService
    ) {
        this.credentialBuilderService.redirectToPage
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.onPageChange({
                    page: 0,
                    pageSize: this.perPage,
                    nbPage: this.pagesShown,
                });
            });
    }

    public openMessageBox(): void {
        this.messageBoxDelete.openMessageBox();
    }

    public closeMessageBox(): void {
        this.euiMessageBoxService.closeMessageBox();
    }

    onRequestDelete(
        item:
            | LearningAchievementSpecView
            | LearningOutcomeSpecView
            | LearningActivitySpecView
            | AssessmentSpecView
            | OrganizationSpecView
            | EntitlementSpecView
            | EuropassCredentialSpecView
    ) {
        this.item = item;
        this.messageBoxDelete.openMessageBox();
    }

    onAcceptDelete() {
        this.onDelete.emit(this.item.oid);
    }

    onPageChange(event: PaginationEvent) {
        /**
         * Paginator gets triggered when data gets introduced
         * the first time thus triggering all the request twice.
         * No events will be emitted if no items exist
         */
        if (this.items.length > 0) {
            this.onRowSelect(this.items[0].oid, 0);
            this.onPage.emit(event);
        }
    }

    onSortChange(event) {
        /**
         * Sort gets triggered when data gets introduced
         * the first time thus triggering all the request twice.
         * No events will be emitted if no items exist
         */
        if (this.items.length > 0) {
            this.onRowSelect(this.items[0].oid, 0);
            this.onSort.emit(event);
        }
    }

    onRowSelect(oid: number, rowIndex?: number) {
        this.selectedRowIndex = rowIndex;
        this.onSelectItem.emit(oid);
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
}
