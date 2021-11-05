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
    private _achievements: AchievementTabView[];
    private _activities: ActivityTabView[];
    private _assessments: AssessmentTabView[];
    private _credentialSubject: CredentialSubjectTabView;
    private _diplomaXML: string;
    private _entitlements: EntitlementTabView[];
    private _issuerCredential: OrganizationTabView;
    private _issuerPresentation: OrganizationTabView;
    private _modalsOpen: number = 0;
    private _subCredentials: EuropassCredentialPresentationLiteView[];
    private _uploadedXML: string;
    private _verificationSteps: VerificationCheckView[];
    private _toolbarLanguage: string;
    private _userId: string;
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

    get userId(): string {
        return this._userId;
    }
    set userId(value: string) {
        this._userId = value;
    }

    private _credentialId: string;
    get credentialId(): string {
        return this._credentialId;
    }
    set credentialId(value: string) {
        this._credentialId = value;
    }

    private _shareLink: string;
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

    get achievements(): AchievementTabView[] {
        return this._achievements;
    }
    set achievements(value: AchievementTabView[]) {
        this._achievements = value;
    }

    get activities(): ActivityTabView[] {
        return this._activities;
    }
    set activities(value: ActivityTabView[]) {
        this._activities = value;
    }

    get assessments(): ActivityTabView[] {
        return this._assessments;
    }
    set assessments(value: ActivityTabView[]) {
        this._assessments = value;
    }

    get credentialSubject(): CredentialSubjectTabView {
        return this._credentialSubject;
    }
    set credentialSubject(value: CredentialSubjectTabView) {
        this._credentialSubject = value;
    }

    get diplomaXML(): string {
        return this._diplomaXML;
    }
    set diplomaXML(value: string) {
        this._diplomaXML = value;
    }

    get entitlements(): EntitlementTabView[] {
        return this._entitlements;
    }
    set entitlements(value: EntitlementTabView[]) {
        this._entitlements = value;
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
        sessionStorage.setItem('activeEntity', JSON.stringify(value));
        this._activeEntity = value;
    }

    get modalsOpen(): number {
        return this._modalsOpen;
    }
    set modalsOpen(value: number) {
        this._modalsOpen = value;
    }

    get subCredentials(): EuropassCredentialPresentationLiteView[] {
        return this._subCredentials;
    }
    set subCredentials(value: EuropassCredentialPresentationLiteView[]) {
        this._subCredentials = value;
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

    get uploadedXML(): string {
        return this._uploadedXML;
    }
    set uploadedXML(value: string) {
        this._uploadedXML = value;
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
}
