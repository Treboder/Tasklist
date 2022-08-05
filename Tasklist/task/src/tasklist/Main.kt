package tasklist

import kotlinx.datetime.*
import kotlinx.datetime.Clock.System
import java.time.LocalTime
import com.squareup.moshi.*
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

fun main() {
    JSONAdapter.load()
    do {
        println("Input an action (add, print, edit, delete, end):")
        val option = readLine()
        when(option) {
            "add" -> TaskEditor.addNewTask()
            "print" -> TaskList.printList()
            "edit" -> TaskEditor.editTask()
            "delete" -> TaskEditor.deleteTask()
            "end" -> JSONAdapter.save()
            else -> println("The input action is invalid")
        }
    } while (option != "end")
    println("Tasklist exiting!")
}

object TaskEditor {
    fun addNewTask() {
        val index = TaskList.tasks.count() + 1
        val prio = getTaskPriority()
        val date = getDate()
        val time = getTime()
        val items = getTaskItems()
        TaskList.tasks.add(Task(index, prio, date, time, items))
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
                    TaskList.updateTaskIndicesAfterDelete()
                    return
                }
                catch (e:Exception) { println("Invalid task number") }
            }
        }
    }

    private fun getTaskToEdit():Task {
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

    public fun getPrioObjectById(id:String):PRIORITY? {
        when(id) {
            "C" -> return PRIORITY.CRITICAL
            "H" -> return PRIORITY.HIGH
            "N" -> return PRIORITY.NORMAL
            "L" -> return PRIORITY.LOW
            else -> return null // println("The input prio is invalid")
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

    fun updateTaskIndicesAfterDelete() {
        for(taskIndex in tasks.indices)
            tasks[taskIndex].userIndex = taskIndex +1
    }

    fun printList() {
        if(!tasks.isEmpty()) {
            printHeader()
            for(task in tasks) {
                printFooter()
                printTaskAttributes(task)
                printTaskItems(task)
            }
            printFooter()
        }
        else
            println("No tasks have been input")
    }

    private fun printHeader() {
        println("+----+------------+-------+---+---+--------------------------------------------+\n" +
                "| N  |    Date    | Time  | P | D |                   Task                     |")
    }

    private fun printFooter() {
        println("+----+------------+-------+---+---+--------------------------------------------+")
    }

    private fun printTaskAttributes(task: Task) {
        val indexSpaces = getHeadlineExtraIdentationByIndex(task.userIndex)
        print("| ${task.userIndex}$indexSpaces| ${task.date} | ${task.time} | ${task.prio.ColorSpace} | ${task.getDueTag()} |")
    }

    private fun printTaskItems(task:Task) {
        printFirstTaskItem(task.items.first())
        if(task.items.size >1 )
            for(itemIndex in 1..task.items.size-1)
                printFollowingTaskItem(task.items[itemIndex])
    }

    private fun printFirstTaskItem(item:String) {
        val splittedString = splitStringIntoSubstringsWith44Characters(item)
        println("${splittedString.first()}|")
        for(lineIndex in 1..splittedString.count()-1)
            println("|    |            |       |   |   |${splittedString[lineIndex]}|")
    }

    private fun printFollowingTaskItem(item:String) {
        val splittedString = splitStringIntoSubstringsWith44Characters(item)
        for(line in splittedString)
            println("|    |            |       |   |   |$line|")
    }

    private fun splitStringIntoSubstringsWith44Characters(longString:String): MutableList<String> {
        val lineLength = 44
        val subStringList = mutableListOf<String>()
        val requiredSubstrings = Math.ceil(longString.length.toDouble()/lineLength).toInt()
        for(round in 0..requiredSubstrings-1) {
            val fromIndex = round * lineLength
            val toIndex = Math.min(fromIndex + lineLength, longString.length)
            subStringList.add(longString.substring(fromIndex, toIndex))
        }
        // no substrings required, string too short
        if(subStringList.isEmpty())
            subStringList.add(longString)
        // fill the last string with spaces until 43 is met
        while(subStringList.last().length < lineLength)
            subStringList[subStringList.count()-1] = subStringList.last() + " "
        return subStringList
    }

    private fun getHeadlineExtraIdentationByIndex(index:Int): String {
        if(index <=9)
            return "  "
        else if(index <=99)
            return " "
        else
            return ""
    }
}

class Task(var userIndex:Int, var prio:PRIORITY, var date: LocalDate, var time:LocalTime, var items:MutableList<String>) {
    fun getDueTag():String {
        val remainingDays = System.now().toLocalDateTime(TimeZone.UTC).date.daysUntil(date)
        if(remainingDays < 0)
            return DUETAG.OVERDUE.ColorSpace
        else if(remainingDays == 0)
            return DUETAG.TODAY.ColorSpace
        else //(remainingDays > 0)
            return DUETAG.INTIME.ColorSpace
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

enum class PRIORITY(val Id:String, val ColorSpace:String) {
    CRITICAL("C", "\u001B[101m \u001B[0m"), // red
    HIGH("H", "\u001B[103m \u001B[0m"),     // yellow
    NORMAL("N", "\u001B[102m \u001B[0m"),   // green
    LOW("L", "\u001B[104m \u001B[0m")       // blue
}

enum class DUETAG(val Id:String, val ColorSpace:String) {
    INTIME("I", "\u001B[102m \u001B[0m"),   // green
    TODAY("T", "\u001B[103m \u001B[0m"),     // yellow
    OVERDUE("O", "\u001B[101m \u001B[0m"), // red

}

object JSONAdapter {
    // https://github.com/square/moshi
    val jsonFile = File("tasklist.json")

    fun readFile():String {
        val jsonFile = File("tasklist.json")
        return jsonFile.readText()
    }

    fun writeFile(content:String) {
        val jsonFile = File("tasklist.json")
        jsonFile.writeText(content)
    }

    fun load() {
        if(jsonFile.exists())
        {
            val moshi: Moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val type = Types.newParameterizedType(List::class.java, JSONTask::class.java, LocalDateTime::class.java, LocalDate::class.java)
            val taskAdapter = moshi.adapter<List<JSONTask?>>(type)
            val file = readFile()
            val tasklist = taskAdapter.fromJson(file)
            for(jsonTask in tasklist!!)
                TaskList.tasks.add(jsonTask!!.getOriginalTaskObject())
        }
    }

    fun save() {
        val moshi: Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val type = Types.newParameterizedType(List::class.java, JSONTask::class.java, LocalDateTime::class.java, LocalDate::class.java)
        val taskAdapter = moshi.adapter<List<JSONTask?>>(type)
        val jsonTasklist = mutableListOf<JSONTask>()
        for(originalTaskObject in TaskList.tasks)
            jsonTasklist.add(JSONTask(originalTaskObject))
        val json: String = taskAdapter.toJson(jsonTasklist)
        writeFile(json)
    }

}

class JSONTask() {

    var userIndex:Int? = null
    var prio:String? = null
    var date:String? = null
    var time:String? = null
    var items:MutableList<String>? = null
    //var testDate = System.now().toLocalDateTime(TimeZone.UTC).date

    constructor(task: Task): this() {
        userIndex = task.userIndex
        prio = task.prio.Id
        date = task.date.toString()
        time = task.time.toString()
        items = task.items
    }

    fun getOriginalTaskObject():Task {
        val task = Task(
            userIndex!!,
            TaskEditor.getPrioObjectById(prio!!)!!,
            LocalDate.parse(date!!),
            LocalTime.parse(time!!),
            items!!
        )
        return task
    }

    override fun toString(): String {
        return "$date $time:$time $prio $items}"
    }
}