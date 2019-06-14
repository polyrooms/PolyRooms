package polyrooms.polyrooms

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import javax.sql.CommonDataSource

class ListOfRoomsAdapter(private val context: Context,
                         private val dataSource: ArrayList<Room>): BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
         return dataSource.size
    }

    override fun getItem(position: Int): Room {
        return dataSource[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = inflater.inflate(R.layout.reserve_row, parent, false)
        val roomNumTextView = rowView.findViewById<TextView>(R.id.roomNumTextView)
        val capacityTextView = rowView.findViewById<TextView>(R.id.capacityTextView)

        val room = getItem(position)
        roomNumTextView.text = room.roomNumber
        capacityTextView.text = room.roomCapacity

        return rowView
    }
}