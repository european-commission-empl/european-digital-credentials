# EDCI Issuer
[![license: EUPL](licence-EUPL%201.2-brightgreen.svg)](https://github.com/teamdigitale/licenses/blob/master/EUPL-1.2)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg)](https://github.com/RichardLitt/standard-readme)
## Table of Contents
- [Background and Tech Stack](#background-and-tech-stack)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Build and deployment](#build-and-deployment)
- [Core functions](#core-functions)
- [External dependencies](#external-dependencies)
- [Interest Links](#interest-Links)
##Background and Tech Stack
#####Background 
Issue degrees, diplomas, certificates of participation or other credentials to your learners. Any school, college, university or training provider in Europe may use this tool to issue credentials for free and secure them with their e-seal. Credentials can be e-mailed to learners or directly deposited to their Europass profiles.
####Tech stack
#####Front-end
Angular 13.2 (eUI v13.1.2 - EUI Framework)
#####Back-end
Java 11, 
Spring,
Jena,
Titanium,
JAXB, 
JPA
CEF DSS – java library to create/validate e-signature (JAdES, PAdES, etc.)
####Developed with:
Although it is possible to change the configuration to work with any OS, application server and database,
 the most tested configuration, the one explained in this document and the one used during development is the following:
#####Application Server
Apache Tomcat 9.0
#####Database
Oracle 19 or MySQL 8.0
#####Operating System
linux RedHat
##Project structure
The issuer application is divided into 7 modules that can also be split in 2 groups, those that are necessary for the app functionality,
 and extra modules that are not required but provide extra functionality.
###Dependency common projects
The next two projects are shared across by multiple of the EDCI applications and are declared as a maven dependency. As a result, these two projects must be downloaded and compiled (or uploaded to a repository) prior to the EDCI Issuer project for the dependencies to be available.
#####edci-data-model
This project contains the dataModel and the utilities related to it, as well as common functionalities regarding RDF like Controlled list SparQL . 
#####edci-commons
This project contains both utility and service classes that can be used by all of the other projects, as well as predefined configuration classes and interfaces.
#####edci-eseal-core 
This module has functions used for the sealing and validation of the signatures of the credentials using the DSS library.
Any change required to the configuration of the DSS module will need to be included in this project.
###Required Modules
#####edci-issuer-common
This module contains classes used by all the other modules, mostly Constants and DTOs that are used across the application.
#####edci-issuer-persistence
this module contains the persistence layer of the application with it's DAOs and Repositories, all of the database related methods and queries reside here.
#####edci-issuer-service
This module contains most of the business logic, therefore most of the back-end core functions of the app reside here.
#####edci-issuer-web
This module contains the angular folder with the front-end code of the application, and the index.jsp from which is served.
Also, here reside all of the configuration files, as well as external or downloadable resources.
It also contains all the configuration directories into the resources/config folder:
* cache - ehCache configurations
* docker - docker configuration files and start script
* eseal - Sealing configurations
* ext - contains the default properties that will be copied to volume on docker creation, once generated they can be changed, effects will take place after server restart.
* issuer - General configurations, such as datasources, url paths, remote endpoints...
* security - Session and OIDC configurations

As the swagger codegen maven plugin resides in this module, compilation of this project will result in the typescript swagger files being recreated. 
#####edci-issuer-web-rest
This module contains all of the Rest API endpoints used by the frontend angular components, thus all of the operations externally available reside here.
Also, the swagger.json file is generated based on the annotations in this module, and during it's compilation time. 
#####edci-issuer-web-rest-swagger
This module contains all necessary files for swagger typescript generation, including mustache templates and custom Code Generators.
###Extra Modules
#####edci-issuer-web-swagger-ui
This module provies an automatic system of API documentation, it will take the swagger.json generated and copy it into this project, which will produce an artifact with the API documentation that can be deployed. 
##Configuration
The EDCI issuer needs to be configured for the particular environment where is going to be deployed, this includes both technical configuration (such as database, mail server...) as well as custom app configurations (QSeal enforce, OIDC security...) 
All of the configuration is stored in files inside the projects and it consists of value/key pairs. Some of it is included in the artifact at compilation time, and some of it is read from the filesystem by the application.
Because of this, the configuration to be used must be decided and set prior to compiling the project. 
#####Profiles
The project can be configured and compiled with a variety of profiles, which can be used to provide a set of configurations for multiple environments.
The profile that is being used is specified at compilation time for both front-end and back-end, and must be the same for both compilation steps.
The current supported profiles are:
* default: this is the default profile used for development, which is the same as dev.
* docker: profile used to generate the docker images.
* dev: this profile is used for a QA environment, the artifact can be deployed in a local tomcat 9+ environment, mostly for integration testing.
* prod: the profile used for live environment.

#####Compile-time configuration files 
Some of the properties that reside in the project, are used in XML files that are included in the artifact are compile time. 
Because of this, changing any of these properties requires a recompilation of the project.

The backend files containing properties that can only be changed in compile time  reside in the edci-issuer-web module, inside the config folder, here you will find the following folders containing configuration:
* cache: contains ehcache.xml file.
* issuer: contains basic properties for all the profiles.

Also, you will find an ext folder with an example of the files required as run-time configuration properties.

For front-end properties, all of the configurations can be found under the /environments folder, with environment.[profile].ts naming. 
#####Run-time configuration files
A good portion of the properties reside outside of the project, by default, the application expects to found this files in the file:${catalina.base}/conf/edci/issuer/ folder.
This path can be changed at the IssuerConfigService class. The expected files are:
* edci/issuer/issuer.properties
* edci/issuer/issuer_front.properties
* edci/issuer/eseal_core.properties
* edci/proxy.properties
* edci/shacl.properties
 
These properties can be changed without recompiling the EDCI Issuer, but keep in mind that the application server must be restarted for the changes to take place.

##Build and deployment 
####Requirements:
#####Compilation requirements
In order to compile the EDCI Issuer you will need
* Maven
* node.js and yarn - note that NodeJS 16.XX is required
######GIT
The source code is stored in this GIT repository: [european-commission-europass/europass-digital-credentials](https://github.com/european-commission-europass/europass-digital-credentials) It can be downloaded easily is by cloning it into it's desired folder with this command:
```
git clone https://github.com/european-commission-europass/europass-digital-credentials.git
```
#####Deployment requirements
* Application Server - the development has been done in tomcat
* Internet accesss - the app requieres access to external resources, a proxy can be configured
* AppServer provided Jar Dependencies in the app server libraries directory:
  (Jars can be found in: edci\edci-issuer\edci-issuer-web\src\main\resources-unfiltered\lib\ext)
    +   bcprov-jdk15on-1.68.jar
    +   javaee-api-7.0.jar
    +   javax.mail-1.5.0.jar
    +   jstl-1.2.jar
    +   the application server must have the drivers for your database of choice
* Relational database

If you are trying to deploy the EDCI Issuer project in a tomcat application server, you can check our [tomcat configuration](../configuration/documentation/tomcatConfiguration.md) page.
####Building process:
####Swagger file generation
The Project structure requires that swagger frontend files are generated before compiling the backend. This allows for the frontend api-related code to be autogenerated in an automatized way every time that back end is compiled.

To do so, a maven plugin in edci-issuer-web-rest-swagger module (com.github.kongchen.swagger-maven-plugin) will generate a swagger.json file based on the annotated endpoints in the edci-issuer-web-rest module.

After that, the module edci-issuer-web will generate the swagger frontend files, using once again a maven plugin (io.swagger.swagger-codegen-maven-plugin). Keep in mind that this requires the previous creation of an empty "dist" directory in edci-issuer-web/src/main/angular/dist.
 
This process is only required to do once if no changes in the code are applied. If the edci-issuer-web-rest endpoints where to be changed, then this generation has to be done again before the frontend code for api calls is available.
Because of this, the best way of doing a first generation of the swagger files, is to simply create an empty "dist" folder in the specified path, and then run a backend compilation, even if it means that it has to be compiled again after the front-end code has also been compiled.

####External dependencies installation
The EDCI Issuer makes uses of external libraries that are not available in public repositories. Because of this, external dependencies are included at /edci/configuration/external-libs folder.
Before building the EDCI Issuer, these dependencies must be installed into the local maven repository, this can easily be done by executing the following commands:
 
 * mvn install:install-file -DgroupId="eu.europa.ec.digit.uxatec.eui" -DartifactId="eui-angular2-servlet" -Dpackaging=jar -Dversion="1.0.0" -Dfile="configuration/external-libs/eui-angular2-servlet-1.0.0.jar"

#####Angular
The front-end application residing in edci-issuer-web must be compiled prior to the backend, to do so, just use the standard yarn install command to update your node_modules directory, and after that use yarn run build-[profile] scripts. Configuration for these scripts can be found in package.json file.
Keep in mind that any change to the API that would change swagger definition, requires a previous compilation of the back-end code.
``` 
yarn
yarn run lint
yarn run build-[profile]
```
Prerequisites and troubleshooting: https://eui.ecdevops.eu/eui-showcase-dev-guide-14.x/docs/01-getting-started/01-prerequisites

#####Maven
This project uses maven to manage the projects build. Keep in mind that some of the edci-eseal-core project artifacts must be downloaded from specific respositories, [Here](../configuration/documentation/mvn_settings.xml) is an example of a basic maven configuration that can be used as a template (keep in mind that some values must be changed). 
Once the maven settings are ready, just execute the usual maven command using parent pom.xml with the desired profile, if no one is selected, default is used.
``` 
mvn clean install -P [profile]
```
##Core functions
This section describes the core functions of the issuer app, which allow the application to process, seal and issue credentials.
Some of this functions can be done only in one way, other may have multiple options.




### Upload JSON
This functionality allows to upload credentials in JSON (not JSON-LD) format with deliveryDetails containing a walletAddress, a basic format of the JSON needed is:

    {
        credential : {Credential Object following ELM Datamodel},
        deliveryDetails : {
            walletAddress : "WalletIdAsAString"
            displayDetails: {Display details object}
        }
    } 

Mandatory fields for the credential are defined at the [european learning model](https://github.com/european-commission-europass/Europass-Learning-Model).

###Credential Builder
This functionality allows to define credential templates through a user interface, which then can be saved into a database. A user may select one of the owned credentials, and send it to any number of recipients. 

####Recipients
A recipient is the subject to which the credential will be issued. After selecting a credential to issue, you will be able to select multiple recipients for the credential. This can be done by either entering them manually on the application itelf, or by downloading an auto-generated excel, which you will need to fill and upload for the recipients to be processed. 

####Custom Fields
During the selection of the recipients method (XLS or dynamic form), a number of fields can be selected from a list. This list depends on the annotations inside the OCB model, and it is available through the /api/v1/credentials/specs/recipients/fields endpoint.  


#####Recipients XLS customization
After a Credential has been created in the OCB, an XLS for that particular credential can be generated and downloaded, filled with recipients, and the reuploaded for processing.

#####Recipients Dynamic Form customization
After a Credential has been created in the OCB, a dynamic form can be filled inside the application to generate the recipients.


###Credential Processing
Valid credentials uploaded by any means are processed at the CredentialsService class. At this time multiple operations are executed over the credentials:
- Each of the credentials goes through a Consumer Pipeline which executes multiple operations based on the consumer classes annotated with @EDCIConsumer
- The credential types are checked, allowed Credential Types are "VerifiableCredential" and "EuropeanDigitalCredential" 
- The credential profiles are checked, allowed profiles reside at the credential type controlled list "http://data.europa.eu/snb/credential/25831c2"
- The credential JSON-LD files are created, and validated against the modified internal SHACL files contained in the app.

At the end of the processing, the JSON-LDs for the valid credentials are created, the invalid credentials are ignored and no file is created for them.
####Custom Fields processing
Credentials created using the OCB, either by Recipients XLS or Dynamic Form methods, have an additional processing phase that will map each recipient and the custom fields belonging to it to the custom values introduced by the user.
This step is done at the IssuerCustomizedModelService class, and requires the oid of a Credential Spec created at the OCB and a filled CustomizedRecipientsDTO either by Form or XLS.

####Consumer Pipeline
Consumers annotated with the @EDCIConsumer annotation and EuropeanDigitalCredentialDTO value for the "applyTo" parameter are applied in the order defined by the "priority" parameter of the annotation.  
The current consumers, in execution order are:

- **CredentialLoopCheckConsumer:** Checks for circular references inside the credential, any credential that contains those is invalid.
- **CredentialIdentifierConsumer:** Creates incremental identifiers for objects part of the datamodel, with a prefix based on @EDCIIdentifier annotations on those classes.
- **CredentialLanguageConsumer:** Completes the information for the primary language using the language controlled list, it will also add the primary language to the available languages if not present.
- **CredentialFullNameConsumer:** Adds the full name to the credential subject, if not added, based on the given name and the family name.
- **AccreditationConsumer:** Downloads external accreditations from QDR, parses them and injects them into the required credentials.
- **ControlledListConsumer:** Completes information of any item belonging to any of the registered controlled lists found in the credential.
- **AccreditationValidationConsumer:** Validates external QDR accreditations.  

If one of the credentials becomes invalid as a result of the execution of any consumer, the isValid field in the credential is set to false, errors are added to the validationErrors field.
When a consumer execution results in an exception, the credential is also set as invalid, and no more consumers are executed, this will also be the case for those credentials that may be marked as invalid during the execution.

###Sealing
During sealing process, the credentials are sealed following configuration properties, after that, the credentials are validated against an Schema, which is also defined at the configuration properties.

There are different processes that can be used to perform the credential sealing, "nexu sealing" and "local sealing" are mutually exclusive and only one can be configured at the same time, meaning that changing from a configuration to another requires a reboot.

The configuration of the sealing process is done through the  "issuer_front.properties", changing the "enabledLocalSealing" property to "true" or "false".
Also, a TSP(TimeStamp Provider) URL must be configured in the "issuer.properties". As well as a signature level (see XadES Profiles at https://ec.europa.eu/cefdigital/DSS/webapp-demo/doc/dss-documentation.html). Lastly, if the property "allow.qseals.only" is set to true, only eSeals will be allowed (no eSignatures).

The sealing and signature validation of credentials in the EDC ecosystem is done through the eseal-core mordule, included as an internal dependency of the edci-issuer. more information on the sealing operations and how those are performed can be found at the [eseal-core module README](../edci-eseal-core/README.md).  

####Nexu sealing
This is the most common process for sealing and the default one. When this is active, the sealing process in the frontend will redirect the user to nexu app (which must be previously installed, see: https://nowina.lu/solutions/java-less-browser-signing-nexu/) to select the certificate.

After selecting the certificate from nexu, the sealing process will continue.

Keep in mind, that this process is mutually exclusive with local sealing, meaning that only one of the systems can be configured as active in any given time.

To configure the nexu sealing, the "enabledLocalSealing" at the "issuer_front.properties" file, must be set to "true".
####Local Sealing
If local sealing is enabled, the credentials will be sealed using the configured local certificate when using the normal sealing process through the front-end application. The user will be required to introduce the password in the frontend, and will be used in server with the stored cert in order to sign the credentials.

To configure the local sealing, the "enabledLocalSealing" at the "issuer_front.properties" file, must be set to "true". But also a certificate (ie: a .pfx file) is required to exist in the server, and the path to the certificate must be specified in the "dss.cert.path" of the "issuer.properties" configuration file. 

###API Sealing
The Api sealing is a form of Local Sealing using an existing certificate in the server meant to be used by using api calls and has 3 main operations:

* /api/v1/public/credentials/seal Seal : Process a JSON to generate a JSON-LD credential and seal it, then return it as a download file.
* /api/v1/public/credentials/seal_and_send Seal and Send : seals a credential and sens it to the specified wallet, returns a viewer link.
* /api/v1/public/credentials/seal_batch Seal Batch: seals a batch of credentials and sends it to the specified wallet, returns 202 if all credentials have valid format.

As with the local sealing, a certificate (ie: a .pfx file) is required to exist in the server, and the path to the certificate must be specified in the "dss.cert.path" of the "issuer.properties" configuration file. 

###Issuing
Issuing can be done in two ways, wallet uploading and email attachments.
* Wallet Upload : The EDCI Issuer will try to uplad the file to the property configured wallet.
* Email attachment : The EDCI Issuer will try to send an email with the credential attached, based on the email field of the credential itself.

If one of the the methods fails, a message is shown.
##External dependencies
The EDCI Issuer project has external dependencies which are mandatory, and must be supplied in one way or another, otherwise core functions will be unusable. This means that the EDCI issuer requires 

####Controlled Lists
The EDCI Issuer makes use of the European Publications Office controlled lists, some of these are mandatory to use for some of the Code fields of the datamodel, you can expand on both of this matters in the [datamodel page]() and the [controlled lists page](../configuration/documentation/controlledLists.md) of this document.

####ESCO Skills and Occupation
The EDCI Issuer makes user of values in the ESCO Framework, to fill the values of certain Code fields. This information is queried through the ESCO Skill API, which must contain the information for the Code field to be correctly processed. 

You can find more about the ESCO Framework utilization in EDCI [here](../configuration/documentation/escoFramework.md) 

####Timestap Provider
At the issuer_dss_[profile].properties file in the back-end server, it is mandatory to provide a valid TimeStamp provider in the form of URL. 

####OIDC Identity Provider
Although a mock user can be enabled por testing purposes, an OIDC Identity provider will be required for the credential builder to work properly. All of the required configurations for this can be found in the security_[profile].properties file.
All fo this is implemented using the Authentication (Basic) flow. For implementations, spring-security and [mitreId](https://github.com/mitreid-connect/) are used. If you want to know more about it, an in-depth explanation on the current configuration can be found [here](https://steps.everis.com/confluence/display/EDCI/EuLogin+OIDC+integration).

By default, this mock user will be enabled with the property "oidc.mock.user.active" set to true. This property can be found into the "issuer_front.properties" and "security.properties" files and should be set to false before enabling the desired authentication.

####Mail Server
In order to send the attached credential through email, this must be configured. It is required that the email session is defined in the application server configuration as explained in the Configuration section of this document.

###Images Generation
To generate Images, the EDCI issuer requires an endpoint to create images in PNG format given some HTML templates.

###Accreditation service
To accreditatate the credentials when an Accreditation Id is provided, the EDCI issuer requires conection to an external service to retrieve the information.

All of the extra configurations for this can be found in the mail_[profile].properties file. 
##Interest Links
* Developer Playground: A developer playground which allows users to build and issue credentials without the need for an e-seal is available [here](https://webgate.acceptance.ec.europa.eu/europass/edci-issuer/). 
* Preparing Credentials: This [page](https://europa.eu/europass/en/preparing-credentials-interoperability) will provide you information on how to prepare your data. 