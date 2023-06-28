# eSeal-core
[![license: EUPL](licence-EUPL%201.2-brightgreen.svg)](https://github.com/teamdigitale/licenses/blob/master/EUPL-1.2)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg)](https://github.com/RichardLitt/standard-readme)

The eSeal-core module is a Java11 library using Spring MVC 5.2.20 and [DSS 5.10.1](https://ec.europa.eu/digital-building-blocks/wikis/display/DIGITAL/Digital+Signature+Service+-++DSS) that is used to seal and verify files following the eIDAS regulation and the related standards.

This library is built to be used with the EDCI ecosystem projects, and only supports the JAdES signature/validation with a limited subset of options.

Current latest version is 1.6.0-RELEASE

##Usage

For usage, download the source code and include by normal means, for instance using maven:

        <dependency>
           <groupId>eu.europa.ec.empl.edci</groupId>
           <artifactId>edci-eseal-core</artifactId>
           <version>1.6.0-RELEASE</version>
        </dependency>
        
It is also available at repository:


The primary entry points for usage are the ESealSignService and ESealValidationService classes, which contain the methods necessary for signature and validation.
###Seal

Sealing with the edci-eseal-core library can be done with two different methods, **local sealing**, that involves using a certificate that is physically stored in the server, or **nexu sealing**, that is done in conjucton with a frontend capable of communicating with the [nexu](https://github.com/nowina-solutions/nexu) browser signing tool.

In any case, the file to be signed must physically exist and be accessible to the application.

The main service used for signing is *EsealSignService*
  
####Local sealing

For Local Sealing to work, a Physical PKCS12 file must exist and be accessible from the application.
The simple way to use it is:

       DSSDocument signedDocument = eSealSignService.signDocument(jsonFilePath, certPath, certPassword);
       signedDocument.save(signedJSONoutputPath);

In this example, the json at "jsonFilePath" will be replaced by the signed version, using the certificate at certPath and the certPassword to open it.

####Nexu sealing

For Signing with nexu, it is a multi-step process, and requires an integration with a Frontend tool that can integrate with it:

First, the frontend must use the /rest/certificates/ to get the certificate info from nexu.

With the certificate information, a signatureBytesDTO can be generated using the method from ESealSignService:

       return eSealSignService.getSignatureBytes(fileAbsolutePathList, certificate, certificateChain);

After that, the frontend must call the /rest/sign method of the nexu using the data generated at the SignatureBytesDTO to generate a SignatureNexuDTO with the nexu response.

With the SignatureNexuDTO, the application can finally call the sign method, which will produce the signed json.
        
       DssDocument signedDocument = eSealSignService.signDocument(filePath,signatureNexuDTO);
       signedDocument.save(signedJSONoutputPath)
       

###Verification

For the Verification process, the json file to be validated can either exist on the host of the application using edci-eseal-core or  alternatively a validation can be run directly on the file's bytes.

The Main 2 methods of verifying a credential are:

    Reports report1 = eSealValidationService.validateJson(jsonBytes);
    Reports report2 = esealValidationService.validateJson(jsonPath);

The validation returns a Reports object that contains all data regarding the validation.

The verification service also contains an utility method to check if a document is signed:

    return eSealValidationService.isSigned(jsonBytes);
    
##Configuration

All of the properties for the edci-eseal-core contain a default value and should not be changed, but there are 4 properties that must be indicated.  

###eSeal.properties

The properties are defined by the eSeal.properties file, which is expected to be at "${catalina.base}/conf/edci/eSeal.properties" path.

This file is mandatory and an example can be found in configuration/properties/example/Seal.properties

###Mandatory fields

####eseal.datasource.db.driverClassName

The driver for the database, for instance com.mysql.cj.jdbc.Driver

####eseal.datasource.db.url

The url of the database, for instance jdbc:mysql://mysqldb:3306/edci

####eseal.datasource.db.username

The database username

####eseal.datasource.db.password

The database password

###Optional fields

####eseal.sealing.payload.base64

Defines if the payload will be encoded in base64
Default value: false

####eseal.job.online.refresh

Defines if an online refresh for OCSP and CRL sources should be done before any validation.
Default value: false

####eseal.advanced.qseal.only

Defines if only esals with advanced/qseal level are permitted. 
Default value: true

###DB Caches and other configurations needed

CRL and OSCP source results are cached inside the database, meaning that a connection to either Oracle or MySQL databases is mandatory.

If a Database that is not either Oracle or MySQL must be added, then EdciJdbCAcheCRLSource must be modified accordingly to support it.
