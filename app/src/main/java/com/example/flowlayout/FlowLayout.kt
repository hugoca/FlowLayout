package com.example.flowlayout

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import java.util.*
import kotlin.math.max

class FlowLayout : ViewGroup {
    private val mHorizontalSpacing = dp2px(16) //每个item横向间距
    private val mVerticalSpacing = dp2px(8) //每个item横向间距
    private val allLines: MutableList<List<View>> = ArrayList() // 记录所有的行，一行一行的存储，用于layout
    var lineHeights: MutableList<Int> = ArrayList() // 记录每一行的行高，用于layout

    constructor(context: Context?) : super(context)

    //反射
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    //主题style
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //四个参数 自定义属性
    private fun clearMeasureParams() {
        allLines.clear()
        lineHeights.clear()
    }

    //度量
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        clearMeasureParams()
        //先度量孩子
        val selfWidth = MeasureSpec.getSize(widthMeasureSpec) //ViewGroup解析的父亲给我的宽度
        val selfHeight = MeasureSpec.getSize(heightMeasureSpec) // ViewGroup解析的父亲给我的高度
        var lineViews: MutableList<View> =
            ArrayList() //保存一行中的所有的view
        var lineWidthUsed = 0 //记录这行已经使用了多宽的size
        var lineHeight = 0 // 一行的行高
        var parentNeededWidth = 0 // measure过程中，子View要求的父ViewGroup的宽
        var parentNeededHeight = 0 // measure过程中，子View要求的父ViewGroup的高
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val childLP = childView.layoutParams
            if (childView.visibility != View.GONE) {
                //将layoutParams转变成为 measureSpec
                val childWidthMeasureSpec = getChildMeasureSpec(
                    widthMeasureSpec, paddingLeft + paddingRight,
                    childLP.width
                )
                val childHeightMeasureSpec = getChildMeasureSpec(
                    heightMeasureSpec, paddingTop + paddingBottom,
                    childLP.height
                )
                childView.measure(childWidthMeasureSpec, childHeightMeasureSpec)

                //获取子view的度量宽高
                val childMesauredWidth = childView.measuredWidth
                val childMeasuredHeight = childView.measuredHeight

                //如果需要换行
                if (childMesauredWidth + lineWidthUsed + mHorizontalSpacing > selfWidth) {

                    //一旦换行，我们就可以判断当前行需要的宽和高了，所以此时要记录下来
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)
                    parentNeededHeight += lineHeight + mVerticalSpacing
                    parentNeededWidth = max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)
                    lineViews = ArrayList()
                    lineWidthUsed = 0
                    lineHeight = 0
                }
                // view 是分行layout的，所以要记录每一行有哪些view，这样可以方便layout布局
                lineViews.add(childView)
                //每行都会有自己的宽和高
                lineWidthUsed += childMesauredWidth + mHorizontalSpacing
                lineHeight = max(lineHeight, childMeasuredHeight)

                //处理最后一行数据
                if (i == childCount - 1) {
                    allLines.add(lineViews)
                    lineHeights.add(lineHeight)
                    parentNeededHeight += lineHeight + mVerticalSpacing
                    parentNeededWidth = max(parentNeededWidth, lineWidthUsed + mHorizontalSpacing)
                }
            }
        }


        //再度量自己,保存
        //根据子View的度量结果，来重新度量自己ViewGroup
        // 作为一个ViewGroup，它自己也是一个View,它的大小也需要根据它的父亲给它提供的宽高来度量
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val realWidth =
            if (widthMode == MeasureSpec.EXACTLY) selfWidth else parentNeededWidth
        val realHeight =
            if (heightMode == MeasureSpec.EXACTLY) selfHeight else parentNeededHeight
        setMeasuredDimension(realWidth, realHeight)
    }

    //布局
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val lineCount = allLines.size
        var curL = paddingLeft
        var curT = paddingTop
        for (i in 0 until lineCount) {
            val lineViews = allLines[i]
            val lineHeight = lineHeights[i]
            for (j in lineViews.indices) {
                val view = lineViews[j]
                val left = curL
                val top = curT
                val right = left + view.measuredWidth
                val bottom = top + view.measuredHeight
                view.layout(left, top, right, bottom)
                curL = right + mHorizontalSpacing
            }
            curT += lineHeight + mVerticalSpacing
            curL = paddingLeft
        }
    }

    companion object {
        fun dp2px(dp: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp.toFloat(),
                Resources.getSystem().displayMetrics
            ).toInt()
        }
    }
}