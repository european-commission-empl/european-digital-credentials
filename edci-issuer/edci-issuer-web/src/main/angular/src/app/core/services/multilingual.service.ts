import { Injectable } from '@angular/core';
import { ContentDTView } from '@shared/swagger';
import { UxLanguage } from '@eui/core';

@Injectable({ providedIn: 'root' })
export class MultilingualService {
    constructor() {}
    formToView(formInput): ContentDTView[] {
        const response: ContentDTView[] = [];
        for (const language in formInput) {
            if (formInput.hasOwnProperty(language) && formInput[language]) {
                response.push({
                    content: formInput[language],
                    language: language
                });
            }
        }
        return response;
    }

    getContentFromLanguage(
        language: string,
        contents: ContentDTView[]
    ): string {
        let label = '';
        contents.forEach((content: ContentDTView) => {
            if (content.language === language) {
                label = content.content;
                return;
            }
        });
        return label;
    }

    getUsedLanguages(languages: UxLanguage[]): string[] {
        const usedLanguages: string[] = [];
        languages.forEach((language: UxLanguage) => {
            usedLanguages.push(language.code.toLowerCase());
        });
        return usedLanguages;
    }

    setUsedLanguages(
        languages: string[],
        defaultLanguage: string
    ): UxLanguage[] {
        const usedLanguages: UxLanguage[] = [];
        usedLanguages.push({
            code: defaultLanguage,
            label: defaultLanguage
        });
        languages.forEach((language: string) => {
            if (language !== defaultLanguage) {
                usedLanguages.push({
                    code: language,
                    label: language
                });
            }
        });
        return usedLanguages;
    }
}
