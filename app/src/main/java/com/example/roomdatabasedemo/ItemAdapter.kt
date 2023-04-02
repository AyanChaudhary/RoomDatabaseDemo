package com.example.roomdatabasedemo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdatabasedemo.databinding.ItemViewForRecyclerviewBinding

class ItemAdapter(private var items:ArrayList<EmployeeEntity>,
 private var updateListener : (id:Int)->Unit,
 private var deleteListener : (id:Int)->Unit):RecyclerView.Adapter<ItemAdapter.ViewHolder>(){


    class ViewHolder(val binding: ItemViewForRecyclerviewBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemViewForRecyclerviewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item=items[position]
        holder.binding.tvItemName.text=item.name
        holder.binding.tvItemEmail.text=item.email
        if(position%2==0){
            holder.binding.llRv.setBackgroundResource(R.color.lightgrey)
        }
        holder.binding.ivEditView.setOnClickListener{
            updateListener.invoke(item.id)
        }
        holder.binding.ivDeleteView.setOnClickListener{
            deleteListener.invoke(item.id)
        }

    }


}