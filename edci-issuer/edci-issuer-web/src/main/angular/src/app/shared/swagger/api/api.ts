export * from './v1.service';
import { V1Service } from './v1.service';
export * from './v2.service';
import { V2Service } from './v2.service';
export const APIS = [V1Service, V2Service];
