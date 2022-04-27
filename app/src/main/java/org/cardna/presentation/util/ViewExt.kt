package org.cardna.presentation.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Spannable
import android.view.*
import android.widget.*
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.cardna.R
import org.cardna.CardNaApplication
import kotlin.math.roundToInt


fun AppCompatActivity.replace(@IdRes frameId: Int, fragment: Fragment) {
    supportFragmentManager
        .beginTransaction()
        .replace(frameId, fragment, null)
        .commit()
}

fun Context.convertDPtoPX(dp: Int): Int {
    val density: Float = this.resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}

@SuppressLint("ResourceType")
fun Context.showCustomDialog(@IdRes layout: Int): Dialog {
    val dialog = Dialog(this)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.setContentView(layout)
    dialog.setContentView(layout)
    dialog.setCancelable(false)
    dialog.getWindow()!!.setGravity(Gravity.CENTER)
    dialog.show()
    return dialog
}

fun Context.shortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.setSrcWithGlide(imageUrl: String, imageView: ImageView) {
    Glide
        .with(this)
        .load(imageUrl)
        .into(imageView)
}

fun SearchView.setTextColor(context: Context, hintColorInt: Int, textColorInt: Int) {
    findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setHintTextColor(context.getColor(hintColorInt))
    findViewById<EditText>(androidx.appcompat.R.id.search_src_text).setTextColor(context.getColor(textColorInt))
}

fun SearchView.setTextSize(size: Float) {
    findViewById<EditText>(androidx.appcompat.R.id.search_src_text).textSize = size
}

fun Activity.setStatusBarTransparent() {
    window.apply {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    if (Build.VERSION.SDK_INT >= 30) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }
}


fun Context.showCustomPopUp(view: ImageButton, arrayInt: Int, baseContext: Context): ListPopupWindow {

    val items = this.resources.getStringArray(arrayInt)
    val popupAdapter =
        object : ArrayAdapter<String>(baseContext, R.layout.item_detail, items) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view = super.getView(position, convertView, parent)
                val color = if (position == 0) {
                    R.color.dark_gray
                } else {
                    R.color.dark_gray
                }
                (view as TextView).setTextColor(ContextCompat.getColor(context, color))
                return view
            }
        }

    val popup = ListPopupWindow(this).apply {
        anchorView = view
        setAdapter(popupAdapter)
        setDropDownGravity(Gravity.NO_GRAVITY)
        width = measureContentWidth(baseContext, popupAdapter)
        height = ListPopupWindow.WRAP_CONTENT
        isModal = true
        val screenWidth = CardNaApplication.pixelRatio.screenWidth / 5
        val screenWidthStr = "-$screenWidth"
        horizontalOffset = screenWidthStr.toInt()
        verticalOffset = -20

        setBackgroundDrawable(ContextCompat.getDrawable(baseContext, R.drawable.img_popup))
    }
    return popup
}

fun Context.setGradientText(inputText: String): Spannable {
    val green = getColor(R.color.main_green)
    val purple = getColor(R.color.main_purple)
    val spannable = inputText.toSpannable()
    spannable[0..inputText.length] =
        LinearGradientSpan(inputText, inputText, green, purple)
    return spannable
}

 fun Context.getPageTransformer(): ViewPager2.PageTransformer {
    val compositePageTransformer = CompositePageTransformer()
    compositePageTransformer.addTransformer(MarginPageTransformer((20 * resources.displayMetrics.density).roundToInt()))

    return compositePageTransformer
}

fun Context.viewPagerAnimation(viewpager : ViewPager2) {
    val compositePageTransformer = getPageTransformer()
    with(viewpager) {
        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 1
        setPageTransformer(compositePageTransformer)
        setPadding(
            (56 * resources.displayMetrics.density).roundToInt(),
            0,
            (56 * resources.displayMetrics.density).roundToInt(),
            0
        )
        getChildAt(0).overScrollMode = androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER
    }
}