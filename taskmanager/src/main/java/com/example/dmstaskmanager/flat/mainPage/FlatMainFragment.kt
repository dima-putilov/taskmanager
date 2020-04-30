package com.example.dmstaskmanager.flat.mainPage

import android.app.Activity
import android.support.v4.app.Fragment
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.speech.RecognizerIntent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.dmstaskmanager.database.DB
import com.example.dmstaskmanager.R

import com.example.dmstaskmanager.classes.HOME
import com.example.dmstaskmanager.classes.HomeType
import com.example.dmstaskmanager.utils.Navigator
import com.google.gson.Gson
import kotlinx.android.synthetic.main.flat_main_fragment.*
import com.example.dmstaskmanager.utils.NavigatorResultCode
import java.io.IOException
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayInputStream
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Bundle
import com.example.dmstaskmanager.classes.ARGUMENT_PAGE_NUMBER
import com.example.dmstaskmanager.flat.CreditListAdapter
import com.example.dmstaskmanager.utils.KeyboardUtils
import java.io.FileNotFoundException

/**
 * Created by dima on 08.11.2018.
 */
class FlatMainFragment : Fragment() {

    var pageNumber: Int = 0

    var listHomeType: List<HomeType> = HomeType.getHomeTypeList()

    private var creditListAdapter: CreditListAdapter? = null
    private var flat_id: Int = -1

    var flat: HOME? = null

    private val MICROPHONE_REQUEST_CODE = 121
    private val MICROPHONE_ADRES_REQUEST_CODE = 122

    companion object {
        fun newInstance(page: Int): FlatMainFragment {
            val pageFragment = FlatMainFragment()
            val arguments = Bundle()
            arguments.putInt(ARGUMENT_PAGE_NUMBER, page)
            pageFragment.setArguments(arguments)
            return pageFragment
        }

        fun getTitle(): String  {
            return "Объект"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageNumber = arguments.getInt(ARGUMENT_PAGE_NUMBER)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.flat_main_fragment, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setParam()

        loadData()

        setListeners()

        updateUI()

        hideKeyboard()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()


    }

    fun setParam() {
        val intent = activity.intent

        if (intent.hasExtra(Navigator.EXTRA_FLAT_KEY)) {
            val taskGson = intent.getStringExtra(Navigator.EXTRA_FLAT_KEY)
            val flat = Gson().fromJson(taskGson, HOME::class.java)
            flat_id = flat._id
        }
    }

    fun setCurrentFlat(flat: HOME) {
        if (this.flat_id != flat._id) {
            this.flat_id = flat._id
            loadData()
        }
    }

    fun loadData() {

        setTypeSpinnerAdapter()

        creditListAdapter = CreditListAdapter(activity)
        creditListAdapter?.let { adapter ->
            spCredit.adapter = adapter.getAdapter()
            val listData = adapter.listData
        }

        if (flat_id > 0) {

            val db = DB(activity)
            db.open()

            flat = db.getFlat(flat_id)
            db.close()

            flat?.also { flat ->
                etFlatName.setText(flat.name)
                etAdres.setText(flat.adres)
                etParam.setText(flat.param)
                etSumma.setText(flat.summa.toString())

                // выделяем элемент
                creditListAdapter?.listData?.let {
                    val curpos = it.indexOf(flat.credit_id)
                    spCredit.setSelection(curpos)
                }

                setCurrentType(flat.type)

                cbFinish.isChecked = flat.finish

                try {
                    val imageStream = ByteArrayInputStream(flat.foto)
                    val bitmap = BitmapFactory.decodeStream(imageStream)

                    imgFoto.setImageBitmap(bitmap)
                } catch (e: Throwable) {
                    Log.d("DMS_CREDIT", "error load foto $e")
                }

            }
        }
    }

    fun setListeners() {
//        spType.setOnItemSelectedListener(object : OnItemSelectedListener {
//            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
//                updateUI()
//            }
//
//            override fun onNothingSelected(parentView: AdapterView<*>) {
//
//            }
//        })

        imgFoto.setOnClickListener { v ->

            if (ActivityCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE), NavigatorResultCode.GalleryRequest.resultCode)
            } else {
                Navigator.navigateChooseImageFromGallery(activity, this)
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == NavigatorResultCode.GalleryRequest.resultCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Navigator.navigateChooseImageFromGallery(activity, this)
            } else {
                //do something like displaying a message that he didn't allow the app to access gallery and you wont be able to let him select from gallery
            }
        }
    }

    fun updateUI() {
        val listHomeTypes = HomeType.getHomeTypeList()
        val type = listHomeTypes.get(spType.selectedItemPosition)

        adresLayout.visibility =
                if (type == HomeType.Automobile)
                    View.GONE
                else
                    View.VISIBLE

    }

    private fun setCurrentType(curType: HomeType) {
        var curpos = -1
        for ((index, value) in listHomeType.withIndex()) {
            if (value == curType) {
                curpos = index
            }
        }

        if (curpos >= 0) {
            spType.setSelection(curpos)
        }
    }

    private fun setTypeSpinnerAdapter() {
        // адаптер
        val listTypeName = ArrayList<String>()
        for (oper in listHomeType) {
            listTypeName.add(oper.title)
        }

        val adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, listTypeName)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = adapter
    }

    // отображаем диалоговое окно для выбора даты
    fun nameMicro(v: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint))
        startActivityForResult(intent, MICROPHONE_REQUEST_CODE)

    }

    // отображаем диалоговое окно для выбора даты
    fun adresMicro(v: View) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.search_hint))
        startActivityForResult(intent, MICROPHONE_ADRES_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return

        if (requestCode == MICROPHONE_REQUEST_CODE) {
            val matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isEmpty()) {
                val str = etFlatName.getText().toString()
                etFlatName.setText(str + " " + matches[0])
            }
        }

        if (requestCode == MICROPHONE_ADRES_REQUEST_CODE) {
            val matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isEmpty()) {
                val str = etAdres.getText().toString()
                etAdres.setText(str + " " + matches[0])
            }
        }

        if (requestCode == NavigatorResultCode.GalleryRequest.resultCode) {
            var bitmap: Bitmap? = null

            if (resultCode == Activity.RESULT_OK) {
                val selectedImage: Uri = data!!.data
                try {
                    //bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, selectedImage)
                    bitmap = decodeSampledBitmapFromUri(activity, selectedImage, 100, 100)
                } catch (e: Exception) {
                    Log.d("DMS_CREDIT", "BITMAP ERROR ${e.message}")

                }
                imgFoto.setImageBitmap(bitmap)
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }


   private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight : Int) : Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight : Int = height / 2
            val halfWidth : Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromUri(context: Context, imageUri: Uri, reqWidth: Int, reqHeight: Int) : Bitmap? {
            var bitmap : Bitmap? = null
            try {
                // Get input stream of the image
                val options = BitmapFactory.Options()
                var iStream = context.contentResolver.openInputStream(imageUri)

                // First decode with inJustDecodeBounds=true to check dimensions
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(iStream, null, options)
                if (iStream != null) {
                    iStream.close()
                }
                iStream = context.contentResolver.openInputStream(imageUri)

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false
                bitmap = BitmapFactory.decodeStream(iStream, null, options)
                if (iStream != null) {
                    iStream.close()
                }
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return bitmap
    }

    private fun hideKeyboard() {
        activity?.let {
            KeyboardUtils.hideKeyboard(it, it.currentFocus)
        }
    }
}