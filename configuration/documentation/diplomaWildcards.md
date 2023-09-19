# Display Parameters
The European digital credentials contain within themselves information about the display inside the "displayParameter" (DisplayParameterDTO) property, these fields include:

- **availableLanguage:** a list of the available languages for the credential.
- **description:** a langString with the description of the credential in multiple languages.
- **individualDisplay:** used to include summary images in base64 inside the credential.
- **primaryLanguage:** the primary language of the credential
- **title:** a langString with the title of the credential in multiple languages.

The IndividualDisplay parameter can be manually introduced in some cases, but most of the time it is better to use the EDCI thymeleaf utilities to generate this image from a thymeleaf template.
 
The structure for storing summary images and how to use the thymeleaf utilities are further detailed in this document
##Individual Display

The Individual display is used to include summary images in base64 within the credential in order to visualize those summary images later on along with the credential details.

The structure for the summary images inside the credential is as follows:

        individualDisplay":[
            {
               "type":"IndividualDisplay",
               "displayDetail":[
                  {
                     "type":"DisplayDetail",
                     "image":{
                        "type":"MediaObject",
                        "content" : "{base64image}", <-- base64 page content
                        "contentEncoding":{
                           "id":"http://data.europa.eu/snb/encoding/6146cde7dd", <-- Base64 encoding
                           "type":"Concept",
                           "inScheme":{
                              "id":"http://data.europa.eu/snb/encoding/25831c2", <-- Encoding controlled list
                              "type":"ConceptScheme"
                           }
                        },
                        "contentType":{
                           "id":"http://publications.europa.eu/resource/authority/file-type/JPEG", -> JPEG content type
                           "type":"Concept",
                           "inScheme":{
                              "id":"http://publications.europa.eu/resource/authority/file-type", -> Content type controlled list
                              "type":"ConceptScheme"
                           }
                        }
                     },
                     "page":"1" <-- Page order
                  }
                  ... More pages....
               ],
               "language":{
                  "id":"http://publications.europa.eu/resource/authority/language/ENG", <-- English language (the language of the pages)
                  "type":"Concept",
                  "inScheme":{
                     "id":"http://publications.europa.eu/resource/authority/language", <-- Language Controlled list
                     "type":"ConceptScheme"
                  }
               }
            }
            .... More languages, each one with it's own set of ....
       ]

Above you can see an example of how a single summary image would look like within the credential JSON.

When uploading a credential through the EDC Ecosystem, in particular the [EDCI-Issuer](../../edci-issuer/README.md), an IndividualDisplay object may be directly included when using the FrontEnd JSON upload or the API.

As an alternative, the thymeleaf utilities can be used to automatically generate the summary images while processing the credentials at the [EDCI-Issuer](../../edci-issuer/README.md).

## Summary Image and Templates

The summary image(s) are stored in the DisplayParameter.IndividualDisplay field and are displayed by the [EDC Viewer](../../edci-viewer/README.md) when visualizing a credential by any means.

When uploading a credential JSON either through the issuer's frontend or API, a deliveryDetails object can be specified within the JSON upload, for more information about the JSON upload format, which does also apply to the API, visit the ["Upload JSON" section of the EDCI-issuer readme](../../edci-issuer/README.md#upload-json)   

When processing the credential in the EDCI-issuer, the thymeleaf template is used in conjunction with the data within the credential to generate an HTML template from which an image is then generated, encoded as base64 and added to the credentials individual display.

There is a default European Digital Credential for Learning (EDC) template  that shows the awarding body's logo (if supplied), the credential's title, and the credential owner's name on a white and blue background. Any credential uploaded to issuer by any means and processed by the consumers will generate a summary image based on this default template.

Alternatively, custom thymeleaf templates can be built in HTML + CSS (no JS) + Thymeleaf Standard dialect inside the EDC's structure. 
Credential builders can use wildcards to reference EDC content (e.g. credential owner's name, activity titles, 
grades, dates, etc.) and incorporate additional translatable labels (e.g. "certifies that", "has achieved", etc.). 
Full examples are published in Github.

## Default summary types
Currently, two types of diploma summeries can be generated by default in the issuer, depending on the credential type:
* Single page summary (For all the credentials that DON'T have the "Diploma Summary" credential type)
* Diploma supplements (For all the credentials that DO have the "Diploma Summary" credential type)

## Generating a custom summary image
A custom summary template can be provided in OCB in order to create a custom summary image, indepedendent from the credential type while retriving information from the credential.

### Template elements
* `Template`: In this element, the information to be displayed and it's disposition are defined. 
  The summary image template is defined using HTML + Thymleaf Standard dialect.
* `background`: An optional image to be displayed in the background of the summary image. 
  For a proper display, the image used must have a din A4 ratio.
* `labels`: Used to define a set of multilanguage labels to be accessed using Thymleaf Standard dialect.

*Important note*: The provided HTML or templates will be sanitised (automatic removal) to prevent Cross site scripting when uploaded.
See the tags and attributes allowed in the [Annex I](#Annex I). For more information: https://owasp.org/www-project-java-html-sanitizer/.

### OCB HTML Template
The three template elements can be prepared in the "HTML Template" tab and linked afterwards to any credential also defined in OCB.

### Upload JSON & public API
The three tempalte elements can be provided for each credential in the upload JSON file used in both the "Upload JOSN" functionality 
from the browser or when uploading credentials from the public API (if enabled).

## Annex I

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