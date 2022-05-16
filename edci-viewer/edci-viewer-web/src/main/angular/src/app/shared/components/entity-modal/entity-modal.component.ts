import { Component, OnInit, Inject } from '@angular/core';
import { EntityModalInformation } from '../../model/entityModalInformation';
import { DYNAMIC_COMPONENT_CONFIG } from '@eui/core';
import { EntityModalConfiguration } from './entity-modal.configuration';

@Component({
    selector: 'edci-viewer-entity-modal',
    templateUrl: './entity-modal.component.html',
    styleUrls: ['./entity-modal.component.scss'],
})
export class EntityModalComponent implements OnInit {
    modalInformation: EntityModalInformation;
    constructor(
        @Inject(DYNAMIC_COMPONENT_CONFIG)
        private configuration: EntityModalConfiguration
    ) {
        this.modalInformation = configuration.modalInformation;
    }

    ngOnInit() {}
}
