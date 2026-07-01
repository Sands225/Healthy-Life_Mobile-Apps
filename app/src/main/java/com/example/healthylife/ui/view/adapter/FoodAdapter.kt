package com.example.healthylife.ui.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.healthylife.databinding.ItemFoodBinding
import com.example.healthylife.model.Food
import com.example.healthylife.util.DateUtils

class FoodAdapter(
    private var items: List<Food>,
    private val onEdit: (Food) -> Unit,
    private val onDelete: (Food) -> Unit
) : RecyclerView.Adapter<FoodAdapter.VH>() {

    class VH(val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root)

    fun submit(list: List<Food>) {
        items = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val f = items[position]
        holder.binding.tvEmoji.text = f.emoji
        holder.binding.tvName.text = f.name
        holder.binding.tvMacros.text =
            "${f.calories} cal · K:${f.carbs.toInt()}g P:${f.protein.toInt()}g L:${f.fat.toInt()}g S:${f.fiber.toInt()}g"
        holder.binding.tvMeta.text = "${f.mealType} · ${DateUtils.toRelativeString(f.date)}"
        holder.binding.btnMore.setOnClickListener { anchor ->
            PopupMenu(anchor.context, anchor).apply {
                menu.add("Edit")
                menu.add("Hapus")
                setOnMenuItemClickListener { item ->
                    when (item.title) {
                        "Edit" -> onEdit(f)
                        "Hapus" -> onDelete(f)
                    }
                    true
                }
                show()
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
