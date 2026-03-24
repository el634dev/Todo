package com.example.todo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class TodoViewModel: ViewModel() {
    val taskList = mutableStateListOf<Task>()

    fun addTask(body: String) {
        //  Add a task and the body is to the one from the function
        taskList.add(Task(body = body))
    }

    fun toggleTaskCompleted(task: Task) {
        //  Find task in the list
        val index = taskList.indexOf(task)
        //  Crates a copy of the task and flips it to the opposite
        taskList[index] = taskList[index].copy(completed = !task)
    }
}