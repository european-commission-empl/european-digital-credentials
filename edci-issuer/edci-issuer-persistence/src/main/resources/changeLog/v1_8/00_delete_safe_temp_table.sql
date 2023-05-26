BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE TEMP_DATA';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/