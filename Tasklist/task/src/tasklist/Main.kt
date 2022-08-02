package tasklist

import kotlinx.datetime.*
import java.time.LocalTime

fun main() {

    do {
        println("Input an action (add, print, end):")
        val option = readLine()
        when(option) {
            "add" -> Interpreter.addNewTask()
            "print" -> Interpreter.printTaskList()
            "end" -> println("Tasklist exiting!")
            else -> println("The input action is invalid")
        }
    } while (option != "end")
}

object TaskList {
    val tasks = mutableListOf<Task>()
}

class Task(val prio:PRIORITY, val deadline:LocalDateTime, val items:MutableList<String>) {
}

object Interpreter {

    fun addNewTask() {
        val prio = getTaskPriority()
        val date = getDate()
        val time = getTime()
        val dateTime = LocalDateTime(date.year, date.month, date.dayOfMonth, time.hour, time.minute)
        val items = getTaskItems()
        TaskList.tasks.add(Task(prio, dateTime, items))
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

    fun printTaskList() {
        if(!TaskList.tasks.isEmpty())
            for(taskIndex in 0 until TaskList.tasks.size) {
                val task = TaskList.tasks[taskIndex]
                val ident = getHeadlineExtraIdentationByIndex(taskIndex)
                val hourLeadingZero = getHourLeadingZeroByDeadline(task.deadline)
                val minuteLeadingZero = getMinuteLeadingZeroByDeadline(task.deadline)
                println("${taskIndex+1} ${ident}${task.deadline.date} $hourLeadingZero${task.deadline.hour}:$minuteLeadingZero${task.deadline.minute} ${task.prio.Id}")
                for(itemIndex in 0 until TaskList.tasks[taskIndex].items.size)
                    println("   ${TaskList.tasks[taskIndex].items[itemIndex]}")
                println()
            }
        else
            println("No tasks have been input")
    }

    private fun getMinuteLeadingZeroByDeadline(deadline:LocalDateTime):String {
        if(deadline.minute.toString().length == 1)
            return "0"
        else
            return ""
    }

    private fun getHourLeadingZeroByDeadline(deadline:LocalDateTime):String {
        if(deadline.hour.toString().length == 1)
            return "0"
        else
            return ""
    }

    private fun getHeadlineExtraIdentationByIndex(index:Int): String {
        if(index < 9)
            return " "
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
