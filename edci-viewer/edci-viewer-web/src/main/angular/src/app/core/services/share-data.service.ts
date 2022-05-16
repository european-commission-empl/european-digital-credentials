import { AssessmentTabView } from './../../shared/swagger/model/assessmentTabView';
import { AssessmentSpecTabView } from './../../shared/swagger/model/assessmentSpecTabView';
import { Injectable } from '@angular/core';
import {
    AchievementTabView,
    ActivityTabView,
    CredentialSubjectTabView,
    EntitlementTabView,
    EuropassCredentialPresentationLiteView,
    OrganizationTabView,
    VerificationCheckView,
} from 'src/app/shared/swagger';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ShareDataService {
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
    private _diplomaXML: string;
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
    private _toolbarLanguage: string;
    private _shareLink: string;

    toolbarLanguageChange: Subject<string> = new Subject<string>();
    diplomaImage$: Subject<string[]> = new Subject<string[]>();

    get shareLink(): string {
        return this._shareLink;
    }
    set shareLink(value: string) {
        this._shareLink = value;
    }

    constructor() {}

    changeToolbarLanguage(value): void {
        this.toolbarLanguageChange.next(value);
    }
    get diplomaXML(): string {
        return this._diplomaXML;
    }
    set diplomaXML(value: string) {
        this._diplomaXML = value;
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

    get verificationSteps(): VerificationCheckView[] {
        return this._verificationSteps;
    }
    set verificationSteps(value: VerificationCheckView[]) {
        this._verificationSteps = value;
    }

    get toolbarLanguage(): string {
        return this._toolbarLanguage;
    }
    set toolbarLanguage(value: string) {
        this._toolbarLanguage = value;
    }

    get hierarchyTree(): string[] {
        return this._hierarchyTree;
    }
    set hierarchyTree(value: string[]) {
        this._hierarchyTree = value;
    }

    setVerificationSteps(verificationSteps: VerificationCheckView[]): void {
        this.verificationSteps$.next(verificationSteps);
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
        | CredentialSubjectTabView
    > {
        return this.emitSelection$.asObservable();
    }

    emitHierarchyTree(hierarchy: string[]) {
        this.hierarchyTree = hierarchy;
        this.emitHierarchy$.next(hierarchy);
    }

    changeHierarchyTree(): Observable<string[]> {
        return this.emitHierarchy$.asObservable();
    }

    emitDiplomaImage(diplomaImage: string[]) {
        this.diplomaImage$.next(diplomaImage);
    }

    changeDiplomaImage(): Observable<string[]> {
        return this.diplomaImage$.asObservable();
    }
}
