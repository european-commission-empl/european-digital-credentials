import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { AchievementsComponent } from './components/achievements/achievements.component';
import { ActivitiesComponent } from './components/activities/activities.component';
import { AssessmentsComponent } from './components/assessments/assessments.component';
import { CredentialOwnerComponent } from './components/credential-owner/credential-owner.component';
import { DisplayDiplomaComponent } from './components/display-diploma/display-diploma.component';
import { EntitlementsComponent } from './components/entitlements/entitlements.component';
import { IssuingOrganizationComponent } from './components/issuing-organization/issuing-organization.component';
import { VerificationComponent } from './components/verification/verification.component';
import { DiplomaDetailsRoutingModule } from './diploma-details-routing.module';
import { DiplomaDetailsComponent } from './diploma-details.component';

@NgModule({
    imports: [DiplomaDetailsRoutingModule, SharedModule],
    declarations: [
        AchievementsComponent,
        ActivitiesComponent,
        AssessmentsComponent,
        CredentialOwnerComponent,
        DiplomaDetailsComponent,
        DisplayDiplomaComponent,
        EntitlementsComponent,
        IssuingOrganizationComponent,
        VerificationComponent,
    ],
})
export class DiplomaDetailsModule {}
