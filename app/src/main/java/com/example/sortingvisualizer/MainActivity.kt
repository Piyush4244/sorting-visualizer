package com.example.sortingvisualizer

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.sortingvisualizer.databinding.ActivityMainBinding
import com.google.android.material.slider.Slider
import kotlinx.coroutines.*
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding

    private var dHeight=0
    private var dWidth=0
    private var isSorting = false
    private var count = 1
    private var sleepTime = 0
    private lateinit var sortType: String
    private lateinit var listView: ArrayList<View>
    private lateinit var listInt: ArrayList<Int>
    private lateinit var spType: Spinner
    private lateinit var spDesign: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //binding.binding.linearlayout.setHorizontalGravity(Gravity.CENTER)
        spType=binding.spType
        spDesign=binding.spDesign
        getDimensions()
        setSpinners()
        binding.linearlayout.setHorizontalGravity(Gravity.CENTER)
        binding.linearlayout.setVerticalGravity(Gravity.TOP)

        Log.i("main","$dHeight ${(dWidth/count)*count}   $dWidth")

        binding.slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener{
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                if(!isSorting)count=slider.value.toInt()
            }

        })

        binding.btnStart.setOnClickListener {
            if(isSorting) {
                Toast.makeText(this,"A sorting algorithm is working right now",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            isSorting = true
            it.isClickable=false
            sleepTime=200
            listInt=createRandomList(count)
            listView=ArrayList<View>()
            for(i in 0 until count){
                listView.add(makeView(i))
            }
            binding.linearlayout.removeAllViews()
            for(i in 0 until count){
                binding.linearlayout.addView(listView[i])
            }
            when(sortType){
                "Bubble Sort"-> doBubbleSorting()
                "Merge Sort"-> doMergeSorting()
                "Selection Sort"->doSelectionSorting()
                "Insertion Sort"-> doInsertionSorting()
                "Quick Sort"-> doQuickSorting()
            }
            binding.btnStart.isClickable=true
        }
    }
    private fun setSpinners() {
        val adapterType:ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this,R.array.typeOfSort,android.R.layout.simple_spinner_item)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spType.adapter = adapterType

        spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) { sortType = "Bubble Sort" }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long ) { sortType = parent?.getItemAtPosition(position).toString() }
        }

        val adapterDesign:ArrayAdapter<CharSequence> =
            ArrayAdapter.createFromResource(this,R.array.typeOfGraph,android.R.layout.simple_spinner_item)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDesign.adapter = adapterDesign

        spDesign.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(parent?.getItemAtPosition(position).toString()) {
                    "Bar Graph Design" -> {
                        binding.linearlayout.setHorizontalGravity(Gravity.CENTER)
                        binding.linearlayout.setVerticalGravity(Gravity.TOP)
                    }
                    else -> { binding.linearlayout.gravity = Gravity.CENTER }
                }
            }
        }
    }

    private fun makeView(index: Int): View {
        val view = View(this@MainActivity)
        view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
        val llp = LinearLayout.LayoutParams((dWidth / count), (dHeight * listInt[index] / count))

        view.layoutParams = llp
        return view
    }

    private fun makeViewByHeight(height: Int): View {
        val view = View(this@MainActivity)
        view.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.colorAccent))
        val llp = LinearLayout.LayoutParams((dWidth / count),height)
        view.layoutParams = llp
        return view
    }

    private fun createRandomList(count:Int): ArrayList<Int> {
        val ans=ArrayList<Int>()
        for(i in 0 until count){
            ans.add((1..count).random(Random(System.nanoTime())))
        }
        return ans
    }


    private fun getDimensions() {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        dHeight = displayMetrics.heightPixels- mC(250f)
        dWidth = displayMetrics.widthPixels
    }

    private fun mC(dp: Float): Int {
        val r: Resources = this.resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            r.displayMetrics
        ).toInt()
    }

    private fun doBubbleSorting() {
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 0 until count) {
                for (j in 0 until count - i - 1) {
                    if (listInt[j] > listInt[j + 1]) {
                        withContext(Dispatchers.Main) {

                            binding.linearlayout.getChildAt(j + 1)
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        R.color.colorRed
                                    )
                                )

                            binding.linearlayout.getChildAt(j)
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        R.color.colorRed
                                    )
                                )

                        }

                        val temp1 = listInt[j]
                        listInt[j] = listInt[j + 1]
                        listInt[j + 1] = temp1

                        //delay(Duration.Companion.nanoseconds(1))

                        withContext(Dispatchers.Main) {

                            val temp2 = listView[j]
                            listView[j] = listView[j + 1]
                            listView[j + 1] = temp2

                            listView[j]
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        R.color.colorAccent
                                    )
                                )
                            listView[j + 1]
                                .setBackgroundColor(
                                    ContextCompat.getColor(
                                        this@MainActivity,
                                        R.color.colorAccent
                                    )
                                )
                            binding.linearlayout.removeAllViews()
                            (0 until count).forEach { i ->
                                binding.linearlayout.addView(listView[i])
                            }
                        }
                    }
                }
            }
        }
        isSorting=false
    }

    private fun doMergeSorting() {
        GlobalScope.launch(Dispatchers.IO) {
            mergeSort(0, count - 1)
            isSorting = false
        }
    }

    private suspend fun mergeSort(l: Int, r: Int) {
        if (l < r) {
            val m = (l+r) / 2
            mergeSort(l, m)
            mergeSort(m + 1, r)
            merge(l, m, r)
        }
    }

    private suspend fun merge(l: Int, m: Int, r: Int) {
        val n1 = m-l+1
        val n2 = r-m
        val lis1 = ArrayList<Int>()
        val lis2 = ArrayList<Int>()
        val view1 = ArrayList<View>()
        val view2 = ArrayList<View>()
        for(i in 0 until n1) {
            lis1.add(listInt[l+i])
            view1.add(listView[l+i])
            withContext(Dispatchers.Main) {
                binding.linearlayout.getChildAt(l+i)
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorRed))
            }
            //Thread.sleep(sleepTime.toLong())
            withContext(Dispatchers.Main) {
                binding.linearlayout.getChildAt(l+i)
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
            }
        }
        for(j in 0 until n2) {
            lis2.add(listInt[m+1+j])
            view2.add(listView[m+1+j])
            withContext(Dispatchers.Main) {
                binding.linearlayout.getChildAt(m+1+j)
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorRed))
            }
            //Thread.sleep(sleepTime.toLong())
            withContext(Dispatchers.Main) {
                binding.linearlayout.getChildAt(m+1+j)
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
            }
        }
        var i = 0
        var j = 0
        var k = l
        while(i<n1 && j<n2) {
            if(lis1[i]< lis2[j]) {
                listInt[k] = lis1[i]
                listView[k] = view1[i]
                i++
            } else {
                listInt[k] = lis2[j]
                listView[k] = view2[j]
                j++
            }
            k++
        }

        while(i<n1) {
            listInt[k] = lis1[i]
            listView[k] = view1[i]
            k++
            i++
        }

        while(j<n2) {
            listInt[k] = lis2[j]
            listView[k] = view2[j]
            k++
            j++
        }

        withContext(Dispatchers.Main) {
            binding.linearlayout.removeAllViews()
            (0 until count).forEach { i ->
                binding.linearlayout.addView(makeViewByHeight(listView[i].height))
            }
        }
    }
    private fun doQuickSorting() {
        GlobalScope.launch(Dispatchers.IO) {
            quickSort(0,count-1)
            for(i in listInt) Log.d("Final","$i")
            isSorting = false
        }
    }

    private suspend fun quickSort(low: Int, high: Int) {
        if(low < high) {
            val pi: Int = partition(low, high)
            quickSort(low, pi - 1)
            quickSort(pi + 1, high)
        }
    }

    private suspend fun partition(low: Int,high: Int): Int {
        val pivot = listInt[high]
        //Thread.sleep(sleepTime.toLong())
        withContext(Dispatchers.Main) {
            binding.linearlayout.getChildAt(high)
                .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorRed))
        }
        var i = (low - 1)
        var tempInt: Int
        var tempView: View
        for (j in low until high) {
            withContext(Dispatchers.Main) {
                binding.linearlayout.getChildAt(j)
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimaryDark))
            }
            //Thread.sleep(sleepTime.toLong())
            withContext(Dispatchers.Main) {
                binding.linearlayout.getChildAt(j)
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
            }
            if (listInt[j] < pivot) {
                i++
                withContext(Dispatchers.Main) {
                    binding.linearlayout.getChildAt(i)
                        .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimaryDark))
                    binding.linearlayout.getChildAt(j)
                        .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimaryDark))
                }
                tempInt = listInt[i]
                listInt[i] = listInt[j]
                listInt[j] = tempInt
                //Thread.sleep(sleepTime.toLong())
                tempView = listView[i]
                listView[i] = listView[j]
                listView[j] = tempView
                withContext(Dispatchers.Main) {
                    binding.linearlayout.getChildAt(i)
                        .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
                    binding.linearlayout.getChildAt(j)
                        .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
                    binding.linearlayout.removeAllViews()
                    (0 until count).forEach { i ->
                        binding.linearlayout.addView(listView[i])
                    }
                }
            }
        }
        i++
        withContext(Dispatchers.Main) {
            binding.linearlayout.getChildAt(i)
                .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
            binding.linearlayout.getChildAt(high)
                .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
        }
        tempInt = listInt[i]
        listInt[i] = listInt[high]
        listInt[high] = tempInt
        //Thread.sleep(sleepTime.toLong())
        tempView = listView[i]
        listView[i] = listView[high]
        listView[high] = tempView
        withContext(Dispatchers.Main) {
            binding.linearlayout.getChildAt(i)
                .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
            binding.linearlayout.getChildAt(high)
                .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
            binding.linearlayout.removeAllViews()
            (0 until count).forEach { i ->
                binding.linearlayout.addView(listView[i])
            }
        }
        return i
    }

    private fun doSelectionSorting() {
        GlobalScope.launch(Dispatchers.IO) {
            var minIndex:Int
            var tempInt:Int
            var tempView:View
            for(i in 0 until count) {
                if(i>0) {
                    listView[i-1].setBackgroundColor(
                        ContextCompat.getColor(this@MainActivity,R.color.colorRed))
                    withContext(Dispatchers.Main) {
                        binding.linearlayout.removeAllViews()
                        (0 until count).forEach { i ->
                            binding.linearlayout.addView(listView[i])
                        }
                    }
                }
                minIndex = i
                for(j in i+1 until count) {
                    withContext(Dispatchers.Main) {
                        binding.linearlayout.getChildAt(j)
                            .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                    }
                    //Thread.sleep(sleepTime.toLong())
                    if(listInt[j] < listInt[minIndex]) {
                        minIndex = j
                    }
                    withContext(Dispatchers.Main) {
                        binding.linearlayout.getChildAt(j)
                            .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
                    }
                }
                tempInt = listInt[minIndex]
                listInt[minIndex] = listInt[i]
                listInt[i] = tempInt

                tempView = listView[minIndex]
                listView[minIndex] = listView[i]
                listView[i] = tempView
            }
            binding.linearlayout.getChildAt(count-1)
                .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorRed))
            for(i in 0 until count) {
                //Thread.sleep(20L)
                binding.linearlayout.getChildAt(i)
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
            }
            isSorting = false
        }
    }

    private fun doInsertionSorting() {
        GlobalScope.launch(Dispatchers.IO) {
            var key: Int
            var keyView:View
            var j:Int
            for(i in 1 until count) {
                listView[i]
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorRed))
            }
            for(i in 1 until count) {
                //Thread.sleep(sleepTime.toLong())
                listView[i]
                    .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
                withContext(Dispatchers.Main) {
                    binding.linearlayout.removeAllViews()
                    (0 until count).forEach { i ->
                        binding.linearlayout.addView(listView[i])
                    }
                }
                key = listInt[i]
                keyView = listView[i]
                j = i-1
                while(j>=0 && listInt[j] > key) {
                    withContext(Dispatchers.Main) {
                        binding.linearlayout.getChildAt(j)
                            .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                        binding.linearlayout.getChildAt(j+1)
                            .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorPrimary))
                    }
                    //Thread.sleep(sleepTime.toLong())
                    withContext(Dispatchers.Main) {
                        binding.linearlayout.getChildAt(j)
                            .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
                        binding.linearlayout.getChildAt(j+1)
                            .setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.colorAccent))
                    }
                    listInt[j+1] = listInt[j]
                    listView[j+1] = listView[j]
                    j--
                }
                listInt[j+1] = key
                listView[j+1] = keyView
            }
            withContext(Dispatchers.Main) {
                binding.linearlayout.removeAllViews()
                (0 until count).forEach { i ->
                    binding.linearlayout.addView(listView[i])
                }
            }
            isSorting = false
        }
    }
}