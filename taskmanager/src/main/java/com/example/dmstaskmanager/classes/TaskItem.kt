package com.example.dmstaskmanager.classes

enum class TaskItemType(type: Int){
    Task (0),
    Group (1)
}

data class TaskItem (
    var type: TaskItemType,
    var item: Any?
) {
    fun areItemsTheSame(newTask: TaskItem) : Boolean {
        return if (this.type == newTask.type) {
            if (this.type == TaskItemType.Group) {
                (this.item as TaskGroup).type == (newTask.item as TaskGroup).type
            } else {
                (this.item as Task).id == (newTask.item as Task).id
            }
        } else {
            false
        }
    }

    fun areContentsTheSame(newTask: TaskItem) : Boolean {
        return if (this.type == newTask.type) {
            if (this.type == TaskItemType.Group) {
                val oldItem = this.item as TaskGroup
                val newItem = newTask.item as TaskGroup
                oldItem.type == newItem.type
                        &&
                        oldItem.isExpanded == newItem.isExpanded
                        &&
                        oldItem.itemsCount == newItem.itemsCount
                        &&
                        oldItem.summa == newItem.summa
            } else {
                val oldItem = this.item as Task
                val newItem = newTask.item as Task

                oldItem.id == newItem.id
                        &&
                        oldItem.name == newItem.name
                        &&
                        oldItem.type == newItem.type
                        &&
                        oldItem.date == newItem.date
                        &&
                        oldItem.summa == newItem.summa
                        &&
                        oldItem.parentId == newItem.parentId
                        &&
                        oldItem.isFinishChecked == newItem.isFinishChecked
                        &&
                        oldItem.updateRevision == newItem.updateRevision
            }
        } else {
            false
        }
    }
}