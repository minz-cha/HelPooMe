package com.app.helpoome

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.app.helpoome.MainActivity.Companion.address
import com.app.helpoome.MainActivity.Companion.name
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomFragment() : BottomSheetDialogFragment() {
//    lateinit var dbHelper: DBHelper
//    lateinit var sqlDB: SQLiteDatabase
//    var imm: InputMethodManager? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        var view = inflater.inflate(R.layout.bottom_sheet_dialog, null)
        val detailBtn = view.findViewById<Button>(R.id.detailBtn)
        val simpleName = view.findViewById<TextView>(R.id.simpleName)
        val simpleAddress = view.findViewById<TextView>(R.id.simpleAddress)

        simpleName.text = name
        simpleAddress.text = address

        detailBtn.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(name, "tolName")
            intent.putExtra(address, "tolAddress")
            startActivity(intent)
            Log.d("simpleNameCheck", name)
        }

        return view
    }
}