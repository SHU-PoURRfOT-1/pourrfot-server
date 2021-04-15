# pourrfot-server

### Development

#### Main Dependency

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

#### Dev Environment

* IntelliJ IDEA 2021.1 (Ultimate Edition)
* adopt-openjdk **11** hotspot (version 11.0.10)
* [MySQL](https://dev.mysql.com/doc/refman/8.0/en/) 8.0.X
* [Flyway](https://flywaydb.org/download/community) 7.7.X

#### Testing

* Unit Test Service-level and Repository-level testing by JUnit5 and Spring Testing
* Integration Test Controller-level(API-level) testing by Junit5 and Spring Testing
* Integration Test Controller-level(API-level) testing manually by `.http` files in the `src/test/resources/http`
  (running by IDEA)
* Integration Test Controller-level(API-level) testing by [Postman](https://www.postman.com/)

Docs:

- https://docs.spring.io/spring-framework/docs/5.3.5/reference/html/testing.html#testing-introduction
- https://docs.spring.io/spring-boot/docs/2.4.4/reference/html/spring-boot-features.html#boot-features-testing

#### Database DDL VCS

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

Please pay attention when writing tests: During the testing phase, due to the use of `org.testcontainers:mysql`, each
test class connects to a MySQL in the container during the startup phase. The MySQL is destroyed after the JVM exit. In
other words, the database is shared between test classes. When you want to test DB result in the test class, you need to
add these code as a field of the class.

```java
@ClassRule
public static MySQLContainer<CustomMySQLContainer> customMySQLContainer=CustomMySQLContainer.getInstance();
```

Docs:

- https://flywaydb.org/documentation/usage/commandline/#download-and-installation
- https://flywaydb.org/documentation/database/testcontainers
- https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-execute-flyway-database-migrations-on-startup

### Deployment

The project will be automatically deployed (after running test and build automatically) to the Aliyun server (Lighthouse
but not ECS) I purchased by this [Github Action](./.github/workflows/gradle.yml).

There is a Nginx (not in Docker) running on the server to back-proxy backend services. You can just access the service
without port but begin with `/api`. [#3](https://github.com/SHU-PoURRfOT-1/pourrfot-server/issues/3)
