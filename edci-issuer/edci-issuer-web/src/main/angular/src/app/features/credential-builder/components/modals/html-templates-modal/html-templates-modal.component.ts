import {
    Component,
    EventEmitter,
    Input,
    OnDestroy,
    OnInit,
    Output,
    ViewEncapsulation
} from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { ResourceDiplomaSpecView, V1Service } from '@shared/swagger';
import { Subject } from 'rxjs';
import { take, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
    selector: 'edci-html-templates-modal',
    templateUrl: './html-templates-modal.component.html',
    styleUrls: ['./html-templates-modal.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class HTMLTemplatesModalComponent implements OnInit, OnDestroy {
    @Input() modalTitle: string;
    @Input() modalId = 'htmlTemplateModal';
    @Input() editHtmlTemplateOid?: number;
    @Output() onCloseModal: EventEmitter<{
        isEdit: boolean;
        oid: number;
        displayName: string;
    }> = new EventEmitter();
    eventSave: Subject<void> = new Subject<void>();
    editHTMLTemplate: ResourceDiplomaSpecView;
    destroy$: Subject<boolean> = new Subject<boolean>();
    isSaveDisabled = false;
    modalTitleBreadcrumb: string[];
    isLoading = false;
    modalData: any;

    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private translateService: TranslateService,
        private api: V1Service,
    ) {}

    ngOnInit() {
        this.modalTitleBreadcrumb =
            this.credentialBuilderService.listModalTitles;
        if (this.editHtmlTemplateOid) {
            this.modalTitle = this.translateService.instant(
                'credential-builder.html-templates-tab.edit'
            );
            this.getHtmlTemplateDetails(this.editHtmlTemplateOid, this.translateService.currentLang);
        } else {
            this.modalTitle = this.translateService.instant(
                'credential-builder.html-templates-tab.new'
            );
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSave(): void {
        this.eventSave.next();
    }

    saveForm(event: any): void {
        this.closeModal(false, event?.oid, event?.displayName);
    }

    closeModal(isEdit: boolean, oid?: number, displayName?: string): void {
        this.onCloseModal.emit({ isEdit, oid, displayName });
    }

    getHtmlTemplateDetails(routeID, currentLanguage) {
        this.isLoading = true;
        this.api.getDiplomaSpec(routeID, currentLanguage).pipe(
            take(1),
            catchError(() => {
                this.isLoading = false;
                return of(null);
            })
        )
            .subscribe({
                next: (v) => {
                    this.modalData = v;
                    this.isLoading = false;
                },
                error: (e) => {
                    console.error(e);
                    this.isLoading = false;
                },
            });
    }
}
