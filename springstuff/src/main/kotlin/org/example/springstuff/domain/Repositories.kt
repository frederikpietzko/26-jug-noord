package org.example.springstuff.domain

import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.spring.data.ExposedCrudRepository
import org.jetbrains.exposed.v1.spring.data.ExposedRepository
import org.jetbrains.exposed.v1.spring.data.query.query
import java.time.Instant

@ExposedRepository(table = Members::class)
interface MemberRepository : ExposedCrudRepository<Member, Long> {
    fun findByEmail(email: String): Member?

    fun joinedAfter(instant: Instant): List<Member> = query {
        Members.selectAll().where { Members.joinedAt greater instant }.orderBy(Members.joinedAt).toList()
    }
}
