import { NgModule } from '@angular/core';

import { TableModule } from 'primeng/table';
import { ProgressBarModule } from 'primeng/progressbar';
import { MultiSelectModule } from 'primeng/multiselect';
import { FileUploadModule } from 'primeng/fileupload';

export const moduleList = [
    TableModule,
    ProgressBarModule,
    FileUploadModule,
    MultiSelectModule
];

@NgModule({
    imports: moduleList,
    exports: moduleList
})
export class PrimeNgModule {}
