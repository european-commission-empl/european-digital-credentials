import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DiplomaDetailsComponent } from './diploma-details.component';
import { IssuingOrganizationComponent } from './components/issuing-organization/issuing-organization.component';
import { CredentialOwnerComponent } from './components/credential-owner/credential-owner.component';
import { AchievementsComponent } from './components/achievements/achievements.component';
import { ActivitiesComponent } from './components/activities/activities.component';
import { SubCredentialsComponent } from './components/sub-credentials/sub-credentials.component';
import { VerificationComponent } from './components/verification/verification.component';
import { EntitlementsComponent } from './components/entitlements/entitlements.component';

export const routes: Routes = [
    {
        path: '',
        component: DiplomaDetailsComponent,
        children: [
            {
                path: '',
                redirectTo: 'diploma',
                pathMatch: 'full'
            },
            {
                path: 'issuing-organization',
                component: IssuingOrganizationComponent
            },
            {
                path: 'credential-owner',
                component: CredentialOwnerComponent
            },
            {
                path: 'achievements',
                component: AchievementsComponent
            },
            {
                path: 'activities',
                component: ActivitiesComponent
            },
            {
                path: 'sub-credentials',
                component: SubCredentialsComponent
            },
            {
                path: 'diploma',
                component: VerificationComponent
            },
            // {
            //     path: 'verification',
            //     component: VerificationComponent
            // },
            {
                path: 'entitlements',
                component: EntitlementsComponent
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class DiplomaDetailsRoutingModule {}
