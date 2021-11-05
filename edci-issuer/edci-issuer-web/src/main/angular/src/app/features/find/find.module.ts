import { NgModule } from '@angular/core';
import { SharedModule } from '@shared/shared.module';

import { FindComponent } from './find.component';
import { FindRoutingModule } from './find-routing.module';

@NgModule({
    imports: [
        SharedModule,
        FindRoutingModule
    ],
    declarations: [
        FindComponent
    ],
})
export class FindModule { }
