# EDCI Viewer
[![license](https://img.shields.io/github/license/:user/:repo.svg)](LICENSE)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
## Table of Contents
- [Background and Tech Stack](#background-and-tech-stack)
- [Project Structure](#project-structure)
- [Configuration](#configuration)
- [Build and deployment](#build-and-deployment)
- [Core functions](#core-functions)
- [External dependencies](#external-dependencies)
- [Interest Links](#interest-Links)
- [License](#license)
## Background and Tech Stack
##### Background 
The EDCI Viewer is JAVA web application that provides a user interface from which credentials that follow the Europass Credential datamodel can be visualized, verified, exported and shared.
The visualization, verification and export of a credential can be done for both, an XML credential, or a credential stored in a wallet, but sharing a credential is only possible when the credential is stored in a wallet.                                                             
#### Tech stack
##### Front-end
Angular 7.x (eUI v7.4.1 - EUI Framework)
##### Back-end
Java 8, 
Spring, 
JAXB,
Mapstruct and
CEF DSS – java library to create/validate e-signature (XAdES, PAdES, etc.)
#### Developed with:
Although it is possible to change the configuration to work with any OS and application server
 the most tested configuration, the one explained in this document and the one used during development is the following:
##### Application Server
Apache Tomcat 9.0
##### Operating System
linux RedHat
## Project structure
The Viewer application is divided into 7 modules that can also be split in 2 groups, those that are necessary for the app functionality,
 and extra modules that are not required but provide extra functionality.
### Dependency common projects
##### edci-commons
The edci-commons project is used accros the viewer, which means that edci-commons must be downloaded and compiled (or uploaded to a repository) for the edci-viewer to have this dependency available.

This project contains both utility and service classes that can be used by all of the other projects, as well as the main DataModel classes.
Also, common functionalities (such as security or DataModel services) are found in this project.
### Required Modules
##### edci-viewer-common
This module contains classes used by all the other modules, mostly Constants and DTOs that are used across the application.
##### edci-viewer-service
This module contains most of the business logic, therefore most of the back-end core functions of the app reside here.
##### edci-viewer-web
This module contains the angular folder with the front-end code of the application, and the index.jsp from which is served.
Also, here reside all of the configuration files, as well as external or downloadable resources.
It also contains all the configuration properties into the resource folder:
* viewer - General configurations, such as datasources, url paths, remote endpoints...
* security - Session and OIDC configurations

As the swagger codegen maven plugin resides in this module, compilation of this project will result in the typescript swagger files being recreated. 
##### edci-viewer-web-rest
This module contains all of the Rest API endpoints used by the frontend angular components, thus all of the operations externally available reside here.
Also, the swagger.json file is generated based on the annotations in this module, and during it's compilation time. 
##### edci-viewer-web-rest-swagger
This module contains all necessary files for swagger typescript generation, including mustache templates and custom Code Generators.
### Extra Modules
##### edci-viewer-web-swagger-ui
This module provies an automatic system of API documentation, it will take the swagger.json generated and copy it into this project, which will produce an artifact with the API documentation that can be deployed. 
##### edci-viewer-ear
This module is only responsible of packaging all of the modules into an ear file for deployment, thus no code resides here.
## Configuration
The EDCI Viewer needs to be configured for the particular environment where is going to be deployed, this includes both technical configuration (such as log levels, api path...) as well as custom app configurations (OIDC security, associated wallet...) 
All of the configuration is stored in files inside the projects and it consists of value/key pairs. It is included in the artifact at compilation time.
Because of this, the configuration must be set prior to compiling the project. 
##### Profiles
The project can be configured and compiled with a variety of profiles, which can be used to provide a set of configurations for multiple environments.
The profile that is being used is specified at compilation time for both front-end and back-end, and must be the same for both compilation steps.
The current supported profiles are:
* default: this is the default profile used for development, which is the same as local-tomcat.
* local-tomcat: profile used for local development, the artifact can be deployed in a local tomcat 9+ environment.
* qa-tomcat: this profile is used for a QA environment, mostly for integration testing.
* acceptance: used mostly for UAT tests in a controlled environment.
* production: the profile used for live environment.
## Configuration
##### Profiles
The project can be configured and compiled with a variety of profiles, which can be used to provide a set of configurations for multiple environments.
The profile that is being used is specified at compilation time for both front-end and back-end, and must be the same for both compilation steps.
The current supported profiles are:
* default: this is the default profile used for development, which is the same as local-tomcat.
* local-tomcat: profile used for local development, the artifact can be deployed in a local tomcat 9+ environment.
* qa-tomcat: this profile is used for a QA environment, mostly for integration testing.
* acceptance: used mostly for UAT tests in a controlled environment.
* production: the profile used for live environment.

##### Compile-time configuration files 
Some of the properties that reside in the project, are used in XML files that are included in the artifact are compile time. 
Because of this, changing any of these properties requires a recompilation of the project.

The backend files containing properties that can only be changed in compile time  reside in the edci-viewer-web module, inside the config folder, here you will find the following folders containing configuration:
* cache: contains ehcache.xml file.
* viewer: contains basic properties for all the profiles.

Also, you will find an ext folder with an example of the files required as run-time configuration properties.

For front-end properties, all of the configurations can be found under the /environments folder, with environment.[profile].ts naming. 
##### Run-time configuration files
A good portion of the properties reside outside of the project, by default, the application expects to found this files in the file:${catalina.base}/conf/edci/viewer/ folder.
This path can be changed at the EDCIViewerConfig class. The expected files are:
* viewer.properties
* viewer_dss.properties
* mail.properties
* proxy.properties
* security.properties
* viewer_front.properties
 
These properties can be changed without recompiling the EDCI Viewer, but keep in mind that the application server must be restarted for the changes to take place.
## Build and deployment 
#### Requirements:
##### Compilation requirements
In order to compile the EDCI Viewer you will need
* Maven
* node.js and npm - note that NodeJS 10.XX is required
##### Deployment requirements
* Application Server - the development has been done in tomcat
* Internet accesss - the app requieres access to external resources, a proxy can be configured
* AppServer provided Jar Dependencies in the app server libraries directory:
  (Jars can be found in: edci\edci-viewer\edci-viewer-web\src\main\resources-unfiltered\lib\ext)
    +   apache-log4j-extras-1.2.17.jar
    +   bcprov-jdk15on-1.68.jar
    +   javaee-api-7.0.jar
    +   javax.mail-1.5.0.jar
    +   jstl-1.2.jar
    +   log4j-log4j-1.2.17.jar
    
If you are trying to deploy the EDCI Viewer project in a tomcat application server, you can check our [tomcat configuration](../configuration/documentation/tomcatConfiguration.md) page.
##### GIT
The source code is stored in this GIT repository: [european-commission-europass/europass-digital-credentials](https://github.com/european-commission-europass/europass-digital-credentials) It can be downloaded easily is by cloning it into it's desired folder with this command:

```
git clone https://github.com/european-commission-europass/europass-digital-credentials.git
```
#### Building process:
#### Swagger file generation
The Project structure requires that swagger frontend files are generated before compiling the backend. This allows for the frontend api-related code to be autogenerated in an automatized way every time that back end is compiled.

To do so, a maven plugin in edci-viewer-web-rest-swagger module (com.github.kongchen.swagger-maven-plugin) will generate a swagger.json file based on the annotated endpoints in the edci-viewer-web-rest module.

After that, the module edci-viewer-web will generate the swagger frontend files, using once again a maven plugin (io.swagger.swagger-codegen-maven-plugin). Keep in mind that this requires the previous creation of an empty "dist" directory in edci-viewer-web/src/main/angular/dist.
 
This process is only required to do once if no changes in the code are applied. If the edci-viewer-web-rest endpoints where to be changed, then this generation has to be done again before the frontend code for api calls is available.
Because of this, the best way of doing a first generation of the swagger files, is to simply create an empty "dist" folder in the specified path, and then run a backend compilation, even if it means that it has to be compiled again after the front-end code has also been compiled.
#### External dependencies intallation
The EDCI Viewer makes uses of external libraries that are not available in public repositories. Because of this, external dependencies are included at /edci/configuration/external-libs folder.
Before building the EDCI Viewer, these dependencies must be installed into the local maven repository, this can easily be done by executing the following commands:
 
 * mvn install:install-file -DgroupId=eu.europa.ec.digit.uxatec.eui -DartifactId=eui-angular2-servlet -Dpackaging=jar -Dversion=1.0.0 -Dfile=configuration/external-libs/eui-angular2-servlet-1.0.0.jar

##### Angular
The front-end application residing in edci-Viewer-web must be compiled prior to the backend, to do so, just use the standard npm install command to update your node_modules directory, and after that use npm run build-[profile] scripts. Configuration for these scripts can be found in package.json file.
Keep in mind that any change to the API that would change swagger definition, requires a previous compilation of the back-end code.
``` 
npm install
npm run build-[profile]
```
##### Maven
This project uses maven to manage the projects build. Keep in mind that some of the edci-dss-utils project artifacts must be downloaded from specific respositories, [Here](../configuration/documentation/mvn_settings.xml) is an example of a basic maven configuration that can be used as a template (keep in mind that some values must be changed). 
Once the maven settings are ready, just execute the usual maven command using parent pom.xml with the desired profile, if no one is selected, default is used.
``` 
mvn clean install -P [profile]
```
##Core functions
This section describes the core functions of the Viewer app, which allow the application visualize, verify and share credentials.

### Credential and Verifiable presentation mappers
The main function of the EDCI Viewer back-end is to get and parse the XML information of the credentials and verifiable presentations and serve it to the 
front-end in a way that is easily read by the angular front-end. This means that all of the XMLs will be parsed and mapped to java objects, and then to JSON. This is done through Mapstruct classes, 
from which main class involved is EuropassCredentialPresentationMapper, which needs to be modified if any new field is to be send to the frontend.
There are two mains forms of mapping and sending the information of the credentials to the front-end, which are available for any visualization method 

### Visualize uploaded XML
When uploading a credential or verifiable presentation XML, the viewer will parse the file and create a java representation of the same, 
which will in turn be sent in JSON format to the viewer front-end, if the credential uploaded is not readable, a 400 http status code is returned.

### Verify uploaded XML
When uploading an credential or verifiable presentation XML, the viewer will send the XML to the associated wallet, and then send the response to the front-end.
The verification does not take place in the viewer, and because of this noi verification logic resides in this project.

### Visualize wallet-stored XML
The viewer provides the possibility to visualize a credential or verifiable presentation for which no XML file is present but is stored in a wallet.
To do this, the user needs to access a particular URL in the application with  /#/view/[userId]/[xmlUUid] structure. Also, the credential must be stored in the viewer's associated wallet. 
Also, both of the systems need to be using the same Identity Provider.
The most usual way of a user getting this view URL is either by a wallet email or from a software that can fetch this information in the associated wallet.
### Verify wallet-stored XML
The viewer provides the possibility to verify a credential or verifiable presentation for which no XML file is present, but is stored in a wallet.
When doing this, the viewer will send a request to the wallet for the validation, and the results will be directly returned to viewer, this means that the validation is not done in the viewer, and the XML actually is never in the viewer.
For this functionality to work, both of the systems need to be using the same Identity provider. 

### Exporting Credentials
The viewer provides two ways to download credentials that are being visualized: XML and PDF. 

When exporting an asset, it does not matter if it corresponds to a credential or to a verifiable presntation, it will always be exported as a verifiable presentation, either as XML or PDF format. 
 
### Create sharelinks
A credential that is stored in a wallet can be temporarily shared by the creation of a link with a similar structure to the wallet-stored view URls. This allows to any other user to view the credential during the time that the sharelink is valid.

The EDCI Wallet provides an API to manage this sharelinks, and the EDCI Viewer provides a bridge to the creating sharelink process, meaning that the user can use a tool in the viewer front-end, which will be processed in the back-end and sent to the associated wallet in order to create a sharelink, the response will be sent back to the viewer.
### Security constraints and Identity propagation
Any operation described in this section that involves an operation over a wallet-stored credential requires that both the viewer and the associated wallet use the same OIDC Identity provider, and that the identity is propagated between the two applications.

When requesting any information from the associated wallet, the viewer will first ask for a new access token to the IDP by using the previously stored refresh token. For this purpose, two grant_types can be used: urn:ietf:params:oauth:grant-type:token-exchange or the refresh_token, the one that is used is defined through the oidc.use.token.exchange property in the security_[profile].properties file.    
## External dependencies
The EDCI Viewer project has external dependencies which are mandatory, and must be supplied in one way or another, otherwise core functions will be unusable. This means that the EDCI Viewer requires 
#### OIDC Identity Provider
Although a mock user can be enabled por testing purposes, an OIDC Identity provider will be required for the credential builder to work properly. All of the required configurations for this can be found in the security_[profile].properties file.
All fo this is implemented using the Authentication (Basic) flow. For implementations, spring-security and [mitreId](https://github.com/mitreid-connect/) are used. If you want to know more about it, an in-depth explanation on the current configuration can be found [here](https://steps.everis.com/confluence/display/EDCI/EuLogin+OIDC+integration).
##Interest Links
* Developer Playground: A developer playground which allows users to build and issue credentials without the need for an e-seal is available [here](https://webgate.acceptance.ec.europa.eu/europass/edci-Viewer/). 
## License
[European Union Public Licence v1.2](https://github.com/teamdigitale/licenses/blob/master/EUPL-1.2)

