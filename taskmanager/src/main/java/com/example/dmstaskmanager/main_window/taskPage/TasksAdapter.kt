package com.example.dmstaskmanager.main_window.taskPage

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dmstaskmanager.R
import com.example.dmstaskmanager.classes.CreditType
import com.example.dmstaskmanager.classes.Task
import com.example.dmstaskmanager.classes.TaskGroup
import com.example.dmstaskmanager.classes.TaskItem
import com.example.dmstaskmanager.classes.TaskItemType
import com.example.dmstaskmanager.classes.TaskType
import com.example.dmstaskmanager.utils.getBegDay
import com.example.dmstaskmanager.utils.isZero
import com.example.dmstaskmanager.utils.months
import kotlinx.android.synthetic.main.group_task.view.clMain
import kotlinx.android.synthetic.main.group_task.view.groupTaskCountLayout
import kotlinx.android.synthetic.main.group_task.view.groupTaskSummaLayout
import kotlinx.android.synthetic.main.group_task.view.ivGroupIndicator
import kotlinx.android.synthetic.main.group_task.view.tvCount
import kotlinx.android.synthetic.main.group_task.view.tvSumma
import kotlinx.android.synthetic.main.item_task.view.cbFinish
import kotlinx.android.synthetic.main.item_task.view.imgDate
import kotlinx.android.synthetic.main.item_task.view.imgType
import kotlinx.android.synthetic.main.item_task.view.llDate
import kotlinx.android.synthetic.main.item_task.view.llMain
import kotlinx.android.synthetic.main.item_task.view.llSumma
import kotlinx.android.synthetic.main.item_task.view.tvDate
import kotlinx.android.synthetic.main.item_task.view.tvName
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import android.support.v7.util.DiffUtil
import com.example.dmstaskmanager.utils.addDays
import com.example.dmstaskmanager.utils.formatDate
import java.security.acl.Group

class DiffTaskItemsListCallback(var oldList: List<TaskItem>, var newList: List<TaskItem>) :  DiffUtil.Callback() {

    companion object {
        const val GROUP_IS_CHECKED_PAYLOAD = "GROUP_IS_CHECKED_PAYLOAD"
        const val GROUP_COUNT_PAYLOAD = "GROUP_COUNT_PAYLOAD"
        const val GROUP_SUMMA_PAYLOAD = "GROUP_SUMMA_PAYLOAD"
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areItemsTheSame(newList[newItemPosition])
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].areContentsTheSame(newList[newItemPosition])
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //return super.getChangePayload(oldItemPosition, newItemPosition)

        val bundle = Bundle()

        if ((oldList[oldItemPosition].type == TaskItemType.Group) && ((newList[newItemPosition].type == TaskItemType.Group))) {

            val oldItem = oldList[oldItemPosition].item as TaskGroup
            val newItem = newList[newItemPosition].item as TaskGroup

            if (oldItem.isExpanded != newItem.isExpanded) {
                bundle.putBoolean(GROUP_IS_CHECKED_PAYLOAD, newItem.isExpanded)
            }
            if (oldItem.itemsCount != newItem.itemsCount) {
                bundle.putInt(GROUP_COUNT_PAYLOAD, newItem.itemsCount)
            }
            if (oldItem.summa != newItem.summa) {
                bundle.putDouble(GROUP_SUMMA_PAYLOAD, newItem.summa)
            }
        }

        if (bundle.isEmpty) {
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }

        return bundle

    }
}

/**
 * task list adapter delegate
 */
interface TaskListAdapterDelegate{
    fun onTaskGroupClick(taskGroup: TaskGroup)

    fun onTaskItemClick(task: Task)

    fun onFinishCheckBoxClick(task: Task)
}


/**
 * Task list adapter
 */
