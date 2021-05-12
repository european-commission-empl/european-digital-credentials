import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { DisplayErrorService } from 'src/app/core/services/response-error-growl.service';
import { EuropassCredentialPresentationLiteView } from 'src/app/shared/swagger';

@Component({
    selector: 'edci-viewer-sub-credentials',
    templateUrl: './sub-credentials.component.html',
    styleUrls: ['./sub-credentials.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class SubCredentialsComponent implements OnInit {
    subCredentials: EuropassCredentialPresentationLiteView[] = JSON.parse(
        sessionStorage.getItem('subCredentials')
    );
    origin: string = window.location.origin || '';
    pathName: string = window.location.pathname || '';

    constructor() {}

    ngOnInit() {}

    previewSubCredential(
        subCredential: EuropassCredentialPresentationLiteView
    ): void {
        // if (subCredential.credentialMetadata.walletAddress && subCredential.uuid) {
        //     // open view/userId/uuid
        //     const userId: string = subCredential.walletAddress.split('/').pop(); // Extract userId from walletAddress
        //     window.open(
        //         `${this.origin}${window.location.pathname}/#/view/${userId}/${subCredential.uuid}`,
        //         `${subCredential.uuid}-${new Date().getTime()}`
        //     );
        // } else {
        //     // open preview and write document
        //     const formParams = new FormData();
        //     formParams.append('xml', subCredential.xml);
        //     this.http
        //         .post(`${environment.apiBaseUrl}/mvc/preview`, formParams, {
        //             responseType: 'text',
        //         })
        //         .subscribe(
        //             (html: string) => {
        //                 let win = window.open(
        //                     '',
        //                     `${subCredential.uuid}-${new Date().getTime()}`,
        //                     `toolbar=no,location=no,directories=no,status=no,menubar=no,
        //                     scrollbars=yes,resizable=yes,width=780,height=800`
        //                 );
        //                 win.document.write(html);
        //                 win.document.close();
        //             },
        //             (response: HttpErrorResponse) => {
        //                 this.displayErrorService.displayRequestErrorGrowl(
        //                     response
        //                 );
        //             }
        //         );
        // }
    }
}
