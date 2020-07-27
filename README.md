[![CircleCI](https://circleci.com/gh/griesba/mssc-beer-order-service.svg?style=svg)](https://circleci.com/gh/griesba/mssc-beer-order-service)


# MSSC Beer Order Service
In order to generate automatically a script for the creation of tables in DB, this configuration can be added in application-localmysql.properties file
#Generate table creation script from JPA
#spring.jpa.properties.javax.persistence.schema-generation.create-source=metadata
#spring.jpa.properties.javax.persistence.schema-generation.scripts.action=create
#spring.jpa.properties.javax.persistence.schema-generation.scripts.create-target=create.sql

A file named 'create.sql' will be generated containing sql script base on JPA entity 