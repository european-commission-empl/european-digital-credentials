/**
 * API
 * API Swagger description
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent }                           from '@angular/common/http';
import { CustomHttpUrlEncodingCodec }                        from '../encoder';

import { Observable }                                        from 'rxjs';

import { PublicSealAndSendView } from '../model/publicSealAndSendView';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class V2Service {

    protected basePath = 'http://localhost:8080/europass2/edci-issuer/api';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {
        if (basePath) {
            this.basePath = basePath;
        }
        if (configuration) {
            this.configuration = configuration;
            this.basePath = basePath || configuration.basePath || this.basePath;
        }
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


    /**
     * Seal a set of credentials and send them to the configured wallet
     * 
     * @param files the JSON files containing the credential
     * @param password the password for the local certificate
     * @param signOnBehalf indicates that the credential should be signed on behalf of another organization
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public batchSealAndSendCredentials(files: Array<Blob>, password: string, signOnBehalf?: boolean, observe?: 'body', reportProgress?: boolean): Observable<any>;
    public batchSealAndSendCredentials(files: Array<Blob>, password: string, signOnBehalf?: boolean, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<any>>;
    public batchSealAndSendCredentials(files: Array<Blob>, password: string, signOnBehalf?: boolean, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<any>>;
    public batchSealAndSendCredentials(files: Array<Blob>, password: string, signOnBehalf?: boolean, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (files === null || files === undefined) {
            throw new Error('Required parameter files was null or undefined when calling batchSealAndSendCredentials.');
        }

        if (password === null || password === undefined) {
            throw new Error('Required parameter password was null or undefined when calling batchSealAndSendCredentials.');
        }


        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (password !== undefined && password !== null) {
            queryParameters = queryParameters.set('password', <any>password);
        }
        if (signOnBehalf !== undefined && signOnBehalf !== null) {
            queryParameters = queryParameters.set('signOnBehalf', <any>signOnBehalf);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'multipart/form-data'
        ];

        const canConsumeForm = this.canConsumeForm(consumes);

        let formParams: { append(param: string, value: any): any; };
        let useForm = false;
        let convertFormParamsToString = false;
        if (useForm) {
            formParams = new FormData();
        } else {
            formParams = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        }

        if (files) {
            files.forEach((element) => {
                formParams = formParams.append('_files', <any>element) || formParams;
            })
        }

        return this.httpClient.post<any>(`${this.basePath}/v2/public/credentials/seal_batch`,
            convertFormParamsToString ? formParams.toString() : formParams,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Create a credential in JSON format and download resulting file.
     * 
     * @param file The JSON file containig the credential
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public createAndDownloadCredential(file: Blob, observe?: 'body', reportProgress?: boolean): Observable<Blob>;
    public createAndDownloadCredential(file: Blob, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Blob>>;
    public createAndDownloadCredential(file: Blob, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Blob>>;
    public createAndDownloadCredential(file: Blob, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (file === null || file === undefined) {
            throw new Error('Required parameter file was null or undefined when calling createAndDownloadCredential.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/xml'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'multipart/form-data'
        ];

        const canConsumeForm = this.canConsumeForm(consumes);

        let formParams: { append(param: string, value: any): any; };
        let useForm = false;
        let convertFormParamsToString = false;
        // use FormData to transmit files using content-type "multipart/form-data"
        // see https://stackoverflow.com/questions/4007969/application-x-www-form-urlencoded-or-multipart-form-data
        useForm = canConsumeForm;
        if (useForm) {
            formParams = new FormData();
        } else {
            formParams = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        }

        if (file !== undefined) {
            formParams = formParams.append('_file', <any>file) || formParams;
        }

        return this.httpClient.post(`${this.basePath}/v2/public/credentials/create`,
            convertFormParamsToString ? formParams.toString() : formParams,
            {
                responseType: "blob",
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Seal a credential in JSON format and download resulting file. Requires a configured local certificate.
     * 
     * @param file The JSON file containig the credential
     * @param password The password for the local certificate 
     * @param signOnBehalf indicates that the credential should be signed on behalf of another organization
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public sealAndDownloadCredential(file: Blob, password: string, signOnBehalf?: boolean, observe?: 'body', reportProgress?: boolean): Observable<Blob>;
    public sealAndDownloadCredential(file: Blob, password: string, signOnBehalf?: boolean, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Blob>>;
    public sealAndDownloadCredential(file: Blob, password: string, signOnBehalf?: boolean, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Blob>>;
    public sealAndDownloadCredential(file: Blob, password: string, signOnBehalf?: boolean, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (file === null || file === undefined) {
            throw new Error('Required parameter file was null or undefined when calling sealAndDownloadCredential.');
        }

        if (password === null || password === undefined) {
            throw new Error('Required parameter password was null or undefined when calling sealAndDownloadCredential.');
        }


        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (password !== undefined && password !== null) {
            queryParameters = queryParameters.set('password', <any>password);
        }
        if (signOnBehalf !== undefined && signOnBehalf !== null) {
            queryParameters = queryParameters.set('signOnBehalf', <any>signOnBehalf);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/xml'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'multipart/form-data'
        ];

        const canConsumeForm = this.canConsumeForm(consumes);

        let formParams: { append(param: string, value: any): any; };
        let useForm = false;
        let convertFormParamsToString = false;
        // use FormData to transmit files using content-type "multipart/form-data"
        // see https://stackoverflow.com/questions/4007969/application-x-www-form-urlencoded-or-multipart-form-data
        useForm = canConsumeForm;
        if (useForm) {
            formParams = new FormData();
        } else {
            formParams = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        }

        if (file !== undefined) {
            formParams = formParams.append('_file', <any>file) || formParams;
        }

        return this.httpClient.post(`${this.basePath}/v2/public/credentials/seal`,
            convertFormParamsToString ? formParams.toString() : formParams,
            {
                params: queryParameters,
                responseType: "blob",
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Seal a credential in JSON format, using the locally stored cert, and sent it to the wallet. Requires a configured local certificate.
     * 
     * @param file The JSON file containing the credential
     * @param password The passworf for the local certificate
     * @param signOnBehalf indicates that the credential should be signed on behalf of another organization
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public sealAndSendCredential(file: Blob, password: string, signOnBehalf?: boolean, observe?: 'body', reportProgress?: boolean): Observable<PublicSealAndSendView>;
    public sealAndSendCredential(file: Blob, password: string, signOnBehalf?: boolean, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<PublicSealAndSendView>>;
    public sealAndSendCredential(file: Blob, password: string, signOnBehalf?: boolean, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<PublicSealAndSendView>>;
    public sealAndSendCredential(file: Blob, password: string, signOnBehalf?: boolean, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (file === null || file === undefined) {
            throw new Error('Required parameter file was null or undefined when calling sealAndSendCredential.');
        }

        if (password === null || password === undefined) {
            throw new Error('Required parameter password was null or undefined when calling sealAndSendCredential.');
        }


        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (password !== undefined && password !== null) {
            queryParameters = queryParameters.set('password', <any>password);
        }
        if (signOnBehalf !== undefined && signOnBehalf !== null) {
            queryParameters = queryParameters.set('signOnBehalf', <any>signOnBehalf);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
            'multipart/form-data'
        ];

        const canConsumeForm = this.canConsumeForm(consumes);

        let formParams: { append(param: string, value: any): any; };
        let useForm = false;
        let convertFormParamsToString = false;
        // use FormData to transmit files using content-type "multipart/form-data"
        // see https://stackoverflow.com/questions/4007969/application-x-www-form-urlencoded-or-multipart-form-data
        useForm = canConsumeForm;
        if (useForm) {
            formParams = new FormData();
        } else {
            formParams = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        }

        if (file !== undefined) {
            formParams = formParams.append('_file', <any>file) || formParams;
        }

        return this.httpClient.post<PublicSealAndSendView>(`${this.basePath}/v2/public/credentials/seal_and_send`,
            convertFormParamsToString ? formParams.toString() : formParams,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
