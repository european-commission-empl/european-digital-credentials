import { NgModule } from '@angular/core';
import { SharedModule } from '@shared/shared.module';
import { TranslateService } from '@ngx-translate/core';
import { CreateComponent } from './create.component';
import { CreateRoutingModule } from './create-routing.module';
import { OverviewComponent } from './overview/overview.component';

import { MatPaginatorIntl } from '@angular/material/paginator';
import { MatPaginatorCustomComponent } from '@shared/components/mat-paginator-custom/mat-paginator-custom.component';

@NgModule({
    imports: [SharedModule, CreateRoutingModule],
    declarations: [CreateComponent, OverviewComponent],
    providers: [{
        provide: MatPaginatorIntl,
        useFactory: (translate) => {
            const service = new MatPaginatorCustomComponent(translate);
            service.injectTranslateService(translate);
            return service;
        },
        deps: [TranslateService] }]

})
export class CreateModule {}
