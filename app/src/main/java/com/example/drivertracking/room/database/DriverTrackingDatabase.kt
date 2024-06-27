
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.drivertracking.room.DAO.EventDAO
import com.example.drivertracking.room.DAO.FaceDAO

@Database(entities=[Event::class,Face::class], version = 1, exportSchema = false)
abstract class DriverTrackingDatabase: RoomDatabase() {
    abstract val eventDAO: EventDAO
    abstract val faceDAO: FaceDAO
    companion object{

        @Volatile
        private var INSTANCE: DriverTrackingDatabase? = null

        fun getInstance(context: Context): DriverTrackingDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DriverTrackingDatabase::class.java,
                        "driver_tracking_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}