import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CredentialBuilderComponent } from './credential-builder.component';
import { CredentialBuilderGuard } from './credential-builder.guard';

const routes: Routes = [
    {
        path: '',
        component: CredentialBuilderComponent,
        canActivate: [CredentialBuilderGuard]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class CredentialBuilderRoutingModule {}
