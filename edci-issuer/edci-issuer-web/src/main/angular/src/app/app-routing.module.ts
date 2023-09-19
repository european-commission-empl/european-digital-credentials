import { NgModule } from '@angular/core';
import { RouterModule, Routes, ExtraOptions } from '@angular/router';

const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'index.jsp', redirectTo: 'home' },
    {
        path: 'home',
        loadChildren: () =>
            import('./features/home/home.module').then((m) => m.HomeModule),
    },
    {
        path: 'create',
        loadChildren: () =>
            import('./features/create/create.module').then(
                (m) => m.CreateModule
            ),
    },
    {
        path: 'credential-builder',
        loadChildren: () =>
            import(
                './features/credential-builder/credential-builder.module'
            ).then((m) => m.CredentialBuilderModule),
    },
    { path: '**', redirectTo: 'home' },
];

const routerOptions: ExtraOptions = {
    useHash: true,
    anchorScrolling: 'enabled',
    onSameUrlNavigation: 'reload',
    enableTracing: false,
    scrollPositionRestoration: 'enabled',
};
@NgModule({
    imports: [RouterModule.forRoot(routes, routerOptions)],
})
export class AppRoutingModule {}
