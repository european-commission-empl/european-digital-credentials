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

import { ByteArrayResource } from '../model/byteArrayResource';
import { ShareLinkResponseView } from '../model/shareLinkResponseView';
import { ShareLinkView } from '../model/shareLinkView';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable()
export class ShareLinksService {

    protected basePath = 'http://localhost:7001/wallet/api';
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
     * Create Share Link of a Credential
     * 
     * @param walletAddress 
     * @param uuid 
     * @param body 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public createShareLink(walletAddress: string, uuid: string, body?: ShareLinkView, observe?: 'body', reportProgress?: boolean): Observable<ShareLinkResponseView>;
    public createShareLink(walletAddress: string, uuid: string, body?: ShareLinkView, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ShareLinkResponseView>>;
    public createShareLink(walletAddress: string, uuid: string, body?: ShareLinkView, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ShareLinkResponseView>>;
    public createShareLink(walletAddress: string, uuid: string, body?: ShareLinkView, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (walletAddress === null || walletAddress === undefined) {
            throw new Error('Required parameter walletAddress was null or undefined when calling createShareLink.');
        }

        if (uuid === null || uuid === undefined) {
            throw new Error('Required parameter uuid was null or undefined when calling createShareLink.');
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

        return this.httpClient.post<ShareLinkResponseView>(`${this.basePath}/sharelinks/${encodeURIComponent(String(walletAddress))}/credentials/${encodeURIComponent(String(uuid))}/sharelink`,
            body,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Download Shared Link Credential
     * 
     * @param shareHash 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public downloadShareLinkCredential(shareHash: string, observe?: 'body', reportProgress?: boolean): Observable<Array<string>>;
    public downloadShareLinkCredential(shareHash: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<string>>>;
    public downloadShareLinkCredential(shareHash: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<string>>>;
    public downloadShareLinkCredential(shareHash: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (shareHash === null || shareHash === undefined) {
            throw new Error('Required parameter shareHash was null or undefined when calling downloadShareLinkCredential.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/octet-stream'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.get<Array<string>>(`${this.basePath}/sharelinks/download/${encodeURIComponent(String(shareHash))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Get Shared Presentation PDF
     * 
     * @param shareHash 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public downloadShareLinkPresentationPDF(shareHash: string, observe?: 'body', reportProgress?: boolean): Observable<ByteArrayResource>;
    public downloadShareLinkPresentationPDF(shareHash: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ByteArrayResource>>;
    public downloadShareLinkPresentationPDF(shareHash: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ByteArrayResource>>;
    public downloadShareLinkPresentationPDF(shareHash: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (shareHash === null || shareHash === undefined) {
            throw new Error('Required parameter shareHash was null or undefined when calling downloadShareLinkPresentationPDF.');
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/pdf'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected != undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.get<ByteArrayResource>(`${this.basePath}/sharelinks/presentation/${encodeURIComponent(String(shareHash))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Get Share Link
     * 
     * @param shareHash 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getShareLink(shareHash: string, observe?: 'body', reportProgress?: boolean): Observable<ShareLinkResponseView>;
    public getShareLink(shareHash: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<ShareLinkResponseView>>;
    public getShareLink(shareHash: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<ShareLinkResponseView>>;
    public getShareLink(shareHash: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        if (shareHash === null || shareHash === undefined) {
            throw new Error('Required parameter shareHash was null or undefined when calling getShareLink.');
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

        return this.httpClient.get<ShareLinkResponseView>(`${this.basePath}/sharelinks/fetch/${encodeURIComponent(String(shareHash))}`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
