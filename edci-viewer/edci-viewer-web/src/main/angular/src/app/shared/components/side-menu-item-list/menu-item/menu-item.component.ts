import {
    Component,
    EventEmitter,
    Input,
    OnInit,
    Output,
    OnDestroy,
    ViewEncapsulation,
} from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import {
    AchievementTabView,
    ActivityTabView,
    AssessmentTabView,
    CredentialSubjectTabView,
    EntitlementTabView,
    OrganizationTabView,
} from 'src/app/shared/swagger';

@Component({
    selector: 'edci-menu-item',
    templateUrl: './menu-item.component.html',
    styleUrls: ['./menu-item.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class MenuItemComponent implements OnInit, OnDestroy {
    private _styleClass: string;
    private _hierarchyTree: string[];

    @Input() label: string;
    @Input() depthLevel: number;
    @Input() parentIds: string[];
    @Input() entityType:
        | 'achievement'
        | 'activity'
        | 'assessment'
        | 'organisation'
        | 'entitlement'
        | 'subject';
    @Input() entity:
        | OrganizationTabView
        | AchievementTabView
        | ActivityTabView
        | EntitlementTabView
        | AssessmentTabView
        | CredentialSubjectTabView;
    @Input() set hierarchyTree(value: string[]) {
        this._hierarchyTree = value;
    }
    get hierarchyTree(): string[] {
        return this._hierarchyTree;
    }
    @Input() set styleClass(value: string) {
        this._styleClass = value;
    }
    get styleClass(): string {
        return this._styleClass;
    }

    @Output() expanded: EventEmitter<boolean> = new EventEmitter<boolean>();
    @Output() selected: EventEmitter<number | string> = new EventEmitter<
        number | string
    >();

    isExpanded: boolean = false;
    isMainElement: boolean;
    isSelectedElement: boolean;
    isTreeElement: boolean;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private router: Router,
        private shareDataService: ShareDataService
    ) {}

    ngOnInit() {
        this.shareDataService
            .changeHierarchyTree()
            .takeUntil(this.destroy$)
            .subscribe((hierarchy: string[]) => {
                if (hierarchy.length === 0) {
                    this.router.navigate(['diploma-details']);
                } else {
                    this.setSelection(hierarchy);
                }
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    changeExpanded(event): void {
        event.stopPropagation();
        this.isExpanded = !this.isExpanded;
        this.expanded.emit(this.isExpanded);
    }

    selectionChange(): void {
        this.router.navigate([`/diploma-details/${this.entityType}`]);
        this.shareDataService.emitEntitySelection(this.entity);
        this.shareDataService.emitHierarchyTree(this.hierarchyTree);
    }

    /**
     * Note for: "setSelection" && "checkTree"
     * Compares the hierarchy (SELECTED ELEMENT) with hierarchyTree
     * Compare logic:
     * - Order matters, if element X does not match, stop comparing,
     *   does not matter if X + 1 matches
     * - Length does not matter, as long as the hierarchyTree is a subset of hierarchy
     *   the element will be marked in one way or another (see "Possible Outcomes" below)
     *
     * Possible Outcomes:
     * - Element X from both hierarchies does not match, meaning that this element is not from
     *   the same tree as the selected one. (Stop comparing reset CSS to default)
     * - Variable hierarchyTree has only 1 element and it matches hierarchy (which has length > 1).
     *   (Set CSS as Main element (parent of all))
     * - Variable hierarchyTree has X elements and they match the elements of hierarchy
     *   which has length > X. (Set CSS as parent element (Note: Not main parent nor selected))
     *   In this case we should also expand the element to show its children
     *   (Link from detail case)
     * - All elements of hierarchyTree match the elements of hierarchy and
     *   hierarchyTree.length === hierarchy.length (Set CSS as selected element)
     */
    private setSelection(hierarchy: string[]): void {
        const isSameTree: boolean = this.sameTreeCheck(hierarchy);
        let isMainElement: boolean = false;
        let isSelectedElement: boolean = false;
        let isTreeElement: boolean = false;
        if (isSameTree) {
            if (this.hierarchyTree.length === 1) {
                isMainElement = true;
            }
            isTreeElement = true;
            isSelectedElement = hierarchy.length === this.hierarchyTree.length;
        }
        this.isMainElement = isMainElement;
        this.isSelectedElement = isSelectedElement;
        this.isTreeElement = isTreeElement;
        if (isSelectedElement) {
            this.router.navigate([`/diploma-details/${this.entityType}`]);
            this.shareDataService.emitEntitySelection(this.entity);
        }
        if (!isSelectedElement && isTreeElement) {
            this.isExpanded = true;
            this.expanded.emit(this.isExpanded);
        }
    }

    private sameTreeCheck(tree: string[]): boolean {
        let isSameTree: boolean = true;
        for (let index = 0; index < this.hierarchyTree.length; index++) {
            if (this.hierarchyTree[index] !== tree[index]) {
                isSameTree = false;
                break;
            }
        }
        return isSameTree;
    }
}
