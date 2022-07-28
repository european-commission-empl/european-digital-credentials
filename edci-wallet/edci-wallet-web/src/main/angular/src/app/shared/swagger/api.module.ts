import {
    NgModule,
    ModuleWithProviders,
    SkipSelf,
    Optional,
} from "@angular/core";
import { Configuration } from "./configuration";
import { HttpClient } from "@angular/common/http";

import { CredentialsService } from "./api/credentials.eu.europa.ec.empl.edci.dss.service";
import { V1credentialsService } from "./api/v1credentials.eu.europa.ec.empl.edci.dss.service";
import { V1sharelinksService } from "./api/v1sharelinks.eu.europa.ec.empl.edci.dss.service";
import { V1walletsService } from "./api/v1wallets.eu.europa.ec.empl.edci.dss.service";

@NgModule({
    imports: [],
    declarations: [],
    exports: [],
    providers: [
        CredentialsService,
        V1credentialsService,
        V1sharelinksService,
        V1walletsService,
    ],
})
export class ApiModule {
    public static forRoot(
        configurationFactory: () => Configuration
    ): ModuleWithProviders {
        return {
            ngModule: ApiModule,
            providers: [
                { provide: Configuration, useFactory: configurationFactory },
            ],
        };
    }

    constructor(
        @Optional() @SkipSelf() parentModule: ApiModule,
        @Optional() http: HttpClient
    ) {
        if (parentModule) {
            throw new Error(
                "ApiModule is already loaded. Import in your base AppModule only."
            );
        }
        if (!http) {
            throw new Error(
                "You need to import the HttpClientModule in your AppModule! \n" +
                    "See also https://github.com/angular/angular/issues/20575"
            );
        }
    }
}
