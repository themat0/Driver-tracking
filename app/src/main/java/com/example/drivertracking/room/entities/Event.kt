import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_table")
data class Event(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "eventID")
    var id: Long = 0L,
    @ColumnInfo(name = "event_name")
    var name: String,
    @ColumnInfo(name = "event_date")
    var date: String,
    @ColumnInfo(name = "event_time")
    var time: String,
    @ColumnInfo(name = "event_location")
    var location: String,
    @ColumnInfo(name = "event_description")
    var description: String,
    @ColumnInfo(name = "event_type")
    var type: String,
)