import { Component, Input, OnInit, OnDestroy, Output , EventEmitter } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { Subject } from 'rxjs';
import { ENTITES_SEARCH_FIELDS, Entities } from '@shared/constants';
import { takeUntil, debounceTime } from 'rxjs/operators';

@Component({
    selector: 'edci-input-search',
    templateUrl: './input-search.component.html',
    styleUrls: ['./input-search.component.scss']
})
export class InputSearchComponent implements OnInit, OnDestroy {

    @Input() title: string;
    @Input() placeHolder: string;
    @Input() entityType: Entities;
    @Output() onChange = new EventEmitter<string>();
    @Output() onEmittedOCBQuery = new EventEmitter<string>();
    private emitFromXChars = 3;
    private _debounceTime = 500;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    public form: FormGroup = new FormGroup({
        inputText : new FormControl()
    });

    get inputTextControl(): FormControl {
        return this.form.get('inputText') as FormControl;
    }

    ngOnInit(): void {
        this.inputTextControl.valueChanges.pipe(
            debounceTime(this._debounceTime),
            takeUntil(this.destroy$))
            .subscribe(value => {
                if (value.length >= this.emitFromXChars || !value) {
                    this.emitEvents(value);
                }
            });
    }

    checkRequiredFields(title, placeHolder) {
        if (!title || !placeHolder) {
            throw new Error('Missing Attribute for component input-search');
        }
    }
    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    onClick(): void {
        this.emitEvents(this.inputTextControl.value);
    }

    emitEvents(value: string): void {
        if (this.entityType) {
            this.onEmittedOCBQuery.emit(this.generateOCBQuery(this.entityType, value));
        }
        this.onChange.emit(value);
    }

    private generateOCBQuery(entityType: Entities, input: string): string {
        const fields = ENTITES_SEARCH_FIELDS[entityType];
        let ocbQuery = '';
        for (let i = 0 ; i < fields.length ; i++) {
            const field = fields[i];
            ocbQuery = ocbQuery.concat(field).concat('~').concat(input);
            if (i < fields.length - 1) {
                ocbQuery = ocbQuery.concat(';');
            }
        }
        return ocbQuery;
    }

}
