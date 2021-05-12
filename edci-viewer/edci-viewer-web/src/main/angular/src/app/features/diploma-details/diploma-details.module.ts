import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';

import { DiplomaDetailsComponent } from './diploma-details.component';
import { DiplomaDetailsRoutingModule } from './diploma-details-routing.module';
import { DisplayDiplomaComponent } from './components/display-diploma/display-diploma.component';
import { IssuingOrganizationComponent } from './components/issuing-organization/issuing-organization.component';
import { CredentialOwnerComponent } from './components/credential-owner/credential-owner.component';
import { AchievementsComponent } from './components/achievements/achievements.component';
import { ActivitiesComponent } from './components/activities/activities.component';
import { SubCredentialsComponent } from './components/sub-credentials/sub-credentials.component';
import { VerificationComponent } from './components/verification/verification.component';
import { EntitlementsComponent } from './components/entitlements/entitlements.component';

@NgModule({
    imports: [DiplomaDetailsRoutingModule, SharedModule],
    declarations: [
        DiplomaDetailsComponent,
        DisplayDiplomaComponent,
        IssuingOrganizationComponent,
        CredentialOwnerComponent,
        AchievementsComponent,
        ActivitiesComponent,
        SubCredentialsComponent,
        VerificationComponent,
        EntitlementsComponent
    ]
})
export class Module {}
