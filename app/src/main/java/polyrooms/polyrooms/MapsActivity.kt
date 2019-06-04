package polyrooms.polyrooms

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val time = intent.extras.get("time") as Time

        dateandtime.setText("Time: " + time.hour + " " + "Day: " + time.day)

    }
}
