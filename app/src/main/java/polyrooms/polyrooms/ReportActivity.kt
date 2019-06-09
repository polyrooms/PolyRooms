package polyrooms.polyrooms

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_report.*
import android.content.Intent
import android.provider.Settings.Secure

class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        }

    fun onSubmit(view : View) {

        val buildingNum: String = buildingNum?.text.toString().trim()
        val roomNum:String = roomNum?.text.toString().trim()
        val description = descrpition?.text.toString().trim()

        if (!checkBuildingNum(buildingNum)) {
            Toast.makeText(this, "You must enter a building number.", Toast.LENGTH_LONG).show()
        } else if (!checkRoomNum(roomNum)) {
            Toast.makeText(this, "You must enter a room number.", Toast.LENGTH_LONG).show()
        } else if (!checkDescription(description)) {
            Toast.makeText(this, "You must enter a description.", Toast.LENGTH_LONG).show()
        } else { // everything gucci time to submit
            val report: Report = Report(description)

            reportRoom(buildingNum, roomNum, report)
            Toast.makeText(this, "Thank you, your report has been sent.", Toast.LENGTH_LONG).show()
            /*
            val emailIntent = Intent(Intent.ACTION_SEND)
            val android_id = Secure.getString(getApplicationContext().getContentResolver(), Secure.ANDROID_ID)
            emailIntent.type = "message/rfc822"
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("polyrooms@gmail.com"))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Room Issue Report from $android_id")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Building: $buildingNum\nRoom: $roomNum\nDescription: $description")
            try {
                startActivity(Intent.createChooser(emailIntent, "Send mail..."))
            } catch (ex: android.content.ActivityNotFoundException) {
                Toast.makeText(this@ReportActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show()
            }*/

            finish()
        }
    }
}

fun checkBuildingNum(bnum : String?): Boolean {
    if (bnum.isNullOrBlank()){
        return false
    }
    return true
}

fun checkRoomNum(rnum : String?): Boolean {
    if (rnum.isNullOrBlank()){
        return false
    }
    return true
}

fun checkDescription(desc : String?): Boolean {
    if (desc.isNullOrBlank()){
        return false
    }
    return true
}


