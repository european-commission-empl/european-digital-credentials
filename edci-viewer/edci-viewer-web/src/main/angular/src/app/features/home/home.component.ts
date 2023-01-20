import {
    Component,
    ElementRef,
    OnDestroy,
    ViewChild,
    ViewEncapsulation,
} from '@angular/core';
import { Router } from '@angular/router';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { Subject } from 'rxjs';
import { ShareDataService } from 'src/app/core/services/share-data.service';
import { environment } from 'src/environments/environment';
import { takeUntil } from 'rxjs/operators';

@Component({
    templateUrl: './home.component.html',
    styleUrls: ['home.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class HomeComponent implements OnDestroy {
    @ViewChild('inputFile') inputFile: ElementRef;
    ePortfolioUrl: string =
        environment.ePortfolioUrl + this.translateService.currentLang;
    hasBranding: boolean = environment.hasBranding;
    homeMenuMainTitle: string = environment.homeMenuMainTitle;
    homeMenuDescription: string = environment.homeMenuDescription;
    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private router: Router,
        private shareDataService: ShareDataService,
        private translateService: TranslateService
    ) {
        this.translateService.onLangChange
            .pipe(takeUntil(this.destroy$))
            .subscribe((event: LangChangeEvent) => {
                this.ePortfolioUrl = environment.ePortfolioUrl + event.lang;
            });
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    /**
     * Reads the content of an uploaded XML file and stores it as string.
     */
    readFile(event: FileList): void {
        const reader = new FileReader();
        reader.readAsText(event[0], 'utf-8');
        reader.onloadend = () => {
            sessionStorage.clear();
            sessionStorage.setItem('diplomaXML', reader.result as string);
            this.router.navigate(['diploma-details']);
        };
    }

    /**
     * Sends the click event to the input
     */
    openDialog() {
        return this.inputFile.nativeElement.click();
    }
}
