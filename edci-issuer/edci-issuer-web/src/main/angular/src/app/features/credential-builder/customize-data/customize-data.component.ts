import {
    Component,
    ElementRef, OnDestroy, OnInit,
    ViewChild, ViewEncapsulation
} from '@angular/core';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { environment } from '@environments/environment';
import { EuiDialogComponent } from '@eui/components/eui-dialog';
import { UxAppShellService, UxLanguage, UxLink } from '@eui/core';
import { LangChangeEvent, TranslateService } from '@ngx-translate/core';
import { CredentialBuilderService } from '@services/credential-builder.service';
import { IssuerService } from '@services/issuer.service';
import { StatusBarService } from '@services/status-bar.service';
import {
    CredentialView,
    CustomizableFieldView,
    CustomizableInstanceSpecView,
    CustomizableSpecView,
    V1Service
} from '@shared/swagger';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
    selector: 'edci-customize-data',
    templateUrl: './customize-data.component.html',
    styleUrls: ['./customize-data.component.scss'],
    encapsulation: ViewEncapsulation.None
})
export class CustomizeDataComponent implements OnInit, OnDestroy {
    @ViewChild('inputFile') inputFile: ElementRef;
    @ViewChild('uploadDialog') uploadDialog: EuiDialogComponent;

    languages: UxLanguage[] = [];
    parts: UxLink[] = [];
    urlActivated: string;
    selectedLanguage: string = this.translateService.currentLang;
    customizableDataArchitect: CustomizableSpecView;
    customizedData: CustomizableSpecView = {
        customizableEntityViews: []
    };

    customizedView: CustomizableInstanceSpecView;
    issuingOid = -1;
    loading = false;

    uploads: Array<File> = [];
    error: boolean;
    errorMessage: string;
    errorOfValidation: boolean;
    errorOfValidationMessage: string;
    inputFileIsEnabled: boolean;
    isLoadingRecipientTemplate: boolean;
    deletedList: Array<File> = [];

    destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private issuerService: IssuerService,
        private translateService: TranslateService,
        private api: V1Service,
        private uxService: UxAppShellService,
        private credentialBuilderService: CredentialBuilderService,
        private router: Router,
        private route: ActivatedRoute,
        private statusBarService: StatusBarService,
    ) {
        this.issuerService.breadcrumbSubject.pipe(takeUntil(this.destroy$)).subscribe((url: string) => {
            this.urlActivated = url;
            this.loadBreadcrumb();
        });
        this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((params: ParamMap) => {
            this.issuingOid = parseInt(params.get('id'));
        });
        this.translateService.onLangChange.pipe(takeUntil(this.destroy$)).subscribe((event: LangChangeEvent) => {
            this.selectedLanguage = event.lang;
            this.loadBreadcrumb();
        });
    }

    ngOnInit() {
        this.loading = true;
        this.loadJSON();
        this.loadBreadcrumb();
        this.statusBarService.setStepStatusBarActive(1);
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    public openDynamicModal(): void {
        this.closeIssuingModeModal();
        this.inputFileIsEnabled = true;
        this.uploadDialog.dismissLabel =
            this.translateService.instant('common.cancel');
        this.uploadDialog.acceptLabel =
            this.translateService.instant('common.upload');
        this.uploadDialog.openDialog();
    }

    public onAccept(dialog: string): void {
        this[dialog].closeDialog();
        this.openDialog();
    }

    closeIssuingModeModal(byError = false): void {
        if (!this.isLoadingRecipientTemplate || byError) {
            this.isLoadingRecipientTemplate = false;
            this.uxService.closeModal('issuingModeModal');
        }
    }

    public openIssuingModeModal(): void {
        this.isLoadingRecipientTemplate = false;
    }

    public openDialog() {
        this.openIssuingModeModal();
        return this.inputFile.nativeElement.click();
    }

    public onDismiss(dialogClosed): void {
        this[dialogClosed].closeDialog();
    }

    public readFile(event: FileList) {
        this.loading = true;
        this.inputFileIsEnabled = false;
        if (event.length === 0) {
            return false;
        }
        this.cleanUploadErrors();
        const file = event[0];
        if (this.validateFile(file)) {
            this.pushFile(file);
            this.inputFile.nativeElement.value = '';
        }
    }

    public checkRelated(field: CustomizableFieldView, position) {
        const findRelation = (relation) => field.fieldPath === relation.relPath;
        this.addCheck(position, this.customizableDataArchitect.customizableEntityViews[position].relations.findIndex(findRelation), true);
    }

    public addCheck(positionField, i, isRelation = false) {
        if (!isRelation) {
            const field = this.customizableDataArchitect.customizableEntityViews[positionField].fields[i];
            if (this.customizedData.customizableEntityViews[positionField].fields.find((f) => f.label === field.label) === undefined) {
                this.customizedData.customizableEntityViews[positionField].fields.push(field);
                this.sortFields('customizedData', positionField);
            } else {
                const fieldsAux = this.customizedData.customizableEntityViews[positionField].fields.filter(f => {
                    return f.label !== field.label;
                });
                this.customizedData.customizableEntityViews[positionField].fields = fieldsAux;
            }
            if (field.relationDependant) {
                this.checkRelated(field, positionField);
            }
        } else {
            const relation = this.customizableDataArchitect.customizableEntityViews[positionField].relations[i];
            const entityFound = this.customizedData.customizableEntityViews[positionField].relations.find((f) =>
                f.label === relation.label);
            if (entityFound === undefined) {
                this.customizedData.customizableEntityViews[positionField].relations.push(relation);
                this.sortFields('customizedData', positionField, false);
            } else {
                const relationAux = this.customizedData.customizableEntityViews[positionField].relations.filter(f => {
                    return f.label !== relation.label;
                });
                this.customizedData.customizableEntityViews[positionField].relations = relationAux;
            }
        }
    }

    public fillForm() {
        this.loading = true;
        this.credentialBuilderService.setCustomizedViewRequestData(this.customizedData);
        this.router.navigateByUrl('credential-builder/issue/form/' + this.issuingOid);
    }

    public downloadExcel() {
        this.loading = true;
        this.issuerService.isExcelDownloadedSubject.pipe(takeUntil(this.destroy$)).subscribe(() => {
            this.loading = false;
        });
        this.issuerService.downloadCustomizableTemplate(this.issuingOid, this.selectedLanguage, this.customizedData);
    }

    back() {
        this.router.navigateByUrl('/credential-builder');
    }

    private generateVoidCustomizedData() {
        this.customizedData.customizableEntityViews = [];
        this.customizableDataArchitect.customizableEntityViews.forEach(entity => {
            const entityAux = JSON.parse(JSON.stringify(entity));
            entityAux.fields = entityAux.fields.filter(f => { return f.mandatory === true; });
            entityAux.relations = entityAux.relations.filter(f => { return f.mandatory === true; });
            this.customizedData.customizableEntityViews.push(entityAux);
        });
        this.loading = false;
    }

    private pushFile(file: File) {
        this.isLoadingRecipientTemplate = true;
        this.uploads = [];
        this.uploads.push(file);
        this.api.issueCredentialsFromRecipientsXLS(file, this.selectedLanguage)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
                next : (res: any) => {
                    if (res.valid) {
                        this.loading = false;
                        this.issuerService.setCredentials(
                            <CredentialView[]>res.credentials
                        );
                        this.router.navigate(['/create/overview']);
                    }
                }
            }).add(() => this.loading = false);
    }

    private cleanUploadErrors() {
        this.error = false;
        this.errorOfValidation = false;
        this.errorMessage = '';
        this.errorOfValidationMessage = '';
    }

    private validateFile(file: File): boolean {
        if (!this.validateSize(file)) {
            this.error = true;
            this.errorMessage = `${this.translateService.instant(
                'file-upload.no-more-than'
            )} ${environment.maxUploadSizeMB} MB`;
            return false;
        }

        if (!this.validateFileExtension(file)) {
            this.error = true;
            this.errorMessage = this.translateService.instant(
                'file-upload.wrongExtension'
            );
            return false;
        }
        return true;
    }

    private validateSize(file): boolean {
        return file.size < 5242880;
    }

    private validateFileExtension(file): boolean {
        const extension = file.name.split('.').pop();
        const validExtensions = ['xls', 'xlsx', 'xlsm'];
        return validExtensions.some((item: string) => item === extension);
    }

    private loadJSON() {
        this.api.getFullCustomizableSpec()
            .pipe(takeUntil(this.destroy$))
            .subscribe((respuesta: any) => {
                this.customizableDataArchitect = respuesta;
                this.prepareData();
            });
    }

    private sortFields(arrayToSort: string, position?, field = true) {
        if (arrayToSort === 'customizedData' && position !== undefined) {
            this[arrayToSort].customizableEntityViews[position].fields.sort(function(a, b) {
                return a.position - b.position;
            });
            this[arrayToSort].customizableEntityViews[position].relations.sort(function(a, b) {
                return a.position - b.position;
            });
        } else if (arrayToSort !== 'customizableDataArchitect' && field) {
            this[arrayToSort].customizableEntityViews.sort(function(a, b) {
                return a.position - b.position;
            });
            this[arrayToSort].customizableEntityViews.forEach(entity => {
                entity.fields.sort(function(a, b) {
                    return a.position - b.position;
                });
            });
        } else if (arrayToSort === 'customizableDataArchitect') {
            this[arrayToSort].customizableEntityViews.sort(function(a, b) {
                return a.position - b.position;
            });
            this[arrayToSort].customizableEntityViews.forEach(entity => {
                entity.fields.sort(function(a, b) {
                    return a.position - b.position;
                });
                entity.relations.sort(function(a, b) {
                    return a.position - b.position;
                });
            });
        } else {
            this[arrayToSort].customizableEntityViews.forEach(entity => {
                entity.relations.sort(function(a, b) {
                    return a.position - b.position;
                });
            });
        }
    }

    private prepareData() {
        this.sortFields('customizableDataArchitect');
        this.generateVoidCustomizedData();
    }

    private loadBreadcrumb() {
        this.parts = [
            new UxLink({
                label: this.translateService.instant(
                    'breadcrumb.digitallySealedCredentials'
                ),
                url: '/home',
            }),
            new UxLink({
                label: this.translateService.instant(
                    'Credential Builder'
                ),
                url: '/credential-builder',
            }),
            new UxLink({
                label: this.translateService.instant(
                    'Fields Selection'
                ),
                url: '/credential-builder/issue/fields/' + this.issuingOid,
            })
        ];
    }

}
