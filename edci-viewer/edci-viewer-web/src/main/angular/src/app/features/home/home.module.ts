import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { HomeRoutingModule } from './home-routing.module';
import { FileUploadModule } from 'primeng/fileupload';

import { HomeComponent } from './home.component';

@NgModule({
    imports: [SharedModule, HomeRoutingModule, FileUploadModule],
    declarations: [HomeComponent],
})
export class Module {}
