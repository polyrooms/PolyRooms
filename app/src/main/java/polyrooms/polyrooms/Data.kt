package polyrooms.polyrooms

import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

enum class Day {
    SUN, MON, TUE, WED, THU, FRI, SAT
}

data class EmptyRoom(val roomNumber : Int)

// contains time intervals in which a room is empty
data class Room(val roomNumber : Int, val emptyIntervals : List<TimeInterval>, val reservations : List<Reservation>)
data class RoomResponse(val roomNumber : Int = -1,
                        val emptyIntervals : List<TimeIntervalResponse> = List(0, {a -> TimeIntervalResponse(TimeResponse(0, 0), TimeResponse(0, 0))}),
                        val reservations : List<ReservationResponse> = List(0, {a -> ReservationResponse(TimeIntervalResponse(TimeResponse(0, 0), TimeResponse(0, 0))) }))

fun RoomResponse.mapToRoom() : Room {
    return Room(roomNumber,
            emptyIntervals.map(TimeIntervalResponse::mapToTimeInterval),
            reservations.map(ReservationResponse::mapToReservation))
}

fun RoomResponse.mapToEmptyRoom() : EmptyRoom {
    return EmptyRoom(roomNumber)
}

data class Building(val buildingNumber : Int, val rooms : List<EmptyRoom>)
data class BuildingResponse(val buildingNumber : Int = -1,
                            val rooms : List<RoomResponse>
                            = List(0, {a : Int -> RoomResponse(-1)}))

// hour ranges from 0 to 23
data class Time(val day : Day, val hour : Int)

fun Time.mapToTimeResponse() : TimeResponse {
    return when (day) {
        Day.SUN -> TimeResponse(0, hour)
        Day.MON -> TimeResponse(1, hour)
        Day.TUE -> TimeResponse(2, hour)
        Day.WED -> TimeResponse(3, hour)
        Day.THU -> TimeResponse(4, hour)
        Day.FRI -> TimeResponse(5, hour)
        Day.SAT -> TimeResponse(6, hour)
    }
}

data class TimeResponse(val day : Int = -1, val hour : Int = -1)

fun Time.compareTo(otherTime : Time) : Int {
    if (day == otherTime.day) {
        return hour - otherTime.hour
    }
    else {
        return mapToTimeResponse().day - otherTime.mapToTimeResponse().day
    }
}

fun TimeResponse.mapToTime() : Time {
    return when (day) {
        0 -> Time(Day.SUN, hour)
        1 -> Time(Day.MON, hour)
        2 -> Time(Day.TUE, hour)
        3 -> Time(Day.WED, hour)
        4 -> Time(Day.THU, hour)
        5 -> Time(Day.FRI, hour)
        6 -> Time(Day.SAT, hour)
        else -> throw RuntimeException("Unrecognized day in TimeResponse: " + day)
    }
}

data class TimeInterval(val start : Time, val finish : Time)

fun TimeInterval.mapToTimeIntervalResponse() : TimeIntervalResponse {
    return TimeIntervalResponse(start.mapToTimeResponse(), finish.mapToTimeResponse())
}

data class TimeIntervalResponse(val start : TimeResponse = TimeResponse(), val finish : TimeResponse = TimeResponse())

fun TimeIntervalResponse.mapToTimeInterval() : TimeInterval {
    return TimeInterval(start.mapToTime(), finish.mapToTime())
}

data class Reservation(val interval : TimeInterval)

fun Reservation.mapToReservationResponse() : ReservationResponse {
    return ReservationResponse(interval.mapToTimeIntervalResponse())
}

data class ReservationResponse(val interval : TimeIntervalResponse = TimeIntervalResponse())

fun ReservationResponse.mapToReservation() : Reservation {
    return Reservation(interval.mapToTimeInterval())
}

// intended for ReservationActivity for query for information about a particular room
// after the query completes, the supplied callback is called with the query result as the argument
fun queryRoom(buildingNumber : Int, roomNumber : Int, callback : (Room?) -> Any) {
    val db = FirebaseDatabase.getInstance()
    val buildings = db.getReference("buildings")
    buildings.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            // finds the room that is queried
            for (buildingData in p0.children) {
                if ((buildingData.child("buildingNumber").value as Long).toInt() == buildingNumber) {
                    val roomsData = buildingData.child("rooms")
                    for (roomData in roomsData.children) {
                        if ((roomData.child("roomNumber").value as Long).toInt() == roomNumber) {
                            val roomResponse = roomData.getValue(RoomResponse::class.java)
                            callback(roomResponse?.mapToRoom())
                        }
                    }
                }
            }
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    })
}

