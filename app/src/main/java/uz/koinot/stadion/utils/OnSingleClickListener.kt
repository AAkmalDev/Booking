package uz.koinot.stadion.utils

import android.os.SystemClock
import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {

    private val MIN_CLICK_INTERVAL: Long = 1000

    private var mLastClickTime: Long = 0

    abstract fun OnSingleClick(v: View)

    override fun onClick(v: View?) {
        val currentClickTime: Long = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - mLastClickTime

        mLastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL)
            return


        OnSingleClick(v!!)
    }
}
//mTextView.setOnClickListener(new OnSingleClickListener() {
//    @Override
//    public void onSingleClick(View v) {
//        if (DEBUG)
//            Log.i("TAG", "onclick！");
//    }
//};