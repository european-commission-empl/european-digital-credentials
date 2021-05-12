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

    public previewCertificate(
        xml: string,
        observe?: 'body',
        reportProgress?: boolean
    ): Observable<string>;
    public previewCertificate(
        xml: string,
        observe?: 'response',
        reportProgress?: boolean
    ): Observable<HttpResponse<string>>;
    public previewCertificate(
        xml: string,
        observe?: 'events',
        reportProgress?: boolean
    ): Observable<HttpEvent<string>>;
    public previewCertificate(
        xml: string,
        observe: any = 'body',
        reportProgress: boolean = false
    ): Observable<any> {
        if (xml === null || xml === undefined) {
            throw new Error(
                'Required parameter xml was null or undefined when calling previewCertificate.'
            );
        }

        let headers = this.defaultHeaders;

        // to determine the Content-Type header
        const consumes: string[] = ['application/x-www-form-urlencoded'];

        const canConsumeForm = this.canConsumeForm(consumes);

        let formParams: { append(param: string, value: any): any };
        let useForm: boolean;
        let convertFormParamsToString = false;
        // use FormData to transmit files using content-type "multipart/form-data"
        // see https://stackoverflow.com/questions/4007969/application-x-www-form-urlencoded-or-multipart-form-data
        useForm = canConsumeForm;
        if (useForm) {
            formParams = new FormData();
        } else {
            formParams = new HttpParams({
                encoder: new CustomHttpUrlEncodingCodec()
            });
        }

        if (xml !== undefined) {
            formParams = formParams.append('xml', <any>xml) || formParams;
        }

        return this.httpClient.post(
            `${environment.viewerBaseUrl}/mvc/preview`,
            convertFormParamsToString ? formParams.toString() : formParams,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                responseType: 'text',
                reportProgress: reportProgress
            }
        );
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
