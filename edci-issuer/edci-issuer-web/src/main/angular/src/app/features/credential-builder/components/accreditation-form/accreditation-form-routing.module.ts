import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AccreditationFormComponent } from './accreditation-form.component';

const routes: Routes = [
    {
        path: '',
        component: AccreditationFormComponent,
    },
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule],
})
export class AccreditationFormRoutingModule {}
