/**
 * API
 * API Swagger description
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


export interface DSSTimestampDTO { 
    base64Timestamp?: string;
    canonicalizationMethod?: string;
    type?: DSSTimestampDTO.TypeEnum;
}
export namespace DSSTimestampDTO {
    export type TypeEnum = 'CONTENT_TIMESTAMP' | 'ALL_DATA_OBJECTS_TIMESTAMP' | 'INDIVIDUAL_DATA_OBJECTS_TIMESTAMP' | 'SIGNATURE_TIMESTAMP' | 'VALIDATION_DATA_REFSONLY_TIMESTAMP' | 'VALIDATION_DATA_TIMESTAMP' | 'DOCUMENT_TIMESTAMP' | 'ARCHIVE_TIMESTAMP';
    export const TypeEnum = {
        CONTENTTIMESTAMP: 'CONTENT_TIMESTAMP' as TypeEnum,
        ALLDATAOBJECTSTIMESTAMP: 'ALL_DATA_OBJECTS_TIMESTAMP' as TypeEnum,
        INDIVIDUALDATAOBJECTSTIMESTAMP: 'INDIVIDUAL_DATA_OBJECTS_TIMESTAMP' as TypeEnum,
        SIGNATURETIMESTAMP: 'SIGNATURE_TIMESTAMP' as TypeEnum,
        VALIDATIONDATAREFSONLYTIMESTAMP: 'VALIDATION_DATA_REFSONLY_TIMESTAMP' as TypeEnum,
        VALIDATIONDATATIMESTAMP: 'VALIDATION_DATA_TIMESTAMP' as TypeEnum,
        DOCUMENTTIMESTAMP: 'DOCUMENT_TIMESTAMP' as TypeEnum,
        ARCHIVETIMESTAMP: 'ARCHIVE_TIMESTAMP' as TypeEnum
    };
}
