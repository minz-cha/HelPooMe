package com.app.helpoome

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapater(var context: Context) : RecyclerView.Adapter<MyAdapater.ViewHolder>() {
    var datas = mutableListOf<DataClass>()

    interface OnItemClickListener : AdapterView.OnItemClickListener {
        fun onItemClick(v: View, data: DataClass, pos: Int)
    }

    private var listener: AdapterView.OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val txtName: TextView = itemView.findViewById(R.id.textView2)
        private val txtAddress: TextView = itemView.findViewById(R.id.textView3)

        fun bind(item: DataClass) {
            // 데이터를 넣어주는데,,,? 조건을 줄수있지 -> 1.dataClass에 checked 변수
            // =-> true/false
            // if item.checked ==true
            txtName.text = item.name
            txtAddress.text = item.address

            itemView.setOnClickListener {
                if (itemView.context is Activity) {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra("dataName", item.name)
                    intent.putExtra("dataAddress", item.address)
                    var activity = (itemView.context) as Activity
                    activity.startActivityForResult(intent, 100)
                }
            }
        }
    }
}
