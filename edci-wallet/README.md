# EDCI Wallet
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

## Background

The EDCI wallet is a Stateless Rest API service with the main purpose of receiving, storing and serving credentials. 
It also can validate a credential or verifiable presentation signature and format, as well as creating and managing temporal share links for this credentials or verifiable presentations.
#### Tech stack
##### Front-end
Angular 7.x (eUI v7.4.1 - EUI Framework) available, but not currently used.
##### Back-end
Java 8, 
Spring,
JAXB,
thymeleaf,
JPA
CEF DSS – java library to create/validate e-signature (XAdES, PAdES, etc.)
#### Developed with:
Although it is possible to change the configuration to work with any OS, application server and database,
 the most tested configuration, the one explained in this document and the one used during development is the following:
##### Application Server
Apache Tomcat 9.0
##### Database
Oracle 19 or MySQL 8.0
##### Operating System
linux RedHat
## Project Structure
he wallet application is divided into 7 modules that can also be split in 2 groups, those that are necessary for the app functionality,
 and extra modules that are not required but provide extra functionality.
### Dependency common projects
The next two projects are shared across by multiple of the EDCI applications and are declared as a maven dependency. As a result, these two projects must be downloaded and compiled (or uploaded to a repository) prior to the EDCI wallet project for the dependencies to be available. 
##### edci-commons
This project contains both utility and service classes that can be used by all of the other projects, as well as the main DataModel classes.
Also, common functionalities (such as security or DataModel services) are found in this project.
##### edci-dss-utils 
This module has functions used for the sealing and validation of the signatures of the credentials using the DSS library.
Any change required to the configuration of the DSS module will need to be included in this project.
### Required Modules
##### edci-wallet-common
This module contains classes used by all the other modules, mostly Constants and DTOs that are used across the application.
##### edci-wallet-persistence
this module contains the persistence layer of the application with it's DAOs and Repositories, all of the database related methods and queries reside here.
##### edci-wallet-service
This module contains most of the business logic, therefore most of the back-end core functions of the app reside here.
##### edci-wallet-web
Here reside all of the configuration files, as well as external or downloadable resources such as PDF thymeleaf templates.
It also contains all the configuration properties into the resource folder:
* wallet - General configurations, such as datasources, url paths, remote endpoints...
* dss - Sealing configurations
* mail - SMTP Server properties and related configurations
* proxy - Proxy configurations
* security - OAUTH configurations
* the application server must have the drivers for your database of choice

If any front-end needs to be added to this project, it is strongly recommended that an angular application is built inside the angular folder of this project using the EUI Framework.
As the swagger codegen maven plugin resides in this module, compilation of this project will result in the typescript swagger files being recreated. 
##### edci-wallet-web-rest
This module contains all of the Rest API endpoints used by the frontend angular components, thus all of the operations externally available reside here.
Also, the swagger.json file is generated based on the annotations in this module, and during it's compilation time. 
##### edci-wallet-web-rest-swagger
This module contains all necessary files for swagger typescript generation, including mustache templates and custom Code Generators.
### Extra Modules
##### edci-wallet-web-swagger-ui
This module provies an automatic system of API documentation, it will take the swagger.json generated and copy it into this project, which will produce an artifact with the API documentation that can be deployed. 
## Configuration
##### Profiles
The project can be configured and compiled with a variety of profiles, which can be used to provide a set of configurations for multiple environments.
The profile that is being used is specified at compilation time for both front-end and back-end, and must be the same for both compilation steps.
The current supported profiles are:
* default: this is the default profile used for development, which is the same as dev.
* docker: profile used to generate the docker images.
* dev: this profile is used for a QA environment, the artifact can be deployed in a local tomcat 9+ environment, mostly for integration testing.
* prod: the profile used for live environment.
##### Compile-time configuration files 
Some of the properties that reside in the project, are used in XML files that are included in the artifact are compile time. 
Because of this, changing any of these properties requires a recompilation of the project.

