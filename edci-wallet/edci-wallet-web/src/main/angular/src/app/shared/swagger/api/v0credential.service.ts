/**
 * Wallet API
 * API Wallet description
 *
 * OpenAPI spec version: 1.0.0
 * Contact: edci.support@eu.com
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

import { Observable }                                        from 'rxjs/Observable';

import { CredentialUploadResponseView } from '../model/credentialUploadResponseView';
import { CredentialVerificationRequestView } from '../model/credentialVerificationRequestView';
import { CredentialView } from '../model/credentialView';
<<<<<<< HEAD
import { EuropassCredentialDTO } from '../model/europassCredentialDTO';
=======
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
import { StatusView } from '../model/statusView';
import { VerificationCheckView } from '../model/verificationCheckView';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class V0credentialService {

    protected basePath = 'http://localhost:8080/europass2/edci-wallet/api';
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
     * Add a credential XML to a existing wallet
     * 
     * @param credentialXML The XML file containing the credential
     * @param walletAddress The wallet Address where the credential will be added
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public addCredential(credentialXML: Blob, walletAddress: string, observe?: 'body', reportProgress?: boolean): Observable<CredentialUploadResponseView>;
    public addCredential(credentialXML: Blob, walletAddress: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<CredentialUploadResponseView>>;
    public addCredential(credentialXML: Blob, walletAddress: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<CredentialUploadResponseView>>;
    public addCredential(credentialXML: Blob, walletAddress: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (credentialXML === null || credentialXML === undefined) {
            throw new Error('Required parameter credentialXML was null or undefined when calling addCredential.');
        }

        if (walletAddress === null || walletAddress === undefined) {
            throw new Error('Required parameter walletAddress was null or undefined when calling addCredential.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (walletAddress !== undefined && walletAddress !== null) {
            queryParameters = queryParameters.set('_walletAddress', <any>walletAddress);
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

        if (credentialXML !== undefined) {
            formParams = formParams.append('_credentialXML', <any>credentialXML) || formParams;
        }

        return this.httpClient.post<CredentialUploadResponseView>(`${this.basePath}/credential/add`,
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
     * Delete an existing credential
     * 
     * @param uuid The ID of the credential
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public deleteCredential(uuid: string, observe?: 'body', reportProgress?: boolean): Observable<StatusView>;
    public deleteCredential(uuid: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<StatusView>>;
    public deleteCredential(uuid: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<StatusView>>;
    public deleteCredential(uuid: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (uuid === null || uuid === undefined) {
            throw new Error('Required parameter uuid was null or undefined when calling deleteCredential.');
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
        ];

        return this.httpClient.get<StatusView>(`${this.basePath}/credential/delete/${encodeURIComponent(String(uuid))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
<<<<<<< HEAD
     * Downloads a credential JSON file
=======
     * Downloads a credential XML file
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
     * 
     * @param uuid The ID of the credential
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
<<<<<<< HEAD
    public downloadJsonCredential(uuid: string, observe?: 'body', reportProgress?: boolean): Observable<EuropassCredentialDTO>;
    public downloadJsonCredential(uuid: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<EuropassCredentialDTO>>;
    public downloadJsonCredential(uuid: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<EuropassCredentialDTO>>;
    public downloadJsonCredential(uuid: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (uuid === null || uuid === undefined) {
            throw new Error('Required parameter uuid was null or undefined when calling downloadJsonCredential.');
=======
    public downloadCredential(uuid: string, observe?: 'body', reportProgress?: boolean): Observable<Array<string>>;
    public downloadCredential(uuid: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<string>>>;
    public downloadCredential(uuid: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<string>>>;
    public downloadCredential(uuid: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (uuid === null || uuid === undefined) {
            throw new Error('Required parameter uuid was null or undefined when calling downloadCredential.');
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
<<<<<<< HEAD
            'application/json'
=======
            'application/octet-stream'
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

<<<<<<< HEAD
        return this.httpClient.get<EuropassCredentialDTO>(`${this.basePath}/credential/download/${encodeURIComponent(String(uuid))}`,
=======
        return this.httpClient.get<Array<string>>(`${this.basePath}/credential/download/${encodeURIComponent(String(uuid))}`,
>>>>>>> 98214b45d868ddd254c561669c1269836e81bd88
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Get verification from a credential XML
     * 
     * @param file The XML file of the credential
     * @param locale The locale for titles and descriptions
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getCredentialVerification(file: Blob, locale: string, observe?: 'body', reportProgress?: boolean): Observable<Array<VerificationCheckView>>;
    public getCredentialVerification(file: Blob, locale: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<VerificationCheckView>>>;
    public getCredentialVerification(file: Blob, locale: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<VerificationCheckView>>>;
    public getCredentialVerification(file: Blob, locale: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (file === null || file === undefined) {
            throw new Error('Required parameter file was null or undefined when calling getCredentialVerification.');
        }

        if (locale === null || locale === undefined) {
            throw new Error('Required parameter locale was null or undefined when calling getCredentialVerification.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (locale !== undefined && locale !== null) {
            queryParameters = queryParameters.set('locale', <any>locale);
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
            formParams = formParams.append('file', <any>file) || formParams;
        }

        return this.httpClient.post<Array<VerificationCheckView>>(`${this.basePath}/credential/verifyXML`,
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
     * List the existing credentials on a wallet with a default locale
     * 
     * @param walletAddress The Wallet Address where the credentials are stored
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public listCredentials(walletAddress: string, observe?: 'body', reportProgress?: boolean): Observable<Array<CredentialView>>;
    public listCredentials(walletAddress: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<CredentialView>>>;
    public listCredentials(walletAddress: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<CredentialView>>>;
    public listCredentials(walletAddress: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (walletAddress === null || walletAddress === undefined) {
            throw new Error('Required parameter walletAddress was null or undefined when calling listCredentials.');
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
        ];

        return this.httpClient.get<Array<CredentialView>>(`${this.basePath}/credential/list/${encodeURIComponent(String(walletAddress))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * List the existing credentials on a wallet based on a locale
     * 
     * @param walletAddress The Wallet Address where the credentials are stored
     * @param locale  The desired locale for the credential&#39;s texts
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public listCredentials_1(walletAddress: string, locale: string, observe?: 'body', reportProgress?: boolean): Observable<Array<CredentialView>>;
    public listCredentials_1(walletAddress: string, locale: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<CredentialView>>>;
    public listCredentials_1(walletAddress: string, locale: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<CredentialView>>>;
    public listCredentials_1(walletAddress: string, locale: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (walletAddress === null || walletAddress === undefined) {
            throw new Error('Required parameter walletAddress was null or undefined when calling listCredentials_1.');
        }

        if (locale === null || locale === undefined) {
            throw new Error('Required parameter locale was null or undefined when calling listCredentials_1.');
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
        ];

        return this.httpClient.get<Array<CredentialView>>(`${this.basePath}/credential/list/${encodeURIComponent(String(walletAddress))}/${encodeURIComponent(String(locale))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Get verification report from a credential ID
     * 
     * @param body The verification request
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public verifyCredential(body: CredentialVerificationRequestView, observe?: 'body', reportProgress?: boolean): Observable<Array<VerificationCheckView>>;
    public verifyCredential(body: CredentialVerificationRequestView, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<VerificationCheckView>>>;
    public verifyCredential(body: CredentialVerificationRequestView, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<VerificationCheckView>>>;
    public verifyCredential(body: CredentialVerificationRequestView, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (body === null || body === undefined) {
            throw new Error('Required parameter body was null or undefined when calling verifyCredential.');
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
            'application/json'
        ];
        const httpContentTypeSelected: string | undefined = this.configuration.selectHeaderContentType(consumes);
        if (httpContentTypeSelected != undefined) {
            headers = headers.set('Content-Type', httpContentTypeSelected);
        }

        return this.httpClient.post<Array<VerificationCheckView>>(`${this.basePath}/credential/verify`,
            body,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}