# Upload credentials in  XML format

In the case of having one or more generated Europass Digital Credentials in XML format, they can be uploaded in the issuer in a single file at a time. To achieve this, a new XML file with a list of credentials has to be created.

This file have a root element: europassCredentials that contains a list of all the credentials that will will be updated. the XSD definition can be found [here](Link to XSD)

Here's an example of an upload XML file containing two credentials ready to upload:

``` xml
<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<europassCredentials xmlns="http://data.europa.eu/snbUpload"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://data.europa.eu/snbUpload http://publications.europa.eu/resource/schema/credential/genericschema_upload_1.0.xsd">
    
    <europassCredential xsi:schemaLocation="http://data.europa.eu/snb http://publications.europa.eu/resource/schema/credential/genericschema.xsd" xmlns="http://data.europa.eu/snb" 
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:cred="http://data.europa.eu/europass/model/credentials/w3c#" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" 
                        xsdVersion="0.10.0" cred:id="urn:credential:114778c4-6chr-4d33-adg3-g5bak25gdf64">
                        [...]
    </europassCredential>
    <europassCredential xsi:schemaLocation="http://data.europa.eu/snb http://publications.europa.eu/resource/schema/credential/genericschema.xsd" xmlns="http://data.europa.eu/snb" 
                        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:cred="http://data.europa.eu/europass/model/credentials/w3c#" xmlns:ds="http://www.w3.org/2000/09/xmldsig#" 
                        xsdVersion="0.10.0" cred:id="urn:credential:244858c4-8ced-4d31-ace5-0fbad25edf25">
                        [...]
    </europassCredential>
</europassCredentials>
``` 
