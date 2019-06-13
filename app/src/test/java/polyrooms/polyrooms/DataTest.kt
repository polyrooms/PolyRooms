package polyrooms.polyrooms
import org.junit.Test
import org.junit.Assert.*

class DataTest {

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
        val room: Room = Room(roomNumber = "101", emptyIntervals = emptyIntervals, reservations = reservedIntervals)

        assertFalse(filterRoom(room, chosenTime))
    }
}