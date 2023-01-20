# Diploma template

## Overview
Apart from the default white and blue Europass Digital Credential (EDC) template, a Verifiable Credential can contain its specific version of 
the EDC to support a customised look and feel. The EDCI Viewer displays an image gneerated from an HTML representation of the credential, so that is why this 
custom credential will be built in HTML + CSS (no JS) + Wildcards or Thymleaf Standard dialect inside the EDC's XML structure, and will use wildcards to choose EDC content 
(credential subject's name, titles, descriptions, etc.) and to show EDC non-related translatable labels ("certifies that", "has achieved", etc.). 
Full examples is showcased in [Annex I](#Annex I) and [Annex II](#Annex II).

## Diploma types
Currently, three types of templates can be built:
* Single page diploma
* Multi page diploma
* Diploma supplement

## Verifiable Credential standard and customising attributes
In the first Verifiable Credential XSD standard (see [Generic schema XSD](https://github.com/european-commission-europass/Europass-Learning-Model/tree/master/Credentials/XSD)), there is a specific section for managing credential customisation:

* `<background/>`: (See MediaObject definition in Generic schema). For a proper display, image used must have a din A4 ratio.
* `<html/>`: This html portion will be included in the EDCI Viewer in a div that ensures din A4 ratio and the background image set as background.
* `<template/>`: This Thymleaf Standard dialect portion will be included in the EDCI Viewer in a div that ensures din A4 ratio and the background image set as background.
* `<labels/>`: Used to define a set of multilanguage labels.

Important: The provided HTML or templates will be sanitised (automatic removal) to prevent Cross site scripting when uploaded. 
See the tags and attributes allowed in the [Annex III](#Annex III). For more information: https://owasp.org/www-project-java-html-sanitizer/.

Either `<html/>` or `<template/>` can be informed, but not both. In the following section they will be described.

## HTML Template

In this case the diploma is build by using HTML + CSS + Wildcards. There are two different kinds of wildcards: the ones extracting data from the Europass Learning Model, and the ones to display custom labels.

### EDC data model wildcards

In order to reach any Data Model value, XML Path Language version 1.0 (XPath 1.0) will be used, which is a W3C query language to get information from XML structures (See https://www.w3.org/TR/xpath/ for additional info). The actual wildcard to include in the <html/> tag would be [$XPATH$]. If we wanted to put a specific data in a title, the resulting XML would be:

``` xml

<europassCredential ...>  
    ...
    <displayParameters>
        ...
        <html><![CDATA[<h2>[$XPATH$]</h2>... ]]>
        </html>
        ...
    </displayParameters>
    ...
</europassCredential>

```

XPath is a very extensive query language, so as long as the XPath expression inside the "[$ + $]" structure is valid, the system will evaluate it, and display the information queried.

Taking into account that EDC's Data Model contains various types of data objects (dates, images, strings, etc.), hereafter are the different special cases supported.

#### Use of namespaces
The default namespace is eup, so that is why EDC XMLs do not have eup prefix in front of every item. Nevertheless, XPath expression MUST contain the namespace in front of every element. For instance, to get to the expiry date, we would use [$/eup:europassCredential/cred:validUntil$].

#### Simple string example
XPath example to get to the subject for an additional note of an activity: [$/eup:europassCredential/eup:credentialSubject/eup:activities/eup:activity[0]/eup:additionalNote/eup:subject$].

``` xml

<europassCredential ...>  
    ...
    <credentialSubject id="urn:epass:person:1">
        ...
        <activities>
            <activity id="urn:epass:activity:1">
                ...
                <additionalNote>
                    <text lang="en" content-type="text/plain">Best modiste</text>
                    <subject>Modiste</subject>
                </additionalNote>
                ...
            </activity>
            ...
        </activities>
        ...
    </credentialSubject>
    ...
</europassCredential>

```

#### Multilanguage string example
XPath example to get to the credential title: [$/eup:europassCredential/eup:title$].

``` xml

<europassCredential ...>  
    ...
    <title>
        <text lang="en" content-type="text/plain">Java Programming Certificate</text>
        <text lang="ca" content-type="text/plain">Certificat de Programació Java</text>
   </title>
    ...
</europassCredential>

```

Note that there is no need to complete the XPath until the <text/> tag. If the XPath resolves to an element containing one or more <text/> children, then the wildcard is replaced with the content in the current language.

#### Dates and datetimes example
The XPath to get to the date of birth of the credential subject would be [$/eup:europassCredential/eup:credentialSubject/eup:dateOfBirth$].

``` xml

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<europassCredential ...>
    ...
    <cred:validFrom>2020-08-11T00:00:00+02:00</cred:validFrom> <!-- datetime -->
    ...
    <credentialSubject>
        <dateOfBirth>1982-09-02</dateOfBirth> <!-- date -->
        ...
    </credentialSubject>
    ...
</europassCredential>

```

Displayed date and datetime would respectively be "02/09/1982" and "11/08/2020 00:00 GMT +0200".

#### Periods example
XPath example to get to an activity workload: [$//eup:activity[@id="urn:epass:activity:1"]/eup:workload$].

``` xml

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<europassCredential ...>  
    ...
    <credentialSubject id="urn:epass:person:1">
        ...
        <activities>
            <activity id="urn:epass:activity:1">
                ...
                <workload>PT100H</workload>
                ...
            </activity>
            ...
        </activities>
        ...
    </credentialSubject>
    ...
</europassCredential>

```

Displayed period would be "100 hours".

#### Images example
XPath to get to an organisation logo: [$/eup:europassCredential/eup:agentReferences/eup:organization[0]/eup:logo$].

``` xml

<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<europassCredential ...>  
    ...
    <agentReferences>
        <organization id="urn:epass:org:1">
            ...
            <logo>
                <contentType ...>
                    ...
                </contentType>
                <contentEncoding ...>
                    ...
                </contentEncoding>
                <contentUrl>https://....jpg</contentUrl>
                <content>/9j/4AAQSkZJRgABAQA...</content> <!-- base64 string -->
            </logo>
            ...
        </organization>
        ...
    </agentReferences>
    ...
</europassCredential>

```

Note that for images, there is no need to complete the XPath until the content tag. In any case, the wildcard will be replaced by the base64 string, so that is why the customised html inside the EDC must have the following structure:

``` xml

<europassCredential ...>  
    ...
    <displayParameters>
        ...
        <html><![CDATA[...<img src="data:image/png;base64,[$XPATH_TO_IMAGE$]"/>... ]]>
        </html>
        ...
    </displayParameters>
    ...
</europassCredential>

```

#### Page break example
In order to generate multi page diplomas, the usage of a page break beacon will be needed, when placing the following tag in the HTML a new page will start after it when presenting the diploma. 

``` xml

<europassCredential ...>  
    ...
    <displayParameters>
        ...
        <html><![CDATA[
            ...
                <span>Page 1 end</span>
            </div>
            <page-break-beacon/>
            <div>
                <span>Page 2 start</span>
            ... ]]>
        </html>
        ...
    </displayParameters>
    ...
</europassCredential>

```

### Labels wildcards
In this case, labels are defined under the <labels/> tag and are easily identified by their key attribute. The actual wildcard to include in the <html/> tag would be ($KEY$). If we wanted to put the label in a paragraph, the resulting XML would be:

``` xml

<europassCredential ...>  
    ...
    <displayParameters>
        ...
        <html><![CDATA[...<p>($KEY$)</p>... ]]>
        </html>
        <labels>
            <label key="KEY">
                <text lang="en" content-type="text/plain">Some label</text>
                <text lang="ca" content-type="text/plain">Un missatge</text>
                ...
            </label>
            ...
        </labels>
    </displayParameters>
    ...
</europassCredential>

```

## Thymeleaf Template

In this case the diploma is build by using Thymeleaf Standard dialect. More information can be found [here](https://www.thymeleaf.org/doc/articles/standarddialect5minutes.html). 

### Data model expressions

In order to reach any Data Model value, variable expression can be used: ${path.to.field}. The expressions will match the credential's data model structure as in the XML. 

Given the following XML fragment:

``` xml

<europassCredential xmlns="http://data.europa.eu/snb" xmlns:cred="http://data.europa.eu/europass/model/credentials/w3c#" 
	xmlns:ds="http://www.w3.org/2000/09/xmldsig#" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	cred:id="urn:credential:89a4ab16-3f30-45a3-b78e-97e32556a370" xsdVersion="0.10.0" 
	xsi:schemaLocation="http://data.europa.eu/snb http://publications.europa.eu/resource/schema/credential/genericschema_1.1.xsd">

	[...]

   <credentialSubject id="urn:epass:person:1">
      
  	  [...]

      <nationalId schemeAgencyName="Italy" spatialID="ITA">12345LA</nationalId>
      <fullName>
         <text content-type="text/plain" lang="en">Ana Andromeda</text>
      </fullName>
      <givenNames>
         <text content-type="text/plain" lang="en">Ana</text>
      </givenNames>
      <familyName>
         <text content-type="text/plain" lang="en">Andromeda</text>
      </familyName>

	  [...]

	</credentialSubject>

	[...]
	
</europassCredential>	


```

We can build the next diploma that prints the credential subject's family name value.

``` xml

<europassCredential ...>  
    ...
    <displayParameters>
        ...
        <template><![CDATA[<h2><p th:text="${credential.credentialSubject.familyName}"></p></h2>... ]]>
        </template>
        ...
    </displayParameters>
    ...
</europassCredential>

```

### Labels expresions

In this case, labels are defined under the <labels/> tag and are easily identified by their key attribute. 
The expression that should be included in the <template/> tag would be #{msg.property}. 
If we wanted to put the label in a paragraph, the resulting XML would be:

``` xml

<europassCredential ...>  
    ...
    <displayParameters>
        ...
        <template><![CDATA[...<p th:text="#{family.name}"></p>... ]]>
        </template>
        <labels>
            <label key="family.name">
                <text lang="en" content-type="text/plain">Family name</text>
                <text lang="ca" content-type="text/plain">Cognom</text>
                ...
            </label>
            ...
        </labels>
    </displayParameters>
    ...
</europassCredential>

```

## Annex I

### Default HMTL template example

``` html

<div style="font-style: normal; padding-bottom: 2rem; padding-top: 6rem;">
    <div style="flex: 0 0 100%; max-width: 100%; display: block; text-align: center;">
        <img style="max-height: 4.5rem; width: auto;" src="[$//eup:organization[@id=/eup:europassCredential/cred:issuer/@idref]/eup:logo$]"/>
        <br/>
        <p style="font-size: 18px; color: #525252;">
            [$//eup:organization[@id=/eup:europassCredential/cred:issuer/@idref]/eup:prefLabel$]
        </p>
    </div>
    <div style="flex: 0 0 100%; max-width: 100%;">
        <p style="padding-left: 0.5rem !important; margin: 3rem 1em; text-align: center !important; font-weight: 700 !important; color: #004494 !important; text-decoration: underline; font-size: 31px;">
            [$/eup:europassCredential/eup:title$]
        </p>
    </div>
    <div style="flex: 0 0 100%; max-width: 100%;">
        <p style="text-align: center; font-weight: 700 !important; font-size: 18px;">
            [$//eup:organization[@id=/eup:europassCredential/cred:issuer/@idref]/eup:prefLabel$]
        </p>
        <p style="text-align: center; font-size: 18px;">
            ($diploma.msg.certifiesThat$)
        </p>
    </div>
    <div style="flex: 0 0 100%; max-width: 100%;">
        <p style="font-weight: 700 !important; font-size: 31px; margin-top: 2rem; margin-bottom: 2rem; color: #004494 !important; text-align: center; font-style: italic !important;">
            [$/eup:europassCredential/eup:credentialSubject/eup:fullName$]
        </p>
    </div>
    <div style="flex: 0 0 100%; max-width: 100%; height: 7cm; margin: 0 auto; width: 87%; padding-bottom: 1rem !important;">
        <p style="font-size: 12px; text-align: center; font-weight: 300 !important;">
            [$/eup:europassCredential/eup:description$]
        </p>
    </div>
    <div style="flex: 0 0 100%; max-width: 100%; margin-left: 2.5rem;">
        <p style="font-style: oblique; text-align: left; font-weight: 300 !important;">
            [$/eup:europassCredential/cred:issued$]
        </p>
    </div>
</div>

```

## Annex II

### Default Thymeleaf Standard dialect template extract example

``` html

<div style="height: 28.8cm; width: 89.8%; padding: 1cm 1cm; background: rgb(246, 246, 246);
">
  <div style="display: inline-block; width: 100%; table-layout: fixed">
    <div style="text-align: center">
      <p style=" font-size: 24px; font-weight: bold; padding-bottom: 16px; margin: 0; color: black; font-weight: bold;"
         th:text="${credential.issuer.prefLabel}"
      ></p>
    </div>
  </div>
  <!-- INFORMATION -->
  <div style="padding-top: 12px">
    <p style="
        font-size: 24px; font-weight: bold; text-align: center; padding: 8px 0; margin: 0; color: black; font-weight: bold;">
      1.&nbsp;<span th:text="#{holder.qualification}" />
    </p>
    <div style="border: double">
      <!-- Credential Subject -->
      <div style="margin: 8px 0; padding: 0 8px">
        <table style="width: 100%; table-layout: fixed">
          <tr>
            <td>
              <div>
                <p style=" font-size: 14px; padding-bottom: 4px; margin: 0; color: black; font-weight: bold;">
                  1.1&nbsp;&nbsp;&nbsp;<span th:text="#{family.name}" />
                </p>
                <div style="border: 1px solid black; margin-left: 28px; background: white; height: 27px;">
                  <p style="
                      font-size: 14px; padding: 4px; margin: 0; color: black; font-weight: bold;" 
					  th:text="${credential.credentialSubject.familyName}"
                  ></p>
                </div>
              </div>
            </td>
            <td>
              <div>
                <p style="font-size: 14px; padding-bottom: 4px; margin: 0; color: black; font-weight: bold;"
                >
                  1.2&nbsp;&nbsp;&nbsp;First name
                </p>
                <div
                        style="border: 1px solid black; margin-left: 28px; background: white; height: 27px;"
                >
                  <p
                          style="font-size: 14px; padding: 4px; margin: 0; color: black; font-weight: bold;"
                          th:text="${credential.credentialSubject.givenNames}"
                  ></p>
                </div>
              </div>
            </td>
          </tr>
        </table>
      </div>
    </div>
    <page-break-beacon />
	<div style=" height: 28.8cm; width: 89.8%; padding: 1cm 1cm; background: rgb(246, 246, 246);">
    <p style=" font-size: 24px; font-weight: bold; text-align: center; padding: 8px 0; margin: 0; color: black; font-weight: bold;"
    >
      2.&nbsp;<span th:text="#{issuing.organization}" />
    </p>
    <div style="border: double">
      <!-- Issuing Organization -->
      <div style="padding: 0 8px">
        <div style="margin: 8px 0; padding: 0 8px">
          <table style="width: 100%; table-layout: fixed">
            <tr>
              <td>
                <div>
                  <p style=" font-size: 14px; padding-bottom: 4px; margin: 0; color: black; font-weight: bold;"
                  >
                    2.1&nbsp;&nbsp;&nbsp;<span th:text="#{name.institution}" />
                  </p>
                  <div style=" border: 1px solid black; margin-left: 28px; background: white; height: 27px;"
                  >
                    <p style=" font-size: 14px; padding: 4px; margin: 0; color: black; font-weight: bold;"
                            th:text="${credential.issuer.prefLabel}"
                    ></p>
                  </div>
                </div>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>

```

## Annex III

### HTML sanitizer rules

| Element    	| Allowed Attributes 	|         	|         	|        	|      	|      	|
|------------	|--------------------	|---------	|---------	|--------	|------	|------	|
| p          	| style              	|         	|         	|        	|      	|      	|
| div        	| style              	|         	|         	|        	|      	|      	|
| h1         	| style              	|         	|         	|        	|      	|      	|
| h2         	| style              	|         	|         	|        	|      	|      	|
| h3         	| style              	|         	|         	|        	|      	|      	|
| h4         	| style              	|         	|         	|        	|      	|      	|
| h5         	| style              	|         	|         	|        	|      	|      	|
| h6         	| style              	|         	|         	|        	|      	|      	|
| ul         	| style              	|         	|         	|        	|      	|      	|
| ol         	| style              	|         	|         	|        	|      	|      	|
| li         	| style              	|         	|         	|        	|      	|      	|
| blockquote 	| style              	|         	|         	|        	|      	|      	|
| b          	| style              	|         	|         	|        	|      	|      	|
| i          	| style              	|         	|         	|        	|      	|      	|
| font       	| style              	|         	|         	|        	|      	|      	|
| s          	| style              	|         	|         	|        	|      	|      	|
| u          	| style              	|         	|         	|        	|      	|      	|
| o          	| style              	|         	|         	|        	|      	|      	|
| sup        	| style              	|         	|         	|        	|      	|      	|
| sub        	| style              	|         	|         	|        	|      	|      	|
| ins        	| style              	|         	|         	|        	|      	|      	|
| del        	| style              	|         	|         	|        	|      	|      	|
| strong     	| style              	|         	|         	|        	|      	|      	|
| strike     	| style              	|         	|         	|        	|      	|      	|
| tt         	| style              	|         	|         	|        	|      	|      	|
| code       	| style              	|         	|         	|        	|      	|      	|
| big        	| style              	|         	|         	|        	|      	|      	|
| small      	| style              	|         	|         	|        	|      	|      	|
| br         	| style              	|         	|         	|        	|      	|      	|
| span       	| style              	|         	|         	|        	|      	|      	|
| em         	| style              	|         	|         	|        	|      	|      	|
| img        	| style              	| border  	|  height 	|  width 	| alt* 	| src* 	|
| table      	| style              	| summary 	| align   	| valign 	|      	|      	|
| tr         	| style              	| summary 	| align   	| valign 	|      	|      	|
| td         	| style              	| summary 	| align   	| valign 	|      	|      	|
| th         	| style              	| summary 	| align   	| valign 	|      	|      	|
| colgroup   	| style              	| summary 	| align   	| valign 	|      	|      	|
| col        	| style              	| summary 	| align   	| valign 	|      	|      	|
| thead      	| style              	| summary 	| align   	| valign 	|      	|      	|
| tbody      	| style              	| summary 	| align   	| valign 	|      	|      	|
| tfoot      	| style              	| summary 	| align   	| valign 	|      	|      	|
| caption    	| style              	| summary 	|         	|        	|      	|      	|

*Only “data” policy allowed


| Allowed Style Attributes List**     	|                         	|                             	|                	|
|------------------------------------	|-------------------------	|-----------------------------	|----------------	|
| -moz-border-radius                 	| border-right            	| margin-top                  	| vertical-align 	|
| -moz-border-radius-bottomleft      	| border-right-color      	| max-height                  	| voice-family   	|
| -moz-border-radius-bottomright     	| border-right-style      	| max-width                   	| volume         	|
| -moz-border-radius-topleft         	| border-right-width      	| min-height                  	| white-space    	|
| -moz-border-radius-topright        	| border-spacing          	| min-width                   	| width          	|
| -moz-box-shadow                    	| border-style            	| outline                     	| word-spacing   	|
| -moz-outline                       	| border-top              	| outline-color               	| word-wrap      	|
| -moz-outline-color                 	| border-top-color        	| outline-style               	|                	|
| -moz-outline-style                 	| border-top-left-radius  	| outline-width               	|                	|
| -moz-outline-width                 	| border-top-right-radius 	| padding                     	|                	|
| -o-text-overflow                   	| border-top-style        	| padding-bottom              	|                	|
| -webkit-border-bottom-left-radius  	| border-top-width        	| padding-left                	|                	|
| -webkit-border-bottom-right-radius 	| border-width            	| padding-right               	|                	|
| -webkit-border-radius              	| box-shadow              	| padding-top                 	|                	|
| -webkit-border-radius-bottom-left  	| caption-side            	| pause                       	|                	|
| -webkit-border-radius-bottom-right 	| color                   	| pause-after                 	|                	|
| -webkit-border-radius-top-left     	| cue                     	| pause-before                	|                	|
| -webkit-border-radius-top-right    	| cue-after               	| pitch                       	|                	|
| -webkit-border-top-left-radius     	| cue-before              	| pitch-range                 	|                	|
| -webkit-border-top-right-radius    	| direction               	| quotes                      	|                	|
| -webkit-box-shadow                 	| elevation               	| radial-gradient()           	|                	|
| azimuth                            	| empty-cells             	| rect()                      	|                	|
| background                         	| font                    	| repeating-linear-gradient() 	|                	|
| background-attachment              	| font-family             	| repeating-radial-gradient() 	|                	|
| background-color                   	| font-size               	| rgb()                       	|                	|
| background-image                   	| font-stretch            	| rgba()                      	|                	|
| background-position                	| font-style              	| richness                    	|                	|
| background-repeat                  	| font-variant            	| speak                       	|                	|
| border                             	| font-weight             	| speak-header                	|                	|
| border-bottom                      	| height                  	| speak-numeral               	|                	|
| border-bottom-color                	| image()                 	| speak-punctuation           	|                	|
| border-bottom-left-radius          	| letter-spacing          	| speech-rate                 	|                	|
| border-bottom-right-radius         	| line-height             	| stress                      	|                	|
| border-bottom-style                	| linear-gradient()       	| table-layout                	|                	|
| border-bottom-width                	| list-style              	| text-align                  	|                	|
| border-collapse                    	| list-style-image        	| text-decoration             	|                	|
| border-color                       	| list-style-position     	| text-indent                 	|                	|
| border-left                        	| list-style-type         	| text-overflow               	|                	|
| border-left-color                  	| margin                  	| text-shadow                 	|                	|
| border-left-style                  	| margin-bottom           	| text-transform              	|                	|
| border-left-width                  	| margin-left             	| text-wrap                   	|                	|
| border-radius                      	| margin-right            	| unicode-bidi                	|                	|

**All these attributes value can be restricted during the process of sanitizing the HTML to remove code that can lead to a XSS attack