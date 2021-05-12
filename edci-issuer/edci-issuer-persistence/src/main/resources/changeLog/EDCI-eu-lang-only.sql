delete from field_CL_ELEM_TARGET_DESC where cl_label_pk in (
select pk from CL_LABEL
where LANG not in ('bg', 'cs', 'da', 'de', 'et', 'el', 'en', 'es', 'fr', 'ga', 'it', 'lv', 'lt', 'hu', 'mt', 'nl', 'pl', 'pt', 'ro', 'sk', 'sl', 'sr', 'fi', 'sv', 'hr', 'is', 'mk', 'no', 'tr'));



delete from field_CL_ELEM_TARGET_NAME where cl_label_pk in (
select pk from CL_LABEL
where LANG not in ('bg', 'cs', 'da', 'de', 'et', 'el', 'en', 'es', 'fr', 'ga', 'it', 'lv', 'lt', 'hu', 'mt', 'nl', 'pl', 'pt', 'ro', 'sk', 'sl', 'sr', 'fi', 'sv', 'hr', 'is', 'mk', 'no', 'tr'));



delete from CL_LABEL
where LANG not in ('bg', 'cs', 'da', 'de', 'et', 'el', 'en', 'es', 'fr', 'ga', 'it', 'lv', 'lt', 'hu', 'mt', 'nl', 'pl', 'pt', 'ro', 'sk', 'sl', 'sr', 'fi', 'sv', 'hr', 'is', 'mk', 'no', 'tr');

delete from field_DT_TEXT_CONTENTS where DT_CONT_PK in (
select pk from DT_CONTENT
where LANGUAGE not in ('bg', 'cs', 'da', 'de', 'et', 'el', 'en', 'es', 'fr', 'ga', 'it', 'lv', 'lt', 'hu', 'mt', 'nl', 'pl', 'pt', 'ro', 'sk', 'sl', 'sr', 'fi', 'sv', 'hr', 'is', 'mk', 'no', 'tr'));



delete from DT_CONTENT
where LANGUAGE not in ('bg', 'cs', 'da', 'de', 'et', 'el', 'en', 'es', 'fr', 'ga', 'it', 'lv', 'lt', 'hu', 'mt', 'nl', 'pl', 'pt', 'ro', 'sk', 'sl', 'sr', 'fi', 'sv', 'hr', 'is', 'mk', 'no', 'tr');
