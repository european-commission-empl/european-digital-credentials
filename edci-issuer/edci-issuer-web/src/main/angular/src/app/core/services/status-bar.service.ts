import { Injectable } from '@angular/core';
import { StepInterface } from '@core/models/step-status-bar.model';
import { BehaviorSubject } from 'rxjs';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class StatusBarService {

    private statusBarSteps$ = new BehaviorSubject<StepInterface[]>(null);
    statusBarStepsObservable = this.statusBarSteps$.asObservable();

    constructor(
    private translateService: TranslateService,
    ) {
        const stepsStatusBar: StepInterface[] = [
            {
                labelKey: 'common.prepare',
                isCompleted: false,
                isActive: true,
                isInvalid: false,
                isDisabled: false,
                index: 0
            },
            {
                labelKey: 'common.customise',
                isCompleted: false,
                isActive: false,
                isInvalid: false,
                isDisabled: false,
                index: 1
            },
            {
                labelKey: 'common.seal',
                isCompleted: false,
                isActive: false,
                isInvalid: false,
                isDisabled: false,
                index: 2
            },
            {
                labelKey: 'common.send',
                isCompleted: false,
                isActive: false,
                isInvalid: false,
                isDisabled: false,
                index: 3
            }
        ];
        this.setStatusBarSteps(stepsStatusBar);
    }

    setStatusBarSteps(statusBarSteps: StepInterface[]) {
        this.statusBarSteps$.next(statusBarSteps);
    }

    setStepStatusBarActive(stepIndex: number) {
        const steps = this.statusBarSteps$.getValue();
        steps.forEach(s => {
            s.isCompleted = s.index < stepIndex;
            s.isActive = s.index === stepIndex;
        });
        this.setStatusBarSteps(steps);
    }

}