class TaskListAdapter constructor(private var context: Context, val delegate: TaskListAdapterDelegate? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        GroupViewHolderDelegate,
        TaskViewHolderDelegate {

    companion object {
        private const val VIEW_TYPE_GROUP = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    var taskItemList: List<TaskItem> = listOf()

    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_GROUP -> {
                val view = LayoutInflater.from(context).inflate(R.layout.group_task,
                        parent, false)
                return TaskGroupViewHolder(view, context, this)
            }
            VIEW_TYPE_ITEM -> {
                val view = LayoutInflater.from(context).inflate(R.layout.item_task,
                        parent, false)
                return TaskViewHolder(view, context, this)
            }
        }

        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view, context, this)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (taskItemList.isEmpty()) return

        when (holder) {
            is TaskGroupViewHolder -> {
                val task = taskItemList.get(position)

                holder.updateUI(task.item as TaskGroup)
            }
            is TaskViewHolder -> {
                val task = taskItemList.get(position)

                holder.updateUI(task.item as Task)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            when (holder) {
                is TaskGroupViewHolder -> {
                    val task = taskItemList.get(position)

                    holder.updateGroup(task.item as TaskGroup , payloads.last() as Bundle)
                }
                is TaskViewHolder -> {
                    val task = taskItemList.get(position)

                    holder.updateUI(task.item as Task)
                }
            }
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        //recyclerView.addItemDecoration(RecyclerViewItemDecoration(context))
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return taskItemList.count()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        if (taskItemList.count() > position) {
            when (taskItemList[position].type) {
                TaskItemType.Group -> return VIEW_TYPE_GROUP
                TaskItemType.Task -> return VIEW_TYPE_ITEM
            }
        }

        return VIEW_TYPE_ITEM
    }

    fun updateList(newTaskItemsList: List<TaskItem>) {

        val diffResult = DiffUtil.calculateDiff(DiffTaskItemsListCallback(this.taskItemList, newTaskItemsList), true)
        diffResult.dispatchUpdatesTo(this)

        this.taskItemList = newTaskItemsList
    }

    // TaskViewHolderDelegate methods
    override fun onTaskGroupClick(taskGroup: TaskGroup) {
        delegate?.onTaskGroupClick(taskGroup)
    }

    override fun onTaskItemClick(task: Task) {
        delegate?.onTaskItemClick(task)
    }

    override fun onFinishCheckBoxClick(task: Task) {
        delegate?.onFinishCheckBoxClick(task)
    }

}

/**
 * Task item view holder delegate
 */
interface GroupViewHolderDelegate{
    fun onTaskGroupClick(taskGroup: TaskGroup)
}

interface TaskViewHolderDelegate{
    fun onTaskItemClick(task: Task)

    fun onFinishCheckBoxClick(task: Task)
}

class TaskViewHolder constructor(view: View, val context: Context, val delegate: TaskViewHolderDelegate? = null): RecyclerView.ViewHolder(view) {

    private val tvDate = view.tvDate
    private val llDate = view.llDate
    private val llSumma = view.llSumma
    private val tvSumma = view.tvSumma
    private val tvName = view.tvName
    private val imgType = view.imgType
    private val imgDate = view.imgDate
    private val cbFinish = view.cbFinish
    private val llMain = view.llMain

    fun updateUI(task: Task) {
        val type = task.type
        val name = task.name
        val date = task.date
        val summa = task.summa

        val parent_id = task.parentId

        val finish = task.finish

        tvName.text = name
        var dateFormat = SimpleDateFormat("E, dd MMMM yyyy 'г.'")
        tvDate.text = dateFormat.format(date)

        tvSumma.text = summa.toString()
        llDate.visibility = View.VISIBLE
        llSumma.visibility = View.VISIBLE

        cbFinish.isChecked = task.isFinishChecked

        when (type) {
            TaskType.Other -> {
                if (date != 0L)
                    llDate.visibility = View.VISIBLE
                else
                    llDate.visibility = View.GONE

                if (isZero(summa))
                    llSumma.visibility = View.GONE
                else
                    llSumma.visibility = View.VISIBLE

            }
            TaskType.Credit -> {
                llDate.visibility = View.VISIBLE
                llSumma.visibility = View.VISIBLE
            }
            TaskType.Arenda -> {
                llSumma.visibility = View.VISIBLE
                llDate.visibility = View.VISIBLE

                tvDate.text = formatDate(date)
            }
            TaskType.Flat, TaskType.FlatCounter -> {
                llSumma.visibility = View.GONE
                dateFormat = SimpleDateFormat("MMM yyyy 'г.'")
                val dateAndTime = Calendar.getInstance()
                dateAndTime.timeInMillis = date
                tvDate.text = months[dateAndTime.get(Calendar.MONTH)] + " " + dateAndTime.get(
                        Calendar.YEAR).toString() + " г."
            }
            else -> {
                llDate.visibility = View.GONE
                llSumma.visibility = View.GONE
            }
        }

        // Тип кредита (1-ипотека, 2-авто-кредит, 3-потребительский, 4-страхование и 5-прочие)
//        Log.d("DMS", "credit type = $type_credit")

        imgType.visibility = View.VISIBLE
        when (task.creditType) {
            CreditType.Flat -> imgType.setImageResource(R.drawable.type_credit_flat)
            CreditType.Auto -> imgType.setImageResource(R.drawable.type_credit_auto)
            CreditType.Stuff -> imgType.setImageResource(R.drawable.type_credit_things)
            CreditType.Ensure -> imgType.setImageResource(R.drawable.type_credit_ensure)
            CreditType.Parking -> imgType.setImageResource(R.drawable.type_credit_parking)

            else -> imgType.visibility = View.INVISIBLE
        }

        var curdate = Date().time

        var date_pay = getBegDay(date)
        curdate = getBegDay(curdate)

        if (type == TaskType.Arenda) {
            date_pay = addDays(date_pay, 14)
        }

        // Кол-во дней до очередного платежа
        val dayRest = ((date_pay - curdate) / (24 * 60 * 60 * 1000)).toInt() // миллисекунды / (24ч * 60мин * 60сек * 1000мс);

        Log.d("DMS", "dayRest = $dayRest")

        imgDate.visibility = View.VISIBLE

        if (dayRest >= 0) {
            if (dayRest <= 14) {
                imgDate.visibility = View.VISIBLE
                when (dayRest) {
                    0 -> imgDate.setImageResource(R.drawable.day0)
                    1 -> imgDate.setImageResource(R.drawable.day1)
                    2 -> imgDate.setImageResource(R.drawable.day2)
                    3 -> imgDate.setImageResource(R.drawable.day3)
                    4 -> imgDate.setImageResource(R.drawable.day4)
                    5 -> imgDate.setImageResource(R.drawable.day5)
                    6 -> imgDate.setImageResource(R.drawable.day6)
                    7 -> imgDate.setImageResource(R.drawable.day7)
                    8 -> imgDate.setImageResource(R.drawable.day8)
                    9 -> imgDate.setImageResource(R.drawable.day9)
                    else -> imgDate.setImageResource(R.drawable.day9plus)
                }
            } else {
                imgDate.visibility = View.GONE
            }
        } else {
            imgDate.setImageResource(R.drawable.day0)
        }

        if (dayRest <= 14 && dayRest > 7)
            tvDate.setTextColor(Color.parseColor("#FF098900"))
        else if (dayRest <= 7 && dayRest > 2)
            tvDate.setTextColor(Color.parseColor("#FF0227CC"))
        else if (dayRest <= 2)
            tvDate.setTextColor(Color.parseColor("#d63301"))
        else
            tvDate.setTextColor(Color.parseColor("#FF0D0C0C"))

        when (type) {
            TaskType.Credit, TaskType.Arenda -> {
                imgType.visibility = View.VISIBLE
                imgDate.visibility = View.VISIBLE
            }
            else -> {
                imgType.visibility = View.GONE
                imgDate.visibility = View.GONE
            }
        }

        val id = task.id

        cbFinish.tag = id.toString()
        cbFinish.setOnClickListener { view ->
            delegate?.onFinishCheckBoxClick(task)
        }

        llMain.tag = id.toString()
        llMain.setOnClickListener { view ->
            delegate?.onTaskItemClick(task)
        }
    }
}

class TaskGroupViewHolder constructor(view: View, val context: Context, val delegate: GroupViewHolderDelegate? = null): RecyclerView.ViewHolder(view) {

    private var clMain = view.clMain
    private var ivGroupIndicator = view.ivGroupIndicator
    private var tvSumma = view.tvSumma
    private var groupTaskCountLayout = view.groupTaskCountLayout
    private var tvCount = view.tvCount
    private var tvName = view.tvName
    private var groupTaskSummaLayout = view.groupTaskSummaLayout

    fun updateUI(group: TaskGroup) {

        clMain.setOnClickListener { view ->
            delegate?.onTaskGroupClick(group)
        }

        tvName.text = group.title

        ivGroupIndicator.isSelected = group.isExpanded

        val summa = group.summa
        tvSumma.text = summa.toString()
        tvCount.text = group.itemsCount.toString()

        if (summa < 0.001) {
            groupTaskSummaLayout.visibility = View.INVISIBLE
        } else {
            groupTaskSummaLayout.visibility = View.VISIBLE
        }

    }

    fun updateGroup(group: TaskGroup, bundle: Bundle) {

        if (bundle.containsKey(DiffTaskItemsListCallback.GROUP_IS_CHECKED_PAYLOAD)) {
            val isSelected = bundle.getBoolean(DiffTaskItemsListCallback.GROUP_IS_CHECKED_PAYLOAD)
            ivGroupIndicator.isSelected = isSelected
//            if (item.checked) {
//                ivGroupIndicator.animate().alpha(1f)
//                        .withStartAction { ivGroupIndicator.visibility = View.VISIBLE }
//            } else {
//                ivGroupIndicator.animate().alpha(0f)
//                        .withEndAction { ivGroupIndicator.visibility = View.GONE }
//            }

        }
        if (bundle.containsKey(DiffTaskItemsListCallback.GROUP_COUNT_PAYLOAD)) {
            val itemsCount = bundle.getInt(DiffTaskItemsListCallback.GROUP_COUNT_PAYLOAD)
            tvCount.text = itemsCount.toString()
        }

        if (bundle.containsKey(DiffTaskItemsListCallback.GROUP_SUMMA_PAYLOAD)) {
            val summa = bundle.getDouble(DiffTaskItemsListCallback.GROUP_SUMMA_PAYLOAD)
            tvSumma.text = summa.toString()
        }

        //ivGroupIndicator.isSelected = group.isExpanded
        //tvCount.text = group.itemsCount.toString()
    }
}