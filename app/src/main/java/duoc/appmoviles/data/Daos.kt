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

    // Todas las acciones del checklist
    @Query("SELECT * FROM acciones ORDER BY id")
    fun obtenerTodas(): Flow<List<AccionEntity>>

    // Guarda una lista de acciones (si ya existe el id, la reemplaza)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(acciones: List<AccionEntity>)

    // Borra todas las acciones
    @Query("DELETE FROM acciones")
    suspend fun borrarTodas()

    // Borra las acciones que ya no están en la lista nueva
    @Query("DELETE FROM acciones WHERE id NOT IN (:ids)")
    suspend fun borrarExcepto(ids: List<Int>)

    // Cambia una acción a pendiente o completada
    @Query("UPDATE acciones SET completada = :completada WHERE id = :id")
    suspend fun actualizarEstado(id: Int, completada: Boolean)
}

@Dao
interface PerfilDao {

    // El perfil guardado (puede ser null si aún no se ha creado)
    @Query("SELECT * FROM perfil WHERE id = 1")
    fun obtener(): Flow<PerfilEntity?>

    // Crea el perfil o lo reemplaza si ya existe
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(perfil: PerfilEntity)
}

@Dao
interface DispositivoDao {

    // Todos los dispositivos, del más nuevo al más antiguo
    @Query("SELECT * FROM dispositivos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<DispositivoEntity>>

    // Registra un dispositivo nuevo
    @Insert
    suspend fun insertar(dispositivo: DispositivoEntity)

    // Elimina un dispositivo
    @Query("DELETE FROM dispositivos WHERE id = :id")
    suspend fun eliminar(id: Int)
}