# pourrfot-server

### Development

#### Dev Environment

* IntelliJ IDEA 2021.1 (Ultimate Edition)
* adopt-openjdk **11** hotspot (version 11.0.10)

#### Testing

* Unit Test Service-level and Repository-level testing in JUnit5
* Integration Test Controller-level testing in Junit5

#### Database DDL VCS

**Please strictly enforce!**

Please install the database version control system [Flyway](https://flywaydb.org/) first. We use it locally as a
command-line tool, as a component in the running and testing stage.

When you first get this project and try to recover the pourrfot database locally. Please follow these steps.

1. rename the file `src/main/resources/db/flyway.conf.example` to `flyway.conf` and replace the database connection
   config with your own. **Don't forget to create a database named `pourrfot`.**
2. run `flyway migrate`.
3. Take a look to the database. The table structure should be fine.

When you try to modify the table structure (including but not limited to adding or modifying fields, adding or modifying
indexes), please put the changes (DDL contents) in the form of `V<version>__<name>.sql` file
in `src/main/resources/db/migration`. For example,there is a `V1__init_database.sql` as initialization.

Please pay attention when writing tests: During the testing phase, due to the use of `org.testcontainers:mysql`, each
test class connects to a MySQL in the container during the startup phase and is destroyed after the test class finishes
execution. In other words, the database is isolated between each test class.

### Deployment

The project will be automatically deployed (after running test and build automatically) to the Aliyun server (Lighthouse
but not ECS) I purchased by this [Github Action](./.github/workflows/gradle.yml).

There is a Nginx (not in Docker) running on the server to back-proxy backend services. You can just access the service
without port but begin with `/api`. [#3](https://github.com/SHU-PoURRfOT-1/pourrfot-server/issues/3)
