import { NgModule } from '@angular/core';
import { RouterModule, Routes, ExtraOptions } from '@angular/router';
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
        loadChildren: './features/home/home.module#Module',
        data: { preload: true },
    },
    {
        path: 'diploma',
        loadChildren: './features/diploma/diploma.module#Module',
    },
    {
        path: 'diploma-details',
        loadChildren:
            './features/diploma-details/diploma-details.module#Module',
    },
    {
        path: 'shareview/:shareLink',
        loadChildren: './features/diploma/diploma.module#Module',
    },
    {
        path: 'view/:userId/:id',
        loadChildren: './features/diploma/diploma.module#Module',
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
