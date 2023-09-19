import { EuropassCredentialPresentationView, EuropassDiplomaView } from '@shared/swagger';

export interface IDiplomaDetailsResolverData {
    credentialDetails: EuropassCredentialPresentationView;
    credentialDiploma: EuropassDiplomaView;
}
