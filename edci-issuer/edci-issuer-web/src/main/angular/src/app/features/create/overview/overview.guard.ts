import { CanActivate, Router } from '@angular/router';
import { IssuerService } from '@services/issuer.service';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class OverviewGuard implements CanActivate {
    constructor(private issuerService: IssuerService, private router: Router) {}

    canActivate() {
        if (this.issuerService.getCredentials().length > 0) {
            return true;
        } else {
            this.router.navigate(['home']);
            return false;
        }
    }
}
