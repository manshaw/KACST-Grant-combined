package arch.binding

import android.net.Uri
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import java.io.File

@BindingAdapter(value = ["imageResource"], requireAll = false)
fun setImageResourceCircular(view: ImageButton, resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter(value = ["url"], requireAll = false)
fun setUrlCircular(view: ImageView, url: String?) {
    Glide.with(view.context).load(url).into(view)
}

@BindingAdapter(value = ["uri"], requireAll = false)
fun setUriCircular(view: ImageView, uri: Uri?) {
    Glide.with(view.context).load(uri).into(view)
}

@BindingAdapter(value = ["file"], requireAll = false)
fun setFile(view: ImageView, file: File?) {
    Glide.with(view.context).load(file).into(view)
}


