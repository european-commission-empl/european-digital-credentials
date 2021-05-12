import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import {
    UxButtonComponentModule,
    UxControlFeedbackComponentModule,
    UxDropdownButtonComponentModule,
    UxDynamicComponentService,
    UxDynamicModalComponentModule,
    UxDynamicModalService,
    UxFormControlComponentModule,
    UxFormGroupComponentModule,
    UxIconComponentModule,
    UxLayoutSidebarItemComponentModule,
    UxLayoutSidebarItemsComponentModule,
    UxModalComponentModule,
    UxTimelineItemComponentModule,
    UxTimelineItemsComponentModule,
    UxTooltipModule,
    UxDatepickerComponentModule
} from '@eui/core';
import { EclAllModule } from '@eui/ecl-core';
import { TranslateModule } from '@ngx-translate/core';
import { AchievementDetailComponent } from './components/achievement-detail/achievement-detail.component';
import { ActivityDetailComponent } from './components/activity-detail/activity-detail.component';
import { AssessmentDetailComponent } from './components/assessment-detail/assessment-detail.component';
import { DiplomaToolbarComponent } from './components/diploma-toolbar/diploma-toolbar.component';
import { EntitlementDetailComponent } from './components/entitlement-detail/entitlement-detail.component';
import { EntityModalComponent } from './components/entity-modal/entity-modal.component';
import { IdentifiersComponent } from './components/identifiers/identifiers.component';
import { MoreInformationComponent } from './components/more-information/more-information.component';
import { OrganizationDetailComponent } from './components/organization-detail/organization-detail.component';
import { OtherDocumentsComponent } from './components/other-documents/other-documents.component';
import { CopyClipboardDirective } from './directives/copy-to-clipboard.directive';
import { JoinPipe } from './pipes/join.pipe';
import { JoinPipeAddress } from './pipes/join.pipe.address';
import { SafePipe } from './pipes/sanitize.pipe';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@NgModule({
    imports: [
        CommonModule,
        EclAllModule,
        FormsModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        RouterModule,
        ToastModule,
        TranslateModule,
        UxButtonComponentModule,
        UxControlFeedbackComponentModule,
        UxDropdownButtonComponentModule,
        UxDynamicModalComponentModule,
        UxFormControlComponentModule,
        UxFormGroupComponentModule,
        UxIconComponentModule,
        UxLayoutSidebarItemComponentModule,
        UxLayoutSidebarItemsComponentModule,
        UxModalComponentModule,
        UxTimelineItemComponentModule,
        UxTimelineItemsComponentModule,
        UxTooltipModule,
        UxDatepickerComponentModule
    ],
    declarations: [
        AchievementDetailComponent,
        ActivityDetailComponent,
        AssessmentDetailComponent,
        CopyClipboardDirective,
        DiplomaToolbarComponent,
        EntitlementDetailComponent,
        EntityModalComponent,
        IdentifiersComponent,
        JoinPipe,
        JoinPipeAddress,
        MoreInformationComponent,
        OrganizationDetailComponent,
        OtherDocumentsComponent,
        SafePipe,
    ],
    exports: [
        AchievementDetailComponent,
        ActivityDetailComponent,
        AssessmentDetailComponent,
        CommonModule,
        CopyClipboardDirective,
        DiplomaToolbarComponent,
        EclAllModule,
        EntitlementDetailComponent,
        EntityModalComponent,
        FormsModule,
        IdentifiersComponent,
        JoinPipe,
        JoinPipeAddress,
        MatProgressSpinnerModule,
        MoreInformationComponent,
        OrganizationDetailComponent,
        OtherDocumentsComponent,
        ReactiveFormsModule,
        RouterModule,
        SafePipe,
        ToastModule,
        TranslateModule,
        UxButtonComponentModule,
        UxControlFeedbackComponentModule,
        UxDropdownButtonComponentModule,
        UxDynamicModalComponentModule,
        UxFormControlComponentModule,
        UxFormGroupComponentModule,
        UxIconComponentModule,
        UxLayoutSidebarItemComponentModule,
        UxLayoutSidebarItemsComponentModule,
        UxModalComponentModule,
        UxTimelineItemComponentModule,
        UxTimelineItemsComponentModule,
        UxTooltipModule,
        UxDatepickerComponentModule
    ],
    providers: [
        UxDynamicComponentService,
        UxDynamicModalService,
        MessageService,
    ],
    entryComponents: [EntityModalComponent],
})
export class SharedModule {}
