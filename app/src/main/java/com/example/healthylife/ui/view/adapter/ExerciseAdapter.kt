package com.example.healthylife.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.healthylife.databinding.ItemExerciseBinding
import com.example.healthylife.model.Exercise
import com.example.healthylife.util.DateUtils

class ExerciseAdapter(
    private var items: List<Exercise>,
    private val onEdit: (Exercise) -> Unit,
    private val onDelete: (Exercise) -> Unit
) : RecyclerView.Adapter<ExerciseAdapter.VH>() {

    class VH(val binding: ItemExerciseBinding) : RecyclerView.ViewHolder(binding.root)

    fun submit(list: List<Exercise>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemExerciseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ex = items[position]
        holder.binding.tvEmoji.text = ex.emoji
        holder.binding.tvName.text = ex.name
        holder.binding.tvDetail.text = "${ex.durationMinutes} menit · ${ex.caloriesBurned} cal"
        holder.binding.tvDate.text = DateUtils.toRelativeString(ex.date)
        holder.binding.btnMore.setOnClickListener { anchor ->
            PopupMenu(anchor.context, anchor).apply {
                menu.add("Edit")
                menu.add("Hapus")
                setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "Edit" -> onEdit(ex)
                        "Hapus" -> onDelete(ex)
                    }
                    true
                }
                show()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
