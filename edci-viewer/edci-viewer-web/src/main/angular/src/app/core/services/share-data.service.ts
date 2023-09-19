import { Router } from '@angular/router';
import { EuropassCredentialPresentationLiteView } from './../../shared/swagger/model/europassCredentialPresentationLiteView';
import { take } from 'rxjs/operators';
import { AssessmentTabView } from './../../shared/swagger/model/assessmentTabView';
import { AssessmentSpecTabView } from './../../shared/swagger/model/assessmentSpecTabView';
import { Injectable } from '@angular/core';
import {
    AchievementTabView,
    ActivityTabView,
    CredentialSubjectTabView,
    EntitlementTabView,
    EuropassCredentialPresentationView,
    EuropassDiplomaView,
    OrganizationTabView,
    V1Service,
    VerificationCheckView
} from 'src/app/shared/swagger';
import { BehaviorSubject, Observable, Subject, tap, forkJoin, of, concatMap } from 'rxjs';
import { SpinnerDialogComponent } from '@shared/components/spinner-dialog/spinner-dialog.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Overlay } from '@angular/cdk/overlay';
import { ViewerService } from './viewer.service';
import { environment } from '@environments/environment';

@Injectable({
    providedIn: 'root',
})
export class ShareDataService {
    private spinnerDialog: MatDialogRef<SpinnerDialogComponent>;
    private verificationStepsObsFactory: (lang: string) => Observable<VerificationCheckView[]>;
    private presentationViewObsFactory: (lang: string) => Observable<EuropassCredentialPresentationView>;
    private diplomaViewObsFactory: (lang: string) => Observable<EuropassDiplomaView>;
    private verificationSteps$ = new BehaviorSubject<VerificationCheckView[]>(
        null
    );
    private emitSelection$ = new BehaviorSubject<
        | OrganizationTabView
        | AchievementTabView
        | ActivityTabView
        | EntitlementTabView
        | AssessmentTabView
        | CredentialSubjectTabView
    >(null);
    private emitHierarchy$ = new BehaviorSubject<string[]>([]);
    private _diplomaJSON: string;
    private _issuerCredential: OrganizationTabView;
    private _issuerPresentation: OrganizationTabView;
    private _verificationSteps: VerificationCheckView[];
    private _ribbonState: number;
    private _activeEntity:
        | OrganizationTabView
        | AchievementTabView
        | ActivityTabView
        | EntitlementTabView
        | AssessmentTabView
        | CredentialSubjectTabView;
    private _hierarchyTree: string[] = [];
    toolbarLanguageChange: Subject<string> = new Subject<string>();
    diplomaImage$: Subject<string[]> = new Subject<string[]>();
    private toolbarLanguage$ = new BehaviorSubject<string>('');
    toolbarLanguageObservable = this.toolbarLanguage$.asObservable();

    constructor(public dialog: MatDialog, public overlay: Overlay, private viewerService: ViewerService, protected api: V1Service,
        protected router: Router) { }

    get shareLink(): string {
        return sessionStorage.getItem('shareLink');
    }
    set shareLink(value: string) {
        sessionStorage.setItem('shareLink', value);
    }

    get walletAddress(): string {
        return sessionStorage.getItem('walletAddress');
    }

    set walletAddress(walletAddress: string) {
        sessionStorage.setItem('walletAddress', walletAddress);
    }

    get credUUID(): string {
        return sessionStorage.getItem('credUUID');
    }

    set credUUID(credUUID: string) {
        sessionStorage.setItem('credUUID', credUUID);
    }

    get europassPresentationView(): EuropassCredentialPresentationView {
        return JSON.parse(sessionStorage.getItem('credentialDetails'));
    }

    set europassPresentationView(europassPresentationView: EuropassCredentialPresentationView) {
        sessionStorage.setItem('credentialDetails', JSON.stringify(europassPresentationView));
    }

    get europassDiplomaView(): EuropassDiplomaView {
        return JSON.parse(sessionStorage.getItem('credentialDiploma'));
    }

    set europassDiplomaView(europassDiplomaView: EuropassDiplomaView) {
        sessionStorage.setItem('credentialDiploma', JSON.stringify(europassDiplomaView));
    }

    get toolbarLanguage(): string {
        return sessionStorage.getItem('toolbarLanguage');
    }

    set toolbarLanguage(value: string) {
        sessionStorage.setItem('toolbarLanguage', value);
    }

    get diplomaJSON(): string {
        return sessionStorage.getItem('diplomaJSON');
    }

    set diplomaJSON(value: string) {
        sessionStorage.setItem('diplomaJSON', value);
    }

    get isPreview(): boolean {
        return JSON.parse(sessionStorage.getItem('isPreview'));
    }
    set isPreview(value: boolean) {
        sessionStorage.setItem('isPreview', JSON.stringify(value));
    }

    get verificationChecks(): VerificationCheckView[] {
        return JSON.parse(sessionStorage.getItem('verificationChecks'));
    }

    set verificationChecks(verificationChecks: VerificationCheckView[]) {
        sessionStorage.setItem('verificationChecks', JSON.stringify(verificationChecks));
    }

    get verificationSteps(): VerificationCheckView[] {
        return this._verificationSteps;
    }
    set verificationSteps(value: VerificationCheckView[]) {
        this._verificationSteps = value;
    }

    doStorageFullClear() {
        sessionStorage.clear();
    }

    doRefreshEuropassPresentationView():
        Observable<[EuropassCredentialPresentationView, EuropassDiplomaView]> {
        const presViObs = this.getPresentationViewObs();
        const dipViObs = this.getDiplomaViewObs();

        if (!dipViObs || !presViObs) {
            window.location.href = environment.viewerBaseUrl;
            return forkJoin([
                of(null),
                of(null)
            ]);
        }

        return forkJoin([
            presViObs,
            dipViObs
        ]);
    }

