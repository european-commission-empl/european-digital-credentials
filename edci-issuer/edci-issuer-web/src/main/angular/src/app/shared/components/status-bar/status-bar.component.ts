import { Component, Input, OnInit, OnDestroy, ViewEncapsulation } from '@angular/core';
import { StepInterface } from '@core/models/step-status-bar.model';
import { UxWizardStep } from '@eui/components/legacy/ux-wizard-step';
import { StatusBarService } from '@services/status-bar.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-status-bar',
    templateUrl: 'status-bar.component.html',
    styleUrls: ['./status-bar.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class StatusBarComponent implements OnInit, OnDestroy {

    stepSelected: any;
    isNavigationAllowed = true;
    currentStepIndex = 0;
    stepsCount = 4;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    @Input()
        steps: StepInterface[] = [];

    constructor(
        private statusBarService: StatusBarService,
    ) {}

    ngOnInit() {
        this.statusBarService.statusBarStepsObservable.pipe(takeUntil(this.destroy$)).subscribe(res => {
            this.steps = res;
        });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onSelectStep(event: UxWizardStep) {
        this.stepSelected = event;
    }

    onToggleNavigation(event: any) {
        this.isNavigationAllowed = !this.isNavigationAllowed;
    }

    onSelectStepRemoteNav(event: any) {
        this.currentStepIndex = event.index;
    }

    onNavigation(increment: number) {
        const newIndex: number = this.currentStepIndex + increment;
        if (newIndex >= 1 && newIndex <= this.stepsCount ) {
            this.currentStepIndex = newIndex;
        }
    }

}
