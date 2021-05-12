DECLARE
    pk_NAME NUMBER := cl_label_seq.NEXTVAL;
    pk_FRAMEWORK NUMBER := cl_label_seq.NEXTVAL;
    pk_ELEM NUMBER := cl_element_seq.NEXTVAL;
BEGIN

DELETE FROM FIELD_CL_ELEM_TARGET_DESC EDESC WHERE EDESC.CL_ELEM_PK IN (SELECT PK FROM CL_ELEMENT ELE WHERE
    ELE.TARGET_NOTATION = 'credential');

DELETE FROM FIELD_CL_ELEM_TARGET_FRWK EFRWK WHERE EFRWK.CL_ELEM_PK IN (SELECT PK FROM CL_ELEMENT ELE WHERE
    ELE.TARGET_NOTATION = 'credential');

DELETE FROM FIELD_CL_ELEM_TARGET_NAME ENAME WHERE ENAME.CL_ELEM_PK IN (SELECT PK FROM CL_ELEMENT ELE WHERE
    ELE.TARGET_NOTATION = 'credential');

DELETE FROM CL_ELEMENT ELE WHERE ELE.TARGET_NOTATION = 'credential';

INSERT INTO cl_element (pk, deprecated_since, last_updated, target_framework_uri, target_notation, uri, external_resource)
VALUES ( pk_ELEM, NULL, SYSDATE, 'http://data.europa.eu/snb/credential/25831c2',
    'credential',
    'http://data.europa.eu/snb/credential/e34929035b',
    'https://data.europa.eu/snb/resource/distribution/v1/xsd/schema/genericschema.xsd');

INSERT INTO CL_LABEL (PK,LANG,NAME) VALUES (pk_NAME,'en','Generic');

INSERT INTO CL_LABEL (PK,LANG,NAME) VALUES (pk_FRAMEWORK,'en','Credential Type');

INSERT INTO field_cl_elem_target_name (
    cl_elem_pk, cl_label_pk
) VALUES (
    pk_ELEM, pk_NAME
);

INSERT INTO field_cl_elem_target_frwk (
    cl_elem_pk, cl_label_pk
) VALUES (
    pk_ELEM, pk_FRAMEWORK
);

END;

/