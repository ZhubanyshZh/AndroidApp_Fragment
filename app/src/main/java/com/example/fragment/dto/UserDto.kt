package com.example.fragment.dto

import java.time.LocalDateTime

data class UserDto(
    var id: Long,
    var username: String,
    var aboutMe: String,
    var email: String,
    var menteeIds: List<Long>,
    var mentorIds: List<Long>,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
)
