import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AchievementsFormComponent } from './components/forms/achievements-form/achievements-form.component';
import { ActivitiesFormComponent } from './components/forms/activities-form/activities-form.component';
import { AssessmentsFormComponent } from './components/forms/assessments-form/assessments-form.component';
import { CredentialsFormComponent } from './components/forms/credentials-form/credentials-form.component';
import { EntitlementsFormComponent } from './components/forms/entitlements-form/entitlements-form.component';
import { HTMLTemplatesFormComponent } from './components/forms/html-templates-form/html-templates-form.component';
import { LearningOutcomesFormComponent } from './components/forms/learning-outcomes-form/learning-outcomes-form.component';
import { OrganizationsFormComponent } from './components/forms/organizations-form/organizations-form.component';
import { CredentialBuilderComponent } from './credential-builder.component';
import { CredentialBuilderGuard } from './credential-builder.guard';
import { CustomizeDataComponent } from './customize-data/customize-data.component';
import { FillFormCustomizeDataComponent } from './fill-form-customise-data/fill-form-customize-data.component';
import { AccreditationFormAccreditingAgentResolver } from './resolvers/accreditation-form/accreditation-form-accrediting-agent.resolver';
import { AccreditationFormResolver } from './resolvers/accreditation-form/accreditation-form.resolver';
import { AchievementsFormAwardingBodiesResolver } from './resolvers/achievements-form/achievements-form-awarding-bodies.resolver';
import { AchievementsFormEntitledToResolver } from './resolvers/achievements-form/achievements-form-entitled-to.resolver';
import { AchievementsFormInfluencedByResolver } from './resolvers/achievements-form/achievements-form-influenced-by.resolver';
import { AchievementsFormLearningOutcomeResolver } from './resolvers/achievements-form/achievements-form-learning-outcome.resolver';
import { AchievementsFormProvenByResolver } from './resolvers/achievements-form/achievements-form-proven-by.resolver';
import { AchievementsFormSubAchievementsResolver } from './resolvers/achievements-form/achievements-form-sub-achievements.resolver';
import { AchievementsFormResolver } from './resolvers/achievements-form/achievements-form.resolver';
import { ActivitiesFormAwardedByResolver } from './resolvers/activities-form/activities-form-awarded-by.resolver';
import { ActivitiesFormDirectedByResolver } from './resolvers/activities-form/activities-form-directed-by.resolver';
import { ActivitiesFormSubActivitiesResolver } from './resolvers/activities-form/activities-form-sub-activities.resolver';
import { ActivitiesFormResolver } from './resolvers/activities-form/activities-form.resolver';
import { AssessmentFormAssessmentsResolver } from './resolvers/assessmens-form/assessment-form-assessments.resolver';
import { AssessmentFormAwardedByResolver } from './resolvers/assessmens-form/assessment-form-awarded-by.resolver';
import { AssessmentFormSubAssessmentsResolver } from './resolvers/assessmens-form/assessment-form-sub-assessments.resolver';
import { AssessmentFormResolver } from './resolvers/assessmens-form/assessment-form.resolver';
import { CredentialFormAchievedResolver } from './resolvers/credential-form/credential-form-achieved.resolver';
import { CredentialFormAssessmentsResolver } from './resolvers/credential-form/credential-form-assessments.resolver';
import { CredentialFormEntitledToResolver } from './resolvers/credential-form/credential-form-entitled-to.resolver';
import { CredentialFormHtmlTemplateResolver } from './resolvers/credential-form/credential-form-html-template.resolver';
import { CredentialFormPerformedResolver } from './resolvers/credential-form/credential-form-performed.resolver';
import { CredentialFormResolver } from './resolvers/credential-form/credential-form.resolver';
import { EntitlementsFormAwardedByResolver } from './resolvers/entitlements-form/entitlements-form-awarded-by.resolver';
import { EntitlementsFormSubEntitlementsResolver } from './resolvers/entitlements-form/entitlements-form-sub-entitlements.resolver';
import { EntitlementsFormValidWithResolver } from './resolvers/entitlements-form/entitlements-form-valid-with.resolver';
import { EntitlementsFormResolver } from './resolvers/entitlements-form/entitlements-form.resolver';
import { HtmlTemplateFormResolver } from './resolvers/html-template-form/html-template-form.resolver';
import { LearningOutcomesFormResolver } from './resolvers/learningOutcomesForm/learning-outcomes-form.resolver';
import { OrganizationFormAccreditationResolver } from './resolvers/organization-form/organization-form-accreditation.resolver';
import { OrganizationsFormResolver } from './resolvers/organization-form/organizations-form.resolver';
import { AccreditationFormComponent } from './components/forms/accreditation-form/accreditation-form.component';

