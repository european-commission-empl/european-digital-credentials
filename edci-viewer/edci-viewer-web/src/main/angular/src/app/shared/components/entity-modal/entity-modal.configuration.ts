import { EntityModalInformation } from '../../model/entityModalInformation';

export class EntityModalConfiguration {
    modalInformation: EntityModalInformation;
    constructor(modalInformation) {
        Object.assign(this, modalInformation);
    }
}