// updates room in database with new reservation
// this does not verify that the reservation fits into the room's empty times/unreserved times -
// that is the caller's responsibility
fun addReservationToRoom(buildingNumber : Int, roomNumber : Int, reservation : Reservation) {
    val db = FirebaseDatabase.getInstance()
    val buildings = db.getReference("buildings")
    buildings.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(p0: DataSnapshot) {
            var reservationsRef : DatabaseReference? = null
            // finds the room that is queried
            for (buildingData in p0.children) {
                if ((buildingData.child("buildingNumber").value as Long).toInt() == buildingNumber) {
                    val roomsData = buildingData.child("rooms")
                    for (roomData in roomsData.children) {
                        if ((roomData.child("roomNumber").value as Long).toInt() == roomNumber) {
                            reservationsRef = roomData.child("reservations").ref
                        }
                    }
                }
            }

            val newID = reservationsRef?.push()?.key.toString()
            reservationsRef?.child(newID)?.setValue(reservation.mapToReservationResponse())
        }

        override fun onCancelled(p0: DatabaseError) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    })
}

// intended for MapActivity to get live data of buildings with empty rooms
class DataStore : ViewModel(){
    val ITEMS: MutableLiveData<List<Building>> = MutableLiveData()

    inner class DataChangeAsyncTask: AsyncTask<Pair<DataSnapshot, Time>, String, String>() {

        fun toBuildings(dataSnapshot: DataSnapshot, selectedTime: Time): List<Building> {
            for (child in dataSnapshot.children) {
                println(child)
            }

            val data = dataSnapshot.children.mapNotNull{ buildingData ->
                var buildingNumber = (buildingData.child("buildingNumber").value as Long).toInt()
                val roomsData = buildingData.child("rooms")
                val rooms = ArrayList<RoomResponse>()
                for (roomData in roomsData.children) {
                    val roomNumber = (roomData.child("roomNumber").value as Long).toInt()
                    val emptyIntervalsData = roomData.child("emptyIntervals")
                    val emptyIntervals = emptyIntervalsData.children.mapNotNull{it.getValue(TimeIntervalResponse::class.java)}
                    println("empty intervals")
                    println(emptyIntervals)
                    val reservationsData = roomData.child("reservations")
                    val reservations = reservationsData.children.mapNotNull{it.getValue(ReservationResponse::class.java)}
                    rooms.add(RoomResponse(roomNumber, emptyIntervals, reservations))
                }
                return@mapNotNull BuildingResponse(buildingNumber, rooms)
            }
            println("response")
            println(data)

            fun BuildingResponse.mapToBuilding() : Building {
                // closure for determining which rooms are empty at the selected time
                fun filterroom(room : RoomResponse) : Boolean {
                    for (intervalResponse in room.emptyIntervals) {
                        val interval = intervalResponse.mapToTimeInterval()
                        // check if selectedTime is in the interval
                        if (interval.start.compareTo(selectedTime) <= 0 &&
                                interval.finish.compareTo(selectedTime) > 0) {
                            // check if the room is not reserved at selectedTime
                            var notReserved = true
                            for (reservationResponse in room.reservations) {
                                val reservation = reservationResponse.mapToReservation()
                                if (reservation.interval.start.compareTo(selectedTime) <= 0 &&
                                        reservation.interval.finish.compareTo(selectedTime) > 0) {
                                    notReserved = false
                                }
                            }
                            return notReserved
                        }
                    }
                    return false
                }
                val filtered = rooms.filter{filterroom(it)}

                return Building(buildingNumber, filtered.map(RoomResponse::mapToEmptyRoom))
            }

            return data.map(BuildingResponse::mapToBuilding)
        }

        override fun doInBackground(vararg ps: Pair<DataSnapshot, Time>?): String {
            val p = ps[0]
            if (p is Pair<DataSnapshot, Time>) {
                val dataSnapshot = p.first
                val selectedTime = p.second
                ITEMS.postValue(toBuildings(dataSnapshot, selectedTime))
            }
            else {
                throw RuntimeException()
            }

            return " "
        }
    }

    // intended for MapActivity to query for buildings
    fun getBuildings(selectedTime : Time): LiveData<List<Building>> {
        println("getLocations")
        if (ITEMS.value == null) {
            println("No items")
            FirebaseDatabase.getInstance().getReference("buildings")
                    .addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            println("DATA CHANGE")
                            DataChangeAsyncTask().execute(Pair(dataSnapshot, selectedTime))
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            println("FAILED TO READ DATA")
                        }
                    })
        }

        return ITEMS
    }
}