package org.example.springstuff

import org.example.springstuff.domain.Member
import org.example.springstuff.domain.MemberRepository
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.jdbc.autoconfigure.DataSourceTransactionManagerAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.Instant

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
class SpringstuffApplication {
    @Bean
    fun demoData(memberRepository: MemberRepository) = ApplicationRunner {
        // Idempotent seeding: the app's file-based H2 survives restarts.
        if (memberRepository.findByEmail("frederik@jug-noord.example") == null) {
            memberRepository.save(Member(name = "Frederik", email = "frederik@jug-noord.example", joinedAt = Instant.now()))
            memberRepository.save(Member(name = "Nordine", email = "nordine@jug-noord.example", joinedAt = Instant.now().minusSeconds(86_400)))
        }

        // derived query from the Spring Data repository
        memberRepository.findByEmail("frederik@jug-noord.example")?.let { println("findByEmail -> $it") }
        // custom Exposed query block inside the repository
        println("joinedAfter -> ${memberRepository.joinedAfter(Instant.now().minusSeconds(3_600))}")
    }
}

fun main(args: Array<String>) {
    runApplication<SpringstuffApplication>(*args)
}
