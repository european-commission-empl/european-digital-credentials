import { NgModule } from '@angular/core';
import { RouterModule, Routes, ExtraOptions } from '@angular/router';

const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'index.jsp', redirectTo: 'home' },
    {
        path: 'home',
        loadChildren: './features/home/home.module#HomeModule'
    },
    {
        path: 'create',
        loadChildren: './features/create/create.module#CreateModule'
    },
    {
        path: 'credential-builder',
        loadChildren:
            './features/credential-builder/credential-builder.module#CredentialBuilderModule'
    },
    { path: '**', redirectTo: 'home' }
];

const routerOptions: ExtraOptions = {
    useHash: true,
    anchorScrolling: 'enabled',
    onSameUrlNavigation: 'reload',
    enableTracing: false,
    scrollPositionRestoration: 'enabled'
};

@NgModule({
    imports: [RouterModule.forRoot(routes, routerOptions)]
})
export class AppRoutingModule {}
