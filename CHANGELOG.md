# Changelog
All notable changes to this project will be documented in this file.

## [1.4.1] - 2021-10-18

### Added
#### Properties
##### conf/edci/issuer/issuer.properties
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
##### conf/edci/issuer/issuer_front.properties    
- new property enabledLocalSealing=false.  
    - See local sealing configuration
##### conf/edci/issuer/wallet.properties    
- new property png.download.url=https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/office/generate/png.
    - Defines the service used to convert HTML templates to a PNG image
##### conf/edci/issuer/viewer.properties    
- new property html.base.href=https://[host]/europass/edci-viewer.
    - Base url for the viewer

### Changed
#### Libraries
- server lib/ext libraries: bcprov-jdk15on-1.62.jar has been replaced with bcprov-jdk15on-1.68.jar
#### Properties
##### conf/edci/issuer/issuer.properties
- updated property dss.signature.level=XAdES-BASELINE-LT
### Deprecated
- N/A
### Removed
- N/A
### Fixed
- N/A
### Security
- N/A