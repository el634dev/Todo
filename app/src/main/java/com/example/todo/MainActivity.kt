package com.example.todo

import android.os.Bundle
import android.widget.CheckBox
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todo.ui.theme.TodoTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                TodoScreen()
            }
        }
    }
}

// ------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(modifier: Modifier = Modifier, todoViewModel: TodoViewModel = viewModel()) {
    var taskBody by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "To-Do List") },
                actions = {
                    IconButton(
                        onClick = { todoViewModel.populateTaskList() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add task to list"
                        )
                    }
                    // --------------------------------------------------------------------
                    IconButton(
                        onClick = { todoViewModel.depopulateTaskList() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear to-do list"
                        )
                    }
                    // --------------------------------------------------------------------
                    IconButton(
                        onClick = { todoViewModel.unCheckTask() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = "Uncheck all items"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier.fillMaxSize().padding(innerPadding)
        ) {
            TextField(
                modifier = Modifier.fillMaxWidth().padding(6.dp),
                value = taskBody,
                onValueChange = { taskBody = it },
                label = { Text("Enter task") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        todoViewModel.addTask(taskBody)
                        taskBody = ""
                    }
                )
            )
            LazyColumn {
                // Key will generate a unique id for each task
                items(items = todoViewModel.taskList, key = { task -> task.id }) { task ->
                    val currentTask by rememberUpdatedState(newValue = task)
                    //  Defines the state box
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            //  it is the default variable for state, must be swiped to the right
                            if (it == SwipeToDismissBoxValue.StartToEnd) {
                                todoViewModel.deleteTask(currentTask)
                                true
                            } else if(it == SwipeToDismissBoxValue.EndToStart){
                                val index = todoViewModel.taskList.indexOf(task)
                                todoViewModel.taskList.removeAt(index)
                                todoViewModel.taskList.add(currentTask)

                                false
                            } else {
                                false
                            }
                        }
                    )
                    // --------------------------------------------------------------------
                    // ---------------* SWIPE TO DISMISS BOX *-------------------------
                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = { SwipeBackground(dismissState) },
                        content = {
                            TaskCard(
                                task,
                                toggleCompleted = todoViewModel::toggleTaskCompleted
                            )
                        },
                        modifier = Modifier.padding(vertical = 1.dp).animateItem()
                    )
                }
            }
        }
    }
}

// --------------------------------------------------------------------
// ---------------* SWIPE BACKGROUND FUNCTION *-------------------------
@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState, modifier: Modifier = Modifier) {
    val color = if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
        Color.Red
    } else if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
        Color.Green
    } else {
        Color.Transparent
    }
    // -------------------------------------------
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxSize().background(color)
    ){
        if (dismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete task"
            )
        } else if (dismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add task"
            )
        }
    }
}

// --------------------------------------------------------------------
// ---------------* SWIPE BACKGROUND FUNCTION *-------------------------


// --------------------------------------------------------------------
// ---------------* TASK CARD FUNCTION *-------------------------------
@Composable
fun TaskCard(task: Task, toggleCompleted: (Task) -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.padding(8.dp).fillMaxWidth()
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = task.body,
                modifier = modifier.padding(start = 12.dp)
            )
            Checkbox(
                checked = task.completed,
                onCheckedChange = {
                    toggleCompleted(task)
                }
            )
        }
    }
}
