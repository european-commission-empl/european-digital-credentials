import {
    Component,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges,
    ViewEncapsulation,
} from '@angular/core';
import {
    UxEuLanguages,
    UxLanguage,
    UxLink,
    UxAppShellService,
} from '@eui/core';
import { EuiTabComponent } from '@eui/components/eui-tabs';
import { TranslateService } from '@ngx-translate/core';
import { IssuerService } from '@services/issuer.service';

@Component({
    selector: 'edci-cb-language-tabs',
    templateUrl: './cb-language-tabs.component.html',
    styleUrls: ['./cb-language-tabs.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class CbLanguageTabsComponent implements OnInit, OnChanges {
    @Input() selectedLanguages: UxLanguage[] = [];
    @Input() language: string;
    @Output() selectedLanguagesChange: EventEmitter<UxLanguage[]> =
        new EventEmitter<UxLanguage[]>();
    @Output() onLanguageChange: EventEmitter<string> = new EventEmitter();
    @Output() onLanguageRemoved: EventEmitter<string> = new EventEmitter();
    @Output() onLanguageAdded: EventEmitter<string> = new EventEmitter();
    languages: UxLink[] = [];
    activeLanguage: string;

    constructor(
        public uxService: UxAppShellService,
        private issuerService: IssuerService,
        private translateService: TranslateService
    ) {
        const languageList = this.issuerService.addMissingLanguages(
            UxEuLanguages.getLanguages()
        );
        languageList.forEach((language) => {
            this.languages.push(
                new UxLink({
                    id: language.code,
                    label: language.label,
                })
            );
        });
        // this.languages = [new UxLink({ id: 'en', label: 'English' })];
    }

    ngOnInit() {
        this.activeLanguage = this.getActiveLanguage(this.language);
    }

    ngOnChanges(changes: SimpleChanges) {
        if (changes.language && changes.language.currentValue) {
            this.activeLanguage = this.getActiveLanguage(
                changes.language.currentValue
            );
        }
    }

    onLanguageSelected(language: UxLink): void {
        if (
            !this.selectedLanguages.find(
                (selectedLanguage) => selectedLanguage.code === language.id
            )
        ) {
            this.onLanguageAdded.emit(language.id.toLowerCase());
            this.selectedLanguages = this.selectedLanguages.concat([
                { code: language.id, label: language.id },
            ]);
            this.selectedLanguagesChange.emit(this.selectedLanguages);
        }
    }

    languageTabClosed(event: EuiTabComponent | any) {
        let removedLang = this.selectedLanguages[event.index];
        this.selectedLanguages = this.selectedLanguages.filter(
            (language) =>
                language.code.toUpperCase() !== removedLang.label.toUpperCase()

        );
        this.selectedLanguagesChange.emit(this.selectedLanguages);
        this.onLanguageRemoved.emit(removedLang.label.toLowerCase());
    }

    languageTabSelected(event: EuiTabComponent | any) {
        this.onLanguageChange.emit(this.selectedLanguages[event.index].label.toLowerCase());
    }

    private getActiveLanguage(languageCode: string = ''): string {
        // const language = UxEuLanguages.getLanguages([languageCode]);
        // if (language && language.length) {
        //     return language[0].label;
        // }
        // const langOfInterface = UxEuLanguages.getLanguages([
        //     this.translateService.currentLang,
        // ]);
        // if (langOfInterface && langOfInterface.length) {
        //     return langOfInterface[0].label;
        // }
        const activeLanguage: UxLink = this.getLanguage(languageCode);
        return activeLanguage ? activeLanguage.label : 'English';
    }

    private getLanguage(languageCode: string): UxLink {
        let language: UxLink = null;
        this.languages.forEach((lang: UxLink) => {
            if (lang.id === languageCode) {
                language = lang;
            }
        });
        return language;
    }
}
