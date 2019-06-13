package polyrooms.polyrooms

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

/*
    This activity is not part of the app. It is an example used  to illustrate how the PolyRooms
    Data API is used.
 */

class DatabaseExampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val viewModel = ViewModelProviders.of(this).get(DataStore::class.java)
        val selectedTime = Time(Day.SUN, 2)

        // Adding an observer to the ViewModel to update markers whenever the database is updated
        viewModel.getBuildings(selectedTime).observe(this, Observer { buildings ->
            setMarkers(buildings)
        })

        // Getting information about a room and passing a callback to be executed once that
        // information is retrieved
        queryRoom("1", "1", ::queryCallback)

        // Adding a reservation to a room
        addReservationToRoom("1", "1",
                Reservation(TimeInterval(Time(Day.MON, 8), Time(Day.MON, 12))))

        // Reporting a room
        reportRoom("1", "1", Report("Room is not accessible"))
    }

    fun queryCallback(room : Room?) {
        print("Room query response: ")
        println(room)
    }

    fun setMarkers(buildings : List<Building>) {
        // Intentionally left empty
    }
}
