import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import {
    UxButtonComponentModule,
    UxControlFeedbackComponentModule,
    UxDatepickerComponentModule,
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
} from '@eui/core';
import { EclAllModule } from '@eui/ecl-core';
import { TranslateModule } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
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
import { MenuItemComponent } from './components/side-menu-tiem-list/menu-item/menu-item.component';
import { MenuListComponent } from './components/side-menu-tiem-list/menu-list/menu-list.component';
import { SideMenuItemListComponent } from './components/side-menu-tiem-list/side-menu-item-list.component';
import { CopyClipboardDirective } from './directives/copy-to-clipboard.directive';
import { HasChildrenPipe } from './pipes/has-children.pipe';
import { JoinPipe } from './pipes/join.pipe';
import { JoinPipeAddress } from './pipes/join.pipe.address';
import { MultilingualPipe } from './pipes/multilingual.pipe';
import { SafePipe } from './pipes/sanitize.pipe';

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
        UxDatepickerComponentModule,
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
        HasChildrenPipe,
        MenuItemComponent,
        MoreInformationComponent,
        MultilingualPipe,
        OrganizationDetailComponent,
        OtherDocumentsComponent,
        SafePipe,
        SideMenuItemListComponent,
        MenuListComponent,
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
        HasChildrenPipe,
        MatProgressSpinnerModule,
        MenuItemComponent,
        MoreInformationComponent,
        MultilingualPipe,
        OrganizationDetailComponent,
        OtherDocumentsComponent,
        ReactiveFormsModule,
        RouterModule,
        SafePipe,
        SideMenuItemListComponent,
        MenuListComponent,
        ToastModule,
        TranslateModule,
        UxButtonComponentModule,
        UxControlFeedbackComponentModule,
        UxDatepickerComponentModule,
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
    ],
    providers: [
        UxDynamicComponentService,
        UxDynamicModalService,
        MessageService,
    ],
    entryComponents: [EntityModalComponent],
})
export class SharedModule {}
