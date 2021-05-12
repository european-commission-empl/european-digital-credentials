import { NgModule } from '@angular/core';
import { SharedModule } from '@shared/shared.module';
import { CredentialBuilderRoutingModule } from './credential-builder-routing.module';
import { CredentialBuilderComponent } from './credential-builder.component';
import { CredentialsComponent } from './components/credentials/credentials.component';
import { AchievementsComponent } from './components/achievements/achievements.component';
import { LearningOutcomesComponent } from './components/learning-outcomes/learning-outcomes.component';
import { ActivitiesComponent } from './components/activities/activities.component';
import { AssessmentsComponent } from './components/assessments/assessments.component';
import { OrganizationComponent } from './components/organization/organization.component';
import { HTMLTemplatesComponent } from './components/html-templates/html-templates.component';
import { EntitlementsComponent } from './components/entitlements/entitlements.component';
import { AchievementsModalComponent } from './components/modals/achievements-modal/achievements-modal.component';
import { HTMLTemplatesModalComponent } from './components/modals/html-templates-modal/html-templates-modal.component';
import { LearningOutcomesModalComponent } from './components/modals/learning-outcomes-modal/learning-outcomes-modal.component';
import { ActivitiesModalComponent } from './components/modals/activities-modal/activities-modal.component';
import { AssessmentsModalComponent } from './components/modals/assessments-modal/assessments-modal.component';
import { EntitlementsModalComponent } from './components/modals/entitlements-modal/entitlements-modal.component';
import { OrganizationsModalComponent } from './components/modals/organizations-modal/organizations-modal.component';
import { CredentialsModalComponent } from './components/modals/credentials-modal/credentials-modal.component';
import { IssueModalComponent } from './components/modals/issue-modal/issue-modal.component';

@NgModule({
    imports: [SharedModule, CredentialBuilderRoutingModule],
    declarations: [
        CredentialBuilderComponent,
        CredentialsComponent,
        AchievementsComponent,
        LearningOutcomesComponent,
        ActivitiesComponent,
        AssessmentsComponent,
        OrganizationComponent,
        EntitlementsComponent,
        AchievementsModalComponent,
        LearningOutcomesModalComponent,
        ActivitiesModalComponent,
        AssessmentsModalComponent,
        EntitlementsModalComponent,
        OrganizationsModalComponent,
        CredentialsModalComponent,
        IssueModalComponent,
        HTMLTemplatesComponent,
        HTMLTemplatesModalComponent
    ]
})
export class CredentialBuilderModule {}