const routes: Routes = [
    {
        path: '',
        component: CredentialBuilderComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'credentials',
        component: CredentialsFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'credentials/:id',
        component: CredentialsFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: {
            credentialDetails: CredentialFormResolver,
            credentialAchieved: CredentialFormAchievedResolver,
            credentialEntitledTo: CredentialFormEntitledToResolver,
            credentialPerformed: CredentialFormPerformedResolver,
            credentialHtmlTemplate: CredentialFormHtmlTemplateResolver,
            credentialAssessed: CredentialFormAssessmentsResolver
        }
    },
    {
        path: 'achievements',
        component: AchievementsFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'achievements/:id',
        component: AchievementsFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: {
            achievementDetails: AchievementsFormResolver,
            achievementSubAchievements: AchievementsFormSubAchievementsResolver,
            achievementLearningOutcome: AchievementsFormLearningOutcomeResolver,
            achievementAwardingBodies: AchievementsFormAwardingBodiesResolver,
            achievementEntitledTo: AchievementsFormEntitledToResolver,
            achievementInfluencedBy: AchievementsFormInfluencedByResolver,
            achievementProvenBy: AchievementsFormProvenByResolver
        }
    },
    {
        path: 'activities',
        component: ActivitiesFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'activities/:id',
        component: ActivitiesFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: {
            activityDetails: ActivitiesFormResolver,
            activityDirectedBy: ActivitiesFormDirectedByResolver,
            activityAwardedBy: ActivitiesFormAwardedByResolver,
            activitySubActivities: ActivitiesFormSubActivitiesResolver
        }
    },
    {
        path: 'assessments',
        component: AssessmentsFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'assessments/:id',
        component: AssessmentsFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: {
            assessmentDetails: AssessmentFormResolver,
            assessmentSubAssessments: AssessmentFormSubAssessmentsResolver,
            assessmentAssessments: AssessmentFormAssessmentsResolver,
            assessmentAwardedBy: AssessmentFormAwardedByResolver
        }
    },
    {
        path: 'entitlements',
        component: EntitlementsFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'entitlements/:id',
        component: EntitlementsFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: {
            entitlementDetails: EntitlementsFormResolver,
            entitlementValidWith: EntitlementsFormValidWithResolver,
            entitlementSubEntitlements: EntitlementsFormSubEntitlementsResolver,
            entitlementAwardedBy: EntitlementsFormAwardedByResolver
        }
    },
    {
        path: 'html-templates',
        component: HTMLTemplatesFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'html-templates/:id',
        component: HTMLTemplatesFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: { htmlTemplateDetails: HtmlTemplateFormResolver }
    },
    {
        path: 'learning-outcomes',
        component: LearningOutcomesFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'learning-outcomes/:id',
        component: LearningOutcomesFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: { learningOutcomeDetails: LearningOutcomesFormResolver }
    },
    {
        path: 'organizations',
        component: OrganizationsFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'organizations/:id',
        component: OrganizationsFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: {
            organizationDetails: OrganizationsFormResolver ,
            accreditation: OrganizationFormAccreditationResolver
        }
    },
    {
        path: 'issue/fields/:id',
        component: CustomizeDataComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'issue/form/:id',
        component: FillFormCustomizeDataComponent,
        canActivate: [CredentialBuilderGuard],
    },
    /* Accreditation */
    /* create single route */
    {
        path: 'accreditation',
        component: AccreditationFormComponent,
        canActivate: [CredentialBuilderGuard],
    },
    {
        path: 'accreditation/:id',
        component: AccreditationFormComponent,
        canActivate: [CredentialBuilderGuard],
        resolve: {
            accreditationDetails: AccreditationFormResolver,
            accreditationAccreditingAgent: AccreditationFormAccreditingAgentResolver
        }
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
})
export class CredentialBuilderRoutingModule {}
