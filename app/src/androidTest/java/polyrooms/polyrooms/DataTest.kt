package polyrooms.polyrooms
import androidx.test.platform.app.InstrumentationRegistry
import com.google.firebase.FirebaseApp
import org.junit.Test
import org.junit.Assert.*

class DataTest {

    @Test
    fun testmapToRoom() {
        val emptyIntervalsResponse = ArrayList<TimeIntervalResponse>()
        emptyIntervalsResponse.add(TimeIntervalResponse(TimeResponse(0, 1), TimeResponse(0,2)))

        val reservationsResponse = ArrayList<ReservationResponse>()
        reservationsResponse.add(ReservationResponse(TimeIntervalResponse(TimeResponse(1, 1), TimeResponse(1, 2))))
        val roomResponse = RoomResponse("1", "35", emptyIntervalsResponse, reservationsResponse)

        val emptyIntervals = ArrayList<TimeInterval>()
        emptyIntervals.add(TimeInterval(Time(Day.SUN, 1), Time(Day.SUN, 2)))

        val reservations = ArrayList<Reservation>()
        reservations.add(Reservation(TimeInterval(Time(Day.MON, 1), Time(Day.MON, 2))))
        val expected = Room("1", "35", emptyIntervals, reservations)

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

    @Test
    fun testEmptyRoom() {
        val emptyRoom: EmptyRoom = EmptyRoom("101A")
        assertEquals(emptyRoom.roomNumber, "101A")
    }

    @Test
    fun testReport() {
        val report: Report = Report("report")
        assertEquals(report.report, "report")
    }

    @Test
    fun testBuilding() {
        val room1: EmptyRoom = EmptyRoom("1")
        val room2: EmptyRoom = EmptyRoom("2")

        val list: List<EmptyRoom> = listOf(room1, room2)
        val building: Building = Building("101A", list)
        assertEquals(building.buildingNumber, "101A")
        assertNotNull(building.rooms)
    }

    @Test
    fun testTime() {
        val day: Day = Day.SUN
        val start: Time = Time(day, 5)
        assertEquals(start.day, Day.SUN)
        assertEquals(start.hour, 5)
    }

    @Test
    fun testReservation() {
        val day: Day = Day.SUN
        val start: Time = Time(day, 5)
        val end: Time = Time(day, 6)

        val timeInterval: TimeInterval = TimeInterval(start, end)
        val reservation: Reservation = Reservation(timeInterval)

        assertEquals(reservation.interval, timeInterval)
    }

    @Test
    // test if returns false when the room is already reserved
    fun testFilterRoom() {
        val chosenTime = Time(Day.SUN, 23)
        val reservedTimeInterval = TimeInterval(Time(Day.SUN, 23), Time(Day.MON, 1))
        val emptyIntervals = arrayListOf(reservedTimeInterval,
                TimeInterval(Time(Day.TUE, 10), Time(Day.TUE, 13)))
        val reservedIntervals = arrayListOf(Reservation(reservedTimeInterval))
        val room= Room(roomNumber = "101", roomCapacity= "35", emptyIntervals = emptyIntervals, reservations = reservedIntervals)

        assertFalse(filterRoom(room, chosenTime))
    }
}