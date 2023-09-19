insert into FIELD_DC_LE_ACT_S_MODE (DC_LE_ACT_S_PK, DT_CODE_PK)
    select PK, MODE_PK from DC_LEARNING_ACT_SPEC where MODE_PK is not null;

alter table DC_LEARNING_ACT_SPEC drop column MODE_PK;