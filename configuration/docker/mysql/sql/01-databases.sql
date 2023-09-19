CREATE DATABASE IF NOT EXISTS edci;
CREATE DATABASE IF NOT EXISTS keycloak;
CREATE USER IF NOT EXISTS 'keycloak'@'%' IDENTIFIED BY 'changeMe';
ALTER USER 'keycloak'@'%' IDENTIFIED BY 'changeMe';
CREATE USER IF NOT EXISTS 'edci'@'%' IDENTIFIED BY 'changeMe';
ALTER USER 'edci'@'%' IDENTIFIED BY 'changeMe';
GRANT ALL ON edci.* to edci;
GRANT ALL ON keycloak.* to keycloak;