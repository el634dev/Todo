package com.example.todo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class TodoViewModel: ViewModel() {
    val taskList = mutableStateListOf<Task>()

    fun populateTaskList() {
        addTask(body = "Study")
        addTask(body = "Buy tea")
        addTask(body = "Buy eggs")
        addTask(body = "Buy milk")
        addTask(body = "Buy cheese")
    }

    //  -----------------------------------------------------------
    // ---------------* CLEAR TASK LIST FUNCTION *-----------------
    fun depopulateTaskList() {
        taskList.clear()
    }

    //  -----------------------------------------------------------
    // ---------------* ADD TASK FUNCTION *------------------------
    fun addTask(body: String) {
        //  Add a task and the body is to the one from the function
        taskList.add(Task(body = body))
    }

    // --------------------------------------------------
    // ---------------* DELETE A TASK FUNCTION *---------
    fun deleteTask(task: Task) {
        taskList.remove(task)
    }

    //  ------------------------------------------------------------
    // ---------------* CHECK A TASK AS COMPLETED FUNCTION *---------
    fun toggleTaskCompleted(task: Task) {
        //  Find task in the list
        val index = taskList.indexOf(task)
        //  Crates a copy of the task and flips it to the opposite
        taskList[index] = taskList[index].copy(completed = !task.completed)
    }

    // -------------------------------------------------------------
    // ---------------* UNCHECK ALL TASKS FUNCTION *----------------
    fun unCheckTask() {
       taskList.replaceAll{ it.copy(completed = false) }
    }
}