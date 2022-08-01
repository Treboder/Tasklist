package tasklist

fun main() {

    do {
        println("Input an action (add, print, end):")
        var option = readLine()
        when(option) {
            "add" -> Interpreter.addNewTask()
            "print" -> Interpreter.printTaskList()
            "end" -> println("Tasklist exiting!")
            else -> println("The input action is invalid")
        }
    } while (option != "end")

}

object TaskList {

    val multiLineTasks = mutableListOf<MutableList<String>>()

}

object Interpreter {

    fun addNewTask() {
        println("Input a new task (enter a blank line to end):")
        var multiLineTask = mutableListOf<String>()
        while(true) {
            val newTask = readLine()!!.replace("\t", "").trim()
            if(!newTask.isEmpty())
                multiLineTask.add(newTask)
            else
                break
        }
        if(!multiLineTask.isEmpty())
            TaskList.multiLineTasks.add(multiLineTask)
        else
            println("The task is blank")
    }

    fun printTaskList() {
        if(!TaskList.multiLineTasks.isEmpty())
            for(taskIndex in 0 until TaskList.multiLineTasks.size) {
                val ident = getIdentationByIndex(taskIndex)
                println("${taskIndex+1} ${ident}${TaskList.multiLineTasks[taskIndex].first()}")
                for(itemIndex in 1 until TaskList.multiLineTasks[taskIndex].size)
                    println("$ident  ${TaskList.multiLineTasks[taskIndex][itemIndex]}")
                println()
            }
        else
            println("No tasks have been input")

    }

    fun getIdentationByIndex(index:Int): String {
        if(index < 9)
            return " "
        else
            return ""
    }
}


