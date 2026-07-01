package com.example.healthylife.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.healthylife.databinding.ItemSleepBinding
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.util.DateUtils

class SleepAdapter(
    private var items: List<SleepRecord>,
    private val onEdit: (SleepRecord) -> Unit,
    private val onDelete: (SleepRecord) -> Unit
) : RecyclerView.Adapter<SleepAdapter.VH>() {

    class VH(val binding: ItemSleepBinding) : RecyclerView.ViewHolder(binding.root)

    fun submit(list: List<SleepRecord>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemSleepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val rec = items[position]
        holder.binding.tvEmoji.text = when (rec.quality) {
            "Baik" -> "😴"; "Cukup" -> "🙂"; else -> "🥱"
        }
        holder.binding.tvDate.text = DateUtils.toRelativeString(rec.date)
        holder.binding.tvDetail.text = "${rec.durationHours.toInt()} jam · ${rec.quality}"
        holder.binding.btnMore.setOnClickListener { anchor ->
            PopupMenu(anchor.context, anchor).apply {
                menu.add("Edit")
                menu.add("Hapus")
                setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "Edit" -> onEdit(rec)
                        "Hapus" -> onDelete(rec)
                    }
                    true
                }
                show()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
