insert into FIELD_DC_ASSESS_S_MODE (DC_ASSESS_S_PK, DT_CODE_PK)
    select PK, MODE_PK from DC_ASSESS_SPEC where MODE_PK is not null;

alter table DC_ASSESS_SPEC drop column MODE_PK;