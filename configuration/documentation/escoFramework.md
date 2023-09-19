#ESCO Skills and Occupations
ESCO is a classification framework for European Skills, Competences, Qualifications and Occupations. The EDCI applications make use of the [ESCO Skill framework](https://ec.europa.eu/esco/portal/home) and it's [API](https://ec.europa.eu/esco/api/search) to download the assets that reside in this lists.
These assets are used in Code fields that have an specific targetFramework which is defined in the application as ESCO datasets. Those are defined in EscoBridgeService and any other dataset that needs to be used should be added there.

Currently, the EDCI applications use the following ESCO datasets:
* OCCUPATION http://data.europa.eu/esco/model#Occupation
* SKILL http://data.europa.eu/esco/model#Skill  
 