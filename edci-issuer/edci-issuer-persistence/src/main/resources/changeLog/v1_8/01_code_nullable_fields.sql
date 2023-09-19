alter table
   DT_CODE
modify
   (TARGET_FRAMEWORK_URI NULL);

alter table
   DT_CODE
modify
   (TARGET_NAME_PK NULL);

alter table
   DT_CODE
modify
   (URI VARCHAR(4000) DEFAULT '_blank');