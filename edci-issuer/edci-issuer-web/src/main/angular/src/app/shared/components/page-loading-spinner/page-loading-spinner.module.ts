import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PageLoadingSpinnerComponent } from './page-loading-spinner.component';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

@NgModule({
    declarations: [
        PageLoadingSpinnerComponent
    ],
    imports: [
        CommonModule,
        MatProgressSpinnerModule
    ],
    exports: [
        PageLoadingSpinnerComponent
    ]
})
export class PageLoadingSpinnerModule { }
