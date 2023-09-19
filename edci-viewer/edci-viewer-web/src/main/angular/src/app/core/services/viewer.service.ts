import { take, catchError, takeUntil } from 'rxjs/operators';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EclLanguage } from '@eui/ecl-core/lib/model/ecl-language.model';
import { Observable, Subject, tap, of } from 'rxjs';
import { EuropassCredentialPresentationView, EuropassDiplomaView, UserDetailsView, V1Service } from 'src/app/shared/swagger';

@Injectable({ providedIn: 'root' })
export class ViewerService {
    userDetails = new Subject();
    diploma: EuropassDiplomaView;
    credential: EuropassCredentialPresentationView;
    destroy$: Subject<boolean> = new Subject<boolean>();

    set userInfo(userDetails: UserDetailsView) {
        this.userDetails.next(userDetails);
        localStorage.setItem('userInfo', JSON.stringify(userDetails));
    }

    constructor(protected httpClient: HttpClient, protected api: V1Service, protected router: Router) { }
    // Adding missing languages on eUI list
    addMissingLanguages(languages: EclLanguage[]): EclLanguage[] {
        const missingLanguages = [
            {
                code: 'sr',
                label: 'srpski',
            },
        ];
        missingLanguages.forEach((missingLanguage) => {
            languages.push(missingLanguage);
        });
        return languages;
    }

    /**
     * Custom non-swagger calls
     */
    public doPost(url: string, body: any): Observable<any> {
        return this.httpClient.post(url, body);
    }

}
