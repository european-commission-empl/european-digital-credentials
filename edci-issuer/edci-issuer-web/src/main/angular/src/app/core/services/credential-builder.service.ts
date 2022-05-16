import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { FormGroup, FormArray, FormControl, Validators } from '@angular/forms';
import {
    NoteDTView,
    TextDTView,
    WebDocumentDCView,
    ContentDTView,
    IdentifierDTView,
} from '@shared/swagger';
import { MultilingualService } from './multilingual.service';
import { get as _get } from 'lodash';
import { webDocumentValidator } from '@shared/validators/web-document.validators';
import { Constants } from '@shared/constants';
import { identifierValidator } from '@shared/validators/identifier.validators';

@Injectable({
    providedIn: 'root',
})
export class CredentialBuilderService {
    public redirectToTab: Subject<number> = new Subject();
    public redirectToPage: Subject<number> = new Subject();
    modalIdNumber: number = 0;
    listModalIds: string[] = [];
    isNewEntityDisabled: boolean;
    listModalTitles: string[] = [];

    constructor(private multilingualService: MultilingualService) {}

    getDTView(multilingualField: FormGroup): TextDTView | NoteDTView {
        let text: TextDTView | NoteDTView = null;
        let contents = this.multilingualService.formToView(
            multilingualField.value
        );
        if (contents.length > 0) {
            text = {
                contents: contents,
            };
        }
        return text;
    }

    getHomePage(homePageURL: string): Array<WebDocumentDCView> {
        let homePage: WebDocumentDCView[] = null;
        if (homePageURL) {
            homePage = [
                {
                    content: homePageURL,
                },
            ];
        }
        return homePage;
    }

    getArrayFromSingleItem(item: any): any[] {
        return item ? [item] : null;
    }

    getOtherDocument(
        documents: FormArray,
        language: string
    ): Array<WebDocumentDCView> {
        const otherDocuments: Array<WebDocumentDCView> = [];
        documents.value.forEach((document) => {
            let content = _get(document, 'webDocumentContent', null);
            if (content) {
                otherDocuments.push({
                    content: content,
                    title: {
                        contents: this.getTitle(document, language),
                    },
                });
            }
        });
        return otherDocuments.length > 0 ? otherDocuments : null;
    }

    extractWebDocuments(
        documents: WebDocumentDCView[],
        formArray: FormArray
    ): void {
        documents.forEach((webDocument: WebDocumentDCView) => {
            formArray.push(
                new FormGroup(
                    {
                        webDocumentTitle: new FormControl(
                            _get(
                                webDocument,
                                'title.contents[0].content',
                                null
                            ),
                            [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]
                        ),
                        webDocumentContent: new FormControl(
                            _get(webDocument, 'content', null),
                            [
                                Validators.maxLength(
                                    Constants.MAX_LENGTH_DEFAULT
                                ),
                                Validators.pattern(
                                    Constants.URL_REGULAR_EXPRESSION
                                ),
                            ]
                        ),
                    },
                    webDocumentValidator
                )
            );
        });
        if (formArray.length === 0) {
            this.addOtherDocumentRow(formArray);
        }
    }
    extractIdentifierContent(
        documents: IdentifierDTView[],
        formArray: FormArray
    ): void {
        documents.forEach((identifier: IdentifierDTView) => {
            formArray.push(
                new FormGroup(
                    {
                        identifier: new FormControl(
                            _get(
                                identifier,
                                'content',
                                null
                            ),
                            [Validators.maxLength(Constants.MAX_LENGTH_DEFAULT)]
                        ),
                    },
                    identifierValidator
                )
            );
        });
        if (formArray.length === 0) {
            this.addIdentifierRow(formArray);
        }
    }

    addOtherDocumentRow(formArray: FormArray) {
        formArray.push(
            new FormGroup(
                {
                    webDocumentTitle: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                    webDocumentContent: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                        Validators.pattern(Constants.URL_REGULAR_EXPRESSION),
                    ]),
                },
                webDocumentValidator
            )
        );
    }

    addIdentifierRow(formArray: FormArray) {
        formArray.push(
            new FormGroup(
                {
                    identifier: new FormControl(null, [
                        Validators.maxLength(Constants.MAX_LENGTH_DEFAULT),
                    ]),
                },
                identifierValidator
            )
        );
    }

    removeOtherDocumentRow(formArray: FormArray, index: number) {
        formArray.removeAt(index);
    }

    removeIdentifierRow(formArray: FormArray, index: number) {
        formArray.removeAt(index);
    }

    getIdentifier(identifier: FormControl): Array<IdentifierDTView> {
        let id: IdentifierDTView[] = null;
        if (identifier.value) {
            id = [
                {
                    content: identifier.value,
                },
            ];
        }
        return id;
    }
    getIdentifierAchievements(identifier: FormArray): Array<IdentifierDTView> {
        const identifiers: Array<IdentifierDTView> = [];
        identifier.value.forEach((document) => {
            let content = _get(document, 'identifier', null);
            if (content) {
                identifiers.push({
                    content: content,

                });
            }
        });
        return identifiers.length > 0 ? identifiers : null;
    }

    isObjectEmpty(object): boolean {
        return !Object.values(object).some((x) => !!x);
    }

    /**
     * If object has no info returns null, else returns the object.
     * @param object
     */
    getObjectIfContent(object): any | null {
        return this.isObjectEmpty(object) ? null : object;
    }

    generateNewIdModal(modalTitle: string): string {
        const newModalId = `newModal${this.modalIdNumber++}`;
        this.listModalIds.push(newModalId);
        this.listModalTitles.push(modalTitle);
        this.isNewEntityDisabled = this.listModalIds.length >= 2;
        return newModalId;
    }

    getIdFromLastModalAndRemove(): string {
        const modalIdDeleted = this.listModalIds.pop();
        if (this.listModalIds.length === 1) {
            this.listModalTitles.pop();
        } else {
            this.listModalTitles = [];
        }
        this.isNewEntityDisabled = this.listModalIds.length >= 2;
        return modalIdDeleted;
    }

    fillMultipleInput<T>(obj: any, selectedOids: number[], itemToPush: any, unsavedItems: any = []): T {
        const oidsOfObjToCompare = obj.content.map((c) => c.oid);
        const oidsToBeIncluded = selectedOids.filter((c) => {
            return !oidsOfObjToCompare.includes(c);
        });
        const newContent = obj.content.concat(
            oidsToBeIncluded.map((o) => {
                return unsavedItems.find(f => f.oid === o);
            })
        );

        itemToPush['isNew'] = true;
        if (selectedOids.includes(itemToPush.oid)) {
            newContent.forEach((element, index) => {
                if (element.oid === itemToPush.oid) {
                    newContent[index] = itemToPush;
                }
            });
        } else {
            newContent.push(itemToPush);
        }
        return {
            content: newContent,
            links: obj.links,
            page: obj.page,
        } as unknown as T;
    }

    private getTitle(document, language: string): ContentDTView[] {
        let title: ContentDTView[] = _get(document, 'webDocumentTitle', null)
            ? [{ content: _get(document, 'webDocumentTitle', null) }]
            : null;
        if (title) {
            title.forEach((cont: ContentDTView) => {
                cont.language = language;
            });
        }
        return title;
    }
}
