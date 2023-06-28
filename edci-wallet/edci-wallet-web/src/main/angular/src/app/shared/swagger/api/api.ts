export * from "./credentials.eu.europa.ec.empl.edci.dss.service";
import { CredentialsService } from "./credentials.eu.europa.ec.empl.edci.dss.service";
export * from "./v1credentials.eu.europa.ec.empl.edci.dss.service";
import { V1credentialsService } from "./v1credentials.eu.europa.ec.empl.edci.dss.service";
export * from "./v1sharelinks.eu.europa.ec.empl.edci.dss.service";
import { V1sharelinksService } from "./v1sharelinks.eu.europa.ec.empl.edci.dss.service";
export * from "./v1wallets.eu.europa.ec.empl.edci.dss.service";
import { V1walletsService } from "./v1wallets.eu.europa.ec.empl.edci.dss.service";
export const APIS = [
    CredentialsService,
    V1credentialsService,
    V1sharelinksService,
    V1walletsService,
];
