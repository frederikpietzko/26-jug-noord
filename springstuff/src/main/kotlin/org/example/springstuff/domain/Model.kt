package org.example.springstuff.domain

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.javatime.timestamp
import java.time.Instant

object Members : LongIdTable("members") {
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val joinedAt = timestamp("joined_at")
}

data class Member(
    val id: Long? = null,
    val name: String,
    val email: String,
    val joinedAt: Instant,
)
