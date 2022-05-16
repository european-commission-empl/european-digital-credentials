import { Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { UxService } from '@eui/base';

@Component({
    selector: 'edci-viewer-display-diploma',
    templateUrl: './display-diploma.component.html',
    styleUrls: ['./display-diploma.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class DisplayDiplomaComponent implements OnDestroy {
    diplomaImg: string[];
    page: number | string = 0;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private shareDataService: ShareDataService,
        private uxService: UxService
    ) {
        this.diplomaImg = JSON.parse(sessionStorage.getItem('diplomaImg'));
        this.shareDataService
            .changeDiplomaImage()
            .takeUntil(this.destroy$)
            .subscribe((diplomaImg) => (this.diplomaImg = diplomaImg));
    }

    ngOnDestroy(): void {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    openDiplomaModal(): void {
        this.page = 0;
        this.uxService.openModal('pdfModal');
    }

    previousPage(): void {
        this.page = (this.page as number) - 1;
    }

    nextPage(): void {
        this.page = (this.page as number) + 1;
    }

    toFirstPage(): void {
        this.page = 0;
    }

    toLastPage(): void {
        this.page = this.diplomaImg.length - 1;
    }

    closeModalPDF(): void {
        this.page = 0;
        this.uxService.closeModal('pdfModal');
    }
}
