package com.reeltracker

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object Store {
    val apps = linkedMapOf(
        "com.instagram.android" to "Instagram",
        "com.zhiliaoapp.musically" to "TikTok",
        "com.ss.android.ugc.trill" to "TikTok",
        "com.facebook.katana" to "Facebook",
        "com.google.android.youtube" to "YouTube",
        "com.snapchat.android" to "Snapchat",
        "com.twitter.android" to "X",
        "com.reddit.frontpage" to "Reddit"
    )
    val names get() = apps.values.distinct()
    private fun p(c: Context) = c.getSharedPreferences("t", 0)
    private fun day() = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())

    @Synchronized
    fun add(c: Context, name: String, reels: Int, secs: Int) {
        val pr = p(c); val kr = "r_${day()}_$name"; val ks = "s_${day()}_$name"
        pr.edit().putInt(kr, pr.getInt(kr, 0) + reels).putLong(ks, pr.getLong(ks, 0) + secs).apply()
    }
    fun reels(c: Context, name: String) = p(c).getInt("r_${day()}_$name", 0)
    fun secs(c: Context, name: String) = p(c).getLong("s_${day()}_$name", 0)
}
