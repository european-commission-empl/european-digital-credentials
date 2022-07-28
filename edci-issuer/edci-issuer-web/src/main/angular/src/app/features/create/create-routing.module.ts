import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { CreateComponent } from './create.component';
import { OverviewComponent } from './overview/overview.component';
import { OverviewGuard } from './overview/overview.guard';

const routes: Routes = [
    {
        path: '',
        component: CreateComponent,
        children: [
            { path: '', redirectTo: 'overview', pathMatch: 'full' },
            {
                path: 'overview',
                component: OverviewComponent,
                canActivate: [OverviewGuard]
            }
        ]
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class CreateRoutingModule {}
