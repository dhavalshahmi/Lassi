package com.lassi.presentation.media.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lassi.R
import com.lassi.common.extenstions.hide
import com.lassi.common.extenstions.inflate
import com.lassi.common.extenstions.loadImage
import com.lassi.common.utils.Logger
import com.lassi.common.utils.getDuration
import com.lassi.data.media.MiMedia
import com.lassi.domain.media.LassiConfig
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_media.*
import java.util.*

class MediaAdapter(
    private val onImageClick: (selectedMedias: ArrayList<MiMedia>) -> Unit
) : RecyclerView.Adapter<MediaAdapter.MyViewHolder>() {
    private val logTag = MediaAdapter::class.java.simpleName
    private val images = ArrayList<MiMedia>()

    fun setList(images: ArrayList<MiMedia>?) {
        if (images != null) {
            this.images.clear()
            this.images.addAll(images)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(parent.inflate(R.layout.item_media))
    }

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(images[position])
    }

    private fun addSelected(image: MiMedia, position: Int) {
        with(LassiConfig.getConfig()) {
            if (selectedMedias.size != maxCount) {
                selectedMedias.add(image)
                notifyItemChanged(position)
            }
        }
    }

    fun removeSelected(image: MiMedia, position: Int) {
        if (LassiConfig.getConfig().selectedMedias.remove(image)) {
            Logger.d(logTag, "removeSelected ${image.path}")
            notifyItemChanged(position)
        } else {
            Logger.d(logTag, "not removeSelected ${image.path}")
        }
    }

    inner class MyViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(miMedia: MiMedia) {
            with(miMedia) {
                var isSelect = isSelected(this)
                tvFolderName.hide()
                viewAlpha.alpha = if (isSelect) 0.5f else 0.0f
                ivSelect.isVisible = isSelect

                ivFolderThumbnail.loadImage(path)
                if (duration != 0L) {
                    tvDuration.visibility = View.VISIBLE
                    tvDuration.text = getDuration(duration)
                }

                containerView.setOnClickListener {
                    isSelect = !isSelect
                    if (!isSelect) {
                        removeSelected(miMedia, adapterPosition)
                    } else {
                        addSelected(miMedia, adapterPosition)
                    }
                    onImageClick(LassiConfig.getConfig().selectedMedias)
                }
            }
        }

        private fun isSelected(image: MiMedia): Boolean {
            for (selectedImage in LassiConfig.getConfig().selectedMedias) {
                if (selectedImage.path.equals(image.path)) {
                    return true
                }
            }
            return false
        }
    }
}