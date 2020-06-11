package cz.covid19cz.erouska.ui.mydata

import android.content.Context
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import cz.covid19cz.erouska.R
import java.lang.IndexOutOfBoundsException

internal class MyDataPagerAdapter(val context: Context) : PagerAdapter() {

    override fun instantiateItem(view: View, position: Int): Any {
        val resId = when (position) {
            0 -> R.id.pageAll
            1 -> R.id.pageCritical
            else -> throw IndexOutOfBoundsException()
        }
        return view.findViewById(resId)
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.my_data_tab_all_title)
            1 -> context.getString(R.string.my_data_tab_critical_title)
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun isViewFromObject(arg0: View, arg1: Any): Boolean {
        return arg0 === arg1 as View
    }
}