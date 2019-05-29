package polyrooms.polyrooms

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        var hour: Int = intent.getIntExtra("hour", 0)
        var min: Int = intent.getIntExtra("min", 0)

        var day: String = intent.getStringExtra("day")

        dateandtime.setText("Time: " + hour + ":" + min + "\n" + "Day: " + day)

    }
}
