import { Component, ViewEncapsulation } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
    selector: 'edci-spinner-dialog',
    templateUrl: 'spinner-dialog.component.html',
    styleUrls: ['./spinner-dialog.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class SpinnerDialogComponent {

    constructor(public dialogRef: MatDialogRef<SpinnerDialogComponent>) {
    }
}
