# EDCI - Europass Digital Credential Interoperability
[![license: EUPL](licence-EUPL%201.2-brightgreen.svg)](https://github.com/teamdigitale/licenses/blob/master/EUPL-1.2)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg)](https://github.com/RichardLitt/standard-readme)

The Europass Digital Credential Infrastructure is a set of standards, services and software which allows institutions to issue digital, tamper-proof qualifications and other learning credentials within the European Education Area. With it learners, employers, education and training providers and other authorised bodies have a simple and trustworthy way of verifying the validly and authenticity of digital credentials.

## Components

### Wallet

The EDCI wallet is a backend eu.europa.ec.empl.edci.dss.service that receives credentials from the issuer, stores them and serves them to wallet front-ends such as the Europass or the EDCI Viewer. It also serves 'share' requests to other systems.

More information: [Wallet](edci-wallet/README.md)

### Issuer

The EDCI Issuer is a web-app provided for any institution that does not wish to develop/install/purchase their own issuing software. It implements the EDCI Credential Standard. The EDCI Issuer is  capable of taking structured data about awarded credentials, and issuing thousands of compliant digitally-signed credentials.

More information: [Issuer](edci-issuer/README.md)

### Viewer

The EDCI Viewer is used to view a Europass Digital Credential received by e-mai or uploaded to a wallet. The credential contains rich data about your accomplishments. The tool will allow you to check the authenticity and validity of the credential, as well as print or download it as pdf.

More information: [Viewer](edci-viewer/README.md)