import { CommonModule } from '@angular/common';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { NgModule } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { UxAllModule } from '@eui/components/legacy';
import { EuiAllModule } from '@eui/components';
import { EclAllModule } from '@eui/ecl-core';
import { AccreditationPanelComponent } from './components/accreditation-panel/accreditation-panel.component';
import { AchievementDetailComponent } from './components/achievement-detail/achievement-detail.component';
import { AchievementInformationPanelComponent } from './components/achievement-detail/achievement-information-panel/achievement-information-panel.component';
import { AchievementLearningOutcomesPanelComponent } from './components/achievement-detail/achievement-learning-outcomes-panel/achievement-learning-outcomes-panel.component';
import { AchievementMainPanelComponent } from './components/achievement-detail/achievement-main-panel/achievement-main-panel.component';
import { AchievementOtherInformationPanelComponent } from './components/achievement-detail/achievement-other-information-panel/achievement-other-information-panel.component';
import { ActivityDetailComponent } from './components/activity-detail/activity-detail.component';
import { ActivityInformationPanelComponent } from './components/activity-detail/activity-information-panel/activity-information-panel.component';
import { ActivityMainPanelComponent } from './components/activity-detail/activity-main-panel/activity-main-panel.component';
import { ActivityOtherInformationPanelComponent } from './components/activity-detail/activity-other-information-panel/activity-other-information-panel.component';
import { AssessmentDetailComponent } from './components/assessment-detail/assessment-detail.component';
import { AssessmentGradingSchemePanelComponent } from './components/assessment-detail/assessment-grading-scheme-panel/assessment-grading-scheme-panel.component';
import { AssessmentInformationPanelComponent } from './components/assessment-detail/assessment-information-panel/assessment-information-panel.component';
import { AssessmentMainPanelComponent } from './components/assessment-detail/assessment-main-panel/assessment-main-panel.component';
import { AssessmentOtherInformationPanelComponent } from './components/assessment-detail/assessment-other-information-panel/assessment-other-information-panel.component';
import { DetailOrganisationSectionComponent } from './components/detail-organisation-section/detail-organisation-section.component';
import { DiplomaToolbarComponent } from './components/diploma-toolbar/diploma-toolbar.component';
import { EntitlementDetailComponent } from './components/entitlement-detail/entitlement-detail.component';
import { EntitlementInformationPanelComponent } from './components/entitlement-detail/entitlement-information-panel/entitlement-information-panel.component';
import { EntitlementMainPanelComponent } from './components/entitlement-detail/entitlement-main-panel/entitlement-main-panel.component';
import { EntitlementOtherInformationPanelComponent } from './components/entitlement-detail/entitlement-other-information-panel/entitlement-other-information-panel.component';
import { EntityModalComponent } from './components/entity-modal/entity-modal.component';
import { IdentifiersComponent } from './components/identifiers/identifiers.component';
import { InformationFieldComponent } from './components/information-field/information-field.component';
import { MoreInformationComponent } from './components/more-information/more-information.component';
import { OrganizationDetailComponent } from './components/organization-detail/organization-detail.component';
import { OtherDocumentsComponent } from './components/other-documents/other-documents.component';
import { PanelComponent } from './components/panel/panel.component';
import { MenuItemComponent } from './components/side-menu-item-list/menu-item/menu-item.component';
import { MenuListComponent } from './components/side-menu-item-list/menu-list/menu-list.component';
import { SideMenuItemListComponent } from './components/side-menu-item-list/side-menu-item-list.component';
import { CopyClipboardDirective } from './directives/copy-to-clipboard.directive';
import { HasChildrenPipe } from './pipes/has-children.pipe';
import { JoinAddressPipe } from './pipes/join-address.pipe';
import { JoinPipe } from './pipes/join.pipe';
import { MultilingualPipe } from './pipes/multilingual.pipe';
import { SafePipe } from './pipes/sanitize.pipe';
import { ToastModule } from 'primeng/toast';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { RouterModule } from '@angular/router';
import { MessageService } from 'primeng/api';

