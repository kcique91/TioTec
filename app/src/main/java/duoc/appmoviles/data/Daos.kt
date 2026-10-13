package duoc.appmoviles.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EvaluacionDao {

    // Guarda una evaluación terminada
    @Insert
    suspend fun insertar(evaluacion: EvaluacionEntity)

    // Todas las evaluaciones, de la más nueva a la más antigua
    @Query("SELECT * FROM evaluaciones ORDER BY fecha DESC")
    fun obtenerTodas(): Flow<List<EvaluacionEntity>>
}

@Dao
interface AccionDao {


    // Borra las acciones que ya no están en la lista nueva
    @Query("DELETE FROM acciones WHERE id NOT IN (:ids)")
    suspend fun borrarExcepto(ids: List<Int>)

    // Todas las acciones del checklist
    @Query("SELECT * FROM acciones ORDER BY id")
    fun obtenerTodas(): Flow<List<AccionEntity>>

    // Guarda una lista de acciones (si ya existe el id, la reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(acciones: List<AccionEntity>)

    // Borra todas las acciones
    @Query("DELETE FROM acciones")
    suspend fun borrarTodas()

    // Cambia una acción a pendiente o completada
    @Query("UPDATE acciones SET completada = :completada WHERE id = :id")
    suspend fun actualizarEstado(id: Int, completada: Boolean)
}

