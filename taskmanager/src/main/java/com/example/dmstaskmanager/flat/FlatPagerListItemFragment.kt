package com.example.dmstaskmanager.flat

import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dmstaskmanager.R
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import android.os.Bundle
import com.example.dmstaskmanager.classes.HOME
import kotlinx.android.synthetic.main.flat_pager_item.*

/**
 * Created by dima on 08.11.2018.
 */
class FlatPagerListItemFragment : Fragment() {

    var pageNumber: Int = 0
    var flat: HOME? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.flat_pager_item, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateUI()
    }

    fun updateUI(){
        if (view == null) {
            return
        }

        flat?.also { flat ->
            tvNamePage.text = flat.name
            tvTypePage.text = flat.type.title
            tvAdresPage.text = flat.adres

            try {
                val imageStream = ByteArrayInputStream(flat.foto)
                val bitmap = BitmapFactory.decodeStream(imageStream)

                imgFotoPage.setImageBitmap(bitmap)
            } catch (e: Throwable) {
            }
        }

    }

}