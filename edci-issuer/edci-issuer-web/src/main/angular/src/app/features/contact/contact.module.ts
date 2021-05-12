import { NgModule } from '@angular/core';
import { SharedModule } from '@shared/shared.module';
import { ContactComponent } from './contact.component';
import { ContactRoutingModule } from './contact-routing.module';

@NgModule({
    imports: [
        SharedModule,
        ContactRoutingModule
    ],
    declarations: [
        ContactComponent
    ],
})
export class ContactModule { }
