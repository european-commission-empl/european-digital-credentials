# Changelog
All notable changes to this project will be documented in this file.

## [1.4.1] - 2021-10-18

### Properties added
#### conf/edci/issuer/issuer.properties
- new users.properties has been added to configure basic authentication for local sealing.   
  - See local sealing configuration + [Spring - Common Authentication Services](https://docs.spring.io/spring-security/site/docs/2.0.x/reference/html/authentication-common-auth-services.html) for more info
- new property dss.cert.path=${catalina.home}/conf/edci/issuer/cert/certificate.pfx.  
  - See local sealing configuration
- new property sealing.api.send.temporal=true.   
  - See local sealing configuration
- new property tmp.data.public.credential.folder=credentials_public/.   
  - See local sealing configuration
- new property png.download.url=https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/office/generate/png.
    - Defines the service used to convert HTMl templates to a PNG image
#### conf/edci/issuer/issuer_front.properties    
- new property enabledLocalSealing=false.  
    - See local sealing configuration
#### conf/edci/wallet/wallet.properties    
- new property png.download.url=https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/office/generate/png.
    - Defines the service used to convert HTML templates to a PNG image
#### conf/edci/viewer/viewer.properties    
- new property html.base.href=https://[host]/europass/edci-viewer.
    - Base url for the viewer

### Properties modified
#### conf/edci/issuer/issuer.properties
- updated property dss.signature.level=XAdES-BASELINE-LT

### Server libraries modified
- server lib/ext libraries: bcprov-jdk15on-1.62.jar has been replaced with bcprov-jdk15on-1.68.jar


## [1.5.0] - 2022-04-XX

### Docker
- Keycloak image is now 17.0.0 - (/auth sections in all propierties should be removed ie:http://host.docker.internal:9000/auth/realms/edci -> http://host.docker.internal:9000/realms/edci)
- docker-compose has been modified for keycloak part, new section in dev with command start-dev (switch to start for prod mode, requires SSL):

``` yml
  keycloak:
    image: quay.io/keycloak/keycloak:latest
    container_name: keycloak
    command: start-dev
    environment:
      - KEYCLOAK_ADMIN=adminuser
      - KEYCLOAK_ADMIN_PASSWORD=adminpassword
      - KC_DB=mysql
      - KC_DB_URL=jdbc:mysql://mysqldb:3306/keycloak
      - KC_DB_USERNAME=keycloakUsername
      - KC_DB_PASSWORD=keyclockPassword
      - KC_FEATURES=token-exchange,admin-fine-grained-authz
    ports:
      - "9000:8080"
    depends_on:
      - mysqldb 
```

The following properties from conf/edci/issuer/security.properties need to be modified if using an older keycloak version (the former start: "host.docker.internal:9000/auth/" has been replaced with "host.docker.internal:9000") 
* oidc.idp.url=http://host.docker.internal:9000/realms/edci
* oidc.idp.end.session.url=http://host.docker.internal:9000/realms/edci/protocol/openid-connect/logout
* oidc.auth.request.url=http://host.docker.internal:9000/realms/edci/protocol/openid-connect/auth
* oidc.idp.introspection.url=http://host.docker.internal:9000/realms/edci/protocol/openid-connect/token/introspect
  
### Properties added
#### conf/edci/issuer/issuer.properties
- new property qms.qmsaccreditation.uri=https://europa.eu/europass/eportfolio/api/learning-opportunities/accreditations/{accr_id} 
  - Defines the endpoint that will be called to retrieve accreditations by ID. (Used in future versions)
- new property max.consecutive.errors.batch.sealing=5
  - Defines the maximum number of consecutive errors that can happen when batch seal via API. If this number is exceeded, the operation stops, informing of the error.
#### conf/edci/issuer/issuer_front.properties
- new property maxUploadSizeMB=15
  - Defines the maximum size in MB of the files uploaded in the issuer.
##### conf/edci/viewer/viewer.properties
- new property png.download.url=https://europa.eu/europass/eportfolio/api/office/generate/png
  - Defines the service used to convert HTML templates to a PNG image
- new property displayDownloadOriginal=true 
  - Enables downloading the credential in xml from the viewer
#### conf/edci/viewer/viewer_front.properties
- new property displayDownloadOriginal=true/false
 - Defines if the "Download Original XML" button should be shown
- new property downloadSharedCredentialUrl=/v1/sharelinks/{shareHash}/presentation
 - Defines the url of the endpoint used to download the original credential in case it is a sharelink, {shareHash} will be substituted by the shareHash on calling the endpoint.
- new property downloadCredentialUrl=/v1/credentials/{walletAddress}/verifiable
 - Defines the url of the endpoint used to download the original credential, {walletAddress} will be substituted by the walletAddress on calling the endpoint.
##### conf/edci/wallet/wallet.properties
- new property store.diploma.database=false
  - If true, the credential's diploma will sotred in the database when generated the first time.
- new property qms.qmsaccreditation.uri=https://europa.eu/europass/eportfolio/api/learning-opportunities/accreditations/{accr_id}
  - Defines the endpoint that will be called to retrieve accreditations by ID. (Used in future versions)
  
### Properties modified
#### conf/edci/issuer/security.properties
- updated property oidc.invalid.session.url=/#/home
  - It needs to be replaced with the value: "/#/home"
#### conf/edci/viewer/security.properties
- updated property oidc.invalid.session.url=/#/home
  - It needs to be replaced with the value: "/#/home"
#### conf/edci/wallet/security.properties
- updated property oidc.invalid.session.url=/#/home
  - It needs to be replaced with the value: "/#/home"

### Properties removed
#### conf/edci/issuer/security.properties
- removed property oidc.anonymous.pattern
  - Unused property

### Server libraries removed
- server "log4j1.X.jar", "slf4j-log4j1.X.jar" and "apache-log-extras-1.X.jar" had been removed
  - new log4j version is not anymore an external lib, making it easier to have separate apps
  
### Profiles modified
The available compilation profiles have been modified. This ones will be the current ones:

- prod (replacing "prod" and "acc")
- dev (replacing "local-tomcat" and "qa-tomcat")
- docker (new, with the same parameters as "dev" for now)

Due to this change and the usage of the configuration files in the server's conf folder, the environment.[profileX].properties in angular had been unified into a single file: environment.back

### XML Schemas added
The following XSD schemas has been generated. All new issued credentials will make use of this new schemas:

* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/genericschema_1.2.xsd
* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/accreditationschema_1.2.xsd
* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/diplomasupplementschema_1.2.xsd

* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/mandatedschema_accred_1.2.xsd
* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/mandatedschema_dp_1.2.xsd
* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/mandatedschema_generic_1.2.xsd

* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/sharedschema_accred_1.2.xsd
* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/sharedschema_dp_1.2.xsd
* https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD/flatten/sharedschema_generic_1.2.xsd

### New diploma template

Diplomas can be build using Thymeleaf. See [diploma documentation](./configuration/documentation/diplomaWildcards.md)