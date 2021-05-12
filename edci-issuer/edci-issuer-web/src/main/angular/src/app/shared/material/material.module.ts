import { NgModule } from '@angular/core';

import { MatTooltipModule,
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatButtonModule,
    MatDialogModule,
    MatCardModule,
    MatProgressBarModule,
    MatBadgeModule,
    MatToolbarModule
} from '@angular/material';

export const moduleList = [
    MatBadgeModule,
    MatTooltipModule,
    MatTableModule,
    MatCheckboxModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatPaginatorModule,
    MatDialogModule,
    MatButtonModule,
    MatCardModule,
    MatProgressBarModule,
    MatToolbarModule
];

@NgModule({
    imports: moduleList,
    exports: moduleList
})
export class MaterialModule {}
