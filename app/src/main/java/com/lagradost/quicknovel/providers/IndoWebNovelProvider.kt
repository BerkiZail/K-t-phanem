package com.lagradost.quicknovel.providers

import com.lagradost.quicknovel.*
import java.util.*

class IndoWebNovelProvider : WPReader() {
    override val name = "IndoWebNovel"
    override val mainUrl = "https://indowebnovel.id"
    override val iconId = R.drawable.big_icon_boxnovel
    override val iconBackgroundId = R.color.boxNovelColor
}
