import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ControlledListFreeComponent } from './controlled-list-free.component';

describe('ControlledListFreeComponent', () => {
    let component: ControlledListFreeComponent;
    let fixture: ComponentFixture<ControlledListFreeComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [ ControlledListFreeComponent ]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ControlledListFreeComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
