import { Component, Inject, ViewEncapsulation } from '@angular/core';
import { MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

@Component({
    templateUrl: './snack-bar.component.html',
    styleUrls: ['./snack-bar.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class SnackBarComponent {

    code: number;
    message: SafeHtml;
    actions: {} = null;

    constructor(@Inject(MAT_SNACK_BAR_DATA) public data: any,
        private sanitizer: DomSanitizer) {
        this.code = this.data.status;
        this.message = sanitizer.bypassSecurityTrustHtml(this.data.message);
    }

}
