import { NgModule } from '@angular/core';
import { FileSizePipe } from '@shared/pipes/file-size.pipe';
import { ExtractLabelPipe, HasLanguagePipe } from './multilingual.pipe';
import { OcbModalBreadcrumbPipe } from './ocb-modal-breadcrumb.pipe';

export const pipes = [
    FileSizePipe,
    ExtractLabelPipe,
    HasLanguagePipe,
    OcbModalBreadcrumbPipe,
];

@NgModule({
    declarations: pipes,
    exports: pipes,
})
export class PipesModule {}
