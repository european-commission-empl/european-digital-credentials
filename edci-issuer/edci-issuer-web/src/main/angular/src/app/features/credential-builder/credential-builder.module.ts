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
import { AccreditationsModalComponent } from './components/modals/accreditation-modal/accreditations-modal.component';
import { CredentialsModalComponent } from './components/modals/credentials-modal/credentials-modal.component';
import { IssueModalComponent } from './components/modals/issue-modal/issue-modal.component';
import { CustomizeDataComponent } from './customize-data/customize-data.component';
import { CredentialsFormComponent } from './components/forms/credentials-form/credentials-form.component';
import { AchievementsFormComponent } from './components/forms/achievements-form/achievements-form.component';
import { ActivitiesFormComponent } from './components/forms/activities-form/activities-form.component';
import { AssessmentsFormComponent } from './components/forms/assessments-form/assessments-form.component';
import { EntitlementsFormComponent } from './components/forms/entitlements-form/entitlements-form.component';
import { HTMLTemplatesFormComponent } from './components/forms/html-templates-form/html-templates-form.component';
import { LearningOutcomesFormComponent } from './components/forms/learning-outcomes-form/learning-outcomes-form.component';
import { OrganizationsFormComponent } from './components/forms/organizations-form/organizations-form.component';
import { FillFormCustomizeDataComponent } from './fill-form-customise-data/fill-form-customize-data.component';
import { AccreditationComponent } from './components/accreditation/accreditation.component';
import { AccreditationFormComponent } from './components/forms/accreditation-form/accreditation-form.component';

@NgModule({
    imports: [SharedModule, CredentialBuilderRoutingModule],
    declarations: [
        CredentialBuilderComponent,
        CredentialsComponent,
        CustomizeDataComponent,
        FillFormCustomizeDataComponent,
        AchievementsComponent,
        LearningOutcomesComponent,
        ActivitiesComponent,
        AssessmentsComponent,
        OrganizationComponent,
        AccreditationsModalComponent,
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
        HTMLTemplatesModalComponent,
        CredentialsFormComponent,
        AchievementsFormComponent,
        ActivitiesFormComponent,
        AssessmentsFormComponent,
        EntitlementsFormComponent,
        HTMLTemplatesFormComponent,
        LearningOutcomesFormComponent,
        OrganizationsFormComponent,
        AccreditationComponent,
        AccreditationFormComponent,
    ],
    exports : [
        OrganizationsModalComponent
    ]
})
export class CredentialBuilderModule {}
