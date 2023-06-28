import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccreditationFormComponent } from './accreditation-form.component';
import { AccreditationFormRoutingModule } from './accreditation-form-routing.module';
import { SharedModule } from '@shared/shared.module';

@NgModule({
    declarations: [AccreditationFormComponent],
    imports: [
        CommonModule,
        AccreditationFormRoutingModule,
        SharedModule
    ]
})
export class AccreditationFormModule { }
