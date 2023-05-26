import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormArray, FormControl } from '@angular/forms';
import { CodeDTView, ContentDTView } from '@shared/swagger';

@Component({
    selector: 'edci-controlled-list-free',
    templateUrl: './controlled-list-free.component.html',
    styleUrls: ['./controlled-list-free.component.scss']
})
export class ControlledListFreeComponent implements OnInit {

    @Input() preSelectedValues: CodeDTView[];
    @Input() selectedLanguage: string;
    @Input() isDisabled: boolean;
    @Output() selectionChanged = new EventEmitter<CodeDTView[]>();
    @Input() labelText = '';
    @Input() tooltipText = '';

    selection: FormArray = new FormArray([]);
    currentText = '';

    constructor() { }

    ngOnInit(): void {

        this.preSelectedValues.forEach(value => {
            this.selection.push(new FormControl(value));
        });

        this.selection.valueChanges.subscribe(this.selectionValueChanged.bind(this));

    }

    addElement(event: any): void {
        this.currentText = this.currentText.trim();
        if (this.currentText && !this.doesAlreadyExist(this.currentText)) {
            const newCode: CodeDTView = {
                targetName : {
                    contents : [{
                        content : this.currentText,
                        language : this.selectedLanguage
                    }]
                }
            };
            this.selection.push(new FormControl(newCode));
            this.currentText = '';
        }
        event.stopPropagation();
        event.preventDefault();
    }

    onChipRemove(event: any) {
        const selectionIndex = this.selection.getRawValue().findIndex(item => item.targetName[this.selectedLanguage] === event.removed.id);
        this.selection.removeAt(selectionIndex);
    }

    private selectionValueChanged(value: CodeDTView[]): void {
        this.selectionChanged.emit(value);
    }

    private doesAlreadyExist(text: string): boolean {
        return this.selection.getRawValue().find(item => {
            return item.targetName[this.selectedLanguage] === text;
        });
    }

}
