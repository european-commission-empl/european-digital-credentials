import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EclLanguage } from '@eui/ecl-core/lib/model/ecl-language.model';
import { Observable, Subject } from 'rxjs';
import { UserDetailsView } from 'src/app/shared/swagger';

@Injectable({ providedIn: 'root' })
export class ViewerService {
    userDetails = new Subject();

    set userInfo(userDetails: UserDetailsView) {
        this.userDetails.next(userDetails);
        localStorage.setItem('userInfo', JSON.stringify(userDetails));
    }

    constructor(protected httpClient: HttpClient) {}
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
