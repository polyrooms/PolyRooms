package polyrooms.polyrooms

import android.animation.Animator
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.VISIBLE
import android.view.Window
import android.view.WindowManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.util.*
@RequiresApi(Build.VERSION_CODES.O) //required for day of week spinner

class MainActivity : AppCompatActivity() {

    var ghour: Int = -1
    var gmin: Int = -1
    var gday: String = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        object : CountDownTimer(850, 1000) {
            override fun onFinish() {
                loadingProgressBarHorizontal.visibility = View.GONE
                rootView.setBackgroundColor(ContextCompat.getColor(this@MainActivity, R.color.mountainMeadow))
                prIconImageView.setImageResource(R.drawable.pricon)
                startAnimation()
            }

            override fun onTick(p0: Long) {}
        }.start()


        val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

        val adapter = ArrayAdapter(
            this,
            R.layout.default_spinner_item,
            days
        )

        val day = LocalDateTime.now().dayOfWeek.value

        // Set the drop down view resource
        adapter.setDropDownViewResource(R.layout.spinner_item)

       /* spinner.adapter = adapter;

        spinner.setSelection(day)

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long){
                gday = spinner.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>){
                // nothing
            }
        }*/
    }

    private fun startAnimation() {

        prIconImageView.animate().apply {
            x(50f)//50
            y(100f)
            duration = 450
        }.setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                afterAnimationView.visibility = VISIBLE
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }
        })
    }

    fun clickTimePicker(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)

        val tpd = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->

            ghour = h
            gmin = m

            val hconverted = convertH(h)
            val mconverted = convertM(m)
            val ampm = getAP(h)
            timeButton.text = hconverted.toString() + ":" + mconverted + " " + ampm

        }),hour,minute,false)

        tpd.show()
    }

    fun clickFind(view: View) {
        if (ghour == -1){
            Toast.makeText(this, "You must select a time", Toast.LENGTH_LONG).show()
        }

        else {
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra("hour", ghour)
            intent.putExtra("day", gday)
            intent.putExtra("min", gmin)
            startActivity(intent)
        }
    }

    fun clickReport(view: View){
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
    }

    fun getAP(hour: Int): String {
        if (hour > 12){
            return "pm"
        }
        else {
            return "am"
        }
    }

    fun convertH(hour: Int): Int {
        if (hour > 12){
            return hour - 12
        }
        else {
            return hour
        }
    }

    fun convertM(min: Int): String {
        if (min < 10){
            return "0" + min
        }

        else {
            return min.toString()
        }
    }
}

