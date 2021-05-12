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
import { Subject } from 'rxjs';

@Injectable({
    providedIn: 'root',
})
export class ShareDataService {
    private _achievements: AchievementTabView[];
    private _activities: ActivityTabView[];
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

    toolbarLanguageChange: Subject<string> = new Subject<string>();

    private _userId: string;
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

    get uploadedXML(): string {
        return this._uploadedXML;
    }
    set uploadedXML(value: string) {
        this._uploadedXML = value;
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
}
