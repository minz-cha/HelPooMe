package com.app.helpoome


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.app.helpoome.MainActivity.Companion.address
import com.app.helpoome.MainActivity.Companion.myAdpater
import com.app.helpoome.MainActivity.Companion.name
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragment() : BottomSheetDialogFragment() {

    //    lateinit var myAdpater: MyAdapater
    var datas = mutableListOf<DataClass>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var view = inflater.inflate(R.layout.bottom_sheet_dialog, null)
        val detailBtn = view.findViewById<Button>(R.id.detailBtn)
        val simpleName = view.findViewById<TextView>(R.id.simpleName)
        val simpleAddress = view.findViewById<TextView>(R.id.simpleAddress)
        val saveBtn = view.findViewById<ImageButton>(R.id.saveBtn)

        simpleName.text = name
        simpleAddress.text = address

        detailBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            startActivity(intent)
            Log.d("simpleNameCheck", name)
        }


        saveBtn.setOnClickListener {
//            if ( //checked = ){
//
            myAdpater.datas.add(DataClass(name = simpleName.text.toString(),
                address = simpleAddress.text.toString()))


            myAdpater.notifyDataSetChanged()
        }
        return view
    }
}