import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DiplomaComponent } from './diploma.component';

const routes: Routes = [{ path: '', component: DiplomaComponent }];

@NgModule({
    imports: [RouterModule.forChild(routes)]
})
export class DiplomaRoutingModule {}
