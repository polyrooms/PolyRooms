package polyrooms.polyrooms

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_reserve_dialog.*
import kotlinx.android.synthetic.main.fragment_reserve_dialog_item.view.*
import kotlin.Exception

const val ARG_ITEM_COUNT = "item_count"
const val ARG_BUILDING = "Building"
const val ARG_ROOM = "Room"
const val ARG_TIME = "Time"
const val ARG_CAPACITY = "Capacity"

/**
 *
 * A fragment that shows a list of rooms as a modal bottom sheet.
 *
 */
class ReserveDialogFragment : BottomSheetDialogFragment() {
    private var mListener: Listener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reserve_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        list.layoutManager = LinearLayoutManager(context)
        list.adapter = ItemAdapter(arguments!!.getInt(ARG_ITEM_COUNT))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = parentFragment
        if (parent != null) {
            mListener = parent as Listener
        } else {
            mListener = context as Listener
        }
    }

    override fun onDetach() {
        mListener = null
        super.onDetach()
    }

    interface Listener {
        fun onItemClicked(position: Int) {
            // nothing
        }
    }

    private inner class ViewHolder internal constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_reserve_dialog_item, parent, false)) {

        internal val roomNum: TextView = itemView.roomNum
        internal val capacity: TextView = itemView.capacity
        internal val reserveSpinner: Spinner = itemView.reserveFor
        internal val reserveButton: Button = itemView.reserveButton

        init {
            reserveButton.setOnClickListener {
                val sharedPreferences = getActivity()?.getSharedPreferences("production", Context.MODE_PRIVATE)

                val buildingNumber = (arguments!!.getSerializable(ARG_BUILDING) as Building).buildingNumber
                val roomNumber = ((arguments!!.getSerializable(ARG_ROOM) as Room).roomNumber)
                val chosenTime = arguments!!.getSerializable(ARG_TIME) as Time
                val day = chosenTime.day
                val startHour = chosenTime.hour
                val endHour : Int

                when (reserveSpinner.selectedItem) {
                    "1 hour" -> endHour = startHour + 1
                    "2 hours" -> endHour = startHour + 2
                    "3 hours" -> endHour = startHour + 3
                    else -> throw Exception("Spinner contains invalid option" + reserveSpinner.selectedItem)
                }

                addReservationToRoom(buildingNumber, roomNumber,
                        Reservation(TimeInterval(Time(day, startHour), Time(day, endHour))))

                val prefsEditor = sharedPreferences?.edit()
                val gson = Gson()
                val json = gson.toJson(Reservation(TimeInterval(Time(day, startHour), Time(day, endHour)))) // myObject - instance of MyObject
                prefsEditor?.putString("reservation", json)
                prefsEditor?.putString("building", buildingNumber)
                prefsEditor?.putString("room", roomNumber)
                prefsEditor?.apply()

                val intent = Intent(context, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    private inner class ItemAdapter internal constructor(private val mItemCount: Int) : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val chosenRoom = arguments!!.getSerializable(ARG_ROOM) as Room

            holder.roomNum.text = chosenRoom.roomNumber
            holder.capacity.text = arguments!!.getString(ARG_CAPACITY)

            val times = arrayListOf<String>()
            getReserveOptions(times, arguments!!.getSerializable(ARG_TIME) as Time, chosenRoom)

            ArrayAdapter(
                    context,
                    android.R.layout.simple_spinner_item,
                    times
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                holder.reserveSpinner.adapter = adapter
            }
        }

        override fun getItemCount(): Int {
            return mItemCount
        }

        fun getReserveOptions(times: ArrayList<String>, chosenTime: Time, chosenRoom: Room) {
            var existsEmpty = false
            val reserveOptions = arrayListOf<String>("1 hour", "2 hours", "3 hours")
            var mutableChosenTime = chosenTime.copy()

            for (interval in chosenRoom.emptyIntervals) {
                while (mutableChosenTime.compareTo(interval.start) >= 0
                        && mutableChosenTime.compareTo(interval.finish) < 0
                        && reserveOptions.isNotEmpty()) {
                    existsEmpty = true

                    times.add(reserveOptions.removeAt(0))
                    mutableChosenTime = Time(day = mutableChosenTime.day, hour = mutableChosenTime.hour + 1)
                }

                if (existsEmpty) {
                    break
                }
            }

            if (!existsEmpty) {
                val exceptionString = "Room with no empty time interval during specified time.\n" +
                        "Room: " + chosenRoom + "Time: " + chosenTime
                throw Exception(exceptionString)
            }
        }
    }

    companion object {
        
        fun newInstance(itemCount: Int, building: Building, room: Room, time: Time, capacity: String): ReserveDialogFragment =
                ReserveDialogFragment().apply {
                    arguments = Bundle().apply {
                        putInt(ARG_ITEM_COUNT, itemCount)
                        putSerializable(ARG_BUILDING, building)
                        putSerializable(ARG_ROOM, room)
                        putSerializable(ARG_TIME, time)
                        putString(ARG_CAPACITY, capacity)
                    }
                }
    }
}
