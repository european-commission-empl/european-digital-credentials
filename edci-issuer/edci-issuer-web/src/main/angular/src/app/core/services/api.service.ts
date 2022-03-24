import { Injectable, Optional, Inject } from '@angular/core';
import {
    HttpHeaders,
    HttpClient,
    HttpResponse,
    HttpEvent,
    HttpParams
} from '@angular/common/http';
import { BASE_PATH, Configuration } from '@shared/swagger';
import { Observable } from 'rxjs';
import { CustomHttpUrlEncodingCodec } from '@shared/swagger/encoder';
import { environment } from '@environments/environment';

/**
 *  OVERRIDE API FILE METHODS
 */

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    protected basePath = `${environment.apiBaseUrl}/api`;
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(
        protected httpClient: HttpClient,
        @Optional() @Inject(BASE_PATH) basePath: string,
        @Optional() configuration: Configuration
    ) {
        if (basePath) {
            this.basePath = basePath;
        }
        if (configuration) {
            this.configuration = configuration;
            this.basePath = basePath || configuration.basePath || this.basePath;
        }
    }

    public doPost(url: string, body: any): Observable<any> {
        return this.httpClient.post(url, body);
    }

    /**
     * @param consumes string[] mime-types
     * @return true: consumes contains 'multipart/form-data', false: otherwise
     */
    private canConsumeForm(consumes: string[]): boolean {
        const form = 'multipart/form-data';
        for (const consume of consumes) {
            if (form === consume) {
                return true;
            }
        }
        return false;
    }
}
