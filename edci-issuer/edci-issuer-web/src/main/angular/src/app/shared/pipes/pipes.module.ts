import { NgModule } from '@angular/core';

import { FileSizePipe } from '@shared/pipes/file-size.pipe';
import { HasLanguagePipe, ExtractLabelPipe } from './multilingual.pipe';

// pipes
export const pipes = [FileSizePipe, ExtractLabelPipe, HasLanguagePipe];

@NgModule({
    declarations: pipes,
    exports: pipes,
})
export class PipesModule {}
