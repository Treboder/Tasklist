package tasklist

import kotlinx.datetime.*
import kotlinx.datetime.Clock.System
import java.time.LocalTime

fun main() {
    do {
        println("Input an action (add, print, edit, delete, end):")
        val option = readLine()
        when(option) {
            "add" -> Interpreter.addNewTask()
            "print" -> TaskList.printList()
            "edit" -> Interpreter.editTask()
            "delete" -> Interpreter.deleteTask()
            "end" -> println("Tasklist exiting!")
            else -> println("The input action is invalid")
        }
    } while (option != "end")
}

object Interpreter {
    fun addNewTask() {
        val prio = getTaskPriority()
        val date = getDate()
        val time = getTime()
        val items = getTaskItems()
        TaskList.tasks.add(Task(prio, date, time, items))
    }

    fun editTask() {
        if(TaskList.tasks.isEmpty())
            println("No tasks have been input")
        else {
            TaskList.printList()
            val taskToEdit = getTaskToEdit()
            var fieldToEdit:String? = null
            while(fieldToEdit == null) {
                println("Input a field to edit (priority, date, time, task):")
                fieldToEdit = readLine()
                when(fieldToEdit) {
                    "priority" -> taskToEdit.prio = getTaskPriority()
                    "date" -> taskToEdit.date = getDate()
                    "time" -> taskToEdit.time = getTime()
                    "task" -> taskToEdit.items = getTaskItems()
                    else -> { fieldToEdit = null; print("Invalid field") }
                }
            }
            println("The task is changed")
        }
    }

    fun getTaskToEdit():Task {
        var taskToEdit:Int? = null
        while(taskToEdit == null) {
            println("Input the task number (1-${TaskList.tasks.size}):")
            try {
                taskToEdit = readLine()!!.toInt()
                TaskList.tasks[taskToEdit-1]
            }
            catch (e:Exception) {  taskToEdit = null; println("Invalid task number") }
        }
        return TaskList.tasks[taskToEdit-1]
    }

    fun deleteTask() {
        if(TaskList.tasks.isEmpty())
            println("No tasks have been input")
        else {
            TaskList.printList()
            while(true) {
                println("Input the task number (1-${TaskList.tasks.size}):")
                try {
                    val taskToDelete = readLine()!!.toInt()
                    TaskList.tasks.removeAt(taskToDelete-1)
                    println("The task is deleted")
                    return
                }
                catch (e:Exception) { println("Invalid task number") }
            }
        }
    }

    private fun getTaskPriority(): PRIORITY {
        while(true) {
            println("Input the task priority (C, H, N, L):")
            val prio = readLine()!!.uppercase()
            when(prio) {
                "C" -> return PRIORITY.CRITICAL
                "H" -> return PRIORITY.HIGH
                "N" -> return PRIORITY.NORMAL
                "L" -> return PRIORITY.LOW
                //else -> println("The input prio is invalid")
            }
        }
    }

    private fun getDate(): LocalDate {
        while(true) {
            println("Input the date (yyyy-mm-dd):")
            try {
                val inputDate = readLine()!!.split("-")
                return LocalDate(inputDate[0].toInt(), inputDate[1].toInt(), inputDate[2].toInt())
            }
            catch (e:Exception) { println("The input date is invalid") }
        }
    }

    private fun getTime(): LocalTime {
        while(true) {
            println("Input the time (hh:mm):")
            try {
                val inputTime = readLine()!!.split(":")
                return LocalTime.of(inputTime[0].toInt(), inputTime[1].toInt())
            }
            catch (e:Exception) { println("The input time is invalid") }
        }
    }

    private fun getTaskItems():MutableList<String> {
        println("Input a new task (enter a blank line to end):")
        val taskItemList = mutableListOf<String>()
        while(true) {
            val newTask = readLine()!!.replace("\t", "").trim()
            if(!newTask.isEmpty())
                taskItemList.add(newTask)
            else
                break
        }
        if(taskItemList.isEmpty())
            println("The task is blank")
        return taskItemList
    }
}

object TaskList {
    val tasks = mutableListOf<Task>()

    fun printList() {
        if(!tasks.isEmpty())
            for(taskIndex in 0 until tasks.size) {
                val ident = getHeadlineExtraIdentationByIndex(taskIndex)
                val task = tasks[taskIndex]
                println("${taskIndex+1} ${ident}${task}")
                for(item in task.items)
                    println("   $item")
                println()
            }
        else
            println("No tasks have been input")
    }

    private fun getHeadlineExtraIdentationByIndex(index:Int): String {
        if(index < 9)
            return " "
        else
            return ""
    }
}

class Task(var prio:PRIORITY, var date: LocalDate, var time:LocalTime, var items:MutableList<String>) {
    fun getDueTag():String {
        val remainingDays = System.now().toLocalDateTime(TimeZone.UTC).date.daysUntil(date)
        if(remainingDays < 0)
            return "O" // overdue
        else if(remainingDays == 0)
            return "T" // today
        else //(remainingDays > 0)
            return "I" // in time
    }

    override fun toString(): String {
        val hourLeadingZero = getHourLeadingZeroByDeadline(time)
        val minuteLeadingZero = getMinuteLeadingZeroByDeadline(time)
        return "$date $hourLeadingZero${time.hour}:$minuteLeadingZero${time.minute} ${prio.Id} ${getDueTag()}"
    }

    private fun getMinuteLeadingZeroByDeadline(deadline:LocalTime):String {
        if(deadline.minute.toString().length == 1)
            return "0"
        else
            return ""
    }

    private fun getHourLeadingZeroByDeadline(deadline:LocalTime):String {
        if(deadline.hour.toString().length == 1)
            return "0"
        else
            return ""
    }
}

enum class PRIORITY(val Id:String, val Rank:Int) {
    CRITICAL("C", 0),
    HIGH("H", 1),
    NORMAL("N", 2),
    LOW("L", 3)
}
