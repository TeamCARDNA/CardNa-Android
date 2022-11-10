package org.cardna.presentation.ui.maincard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.cardna.R
import org.cardna.data.remote.model.card.MainCard
import org.cardna.databinding.ItemMainCardViewBinding

class MainCardAdapter(private val clickListener: () -> Unit) :
    ListAdapter<MainCard, MainCardAdapter.ViewHolder>(MainCardComparator()) {

    inner class ViewHolder(private val binding: ItemMainCardViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(data: MainCard) {
            with(binding) {
                Glide
                    .with(itemView.context)
                    .load(data.cardImg)
                    .into(ivMainCardImage)
                tvMainCardTitle.text = data.title
                clMaincardContainer.setBackgroundResource(
                    if (data.isMe) {
                        R.drawable.bg_green_null_black_radius_2
                    } else {
                        R.drawable.bg_right_null_black_radius_2
                    }
                )
                itemView.setOnClickListener {
                    clickListener()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMainCardViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        holder.onBind(data)
    }

    class MainCardComparator : DiffUtil.ItemCallback<MainCard>() {
        override fun areItemsTheSame(
            oldItem: MainCard,
            newItem: MainCard
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MainCard,
            newItem: MainCard
        ): Boolean {
            return oldItem.id == newItem.id
        }
    }
}