import { UxDynamicComponentService } from '@eui/core';
import { SectionComponent } from './components/section/section.component';
import { ContactDetailComponent } from './components/contact-detail/contact-detail.component';
import { SpinnerDialogComponent } from './components/spinner-dialog/spinner-dialog.component';
import { MaterialModule } from './material/material.module';
import { AccreditationComponent } from './components/accreditation-detail/accreditation-detail.component';

@NgModule({
    imports: [
        CommonModule,
        DragDropModule,
        EuiAllModule,
        FormsModule,
        MaterialModule,
        MatProgressSpinnerModule,
        ReactiveFormsModule,
        RouterModule,
        ToastModule,
        TranslateModule,
        UxAllModule,
    ],
    declarations: [
        AccreditationPanelComponent,
        AccreditationComponent,
        AchievementDetailComponent,
        AchievementInformationPanelComponent,
        AchievementLearningOutcomesPanelComponent,
        AchievementMainPanelComponent,
        AchievementOtherInformationPanelComponent,
        ActivityDetailComponent,
        ActivityInformationPanelComponent,
        ActivityMainPanelComponent,
        ActivityOtherInformationPanelComponent,
        AssessmentDetailComponent,
        AssessmentGradingSchemePanelComponent,
        AssessmentInformationPanelComponent,
        AssessmentMainPanelComponent,
        AssessmentOtherInformationPanelComponent,
        CopyClipboardDirective,
        DetailOrganisationSectionComponent,
        DiplomaToolbarComponent,
        EntitlementDetailComponent,
        EntitlementInformationPanelComponent,
        EntitlementMainPanelComponent,
        EntitlementOtherInformationPanelComponent,
        EntityModalComponent,
        HasChildrenPipe,
        IdentifiersComponent,
        InformationFieldComponent,
        JoinAddressPipe,
        JoinPipe,
        MenuItemComponent,
        MenuListComponent,
        MoreInformationComponent,
        MultilingualPipe,
        OrganizationDetailComponent,
        OtherDocumentsComponent,
        PanelComponent,
        SectionComponent,
        SafePipe,
        SideMenuItemListComponent,
        SectionComponent,
        SpinnerDialogComponent,
        ContactDetailComponent,
    ],
    exports: [
        AccreditationPanelComponent,
        AccreditationComponent,
        AchievementDetailComponent,
        AchievementInformationPanelComponent,
        AchievementLearningOutcomesPanelComponent,
        AchievementMainPanelComponent,
        AchievementOtherInformationPanelComponent,
        ActivityDetailComponent,
        ActivityInformationPanelComponent,
        ActivityMainPanelComponent,
        ActivityOtherInformationPanelComponent,
        AssessmentDetailComponent,
        AssessmentGradingSchemePanelComponent,
        AssessmentInformationPanelComponent,
        AssessmentMainPanelComponent,
        AssessmentOtherInformationPanelComponent,
        CommonModule,
        CopyClipboardDirective,
        ContactDetailComponent,
        DetailOrganisationSectionComponent,
        DiplomaToolbarComponent,
        DragDropModule,
        EntitlementDetailComponent,
        EntitlementInformationPanelComponent,
        EntitlementMainPanelComponent,
        EntitlementOtherInformationPanelComponent,
        EntityModalComponent,
        EuiAllModule,
        FormsModule,
        HasChildrenPipe,
        IdentifiersComponent,
        InformationFieldComponent,
        JoinAddressPipe,
        JoinPipe,
        MaterialModule,
        MatProgressSpinnerModule,
        MenuItemComponent,
        MenuListComponent,
        MoreInformationComponent,
        MultilingualPipe,
        OrganizationDetailComponent,
        OtherDocumentsComponent,
        PanelComponent,
        ReactiveFormsModule,
        RouterModule,
        SafePipe,
        SectionComponent,
        SideMenuItemListComponent,
        SpinnerDialogComponent,
        ToastModule,
        TranslateModule,
        UxAllModule,
    ],
    providers: [
        UxDynamicComponentService,
        MessageService,
    ],
    entryComponents: [EntityModalComponent, SpinnerDialogComponent],
})
export class SharedModule {}
