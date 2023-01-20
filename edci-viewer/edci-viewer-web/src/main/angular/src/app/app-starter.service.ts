import { Inject, Injectable } from '@angular/core';
import {
    CONFIG_TOKEN,
    UserService,
    EuiAppConfig,
    UserDetails,
    UserPreferences,
    I18nService,
} from '@eui/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, zip } from 'rxjs';
import { switchMap } from 'rxjs/operators';

@Injectable({
    providedIn: 'root',
})
export class AppStarterService {
    defaultUserPreferences: UserPreferences;

    constructor(
        protected userService: UserService,
        protected i18nService: I18nService,
        @Inject(CONFIG_TOKEN) private config: EuiAppConfig,
        protected http: HttpClient,
    ) {
    }

    start(): Observable<any> {

        return zip(
            this.initUserService().pipe(
                switchMap((userStatus) => {
                    console.log(userStatus);
                    return this.i18nService.init();
                }),
            ),
        );
    }

    /**
     * Fetches user details,
     * create user: UserState object
     * then initialise to the UserService on run time
     */
    initUserService(): Observable<any> {
        return zip(
            this.fetchUserDetails(),
        ).pipe(
            switchMap(([userDetails]) => {
                return this.userService.init(userDetails);
            }));
    }

    /**
     * Fetches user details
     */
    private fetchUserDetails(): Observable<UserDetails> {
        // const url = this.config.modules.your_custom_module.your_custom_endpoint
        const moduleCoreApi = this.config.modules.core;
        const url = `${moduleCoreApi.base}${moduleCoreApi.userDetails}`;
        const user = { userId: 'anonymous' };

        if (!url) {
            return of(user);
        }
        return this.http.get<UserDetails>(url);
    }
}
