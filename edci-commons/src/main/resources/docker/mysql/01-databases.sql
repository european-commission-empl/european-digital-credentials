CREATE DATABASE IF NOT EXISTS edci;
CREATE DATABASE IF NOT EXISTS keycloak;
CREATE USER IF NOT EXISTS 'keycloak'@'%' IDENTIFIED BY 'kyk1234';
CREATE USER IF NOT EXISTS 'edci'@'%' IDENTIFIED BY '1234mraf';
GRANT ALL ON edci.* to edci;
GRANT ALL ON keycloak.* to keycloak;