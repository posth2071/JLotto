package com.JLotto.JLotto.Adapter.Expandable

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.JLotto.JLotto.Activity.Dialog.DialogClass
import com.JLotto.JLotto.Activity.MainActivity
import com.JLotto.JLotto.DataBase.DBinfo
import com.JLotto.JLotto.R
import com.JLotto.JLotto.Util.Logger
import kotlinx.android.synthetic.main.dialog_groupname.view.*
import kotlinx.android.synthetic.main.dialog_listitem.view.*
import kotlinx.android.synthetic.main.dialog_qrcord.view.*

class ExpandableListAdapter(
        private val context: Context,
        private val items: List<List<DBinfo>>
) : BaseExpandableListAdapter() {

    override fun getGroupCount(): Int = items.size

    override fun getGroupId(parent: Int): Long = parent.toLong()

    override fun getGroup(parent: Int): List<DBinfo> = items[parent]

    override fun getChildrenCount(parent: Int): Int = items[parent].size

    override fun getChildId(parent: Int, child: Int): Long = child.toLong()

    override fun getChild(parent: Int, child: Int): DBinfo = items[parent][child]

    override fun isChildSelectable(parent: Int, child: Int): Boolean = true

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(parent: Int, isExpanded: Boolean, convertView: View?, parentview: ViewGroup?): View {
        var view = convertView
        if (convertView == null) {

        }
        val parentViewHolder = view?.let {
            // convertView가 Null이 아니라면 getTag()
            it.getTag() as ParentViewHolder

        } ?: let {
            // convertView가 Null이라면 setTag() 후 ViewHolder 반환
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.dialog_groupname, parentview, false)

            ParentViewHolder(view!!.dialog_GroupName, view!!.dialog_GroupImage).apply { view!!.setTag(this) }
        }

        with(parentViewHolder) {
            groupName.setText((getGroup(parent)[0].turn).toString())
            setArrow(groupImage, isExpanded)
        }
        return view!!
//            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//            val parentView = inflater.inflate(R.layout.dialog_groupname, parentview, false)
//
//            with(parentView) {
//                dialog_GroupName.setText((getGroup(parent)[0].turn).toString())
//            }

//            setArrow(parent, parentView, isExpanded)

//            return parentview
    }

    override fun getChildView(parent: Int, child: Int, isLastChild: Boolean, convertView: View?, parentView: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val childView = inflater.inflate(R.layout.dialog_listitem, parentView, false)
        val dbinfo: DBinfo = getChild(parent, child)

        childView.dialog_Turn.setText(dbinfo.turn.toString())
        childView.dialog_HallPair.setText(MainActivity.checkHallPair(dbinfo.numset))

        with(childView) {
            dialog_Turn.setText(dbinfo.turn.toString())

            dialog_HallPair.setText(MainActivity.checkHallPair(dbinfo.numset))

            dialog_delete.setOnClickListener {
                Logger.d("코틀린", "클릭 numberset [${dbinfo.numset}]")
                DialogClass(context, 5, dbinfo).show()
            }

            val numbers = dbinfo.numset.split(",")
            dialog_Num1.setImageResource(MainActivity.num_ID[numbers[0].toInt()-1])
            dialog_Num2.setImageResource(MainActivity.num_ID[numbers[1].toInt()-1])
            dialog_Num3.setImageResource(MainActivity.num_ID[numbers[2].toInt()-1])
            dialog_Num4.setImageResource(MainActivity.num_ID[numbers[3].toInt()-1])
            dialog_Num5.setImageResource(MainActivity.num_ID[numbers[4].toInt()-1])
            dialog_Num6.setImageResource(MainActivity.num_ID[numbers[5].toInt()-1])
        }


        return childView
    }

    private fun setArrow(groupImage: ImageView, isExpandable: Boolean) {
        if (isExpandable) groupImage.dialog_GroupImage.setColorFilter(Color.parseColor("#ADD8E6"))
        else groupImage.dialog_GroupImage.setColorFilter(Color.parseColor("#00BFFF"))
    }

    private class ParentViewHolder(
            val groupName: TextView,
            val groupImage: ImageView
    )

    private class ChildViewHolder(
            private val tV_Turn: TextView,
            private val tv_HallPair: TextView,
            private val btn_Delete: Button,
            private val iv_Numset: List<ImageView>
    )
}