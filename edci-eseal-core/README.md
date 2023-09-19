# eSeal-core
[![license: EUPL](licence-EUPL%201.2-brightgreen.svg)](https://github.com/teamdigitale/licenses/blob/master/EUPL-1.2)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg)](https://github.com/RichardLitt/standard-readme)

Digital signing and validation of the JSON-LD credentials in the EDC ecosystem is done through this module, which uses the [Digital Signature Service](https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/doc/dss-documentation.html) library that follows the [eIDAS](https://en.wikipedia.org/wiki/EIDAS) regulation to do so. 

The eSeal-core is a Java11 module, which uses Spring MVC 5.2.20 and DSS 5.11.1 and is built to be used with the EDC ecosystem projects. Note that not all of the operations that the DSS can perform are implemented by the eSeal-core module, only those relevant to the EDC ecosystem.

This module provides support mainly for 3 operations:

- **Signing:** Allows signing JSON-LD credentials using JAdES with both nexu and API.
- **Verification:** Allows verifying JSON-LD credentials and producing a DSS Reports object. The level of qualification expected can be configured through properties.
- **Extension:** Allows extending both JSON-LD and XML credentials.

## Signature
In EDC, the [Verifiable Credentials](https://www.w3.org/TR/vc-data-model/) are signed using [JAdES](https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/doc/dss-documentation.html#_jades_signature_jws) with the JSON serialization, which is an extension of [JWS](https://datatracker.ietf.org/doc/html/rfc7515). Compact(JWT) and flattened serializations are not currently supported.

The credentials files are digested using SHA256 and RSA by default, the packaging used is "enveloping" and the payload is not encoded. Changing any of these configuration is not supported, the resulting format has the structure below.

```json
    {
      "payload" : {
        //Unencoded Verfiable credential JSON-LD
      },
      signatures : [
        protected : //The encoded JWS Protected header
        header : {
          etsiU : [
            //The encoded etsiU components
          ],
          signature : //The JWS signature
        }
      ]
    }
```

The signature level is defined by the property eseal.sealing.signature.level and it is JAdES-BASELINE-LT by default, for more detailed information about the signature levels visit the [relevant section in the DSS Documentation](https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/doc/dss-documentation.html#SignatureClasses).

All of the signing certificates used must belong to one of the qualifications configured at the *eseal.certificate.qualification.allowed* property, if a signature attempt is made with a certificate that does not match the configured qualifications, an error is thrown.

### Signature implementation
 
The main entry point for DSS signature methods in the eSeal-core containing the operations implemented within the EDC ecosystem is the ESealSignService class, the main implementations are found in the following methods:

- **signDocument(String toSignDocumentPath, String certPath, String password):** Signs a document using the local certificate 
- **extendDocumentSignature(byte[] signedDocument):** extends the signature of a JSON-LD credential.
- **extendDocumentSignatureXML(byte[] signedDocument):** extends the signature of a XML credential.
- **getSignatureBytes(String fileAbsolutePathList, String certificate, List<String> certificateChain):** Generates the signature bytes for the provided file with the provided certificate, this is used in combination with NexU software for the first step of the sealing operation.
- **signDocument(String filePath, SignatureNexuDTO signatureNexuDTO):** Signs document after generating the SignatureNexuDTO using the signatureBytes and Nexu.

These methods cover the EDC signature requirements for both NEXU and local API sealing.

#### JAdES

The EDC implementation for signing JAdES operations can be found mainly in the JadesSignService class, this implementation has capabilities for both signing and extending signature and the main methods of this service are:

- **signDocument(String toSignDocumentPath, String certPath, String password):** Signs a document using the configured local certificate.
- **extendDocumentSignature(DSSDocument toSignDocument):**  Extends a signature of a signed DSSDocument.
- **signTokenDocument(DSSDocument toSignDocument, SignatureTokenConnection signingToken):** Signs a document using the first key found in SignatureTokenConnection, used in local certificate sealing.

These methods cover the EDC signature requirements for JAdES operations that are not covered within the main entry point at ESealSignService.

#### XAdES 

The EDC implementation for XAdES operations can be found mainly in XadesSignSevice class. Currently, EDC does not support XAdES signing anymore and only supports operations regarding signature extension. The main method of this service is:

- **extendDocumentSignature(DSSDocument toSignDocument):** Extends the signature of a signed XML file.

XAdES/XML support in the eSeal-core module is limited, and the signing operation is not supported.

#### Certificate information

During Sealing, operations may be performed over a certificate for various reasons, the eSeal-core module provides an EsealCertificateService class which contains the following implementations:

- **Extract information:** Extract information from a certificate, to be added as data or taken into account for buisness logic.
- **Get Certificate Signature token:** Used in the local API sealing process.
  
 The certificate service can be used without the need of signing a document to access certificate utilities if needed.

## Validation

In EDC, all of the credentials are expected to be signed using either [JAdES](https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/doc/dss-documentation.html#_jades_signature_jws) or [XAdES](https://ec.europa.eu/digital-building-blocks/DSS/webapp-demo/doc/dss-documentation.html#_xades_xml), no other signature methods or cryptographic suites are supported even if described at [JWA RFC](https://www.rfc-editor.org/rfc/rfc7518).

The eSeal-core module can also be configured to expect certain signature levels and only consider valid a signature that is not only passing DSS validation but also has the expected signature level.  

### Validation implementation

The main entry point for DSS validation methods in the eSeal-core module is the ESealValidationService class, which contains the implemented operations within the EDC ecosystem. Main implementation methods are as follows:

- **isSigned(byte[], DataLoader), Multiple overloads:** Checks if a file is already signed.
- **ValidateJson(byte[], DataLoader), Multiple overloads:** Validates the signature of a JSON-LD file, returns a DSS report with the results.
- **ValidateXML(byte[], DataLoader), Multiple overloads:** Validates the signature of a XML file.

The default expected Signature level for validation is BASELINE-LTA JSON-LD and all levels for XML files, and can be configured through the **eseal.sealing.signature.level.list.json** and **eseal.sealing.signature.level.list.xml properties**.

#### JAdES

The EDC implementation for JAdES validation operations can be found in JadesValidationService class, currently the only operation supported is the validation of JSON-LD files. The result of the validation is returned in a DSS Reports object.

#### XAdES 

The EDC implementation for XAdES validation operations can be found in XadesValidationSErvice class, once again the only operation supported is the validation of XML files. The result of the validation is returned in a DSS Reports object.

## Usage

### Installation
For usage, download the source code and include using any dependency management tool, maven is recommended.

        <dependency>
           <groupId>eu.europa.ec.empl.edci</groupId>
           <artifactId>edci-eseal-core</artifactId>
           <version>2.1.0-RELEASE</version>
        </dependency>
        

As previously mentioned, the primary entry points for usage are the **ESealSignService** and **ESealValidationService** classes, which contain the methods necessary for signature and validation.

### Sealing

Sealing with the eSeal-core module can be done with two different methods, **local sealing**, that involves using a certificate that is physically stored in the server, or **nexu sealing**, that is done in conjucton with a frontend capable of communicating with the [nexu](https://github.com/nowina-solutions/nexu) browser signing tool.

In any case, the file to be signed must physically exist and be accessible to the application.

The main service used for signing is *EsealSignService* and it supports only JSON-LD format, as previously specified.
  
#### Local sealing

The local sealing functionality is used to seal using a local certificate and without the nexu browser signing tool. For the local sealing to work, a certificate file must exist, be accessible from the application and have the required minimum qualification configured in the properties.
The simplest way to use it is:

       DSSDocument signedDocument = eSealSignService.signDocument(jsonFilePath, certPath, certPassword);
       signedDocument.save(signedJSONoutputPath);

In this example, the json at "jsonFilePath" will be replaced by the signed version, using the certificate at certPath and the certPassword to open it.

#### Nexu sealing

The nexu signing is a multi-step process, and requires an integration with a Frontend tool integrated with the [nexu browser tool](https://github.com/nowina-solutions/nexu).

The steps for performing a sealing using the nexu tool, in conjunction with a frontend capable of doing so, are as follows:

- First, the frontend must use the /rest/certificates/ to get the certificate info from nexu.

- With the certificate information, a signatureBytesDTO can be generated using the *getSignatureBytes* method from *ESealSignService*:

       return eSealSignService.getSignatureBytes(fileAbsolutePathList, certificate, certificateChain);

- After that, the frontend must call the /rest/sign method of the nexu using the data generated at the SignatureBytesDTO to generate a SignatureNexuDTO with the nexu response.

- With the SignatureNexuDTO, the application can finally call the sign method, which will produce the signed json.
        
       DssDocument signedDocument = eSealSignService.signDocument(filePath,signatureNexuDTO);
       signedDocument.save(signedJSONoutputPath)
       
### Verification

Verification in the eseal-core module consists of mainly 3 core functions, generating a verification report from a JSON-LD, generating a verification report from an XML and checking if a JSON-LD credential is signed or not.
The main entry point for these two operations is the *EsealValidationService* class, and the main methods are:

- **isSigned(byte[] jsonBytes)**: Checks if a JSON-LD credential is signed
- **validateJson(byte[] jsonBytes)**: generates a validation report for a JSON-LD credential.
- **validateXml(byte[] xmlBytes)**: generates a validation report for an XML credential.

All of these methods have multiple overloads to be used either with the file bytes or a path of a file residing in the system.

#### JAdES

The JAdES implementation of the validation service can be found at the *JadesValidationService* class. It contains the validateJson implementation but does not contain the isSigned implementation, which resides in the *EsealValidationService* class.
It also contains an extra **getCredentialOrPayload** to extract the payload of a JSON-LD credential for parsing.

#### XAdES

The XAdES implementation of the validation service can be found at the *XadesValidationService* class. It contains the XML implementation for the generation of the validation reports with the validateXML methods.

## Configuration


All of the properties for the edci-eseal-core contain a default value and should not be changed, but there are 4 properties that must be indicated.  

### eSeal.properties

The properties are defined by the eSeal.properties file, which is expected to be at "${catalina.base}/conf/edci/eSeal.properties" path.

This file is mandatory and an example can be found in configuration/properties/example/Seal.properties

### Mandatory fields

- **eseal.datasource.db.driverClassName:** The driver for the database, for instance com.mysql.cj.jdbc.Driver
- **eseal.datasource.db.url:** The url of the database, for instance jdbc:mysql://mysqldb:3306/edci
- **eseal.datasource.db.target-database:** the database type in String form, for instance MySQL
- **eseal.datasource.db.username:** The database username
- **eseal.datasource.db.password:** The database password

### Optional fields

- **eseal.tsp.server:** the TSP server, default is http://dss.nowina.lu/pki-factory/tsa/good-tsa
- **eseal.certificate.qualification.allowed:** a comma separated list of qualifications allowed, by default QCERT_FOR_ESEAL_QSCD,QCERT_FOR_ESEAL. See DSS CertificateQualification class for a list of all available certifications.
- **eseal.advanced.qseal.only:** if set to true, only eseals of advanced level and superior will be able to sign. true by default.
- **eseal.sealing.signature.level.list.json:** a comma separated list of the signature levels allowed for JSON-LD, default is JAdES-BASELINE-LTA. Check the DSS *SignatureLevel* class for the full list of the signature levels.
- **eseal.sealing.signature.level.list.xml properties:** a comma separated list of the signature levels allowed for XML, default is all. Check the DSS *SignatureLevel* class for the full list of the signature levels.

### DB Caches and other configurations needed

CRL and OSCP source results are cached inside the database, meaning that a connection to either Oracle or MySQL databases is mandatory.

If a Database that is not either Oracle or MySQL must be added, then EdciJdbCAcheCRLSource must be modified accordingly to support it.
