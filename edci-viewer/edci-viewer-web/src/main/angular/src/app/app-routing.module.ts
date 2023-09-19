import { NgModule } from '@angular/core';
import { RouterModule, Routes, ExtraOptions } from '@angular/router';
import { CustomPreloadingStrategy } from './custom-preloading';
import { ViewerGuard } from './guards/viewer.guard';
import { DetailsShareLinkResolverV1 } from '@features/diploma-details/resolvers/v1/sharelink/details-sharelink.v1.resolver';
import { UUIDResolverV2 } from '@features/diploma-details/resolvers/v2/uuid.v2.resolver';
import { UUIDResolverV1 } from '@features/diploma-details/resolvers/v1/uuid/uuid.v1.resolver';
import { SharelinkResolver } from '@features/diploma-details/resolvers/v1/sharelink/sharelink.v1.resolver';

const routes: Routes = [
    {
        path: '', redirectTo: 'home', pathMatch: 'full' },
    {
        path: 'index.jsp', redirectTo: 'home' },
    {
        path: 'home',
        loadChildren: () => import('./features/home/home.module').then(m => m.HomeModule),
        data: { preload: true },
    },
    {
        path: 'diploma-details',
        loadChildren: () => import('./features/diploma-details/diploma-details.module').then(m => m.DiplomaDetailsModule),
    },
    {
        path: 'shareview/:shareLink',
        loadChildren: () => import('./features/diploma-details/diploma-details.module').then(m => m.DiplomaDetailsModule),
        resolve : {
            credentialDetails : SharelinkResolver
        }
    },
    {
        path: 'view/:id',
        loadChildren: () => import('./features/diploma-details/diploma-details.module').then(m => m.DiplomaDetailsModule),
        canActivate: [ViewerGuard],
        resolve : {
            credentialDetails : UUIDResolverV2
        }
    },
    {
        path: 'view/:userId/:id',
        loadChildren: () => import('./features/diploma-details/diploma-details.module').then(m => m.DiplomaDetailsModule),
        canActivate: [ViewerGuard],
        resolve : {
            credentialDetails : UUIDResolverV1
        }
    },
    {
        path: '**',
        redirectTo: 'home'
    }
];

const routerOptions: ExtraOptions = {
    useHash: true,
    enableTracing: false,
    anchorScrolling: 'enabled',
    scrollPositionRestoration: 'enabled',
    onSameUrlNavigation: 'reload',
    preloadingStrategy: CustomPreloadingStrategy,
};
@NgModule({
    imports: [RouterModule.forRoot(routes, routerOptions)],
    providers: [CustomPreloadingStrategy],
})
export class AppRoutingModule {}
