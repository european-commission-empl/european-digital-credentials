BEGIN
   EXECUTE IMMEDIATE 'DROP TABLE COUNTRIES_TEMP';
EXCEPTION
   WHEN OTHERS THEN
      IF SQLCODE != -942 THEN
         RAISE;
      END IF;
END;
/