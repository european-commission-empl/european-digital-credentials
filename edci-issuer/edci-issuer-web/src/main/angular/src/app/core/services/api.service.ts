import { Injectable, Optional, Inject } from '@angular/core';
import {
    HttpHeaders,
    HttpClient,
    HttpResponse,
    HttpEvent,
    HttpParams,
} from '@angular/common/http';
import {
    BASE_PATH,
    Configuration,
    CredentialFileUploadResponseView,
} from '@shared/swagger';
import { Observable } from 'rxjs';
import { CustomHttpUrlEncodingCodec } from '@shared/swagger/encoder';
import { environment } from '@environments/environment';

/**
 *  OVERRIDE API FILE METHODS
 */

@Injectable({
    providedIn: 'root',
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
     * Upload some credentials in XML
     *
     * @param files the XML files containing the credential
     * @param locale locale
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public addCredentials(
        files: Array<Blob>,
        locale?: string,
        observe?: 'body',
        reportProgress?: boolean
    ): Observable<CredentialFileUploadResponseView>;
    public addCredentials(
        files: Array<Blob>,
        locale?: string,
        observe?: 'response',
        reportProgress?: boolean
    ): Observable<HttpResponse<CredentialFileUploadResponseView>>;
    public addCredentials(
        files: Array<Blob>,
        locale?: string,
        observe?: 'events',
        reportProgress?: boolean
    ): Observable<HttpEvent<CredentialFileUploadResponseView>>;
    public addCredentials(
        files: Array<Blob>,
        locale?: string,
        observe: any = 'body',
        reportProgress = false
    ): Observable<any> {
        if (files === null || files === undefined) {
            throw new Error(
                'Required parameter files was null or undefined when calling addCredentials.'
            );
        }

        let queryParameters = new HttpParams({
            encoder: new CustomHttpUrlEncodingCodec(),
        });
        if (locale !== undefined && locale !== null) {
            queryParameters = queryParameters.set('locale', <any>locale);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        const httpHeaderAccepts: string[] = ['application/json'];
        const httpHeaderAcceptSelected: string | undefined =
            this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const formParams = new FormData();

        if (files) {
            files.forEach((element) => {
                formParams.append('_files', <Blob>element);
            });
        }

        return this.httpClient.post<CredentialFileUploadResponseView>(
            `${this.basePath}/v1/credentials/upload`,
            formParams,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress,
            }
        );
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
