import { Component, OnInit, OnDestroy } from '@angular/core';
import { PageLoadingSpinnerService } from '@services/page-loading-spinner.service';
import { takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';

@Component({
    selector: 'edci-page-loading-spinner',
    templateUrl: './page-loading-spinner.component.html',
})
export class PageLoadingSpinnerComponent implements OnInit, OnDestroy {
    isLoading = false;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(private readonly pageLoadingSpinnerService: PageLoadingSpinnerService) { }

    ngOnInit(): void {
        this.pageLoadingSpinnerService.getPageLoaderStatus()
            .pipe(takeUntil(this.destroy$))
            .subscribe((status) => {
                this.isLoading = status;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }
}
