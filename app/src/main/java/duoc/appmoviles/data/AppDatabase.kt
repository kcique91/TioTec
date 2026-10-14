package duoc.appmoviles.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        EvaluacionEntity::class,
        AccionEntity::class,
        PerfilEntity::class,
        DispositivoEntity::class,
        SolicitudEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun evaluacionDao(): EvaluacionDao
    abstract fun accionDao(): AccionDao
    abstract fun perfilDao(): PerfilDao
    abstract fun dispositivoDao(): DispositivoDao

    abstract fun solicitudDao(): SolicitudDao

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
                )
                    // Si cambia la versión, borra y recrea las tablas (datos de prueba)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { instancia = it }
            }
        }
    }
}