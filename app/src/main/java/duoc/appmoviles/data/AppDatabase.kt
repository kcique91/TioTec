package duoc.appmoviles.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EvaluacionEntity::class, AccionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun accionDao(): AccionDao

    companion object {
        // Guarda la única instancia de la base de datos
        @Volatile
        private var instancia: AppDatabase? = null

        // Devuelve la base de datos; la crea solo la primera vez
        fun obtener(context: Context): AppDatabase {
            return instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tiotec.db"
                ).build().also { instancia = it }
            }
        }
    }
}