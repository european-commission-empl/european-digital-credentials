import {
    Component,
    OnInit,
    Input,
    Output,
    EventEmitter,
    ViewEncapsulation, OnDestroy
} from '@angular/core';
import {
    LearningAchievementSpecView,
    LearningOutcomeSpecView,
    LearningActivitySpecView,
    OrganizationSpecView,
    EntitlementSpecView,
    AssessmentSpecView,
    EuropassCredentialSpecView,
} from '@shared/swagger';
import { UxService } from '@eui/core';
import { Subject } from 'rxjs';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-cb-table',
    templateUrl: './cb-table.component.html',
    styleUrls: ['./cb-table.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CbTableComponent implements OnInit, OnDestroy {

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
    @Input() perPage: number = 7;
    @Input() totalItems: number = 0;
    @Input() loading: boolean = true;
    @Input() set selectedItem(value) {
        this.onRowSelect(0, value.oid);
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
    selectedRowIndex: number = 0;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private uxService: UxService,
                private credentialBuilderService: CredentialBuilderService) {
        this.credentialBuilderService.redirectToPage.pipe(takeUntil(this.destroy$)).subscribe(() => {
            this.first = 0;
            this.onPageChange({ first: this.first, rows: this.perPage });
        });
    }

    ngOnInit() {}

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
        this.uxService.openMessageBox('messageBoxDelete');
    }

    onAcceptDelete(event) {
        if (event === true) {
            this.onDelete.emit(this.item.oid);
        }
    }

    onPageChange(event) {
        this.first = event.first;
        this.onRowSelect(0, this.items[0].oid);
        this.onPage.emit(event);
    }

    onSortChange(event) {
        this.onRowSelect(0, this.items[0].oid);
        this.onSort.emit(event);
    }

    onRowSelect(rowIndex: number, oid: number) {
        this.selectedRowIndex = rowIndex;
        this.onSelectItem.emit(oid);
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
}
