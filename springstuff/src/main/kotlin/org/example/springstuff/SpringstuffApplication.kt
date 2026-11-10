package org.example.springstuff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication

/**
 * Boot's JDBC transaction manager auto-configuration is excluded so that the Exposed starter's
 * SpringTransactionManager is exposed as the single `transactionManager` bean — repositories and
 * TransactionTemplate/`@Transactional` all run through Exposed then.
 *
 * The SOAP client (CXF-generated classes from countries.wsdl) is available via [org.example.springstuff.service.RemoteCountryService].
 */
@SpringBootApplication(exclude = [DataSourceTransactionManagerAutoConfiguration::class])
class SpringstuffApplication

fun main(args: Array<String>) {
    runApplication<SpringstuffApplication>(*args)
}
