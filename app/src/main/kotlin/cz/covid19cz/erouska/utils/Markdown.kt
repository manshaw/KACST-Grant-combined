package cz.covid19cz.erouska.utils

import android.content.Context
import android.widget.TextView
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.image.glide.GlideImagesPlugin
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.parser.Parser

class Markdown(val context: Context) {
    private val markwon by lazy {
        Markwon.builder(context)
            .usePlugin(GlideImagesPlugin.create(context))
            .usePlugin(object: AbstractMarkwonPlugin() {
                override fun configureParser(builder: Parser.Builder) {
                    builder.extensions(listOf(AutolinkExtension.create()))
                }
            })
            .build()
    }

    fun show(textView: TextView, markdown: String?) {
       markdown?.replace("\\n", "\n")?.let {
            markwon.setMarkdown(textView, it)
        }
    }
}
