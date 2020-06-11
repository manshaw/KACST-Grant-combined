package arch.view

import android.content.Intent
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import arch.event.LiveEvent
import arch.event.NavigationEvent
import arch.extensions.navigate
import arch.viewmodel.BaseArchViewModel
import cz.stepansonsky.mvvm.BR
import cz.stepansonsky.mvvm.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.reflect.KClass

/**
 * Created by Stepan on 06.10.2016.
 */

abstract class BaseArchActivity<B : ViewDataBinding, out VM : BaseArchViewModel>(@LayoutRes var layoutId: Int, viewModelClass: KClass<VM>) :
    AppCompatActivity() {

    protected val viewModel: VM by viewModel(viewModelClass)
    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(viewModel)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this
        binding.setVariable(BR.lifecycle, this)

        binding.setVariable(BR.vm, viewModel)

        subscribe(NavigationEvent::class, Observer { event ->
            navController().navigate(event)
        })
    }

    protected fun <T : LiveEvent> subscribe(eventClass: KClass<T>, eventObserver: Observer<T>) {
        viewModel.subscribe(this, eventClass, eventObserver)
    }

    override fun onDestroy() {
        lifecycle.removeObserver(viewModel)
        super.onDestroy()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    protected fun navigate(@IdRes resId: Int, args: Bundle? = null, navOptions: NavOptions? = null) {
        navController().navigate(resId, args, navOptions)
    }

    protected fun navigate(directions: NavDirections, navOptions: NavOptions? = null) {
        navController().navigate(directions, navOptions)
    }

    protected fun navController(): NavController {
        return findNavController(R.id.nav_host_fragment)
    }
}
