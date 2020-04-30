package com.example.dmstaskmanager.classes

data class TaskGroup (
        var type: TaskType = TaskType.None,
        var id: Int = 0,
        var title: String = "",
        var count: Int = 0,
        var summa: Double = 0.toDouble(),

        var itemsCount: Int = 0,
        var items: List<Task> = listOf()
) {
    var isExpanded: Boolean = false
}