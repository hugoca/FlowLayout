package com.example.flowlayout

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : Activity() {
    private var mSearchList: MutableList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initList()
        initView()
    }

    fun initList(){
        mSearchList.add("奥迪")
        mSearchList.add("保时捷")
        mSearchList.add("阿斯顿·马丁")
        mSearchList.add("马自达")
        mSearchList.add("劳斯莱斯")
        mSearchList.add("玛莎拉蒂")
        mSearchList.add("沃尔沃")
        mSearchList.add("奔驰")
        mSearchList.add("丰田")
        mSearchList.add("兰博基尼")
    }

    @SuppressLint("ResourceAsColor")
    fun initView(){
        for (i in 0 until mSearchList.size){
            var view=TextView(this)
            view.setText(mSearchList.get(i))
            view.setPadding(15,10,15,10)
            view.setTextColor(R.color.colorText)
            view.setBackgroundResource(R.drawable.shape_button_circular)
            flow_layout1.addView(view)
        }
    }
}