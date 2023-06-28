import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { CredentialBuilderService } from './credential-builder.service';
import { Router } from '@angular/router';

@Injectable({
    providedIn: 'root'
})
export class PageLoadingSpinnerService {
    private isLoading$ = new BehaviorSubject<boolean>(false);
    constructor(
        public credentialBuilderService: CredentialBuilderService,
        private router: Router,
    ) {}

    getPageLoaderStatus() {
        return this.isLoading$.asObservable();
    }
    startPageLoader() {
        this.isLoading$.next(true);
    }

    stopPageLoader() {
        this.isLoading$.next(false);
    }

    redirectToCredentialsTab(index: number) {
        this.credentialBuilderService.setOcbTabSelected(index);
        this.router.navigateByUrl('credential-builder');
        this.stopPageLoader();
    }
}
