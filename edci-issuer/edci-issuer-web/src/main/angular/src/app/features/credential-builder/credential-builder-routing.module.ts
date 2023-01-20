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
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
})
export class CredentialBuilderRoutingModule {}
