package polyrooms.polyrooms

import android.os.Bundle
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReserveActivity : AppCompatActivity(), ReserveDialogFragment.Listener {

    private lateinit var listView: ListView
    private val listOfAvailableRooms : ArrayList<Room> = arrayListOf()
    private lateinit var adapter: ListOfRoomsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reserve)

        val building = intent.extras.get("Building") as Building

        listView = findViewById<ListView>(R.id.listOfRooms)
        adapter = ListOfRoomsAdapter(this, listOfAvailableRooms)
        listView.adapter = adapter

        for (room in building.rooms) {
            println("QUERYING ROOM: " + room.roomNumber)
            queryRoom(building.buildingNumber, room.roomNumber, ::roomQueryCallback)
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedRoom = listOfAvailableRooms[position]
            Toast.makeText(this, selectedRoom.roomNumber, Toast.LENGTH_LONG).show()
            ReserveDialogFragment.newInstance(1, building, selectedRoom,
                    intent.extras.get("Time") as Time, "35")
                    .show(supportFragmentManager, "dialog")
        }
    }

    private fun roomQueryCallback(room: Room?) {
        listOfAvailableRooms.add(room!!)
        println("CALLBACK FROM ROOM: " + room.roomNumber)
        adapter.notifyDataSetChanged()
    }
}