package org.example.springstuff.demo

import org.example.springstuff.domain.Member
import org.example.springstuff.domain.MemberRepository
import org.example.springstuff.domain.Members
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.time.Instant

@Configuration
class DemoDataConfig {
    @Bean
    fun demoData(memberRepository: MemberRepository, transactionManager: PlatformTransactionManager) =
        ApplicationRunner {
            TransactionTemplate(transactionManager).executeWithoutResult { SchemaUtils.create(Members) }

            memberRepository.save(Member(name = "Frederik", email = "frederik@jug-noord.example", joinedAt = Instant.now()))
            memberRepository.save(Member(name = "Nordine", email = "nordine@jug-noord.example", joinedAt = Instant.now().minusSeconds(60 * 60 * 24)))

            // derived query from the Spring Data repository
            memberRepository.findByEmail("frederik@jug-noord.example")?.let { println("findByEmail -> $it") }
            // custom Exposed query block inside the repository
            println("joinedAfter -> ${memberRepository.joinedAfter(Instant.now().minusSeconds(3600))}")
        }
}