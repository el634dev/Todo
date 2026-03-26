package com.example.todo

import java.util.UUID

data class Task(
    var body: String = "",
    var completed: Boolean = false,
    var id: UUID = UUID.randomUUID()
)
