import { CredentialView } from '@shared/swagger';

export interface FileUploadResponseView {
    valid?: boolean;
    credentials?: Array<CredentialView>;
}