The backend files containing properties that can only be changed in compile time  reside in the edci-wallet-web module, inside the config folder, here you will find the following folders containing configuration:
* cache: contains ehcache.xml file.
* wallet: contains basic properties for all the profiles.

Also, you will find an ext folder with an example of the files required as run-time configuration properties.

For front-end properties, all of the configurations can be found under the /environments folder, with environment.[profile].ts naming. 
##### Run-time configuration files
A good portion of the properties reside outside of the project, by default, the application expects to found this files in the file:${catalina.base}/conf/edci/wallet/ folder.
This path can be changed at the EDCIWalletConfig class. The expected files are:
* wallet.properties
* wallet_dss.properties
* mail.properties
* proxy.properties
* security.properties
 
These properties can be changed without recompiling the EDCI Wallet, but keep in mind that the application server must be restarted for the changes to take place.
##### Application server JNDI resources
There are some important configurations that need to already present in the application server, namely the database and the mail session.

The mail session must be named mail/Session.

The database datasource must be named jdbc/mare.
## Build and deployment
#### Requirements:
##### Compilation requirements
In order to compile the EDCI Wallet you will need
* Maven
##### Deployment requirements
* Application Server - the development has been done in tomcat
* Internet accesss - the app requieres access to external resources, a proxy can be configured
* AppServer provided Jar Dependencies in the app server libraries directory: 
  (Jars can be found in: edci\edci-wallet\edci-wallet-web\src\main\resources-unfiltered\lib\ext)
    +   bcprov-jdk15on-1.68.jar
    +   javaee-api-7.0.jar
    +   javax.mail-1.5.0.jar
    +   jstl-1.2.jar
    +   the application server must have the drivers for your database of choice
* Relational database

If you are trying to deploy the EDCI Wallet project in a tomcat application server, you can check our [tomcat configuration](../configuration/documentation/tomcatConfiguration.md) page.

