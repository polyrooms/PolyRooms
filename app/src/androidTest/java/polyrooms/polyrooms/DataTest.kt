package polyrooms.polyrooms

import androidx.test.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import org.junit.Test
import org.junit.Assert.*

class DataTest {

    @Test
    fun testmapToRoom() {
        val emptyIntervalsResponse = ArrayList<TimeIntervalResponse>()
        emptyIntervalsResponse.add(TimeIntervalResponse(TimeResponse(0, 1), TimeResponse(0,2)))

        val reservationsResponse = ArrayList<ReservationResponse>()
        reservationsResponse.add(ReservationResponse(TimeIntervalResponse(TimeResponse(1, 1), TimeResponse(1, 2))))
        val roomResponse = RoomResponse("1", emptyIntervalsResponse, reservationsResponse)

        val emptyIntervals = ArrayList<TimeInterval>()
        emptyIntervals.add(TimeInterval(Time(Day.SUN, 1), Time(Day.SUN, 2)))

        val reservations = ArrayList<Reservation>()
        reservations.add(Reservation(TimeInterval(Time(Day.MON, 1), Time(Day.MON, 2))))
        val expected = Room("1", emptyIntervals, reservations)

        assertEquals(roomResponse.mapToRoom(), expected)
    }


    @Test
    fun testqueryRoom() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext())

        fun queryCallback(room : Room?) {
            assertEquals(room?.roomNumber, "1")
        }

        queryRoom("1", "1", ::queryCallback)
        Thread.sleep(5000)
    }

    @Test
    fun testaddReservationToRoom() {
        FirebaseApp.initializeApp(InstrumentationRegistry.getInstrumentation().getTargetContext())

        val reservation = Reservation(TimeInterval(Time(Day.SUN, 0), Time(Day.SUN, 1)))
        addReservationToRoom("1", "1", reservation)

        fun queryCallback(room : Room?) {
            assertEquals(room?.reservations?.get(room?.reservations?.size - 1), reservation)
        }

        queryRoom("1", "1", ::queryCallback)
        Thread.sleep(5000)
    }
}