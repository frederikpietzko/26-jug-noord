package org.example.springstuff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication

/**
 * Boot's JDBC transaction manager auto-configuration is excluded so that the Exposed starter's
 * SpringTransactionManager is exposed as the single `transactionManager` bean — repositories and
 * `@Transactional` all run through Exposed then.
 *
 * Schema DDL is owned by Flyway: scripts in `src/main/resources/db/migration` (generated via
 * `./gradlew generateMigrations` from the Exposed table objects) are applied at startup.
 * The SOAP client (CXF-generated classes from countries.wsdl) is available via [org.example.springstuff.service.RemoteCountryService].
 */
@SpringBootApplication(exclude = [DataSourceTransactionManagerAutoConfiguration::class])
class SpringstuffApplication

fun main(args: Array<String>) {
    runApplication<SpringstuffApplication>(*args)
}
