package com.example.dmstaskmanager.useCase

import android.content.Context
import android.database.Cursor
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.classes.CreditType
import com.example.dmstaskmanager.classes.Task
import com.example.dmstaskmanager.classes.TaskGroup
import com.example.dmstaskmanager.classes.TaskType
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GetTaskUseCase {

    var getTaskItemsListSubject: PublishSubject<List<TaskGroup>> = PublishSubject.create<List<TaskGroup>>()
    var closeTaskSubject: PublishSubject<Boolean> = PublishSubject.create<Boolean>()

    private var threadJob: Job? = null

    fun cancel() {
        if (threadJob != null && threadJob!!.isActive) {
            threadJob?.cancel()
        }
    }

    fun closeTask(context: Context, task: Task) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val db = DB(context)

            db.open()

            db.task_setFinish(task.id)

            db.close()

            closeTaskSubject.onNext(true)

        }

    }

    fun getTaskItemsList(context: Context) {

        threadJob?.cancel()

        threadJob = GlobalScope.launch {

            val listData = createTaskItemsList(context)

            getTaskItemsListSubject.onNext(listData)

        }

    }

    private fun createTaskItemsList(context: Context) : MutableList<TaskGroup> {

        val listData: MutableList<TaskGroup> = mutableListOf()

        val db = DB(context)

        db.open()

        // очищаем список

        listData.clear()

        val ct: Cursor? = db.taskType

        if (ct != null) {

            if (ct.moveToFirst()) {
                do {

                    val type = ct.getInt(ct.getColumnIndex(DB.CL_TASK_TYPE))
                    val title = ct.getString(ct.getColumnIndex(DB.CL_TASK_NAME))
                    val count = ct.getInt(ct.getColumnIndex(DB.CL_TASK_COUNT))
                    val summa = ct.getDouble(ct.getColumnIndex(DB.CL_TASK_SUMMA))

                    val taskType = TaskType.getById(type)

                    val itemsList = mutableListOf<Task>()

                    val c: Cursor? = db.getTaskByType(type)

                    if (c != null) {

                        if (c.moveToFirst()) {
                            do {

                                var id = c.getInt(c.getColumnIndex(DB.CL_ID))
                                val type = c.getInt(c.getColumnIndex(DB.CL_TASK_CREDIT_TYPE))
                                val title = c.getString(c.getColumnIndex(DB.CL_TASK_NAME))
                                val summa = c.getDouble(c.getColumnIndex(DB.CL_TASK_SUMMA))
                                val date = c.getLong(c.getColumnIndex(DB.CL_TASK_DATE))
                                var parentId = c.getInt(c.getColumnIndex(DB.CL_TASK_PARENT_ID))
                                val finish = (c.getInt(c.getColumnIndex(DB.CL_TASK_FINISH)) == 1)

                                val creditType = CreditType.getById(type + 1)

                                val newTask = Task(
                                        id = id,
                                        creditType = creditType,
                                        date = date,
                                        type = taskType,
                                        name = title,
                                        parentId = parentId,
                                        finish = finish,
                                        summa = summa)
                                itemsList.add(newTask)

                            } while (c.moveToNext())
                        }
                        c.close()
                    }

                    val newGroup = TaskGroup(type = taskType, title = title, count = count, summa = summa, items = itemsList, itemsCount = itemsList.size)

                    listData.add(newGroup)

                } while (ct.moveToNext())
            }
            ct.close()
        }

        db.close()

        return listData
    }

}

