import { NgModule } from '@angular/core';
import { ExtraOptions, RouterModule, Routes } from '@angular/router';
import { CustomPreloadingStrategy } from './custom-preloading';
import { ViewerGuard } from './guards/viewer.guard';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    {
        path: 'index.jsp',
        redirectTo: 'home',
    },
    {
        path: 'home',
        loadChildren: './features/home/home.module#HomeModule',
        data: { preload: true },
    },
    {
        path: 'diploma-details',
        loadChildren:
            './features/diploma-details/diploma-details.module#DiplomaDetailsModule',
    },
    {
        path: 'shareview/:shareLink',
        loadChildren:
            './features/diploma-details/diploma-details.module#DiplomaDetailsModule',
    },
    {
        path: 'view/:userId/:id',
        loadChildren:
            './features/diploma-details/diploma-details.module#DiplomaDetailsModule',
        canActivate: [ViewerGuard],
    },
    { path: '**', redirectTo: 'home' },
];

const routerOptions: ExtraOptions = {
    useHash: true,
    enableTracing: false,
    anchorScrolling: 'enabled',
    onSameUrlNavigation: 'ignore',
    preloadingStrategy: CustomPreloadingStrategy,
};

@NgModule({
    imports: [RouterModule.forRoot(routes, routerOptions)],
    providers: [CustomPreloadingStrategy],
})
export class AppRoutingModule {}
