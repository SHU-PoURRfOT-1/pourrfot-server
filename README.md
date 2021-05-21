# pourrfot-server

[![wakatime](https://wakatime.com/badge/github/SHU-PoURRfOT-1/pourrfot-server.svg)](https://wakatime.com/badge/github/SHU-PoURRfOT-1/pourrfot-server)

[![codecov](https://codecov.io/gh/SHU-PoURRfOT-1/pourrfot-server/branch/main/graph/badge.svg?token=8YX7TV9X0V)](https://codecov.io/gh/SHU-PoURRfOT-1/pourrfot-server)

[![Actions Status: Java CI with Gradle and CD with ssh](https://github.com/SHU-PoURRfOT-1/pourrfot-server/workflows/Java%20CI%20with%20Gradle%20and%20CD%20with%20ssh/badge.svg)](https://github.com/SHU-PoURRfOT-1/pourrfot-server/actions?query=workflow%3A"Java+CI+with+Gradle+and+CD+with+ssh")

## Integration

Swagger Docs: [HERE](http://47.98.133.186/api/swagger-ui/index.html)

Pourrfot-CAS: [HERE](https://github.com/SHU-PoURRfOT-1/pourrfot-cas)

### Summary: How to get token

```http request
###
POST http://47.98.133.186/cas/api/oauth/password-token
Accept: application/json
Content-Type: application/json

{
    "clientId": "pourrfot-web",
    "grantType": "PASSWORD",
    "password": "admin",
    "username": "admin"
}

###
POST http://47.98.133.186/cas/api/oauth/password-token
Accept: application/json
Content-Type: application/json

{
    "clientId": "pourrfot-web",
    "grantType": "PASSWORD",
    "password": "teacher",
    "username": "teacher"
}

###
POST http://47.98.133.186/cas/api/oauth/password-token
Accept: application/json
Content-Type: application/json

{
    "clientId": "pourrfot-web",
    "grantType": "PASSWORD",
    "password": "student",
    "username": "student"
}
```

## Development

### Main Dependencies

* `org.springframework.boot`
* `org.projectlombok:lombok`
* `org.flywaydb:flyway-core`
* `org.springframework.boot:spring-boot-starter-web`
* `io.springfox:springfox-boot-starter`
* `com.baomidou:mybatis-plus-boot-starter`

Docs:

- https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
- https://docs.spring.io/spring-framework/docs/5.3.5/reference/html/index.html
- https://mp.baomidou.com/
- https://www.baeldung.com/swagger-2-documentation-for-spring-rest-api

For more official docs, please read [this](./HELP.md).

### Dev Environment

* IntelliJ IDEA 2021.1 (Ultimate Edition)
* adopt-openjdk **11** hotspot (version 11.0.10)
* [MySQL](https://dev.mysql.com/doc/refman/8.0/en/) 8.0.X
* [Flyway](https://flywaydb.org/download/community) 7.7.X
* Docker

_I suggest you to install
this [IDEA plugin](https://github.com/1tontech/intellij-spring-assistant/issues/18#issuecomment-770574762) for Spring
development._

_If you haven't Docker, you can't run tests with `org.testcontainers:mysql` locally._

### Development Rules

- **Never commit sensitive information, local information to Git**

For example, **do not update the `application.yaml` in the `main/resources` or `test/resources` directory, or add
the `flayway.conf` (it's git-ignored) to Git only for your local requirement.** You can add a git-ignored file
named `application-local.yaml`.

If you want to add new sensitive configuration in the yaml, please add
more [Github Secrets](https://github.com/SHU-PoURRfOT-1/pourrfot-server/settings/secrets/actions) and use them in
the [CI/CD Github Action Workflows](./.github/workflows/gradle.yml).

- There has to be an automated integration test for a controller

**Automation** is the belief.

- No SQL in `.xml` **for all single-table CRUD operations**

Please use `mybatis-plus` to do this without writing the SQL yourself.

### Testing

* Unit Test Service-level and Repository-level testing by JUnit5 and Spring Testing
* Integration Test Controller-level(API-level) testing by Junit5 and Spring Testing
* Integration Test Controller-level(API-level) testing manually by `.http` files in the `src/test/resources/http`
  (running by IDEA)
* Integration Test Controller-level(API-level) testing by [Postman](https://www.postman.com/)

Docs:

- https://docs.spring.io/spring-framework/docs/5.3.5/reference/html/testing.html#testing-introduction
- https://docs.spring.io/spring-boot/docs/2.4.4/reference/html/spring-boot-features.html#boot-features-testing

Please pay attention when writing tests: During the testing phase, due to the use of `org.testcontainers:mysql`, each
test class connects to a MySQL in the container during the startup phase. The MySQL is destroyed after the JVM exit. In
other words, the database is shared between test classes. When you want to test DB result in the test class, you need to
add these code as a field of the class.

```java
@ClassRule
public static MySQLContainer<CustomMySQLContainer> customMySQLContainer=CustomMySQLContainer.getInstance();
```

If you don't have Docker locally, you can comment out the field above temporarily, and add a test-scope profile
named `application-local.yaml` to `src/test/resources`, and add an annotation `@ActiveProfiles(local)` to the test
class. Then the specific test class can connect your local database instead of use the one in the container. **Please
restore them after your local test passing.**

Docs:

- https://www.baeldung.com/spring-boot-testcontainers-integration-test

### Database DDL VCS

**Please strictly enforce!**

Please install the database version control system Flyway first. We use it locally as a command-line tool, as a
component in the running and testing stage. Then make sure that You have installed MySQL 8.0.

When you first get this project and try to recover the pourrfot database locally. Please follow these steps.

1. Copy the file `src/main/resources/db/flyway.conf.example` to `flyway.conf` and replace the database connection config
   with your own. **Don't forget to create a database named `pourrfot`.**
2. run `flyway migrate`.
3. Take a look to the database. The table structure should be fine.

When you try to modify the table structure (including but not limited to adding or modifying fields, adding or modifying
indexes), please put the changes (DDL contents) in the form of `V<version>__<name>.sql` file
in `src/main/resources/db/migration`. For example,there is a `V1__init_database.sql` as initialization.

Flyway will record every SQL commit, **so you absolutely can't update the SQL committed before whether it's local or
online environment**. If you want to modify the tables, the only way is to **create a new SQL file** to change its
structure.

As a short, when you want to modify the database structure, please follow these steps.

1. Add an SQL file with DDL only. The file name follow the pattern `V<version>__<name>.sql`.
2. **Reformat the file in IDEA.**
3. **run `flyway migrate` locally to execute the modification** to verify the correctness of the SQL file.
4. commit the SQL file.

Docs:

- https://flywaydb.org/documentation/usage/commandline/#download-and-installation
- https://flywaydb.org/documentation/database/testcontainers
- https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-execute-flyway-database-migrations-on-startup

## Deployment

The project will be automatically deployed (after running test and build automatically) to the Aliyun server (Lighthouse
but not ECS) I purchased by this [Github Action](./.github/workflows/gradle.yml).

There is a Nginx (not in Docker) running on the server to back-proxy backend services. You can just access the service
without port but begin with `/api`. [#3](https://github.com/SHU-PoURRfOT-1/pourrfot-server/issues/3)
