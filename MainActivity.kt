package com.reeltracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.widget.*

class MainActivity : Activity() {
    private lateinit var tv: TextView
    private val h = Handler(Looper.getMainLooper())
    private val loop = object : Runnable { override fun run() { render(); h.postDelayed(this, 1000) } }

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(48, 96, 48, 48) }
        val b = Button(this).apply {
            text = "Enable tracking (Accessibility)"
            setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        }
        tv = TextView(this).apply { textSize = 18f; setPadding(0, 32, 0, 0) }
        root.addView(b); root.addView(tv)
        setContentView(ScrollView(this).apply { addView(root) })
    }

    override fun onResume() { super.onResume(); h.post(loop) }
    override fun onPause() { super.onPause(); h.removeCallbacks(loop) }

    private fun fmt(s: Long) = "%dh %02dm %02ds".format(s / 3600, s % 3600 / 60, s % 60)

    private fun render() {
        val on = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
            ?.contains(packageName) == true
        var tr = 0; var ts = 0L
        val sb = StringBuilder()
        for (n in Store.names) {
            val r = Store.reels(this, n); val t = Store.secs(this, n)
            tr += r; ts += t
            sb.append("$n\n   $r reels  |  ${fmt(t)}\n\n")
        }
        tv.text = (if (on) "Status: ON\n\n" else "Status: OFF - tap the button, find Reel Tracker, turn it on\n\n") +
            "TODAY TOTAL\n$tr reels  |  ${fmt(ts)}\n\n" + sb
    }
}
