import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { DiplomaRoutingModule } from './diploma-routing.module';
import { DiplomaComponent } from './diploma.component';

@NgModule({
    imports: [SharedModule, DiplomaRoutingModule],
    declarations: [DiplomaComponent],
    providers: []
})
export class Module {}
