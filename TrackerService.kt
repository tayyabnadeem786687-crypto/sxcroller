package com.reeltracker

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.accessibility.AccessibilityEvent

class TrackerService : AccessibilityService() {
    private val h = Handler(Looper.getMainLooper())
    private var cur: String? = null
    private val lastScroll = HashMap<String, Long>()

    private val tick = object : Runnable {
        override fun run() {
            val pm = getSystemService(PowerManager::class.java)
            val n = cur?.let { Store.apps[it] }
            if (n != null && pm.isInteractive) Store.add(this@TrackerService, n, 0, 1)
            h.postDelayed(this, 1000)
        }
    }

    override fun onServiceConnected() { h.post(tick) }

    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        val pkg = e.packageName?.toString() ?: return
        when (e.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> cur = pkg
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> {
                val name = Store.apps[pkg] ?: return
                cur = pkg
                val now = System.currentTimeMillis()
                // one swipe fires a burst of events; count a new reel after a short pause
                if (now - (lastScroll[pkg] ?: 0L) > 800) Store.add(this, name, 1, 0)
                lastScroll[pkg] = now
            }
        }
    }

    override fun onInterrupt() {}
    override fun onDestroy() { h.removeCallbacksAndMessages(null); super.onDestroy() }
}