##### GIT
The source code is stored in this GIT repository: [european-commission-europass/europass-digital-credentials](https://github.com/european-commission-europass/europass-digital-credentials) It can be downloaded easily is by cloning it into it's desired folder with this command:
```
git clone https://github.com/european-commission-europass/europass-digital-credentials.git
```
#### Building process:
#### External dependencies intallation
The EDCI Wallet makes uses of external libraries that are not available in public repositories. Because of this, external dependencies are included at /edci/configuration/external-libs folder.
Before building the EDCI Wallet, these dependencies must be installed into the local maven repository, this can easily be done by executing the following commands:

 * mvn install:install-file -DgroupId="eu.europa.ec.digit.uxatec.eui" -DartifactId="eui-angular2-servlet" -Dpackaging=jar -Dversion="1.0.0" -Dfile="configuration/external-libs/eui-angular2-servlet-1.0.0.jar"

##### Maven
This project uses maven to manage the projects build. Keep in mind that some of the edci-dss-utils project artifacts must be downloaded from specific respositories, [Here](../configuration/documentation/mvn_settings.xml) is an example of a basic maven configuration that can be used as a template (keep in mind that some values must be changed). 
Once the maven settings are ready, just execute the usual maven command using parent pom.xml with the desired profile, if no one is selected, default is used.
``` 
mvn clean install -P [profile]
```
## Core functions
In this section, the more i

### Wallet Creation
The EDCI wallet allows for wallets to be created through a creation endpoint based on an email and a userId.
If the credential request is  sent only with the email field, a temporal wallet will be created.
On the other hand, if the credential request is sent with both the userId and the email, the system will try to find a temporal wallet with the specified email and update it to a permanent one.

This means, that the only way to create a permanent wallet is to have both an email and a userId and using that information when creating the wallet via API.  
### Bulk Creation
The EDCI wallet also allows for the creation of a bulk of temporal wallets based on a set of emails. This method only allows for the creation of temporal wallets.
### Add Credential
The EDCI wallet project provides a way to add a credential to an  based on a user ID and an XML File. Before storing the XML file, the wallet will first check that it is signed and the signature is a qualified qseal, and depending no configuration it will be allowed or not.
It is required the the uploadad credential follows the [europass data model](https://github.com/european-commission-europass/Europass-Learning-Model) framework.

When a credential is sent for a userId that does not have already a wallet, a temporal wallet is created.

### Add Verifiable Presentation
The EDCI wallet project also provides a way to add a verfiable presentation based on a user ID and an XML file. Similar to the Credential XMLs, it can be configured to accept or reject the unsigned verifiable presentation, and must also follow the [europass data model](https://github.com/european-commission-europass/Europass-Learning-Model) framework.
Here, once again the signature is checked to verify that belongs to a qualified qseal and will be allowed to be stored or not based on configuration.

A verifiable presentation can be added to both a temporal or a permanent wallet.

### Create and delete Sharelinks
It is possible to share a credential by creating a sharelink of a credential that is stored in a temporal wallet. When creating a sharelink, a expiration date must be supplied, and the user that is creating the sharelink must be the owner of the credential.

Currently, there is no possibility to share any credential stored in a temporal wallet.
### Get Credential
The basic function of the wallet is to serve stored XML credentials that reside in the wallets to different applications that make use of those. 
This functionality is available in both JSON and XML format at the same endpoint, the desired format can be specified with the "accept" header
When serving a Credential, an Identity check is performed, meaning that a credential is only available to the owner.
### Get Verifiable presentation
The wallet can also serve stored XML for verifiable presentation inside of the wallets. Similar to the XML Credentials, 
applications may make use of those in a variety of ways, or simply download it. When doing this, the credential will be transformed into a shared credential and embedded into a [Verifiable presentation](../configuration/documentation/issuerBuildVP.md). 
Once again, this functionality is available in both JSON and XML format at the same endpoint, by setting the "accept" header. Also, when serving a Verifiable presentation, an Identity check is performed, meaning that a credential is only available to the owner.
  
### Export Verifiable presentation
The wallet can export verifiable presentations from any of the credentials or verifiable presentations stored. Once again, any credential will be transformed into a shared credential and embedded into a [Verifiable presentation](../configuration/documentation/issuerBuildVP.md).
Also, there is an Identity check when trying to export a credential, meaning that only the owner is able to do that.

The PDF exporting is done through a template that is processed twice, one in the wallet for variable substitution and one in an external Thymeleaf processor which will generate the final PDF.
 
## External dependencies
The EDCI Wallet project requires that some external credentials are in place before deploying the project into any environment.

#### Controlled Lists
The EDCI Issuer makes use of the European Publications Office controlled lists, some of these are mandatory to use for some of the Code fields of the datamodel, you can expand on both of this matters in the [datamodel page]() and the [controlled lists page]() of this document.

### PDF Generation
To generate PDFs, the EDCI wallet requires an endpoint to create a PDF file from the credential information using some HTML templates.

### Images Generation
To generate Images, the EDCI wallet requires an endpoint to create images in PNG format given some HTML templates.

### Accreditation service
To accreditatate the credentials when an Accreditation Id is provided, the EDCI wallet requires conection to an external service to retrieve the information.

To do this, the thymeleaf API must accept a POST call from the EDCI Wallet, with a "html" parameter containing the HTML template. 
### OAUTH Identity provider
The EDCI Wallet requires an OAUTH Identity provider. Although the EDCI wallet is a stateless REST API and does not provide login functionality, it acts as a resource server and some of the endpoints are only available to wallet owners.

Keep in mind that any application that makes use of the wallet and requires access tot secured endpoints must use the same IDP as the EDCI Wallet.
The configuration for the oauth Identity provider can be found in the security_[profile].properties file of the edci-wallet-web project.

### Mail server
The EDCI Wallet requires a configured mail server.It is required that the email session is defined in the application server configuration as explained in the Configuration section of this document.
All of the extra configurations for this can be found in the mail_[profile].properties file.  

* XML upload format: The XML upload format can be found [here](https://github.com/european-commission-europass/Europass-Learning-Model).
* Verifiable Presentation format: How to build a [verifiable presentation](../configuration/documentation/issuerBuildVP.md)
