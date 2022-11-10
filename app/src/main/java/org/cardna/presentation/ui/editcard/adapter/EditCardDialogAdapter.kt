package org.cardna.presentation.ui.editcard.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.cardna.R
import org.cardna.data.remote.model.card.CardData
import org.cardna.databinding.ItemEditCardDialogBinding
import org.cardna.presentation.ui.editcard.viewmodel.EditCardViewModel
import timber.log.Timber

class EditCardDialogAdapter(
    val lifecycleOwner: LifecycleOwner,
    val editCardViewModel: EditCardViewModel
) :
    ListAdapter<CardData, EditCardDialogAdapter.ViewHolder>(EditCardDialogComparator()) {


    inner class ViewHolder(private val binding: ItemEditCardDialogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun onBind(data: CardData) {
            with(binding) {
                Glide
                    .with(itemView.context)
                    .load(data.cardImg)
                    .into(ivCardpackRecyclerview)
                tvEditcarddialogTitle.text = data.title
                clRvItem.setBackgroundResource(setBackground(data.isMe))
            }

            editCardViewModel.selectedCardList.observe(lifecycleOwner) { selectedCardList ->
                if (selectedCardList.contains(data.id)) {
                    binding.tvRepresentcardCount.text =
                        (selectedCardList.indexOf(data.id) + 1).toString()
                    binding.tvRepresentcardCount.visibility = View.VISIBLE
                } else {
                    binding.tvRepresentcardCount.visibility = View.INVISIBLE
                }
            }

            editCardViewModel.selectedCardList.observe(lifecycleOwner) { list ->
                itemView.setOnClickListener {
                    binding.tvRepresentcardCount.apply {
                        visibility = if (visibility == View.INVISIBLE && list.size < 7) {
                            editCardViewModel.setAddCard(data.id)
                            text = list.size.toString()
                            View.VISIBLE
                        } else {
                            if (visibility == View.VISIBLE) {
                                editCardViewModel.setDeleteCard(data.id)
                            }
                            View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: EditCardDialogAdapter.ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EditCardDialogAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemEditCardDialogBinding.inflate(layoutInflater, parent, false)

        return ViewHolder(binding)
    }

    class EditCardDialogComparator : DiffUtil.ItemCallback<CardData>() {
        override fun areItemsTheSame(oldItem: CardData, newItem: CardData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CardData, newItem: CardData): Boolean {
            return oldItem == newItem
        }
    }

    private fun setBackground(isMe: Boolean): Int {
        return if (isMe) {
            R.drawable.bg_main_green_radius_8
        } else {
            R.drawable.bg_main_purple_radius_8
        }
    }
}