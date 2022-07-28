#Controlled Lists
The [European Publication Office](https://op.europa.eu/en/home) is a catalogue of official datasets coming from EU institutions and other bodies.
Each controlled list represents data for a particular asset in a standarized, codified way.

All of the controlled lists in the European Publication Office are stored externally in RDF format, which must be either read and parsed by applications using them, or queried through the SparQL API. A tool for testing queries can be found [here](http://publications.europa.eu/webapi/rdf/sparql)
Keep in mind that, although the Publicacion Office provides a variety of datasets, EDCI applications only use some of them.

When used in datamodel field that is within the Credential or Verifiable presentation model, each item of a controlled list is converted to a Code Object ([check datamodel for more info](https://github.com/european-commission-europass/Europass-Learning-Model)). Inside the project, these controlled lits are defined at ControlledList enum, any additional controlled list to use should be added there.

Also, when issuing a credential, if a controlled list item is found, the application will try to download the latest version of the same by using the URI and targetFrameworkURI attributes.
Because of this, the controlled lists that are used can be divided into Europass and Non-Europass Controlled lists based on the targetFrameworkURI attribute.

## Europass Controlled lists
These lists are mandatory, which means that any field that is found on a credential or verifiable presentation and is from an europass controlled list, will be required to be downloaded to ensure that it exists and the last version of the code is used. 
If during the processing, a field that is considered from a mandatory ControlledList is not find in the specified URI, the credential or verifiable presentation will be considered invalid.
Find below the currently used europass Controlled lists:

* CREDENTIAL_TYPE http://data.europa.eu/snb/credential/25831c2
* LEARNING_SETTING http://data.europa.eu/snb/learning-setting/25831c2
* SUPERV_VERIF http://data.europa.eu/snb/supervision-verification/25831c2 
* ACCREDITATION http://data.europa.eu/snb/accreditation/25831c2 
* VERIFICATION_STATUS http://data.europa.eu/snb/verification-status/25831c2 
* ENTITLEMENT_STATUS http://data.europa.eu/snb/entitlement-status/25831c2 
* EQF http://data.europa.eu/snb/eqf/25831c2 
* NQF http://data.europa.eu/snb/qdr/25831c2 
* ISCED_F http://data.europa.eu/snb/isced-f/25831c2 


## Non-Europass Controlled lists
These lists are not mandatory, although any field that is found on a credential or verifiable presentation which belongs to this category will also be tried to download, a failure in the process will not result in an invalid credential.
Find below the currently used non-europass Controlled lists:

* HUMAN_SEX http://publications.europa.eu/resource/authority/human-sex 
* LANGUAGE http://publications.europa.eu/resource/authority/language 
* COUNTRY http://publications.europa.eu/resource/authority/country 
* ATU http://publications.europa.eu/resource/authority/atu 
* CURRENCY http://publications.europa.eu/resource/authority/currency 
* FILE_TYPE http://publications.europa.eu/resource/authority/file-type 
* CORPORATE_BODY http://publications.europa.eu/resource/authority/corporate-body 
* DATASET_TYPE http://publications.europa.eu/resource/authority/dataset-type  
* FREQUENCY http://publications.europa.eu/resource/authority/frequency 
* LEARNING_ACT http://data.europa.eu/snb/learning-activity/25831c2 
* ASSESSMENT http://data.europa.eu/snb/assessment/25831c2 
* LEARNING_SCHECDULE http://data.europa.eu/snb/learning-schedule/25831c2 
* LEARNING_OPPORTUNITY http://data.europa.eu/snb/learning-opportunity/25831c2 
* LEARNING_ASSESSMENT http://data.europa.eu/snb/learning-assessment/25831c2 
* TARGET_GROUPS http://data.europa.eu/snb/target-groups/25831c2 
* EDUCATION_CREDIT http://data.europa.eu/snb/education-credit/25831c2 
* COM_CHANNEL http://data.europa.eu/snb/com-channel/25831c2
* COM_CHANNEL_USG http://data.europa.eu/snb/com-channel-usg/25831c2 
* VERIFICATION_CHECKS http://data.europa.eu/snb/verification/25831c2 
* ENTITLEMENT http://data.europa.eu/snb/entitlement/25831c2 
* QUALIFICATION_TOPIC http://data.europa.eu/snb/qualification-topic/25831c2 
* SKILL_TYPE http://data.europa.eu/snb/skill-type/25831c2
* SKILL_REUSE_LEVEL http://data.europa.eu/snb/skill-reuse-level/25831c2


