import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AchievementsComponent } from './components/achievements/achievements.component';
import { ActivitiesComponent } from './components/activities/activities.component';
import { AssessmentsComponent } from './components/assessments/assessments.component';
import { CredentialOwnerComponent } from './components/credential-owner/credential-owner.component';
import { EntitlementsComponent } from './components/entitlements/entitlements.component';
import { IssuingOrganizationComponent } from './components/issuing-organization/issuing-organization.component';
import { VerificationComponent } from './components/verification/verification.component';
import { DiplomaDetailsComponent } from './diploma-details.component';

export const routes: Routes = [
    {
        path: '',
        component: DiplomaDetailsComponent,
        children: [
            {
                path: '',
                redirectTo: 'diploma',
                pathMatch: 'full',
            },
            {
                path: 'organisation',
                component: IssuingOrganizationComponent,
            },
            {
                path: 'subject',
                component: CredentialOwnerComponent,
            },
            {
                path: 'achievement',
                component: AchievementsComponent,
            },
            {
                path: 'activity',
                component: ActivitiesComponent,
            },
            {
                path: 'assessment',
                component: AssessmentsComponent,
            },
            {
                path: 'diploma',
                component: VerificationComponent,
            },
            {
                path: 'entitlement',
                component: EntitlementsComponent,
            },
        ],
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
})
export class DiplomaDetailsRoutingModule {}
