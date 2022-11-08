package com.daomingedu.musicplayer.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.daomingedu.musicplayer.R
import com.daomingedu.musicplayer.db.LocalMusic

/**
 * Description
 * Created by jianhongxu on 2021/10/20
 */
class LocalMusicAdapter(data: MutableList<LocalMusic>) :
    BaseQuickAdapter<LocalMusic, BaseViewHolder>(R.layout.item_grade, data) {
    override fun convert(holder: BaseViewHolder, item: LocalMusic) {
        holder.setText(R.id.textView,item.title)
    }
}