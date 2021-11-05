import { Component, ViewEncapsulation, OnDestroy, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';
import { NexUService, MessageEmitter } from '@services/nexU.service';
import { Subscription } from 'rxjs';

@Component({
    selector: 'edci-nexu-dialog',
    templateUrl: 'nexu-dialog.component.html',
    styleUrls: ['./nexu-dialog.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class NexUDialogComponent implements OnInit, OnDestroy {
    private testStepsSubscription: Subscription;
    data: MessageEmitter[];

    constructor(private nexU: NexUService,
      public dialogRef: MatDialogRef<NexUDialogComponent>) {
    }

    ngOnInit() {
        this.nexU.resetSteps();
        this.testStepsSubscription = this.nexU.getTestSteps().subscribe(
            (data: MessageEmitter[]) => this.data = data);
    }

    ngOnDestroy() {
        if (this.testStepsSubscription) {
            this.testStepsSubscription.unsubscribe();
        }
    }

    public onNoClick(): void {
        this.dialogRef.close();
    }
}