    getUploadDetails(locale: string):
        Observable<[EuropassCredentialPresentationView, EuropassDiplomaView]> {
        let jsonFile = new Blob([this.diplomaJSON], {
            type: 'application/ld+json',
        });
        return forkJoin([
            this.api.getCredentialDetail(jsonFile, locale)
                .pipe(take(1),
                    tap((data) => this.europassPresentationView = data)),
            this.api.getCredentialDiploma(jsonFile, locale)
                .pipe(take(1),
                    tap((data) => this.europassDiplomaView = data))
        ]);
    }

    changeToolbarLanguage(value): void {
        this.toolbarLanguageChange.next(value);
    }

    get issuerCredential(): OrganizationTabView {
        return this._issuerCredential;
    }
    set issuerCredential(value: OrganizationTabView) {
        this._issuerCredential = value;
    }

    get issuerPresentation(): OrganizationTabView {
        return this._issuerPresentation;
    }
    set issuerPresentation(value: OrganizationTabView) {
        this._issuerPresentation = value;
    }

    get activeEntity() {
        return this._activeEntity;
    }
    set activeEntity(value) {
        this._activeEntity = value;
    }

    get ribbonState(): number {
        return this._ribbonState;
    }
    set ribbonState(value: number) {
        this._ribbonState = value;
    }

    get hierarchyTree(): string[] {
        return this._hierarchyTree;
    }
    set hierarchyTree(value: string[]) {
        this._hierarchyTree = value;
    }

    setPresentationViewObs(presentationViewObs: (lang: string) => Observable<EuropassCredentialPresentationView>) {
        this.presentationViewObsFactory = presentationViewObs;
        sessionStorage.setItem('presentationViewObsFactory', presentationViewObs.toString());

    }

    getPresentationViewObs(): Observable<EuropassCredentialPresentationView> {
        if (!this.presentationViewObsFactory) {
            let factoryString = sessionStorage.getItem('presentationViewObsFactory');
            if (factoryString.indexOf('jsonFile') !== -1) {
                return null;
            }
            factoryString = factoryString.replace('shareHash', '"' + this.shareLink + '"');
            factoryString = factoryString.replace('walletAddress', '"' + this.walletAddress + '"');
            factoryString = factoryString.replace('credId', '"' + this.credUUID + '"');
            this.presentationViewObsFactory = eval(factoryString);
        }
        return this.presentationViewObsFactory(this.toolbarLanguage ? this.toolbarLanguage : '');
    }

    setDiplomaViewObs(diplomaViewObs: (lang: string) => Observable<EuropassDiplomaView>) {
        this.diplomaViewObsFactory = diplomaViewObs;
        sessionStorage.setItem('diplomaViewObsFactory', diplomaViewObs.toString());
    }

    getDiplomaViewObs(): Observable<EuropassDiplomaView> {
        if (!this.diplomaViewObsFactory) {
            let factoryString = sessionStorage.getItem('diplomaViewObsFactory');
            if (factoryString.indexOf('jsonFile') !== -1) {
                return null;
            }
            factoryString = factoryString.replace('shareHash', '"' + this.shareLink + '"');
            factoryString = factoryString.replace('walletAddress', '"' + this.walletAddress + '"');
            factoryString = factoryString.replace('credId', '"' + this.credUUID + '"');
            this.diplomaViewObsFactory = eval(factoryString);
        }
        return this.diplomaViewObsFactory(this.toolbarLanguage ? this.toolbarLanguage : '');
    }

    setVerificationStepsObs(verificationStepsObs: (lang: string) => Observable<VerificationCheckView[]>) {
        this.verificationStepsObsFactory = verificationStepsObs;
    }

    getVerificationStepsObs(): Observable<VerificationCheckView[]> {
        if (this.isPreview) {
            return of(null);
        }

        if (this.verificationChecks) {
            return of(this.verificationChecks);
        }

        return this.verificationStepsObsFactory(this.toolbarLanguage ? this.toolbarLanguage : '');
    }

    setVerificationSteps(verificationSteps: VerificationCheckView[]): void {
        this.verificationChecks = verificationSteps;
    }

    getVerificationSteps(): Observable<VerificationCheckView[]> {
        return this.verificationSteps$.asObservable();
    }

    emitEntitySelection(
        entity:
            | OrganizationTabView
            | AchievementTabView
            | ActivityTabView
            | EntitlementTabView
            | AssessmentTabView
            | CredentialSubjectTabView
    ): void {
        this.activeEntity = entity;
        this.emitSelection$.next(entity);
    }

    changeEntitySelection(): Observable<
        | OrganizationTabView
        | AchievementTabView
        | ActivityTabView
        | EntitlementTabView
        | AssessmentTabView
        | CredentialSubjectTabView> {
        return this.emitSelection$.asObservable();
    }

    emitHierarchyTree(hierarchy: string[]) {
        this.hierarchyTree = hierarchy;
        this.emitHierarchy$.next(hierarchy);
    }

    changeHierarchyTree(): Observable<string[]> {
        return this.emitHierarchy$.asObservable();
    }

    public openSpinnerDialog() {
        this.spinnerDialog = this.dialog.open(SpinnerDialogComponent, {
            backdropClass: 'blueBackdrop',
            maxWidth: '100vw',
            minWidth: '100vw',
            maxHeight: '100vh',
            minHeight: '100vh',
        });
    }

    public closeSpinnerDialog() {
        if (this.spinnerDialog !== null && this.spinnerDialog !== undefined) {
            this.spinnerDialog.close();
        }
    }
}
