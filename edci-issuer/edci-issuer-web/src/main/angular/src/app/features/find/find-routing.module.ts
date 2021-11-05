import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FindComponent } from './find.component';

const routes: Routes = [
    { path: '', component: FindComponent },
];

@NgModule({
    imports: [
        RouterModule.forChild(routes)
    ],
})
export class FindRoutingModule {}
