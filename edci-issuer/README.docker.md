# EDCI Issuer Docker
[![license](https://img.shields.io/github/license/:user/:repo.svg)](LICENSE)
[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat-square)](https://github.com/RichardLitt/standard-readme)
## What is EDCI Issuer?
EDCI Issuer is a JAVA web app that can issue degrees, diplomas, certificates of participation or other credentials to your learner. Also, the EDCI Issuer provides multiple ways, such as XLS uploading or web-app creation, to generate the credentials XML.
All of the generated credentials follow the Europass Learning Data model, for more information and related material of the Europass Learning data model, please visit [the github page](https://github.com/european-commission-europass/Europass-Learning-Model) 
This tool is designed in a way that Any school, college, university or training provider in Europe may use this tool to issue credentials for free and secure them with their e-seal. 
The resulting credentials can be e-mailed to learners or directly deposited to their Europass profiles.

## Starting EDCI Issuer
For this image, the EDCI issuer is deployed in a tomcat server using port 8080, exposing that port will have a 
Starting an instance of the EDCI Issuer with a minimum setup wil be done as follows:

    docker run -p 8080:8080 kiceurope/issuer
    
This will start the docker with the default localhost configuration, in order to change the configuration, a volume for the configuration files must be created.

## Volumes and configuration

The EDCI Issuer stores the configuration in a series of property files, if any change needs to be applied, the properties must be changed and the web app must be restarted.
The configuration is stored by default at $CATALINA_HOME/conf/edci/issuer, meaning that there are only two ways of changing it in a permanent way:

* Building a custom image FROM kiceurope/issuer
* Mounting a volume for the configuration folder

The configuration is copied into directory after the container startup, meaning that even if the volume is already mounted, default configuration will be copied to the directory unless the configuration files already exists.
Because of this, the best way to change the configuration and test the changes is to mount the configuration volume, set up the required changes and restart the docker container.

Another useful volume that can be mounted is the logs directory of the tomcat at $CATALINA_HOME/logs to be able to keep those logs available.

Lastly, the credentials generated on runtime are temporarily stored at $CATALINA_HOME/temp/credentials, a volume mounted on that folder may be useful for debug purposes.

Starting the issuer with all 3 volumes enabled will look similar to:


    docker run -p 8080:8080 -v /opt/issuer:/usr/local/tomcat/issuer -v /opt/logs:/usr/local/tomcat/logs -v /opt/credentials:/usr/local/tomcat/credentials kiceurope/issuer
    
## Environment Variables
There are some optional environment variables that may be used for enabling JPDA debbuging, and waiting for a host:

<table>
    <tr>
        <th>
            Variable
        </th>
        <th>
            Description    
        </th>
    </tr>
    <tr>
        <td>
            JPDA_ENABLED
        </td>
        <td>
            enable JPDA for debug/development purposes
        </td>
    </tr>
    <tr>
        <td>
            JPDA_TRANSPORT
        </td>
        <td>
            define the JPDA transport, recommended is dt_socket
        </td>
    </tr>
    <tr>
        <td>
            JPDA_ADDRESS
        </td>
        <td>
            define the JPDA port, if enabled it must also be exposed to the host when running the container
        </td>
    </tr>    
    <tr>
        <td>
            WAIT_FOR_HOST
        </td>
        <td>
            define a host to wait for, before starting this container, used mostly for dockerized dependencies
        </td>
    </tr>
    <tr>
        <td>
            WAIT_FOR_PORT
        </td>
        <td>
            the hostname for which the container must wait, required if used WAIT_FOR_HOST          
        </td>
    </tr>
    <tr>
        <td>
            WAIT_FOR_TIMEOUT
        </td>           
        <td>
            maximum waiting time in seconds befor timing out 
        </td>        
    </tr>
</table>

## External dependencies

The EDCI Issuer requires a database, you can use either a dockerized version of the database, or configure the properties of the edci-issuer in the issuer.properties file to the desired database.

Also, the EDCI Issuer's OCB templates are stored based on a user system, but for this to work an OIDC Identity provider must be configured. For this, we recommend [keycloak](https://www.keycloak.org/). 
If a fully Identity Provider is not required, you can make use of a mocked user, using the security.properties and issuer_front.properties configuration files.

The OIDC provider must work using a proper hostname, if you are running the Identity provider inside a docker image, you can use docker's host.docker.internal URL to point to the Identity provider inside the issuer's configuration files. 


For the preview, an instance of the [EDCI Viewer](http://edci-viewer-link) must also be configured in the issuer.properties and issuer_front.properties files.

Most importantly, the issuer requires an instance of the [EDCI Wallet](http://edci-wallet-link) configured in the issuer.properties file, to be able to issue the credentials to a wallet.


## EDCI Ecosystem

The EDCI ecosystem is composed of the [EDCI Issuer](http://edci-issuer-link), the [EDCI Wallet](https://edci-wallet-link) and the [EDCI Issuer](https://edci-issuer-link). Moreover, a database and an Identity provider are required for the full functionality.

Because of this, and if you with to use this dockerized version of the EDCI ecosystem, we recommend to use a docker-compose file to make the communication between containers easier.

An exemple of a full docker-compose file can be fond below: 

    version: "3.0"
    services:
      mysqldb:
        image: kiceurope/mysqldb:latest
        container_name: mysqldb
        environment:
          - MYSQL_ROOT_PASSWORD=1234mraf
          - MYSQL_USER=edci
          - MYSQL_PASSWORD=1234mraf
        volumes:
          - "C://EDCIEcosystem/dockers/docker_mysql/datadir:/var/lib/mysql"
        ports:
          - 3307:3306
          - 33060:33060
      keycloak:
        image: quay.io/keycloak/keycloak:latest
        container_name: keycloak
        command: -b 0.0.0.0 -Dkeycloak.profile.feature.token_exchange=enabled -Dkeycloak.profile.feature.admin_fine_grained_authz=enabled
        environment:
          - KEYCLOAK_USER=admin
          - KEYCLOAK_PASSWORD=admin
          - DB_VENDOR=MYSQL
          - DB_ADDR=mysqldb
          - DB_DATABASE=keycloak
          - DB_USER=keycloak
          - DB_PASSWORD=kyk1234
        ports:
          - "9000:8080"
        depends_on:
          - mysqldb
      viewer:
        image: kiceurope/viewer:QA
        container_name: viewer
        build: edci-viewer/
        environment:
          - JPDA_ENABLED=true
          - JPDA_TRANSPORT=dt_socket
          - JPDA_ADDRESS=8000
          - WAIT_FOR_HOST=mysqldb
          - WAIT_FOR_PORT=3306
          - WAIT_FOR_TIMEOUT=60
        volumes:
          - "C://EDCIEcosystem/dockers/docker_viewer/viewer:/usr/local/tomcat/conf/edci/viewer"
          - "C://EDCIEcosystem/dockers/docker_viewer/logs:/usr/local/tomcat/logs"
        ports:
          - "8282:8080"
          - "9282:8000"
      wallet:
        image: kiceurope/wallet:QA
        container_name: wallet
        build: edci-wallet/
        environment:
          - JPDA_ENABLED=true
          - JPDA_TRANSPORT=dt_socket
          - JPDA_ADDRESS=8000
          - WAIT_FOR_HOST=mysqldb
          - WAIT_FOR_PORT=3306
          - WAIT_FOR_TIMEOUT=60
        volumes:
          - "C://EDCIEcosystem/dockers/docker_wallet/wallet:/usr/local/tomcat/conf/edci/wallet"
          - "C://EDCIEcosystem/dockers/docker_wallet/logs:/usr/local/tomcat/logs"
        ports:
          - "8181:8080"
          - "9181:8000"
      issuer:
        image: kiceurope/issuer:QA
        container_name: issuer
        build: edci-issuer/
        environment:
          - JPDA_ENABLED=true
          - JPDA_TRANSPORT=dt_socket
          - JPDA_ADDRESS=8000
          - WAIT_FOR_HOST=mysqldb
          - WAIT_FOR_PORT=3306
          - WAIT_FOR_TIMEOUT=60
        volumes:
          - "C://EDCIEcosystem/dockers/docker_issuer/issuer:/usr/local/tomcat/conf/edci/issuer"
          - "C://EDCIEcosystem/dockers/docker_issuer/credentials:/usr/local/tomcat/temp/credentials"
          - "C://EDCIEcosystem/dockers/docker_issuer/logs:/usr/local/tomcat/logs"
        ports:
          - "8383:8080"
          - "9383:8000"

Here, a mysql database and a keycloak server are being used in conjunction with the issuer, viewer and wallet applications. Notice that in this case,  all 3 EDCI applications have been configured with enabled JPDA for debugging purposes.

Keep in mind, shen using the full Ecosystem in this way, that all EDCI dockers do have installed a wait-for-it script, meaning that you can use the WAIT_FOR_XX in order to wait for relevant dependencies.
Also, notice that the keycloak docker is started with fine grain authorization and token exchange enabled, this is necessary if both viewer and wallet are present in the ecosystem and are required to interact.

For this setup, dockers must refer to each other by the internal eu.europa.ec.empl.edci.dss.service name (issuer/wallet/viewer), when doing internal calls. However, keycloak must still be accessed through "host.docker.internal" URL, or configured with a proper domain depending on the desired integration. 

## License

XXXXXX