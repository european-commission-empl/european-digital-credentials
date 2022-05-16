import { Pipe, PipeTransform } from '@angular/core';
import { ContentDTView, NoteDTView } from '@shared/swagger';

// tslint:disable-next-line: pipe-prefix
@Pipe({ name: 'extractLabel' })
export class ExtractLabelPipe implements PipeTransform {
    transform(
        contents: ContentDTView[],
        language: string,
        useDefault: boolean = false
    ): string {
        let label: string = '';
        let defaultLabel: string = '';
        if (contents && contents.length > 0) {
            defaultLabel = contents[0].content;
            contents.forEach((content: ContentDTView) => {
                if (content.language === language) {
                    label = content.content;
                } else if (useDefault && content.language === 'en') {
                    defaultLabel = content.content;
                }
            });
        }
        return !label && useDefault ? defaultLabel : label;
    }
}

// tslint:disable-next-line: pipe-prefix
@Pipe({ name: 'noteHasLanguage' })
export class HasLanguagePipe implements PipeTransform {
    transform(notes: NoteDTView[], language: string): boolean {
        let hasLanguage: boolean = false;
        if (notes && notes.length > 0) {
            notes.forEach((note: NoteDTView) => {
                if (new ExtractLabelPipe().transform(note.contents, language)) {
                    hasLanguage = true;
                }
            });
        }
        return hasLanguage;
    }
}
