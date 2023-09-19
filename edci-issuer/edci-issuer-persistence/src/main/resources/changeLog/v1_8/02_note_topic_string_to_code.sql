CREATE TABLE TEMP_DATA (DT_NOTE_PK, DT_CODE_PK, DT_TEXT_PK, DT_CONTENT_PK, topic, language)
  AS (select DT_NOTE.PK, DT_CODE_SEQ.nextval, DT_TEXT_SEQ.nextval, DT_CONTENT_SEQ.nextval, DT_NOTE.topic, DT_CONTENT.language
from DT_CONTENT
join FIELD_DT_NOTE_CONTENTS on field_dt_note_contents.dt_cont_pk = dt_content.pk
join DT_NOTE on field_dt_note_contents.dt_note_pk = DT_NOTE.pk
where topic is not null);

merge into TEMP_DATA
using (select TEMP_DATA.DT_NOTE_PK, min(TEMP_DATA.DT_CODE_PK) AS MIN_CODE, min(TEMP_DATA.DT_TEXT_PK) AS MIN_TEXT from
(select DT_NOTE_PK, count(DT_NOTE_PK) as TEXTS from TEMP_DATA group by DT_NOTE_PK) TEXT_GROUP
join TEMP_DATA on TEXT_GROUP.DT_NOTE_PK = TEMP_DATA.DT_NOTE_PK
where TEXTS > 1
group by TEMP_DATA.DT_NOTE_PK) AUX
ON (TEMP_DATA.DT_NOTE_PK = AUX.DT_NOTE_PK)
when matched then update
set TEMP_DATA.DT_TEXT_PK = AUX.MIN_TEXT, TEMP_DATA.DT_CODE_PK = AUX.MIN_CODE;

Insert into dt_Content (PK, CONTENT, FORMAT, LANGUAGE)
    select DT_CONTENT_PK, topic, 'text/plain', language from TEMP_DATA;

Insert into dt_text (PK)
    select distinct(DT_TEXT_PK) from TEMP_DATA;

INSERT INTO FIELD_DT_TEXT_CONTENTS (
    Select TEMP.DT_TEXT_PK, TEMP.DT_CONTENT_PK from TEMP_DATA TEMP
);

Insert into dt_code (PK, TARGET_NAME_PK)
    select min(TEMP_DATA.DT_CODE_PK), min(TEMP_DATA.DT_TEXT_PK) from TEMP_DATA group by DT_NOTE_PK;

merge into DT_NOTE
using (select DT_NOTE_PK, DT_CODE_PK from TEMP_DATA group by DT_NOTE_PK, DT_CODE_PK) TEMP
on (DT_NOTE.PK = TEMP.DT_NOTE_PK)
when matched then update
set DT_NOTE.SUBJECT_PK = TEMP.DT_CODE_PK;

alter table DT_NOTE drop column TOPIC;

DROP TABLE TEMP_DATA;