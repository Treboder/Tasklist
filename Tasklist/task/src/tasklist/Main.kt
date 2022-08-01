package tasklist

fun main() {
    // write your code here

    val taskList = mutableListOf<String>()

    println("Input the tasks (enter a blank line to end):")
    while(true) {
        val newTask = readLine()!!.replace("\t", "").trim()
        if(!newTask.isEmpty())
            taskList.add(newTask)
        else
            break
    }

    if(!taskList.isEmpty())
        for(index in 0 until taskList.size) {
            if(index < 9)
                println("${index+1}  ${taskList[index]}")
            else
                println("${index+1} ${taskList[index]}")
        }
    else
        println("No tasks have been input.")


}


