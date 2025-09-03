package com.example.textexplainer

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ExplainTileService : TileService() {
    override fun onStartListening() {
        qsTile?.state = Tile.STATE_ACTIVE
        qsTile?.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val clip = (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).primaryClip
        val text = clip?.getItemAt(0)?.coerceToText(this)?.toString()?.trim()
        val i = Intent(this, ExplainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("text", text ?: "")
        }
        startActivityAndCollapse(i)
    }
}
