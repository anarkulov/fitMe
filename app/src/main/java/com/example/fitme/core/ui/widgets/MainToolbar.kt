package com.example.fitme.core.ui.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.example.fitme.R
import com.example.fitme.core.extentions.fetchColor
import com.example.fitme.core.extentions.invisible

class MainToolbar : AppBarLayout {

    init { inflate(context, R.layout.view_toolbar, this) }

    var title = ""
        set(value) {
            field = value
            findViewById<TextView>(R.id.title).text = value

        }

    private var textColor = 0
        set(value) {
            field = value
            if (field != 0) {
                findViewById<TextView>(R.id.title).setTextColor(value)
            }

        }

    var bgColor = 0
        set(value) {
            field = value
            setBackgroundColor(value)
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs) }

    private fun init(attrs: AttributeSet) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.MainToolbar, 0, 0)
        try {
            title = ta.getString(R.styleable.MainToolbar_mt_title) ?: ""
            bgColor = ta.getInt(R.styleable.MainToolbar_mt_background, 0)
            textColor = ta.getInt(R.styleable.MainToolbar_mt_textColor, 0)
        } finally {
            ta.recycle()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        setBackgroundColor(bgColor)
        findViewById<TextView>(R.id.title).text = title

        if (textColor != 0) {
            findViewById<TextView>(R.id.title).setTextColor(textColor)
        }
    }

    fun bind(
        leftButton: ActionInfo? = null,
        rightButton: ActionInfo? = null,
        layout: LayoutInfo? = null
    ) {
        val ivButtonLeft = findViewById<ImageView>(R.id.action_left)
        val ivButtonRight = findViewById<ImageView>(R.id.action_right)
        ivButtonLeft.invisible = leftButton == null
        leftButton?.let {
            ivButtonLeft.setImageDrawable(getDrawable(it.iconRes))
            if (it.iconTint != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ivButtonLeft.imageTintList = ColorStateList.valueOf(context.fetchColor(it.iconTint))
                }
            }
            ivButtonLeft.setOnClickListener { leftButton.onClick() }
        }

        ivButtonRight.invisible = rightButton == null
        rightButton?.let {
            ivButtonRight.setImageDrawable(getDrawable(it.iconRes))
            if (it.iconTint != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ivButtonRight.imageTintList = ColorStateList.valueOf(context.fetchColor(it.iconTint))
                }
            }
            ivButtonRight.setOnClickListener { rightButton.onClick() }
        }

        layout?.let {
            if (it.backgroundColor != 0) {
                this.setBackgroundColor(context.fetchColor(it.backgroundColor))
                findViewById<RelativeLayout>(R.id.app_bar_layout).setBackgroundColor(context.fetchColor(it.backgroundColor))
            }

            if (it.title != 0) {
                findViewById<TextView>(R.id.title).text = context.getString(it.title)
            }
        }
    }

    private fun getDrawable(iconRes: Int?) : Drawable? {
        val autoMirrored = ContextCompat.getDrawable(context,iconRes ?: R.drawable.ic_back)
        autoMirrored?.isAutoMirrored = true
        return autoMirrored
    }

    data class ActionInfo(
        @DrawableRes val iconRes: Int? = null,
        @ColorRes val iconTint: Int = 0,
        val onClick: () -> Unit
    )

    data class LayoutInfo(
            @ColorRes val backgroundColor: Int = 0,
            @StringRes val title: Int = 0
    )
}
