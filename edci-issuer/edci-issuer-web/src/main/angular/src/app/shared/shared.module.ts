import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
    UxAutoCompleteComponentModule,
    UxAutoCompleteTagComponentModule,
    UxBlockContentComponentModule,
    UxButtonComponentModule,
    UxControlFeedbackComponentModule,
    UxDatepickerComponentModule,
    UxDropdownButtonComponentModule,
    UxDropdownButtonItemComponentModule,
    UxDynamicComponentService,
    UxDynamicModalComponentModule,
    UxDynamicModalService,
    UxFormControlComponentModule,
    UxFormGroupComponentModule,
    UxIconComponentModule,
    UxLayoutHorizontalModule,
    UxMessageBoxComponentModule,
    UxModalComponentModule,
    UxPanelComponentModule,
    UxPopoverComponentModule,
    UxTabComponentModule,
    UxTabsComponentModule,
    UxTooltipModule,
    UxFileUploadComponentModule,
} from '@eui/core';
import { EclAllModule } from '@eui/ecl-core';
import { TranslateModule } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { AutocompleteComponent } from './components/autocomplete/autocomplete.component';
import { BreadCrumbComponent } from './components/breadcrumb/breadcrumb.component';
import { CbLanguageTabsComponent } from './components/cb-language-tabs/cb-language-tabs.component';
import { CbModalFooterComponent } from './components/cb-modal-footer/cb-modal-footer.component';
import { CbTabHeaderComponent } from './components/cb-tab-header/cb-tab-header.component';
import { CbTableComponent } from './components/cb-table/cb-table.component';
import { ControlledListComponent } from './components/controled-list/controlled-list.component';
import { ControlledListSelectComponent } from './components/controlled-list-select/controlled-list-select.component';
import { CustomHtmlLabelsComponent } from './components/custom-html-labels/custom-html-labels.component';
import { MoreInformationComponent } from './components/more-information/more-information.component';
import { NexUDialogComponent } from './components/nexU-dialog/nexu-dialog.component';
import { ProgressbarComponent } from './components/progress-bar/progress-bar.component';
import { SnackBarComponent } from './components/snack-bar/snack-bar.component';
import { SpinnerDialogComponent } from './components/spinner-dialog/spinner-dialog.component';
import { UploadComponent } from './components/upload/upload.component';
import { RouteTransformerDirective } from './directives/routeTransformer.directive';
import { MaterialModule } from './material/material.module';
import { ExtractLabelPipe } from './pipes/multilingual.pipe';
import { PipesModule } from './pipes/pipes.module';
import { PrimeNgModule } from './primeng/primeng.module';

@NgModule({
    imports: [
        CommonModule,
        DragDropModule,
        EclAllModule,
        FormsModule,
        MaterialModule,
        PipesModule,
        PrimeNgModule,
        ReactiveFormsModule,
        RouterModule,
        ToastModule,
        TranslateModule,
        UxAutoCompleteComponentModule,
        UxAutoCompleteTagComponentModule,
        UxBlockContentComponentModule,
        UxButtonComponentModule,
        UxControlFeedbackComponentModule,
        UxDatepickerComponentModule,
        UxDropdownButtonComponentModule,
        UxDropdownButtonItemComponentModule,
        UxDynamicModalComponentModule,
        UxFormControlComponentModule,
        UxFormGroupComponentModule,
        UxIconComponentModule,
        UxLayoutHorizontalModule,
        UxMessageBoxComponentModule,
        UxMessageBoxComponentModule,
        UxModalComponentModule,
        UxPanelComponentModule,
        UxPopoverComponentModule,
        UxTabComponentModule,
        UxTabsComponentModule,
        UxFileUploadComponentModule,
        UxTooltipModule,
    ],
    declarations: [
        AutocompleteComponent,
        BreadCrumbComponent,
        CbLanguageTabsComponent,
        CbModalFooterComponent,
        CbTabHeaderComponent,
        CbTableComponent,
        ControlledListComponent,
        ControlledListSelectComponent,
        CustomHtmlLabelsComponent,
        MoreInformationComponent,
        NexUDialogComponent,
        ProgressbarComponent,
        RouteTransformerDirective,
        SnackBarComponent,
        SpinnerDialogComponent,
        UploadComponent,
    ],
    exports: [
        AutocompleteComponent,
        BreadCrumbComponent,
        CbLanguageTabsComponent,
        CbModalFooterComponent,
        CbTabHeaderComponent,
        CbTableComponent,
        CommonModule,
        ControlledListComponent,
        ControlledListSelectComponent,
        CustomHtmlLabelsComponent,
        DragDropModule,
        EclAllModule,
        FormsModule,
        MaterialModule,
        MoreInformationComponent,
        NexUDialogComponent,
        PipesModule,
        PrimeNgModule,
        ProgressbarComponent,
        ReactiveFormsModule,
        RouterModule,
        SnackBarComponent,
        SpinnerDialogComponent,
        ToastModule,
        TranslateModule,
        UploadComponent,
        UxAutoCompleteComponentModule,
        UxAutoCompleteTagComponentModule,
        UxBlockContentComponentModule,
        UxButtonComponentModule,
        UxControlFeedbackComponentModule,
        UxDatepickerComponentModule,
        UxDropdownButtonComponentModule,
        UxDropdownButtonItemComponentModule,
        UxDynamicModalComponentModule,
        UxFormControlComponentModule,
        UxFormGroupComponentModule,
        UxIconComponentModule,
        UxLayoutHorizontalModule,
        UxMessageBoxComponentModule,
        UxMessageBoxComponentModule,
        UxModalComponentModule,
        UxPanelComponentModule,
        UxPopoverComponentModule,
        UxTabComponentModule,
        UxTabsComponentModule,
        UxFileUploadComponentModule,
        UxTooltipModule,
    ],
    providers: [
        ExtractLabelPipe,
        PipesModule,
        UxDynamicComponentService,
        UxDynamicModalService,
        MessageService,
    ],
    entryComponents: [
        NexUDialogComponent,
        SnackBarComponent,
        SpinnerDialogComponent,
    ],
})
export class SharedModule {}
