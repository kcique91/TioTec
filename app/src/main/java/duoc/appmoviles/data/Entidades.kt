package duoc.appmoviles.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Tabla del historial: una fila por cada evaluación terminada
@Entity(tableName = "evaluaciones")
data class EvaluacionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fecha: Long,      // momento en que terminó, en milisegundos
    val puntaje: Int,     // porcentaje de medidas que cumple
    val brechas: Int      // cuántas preguntas respondió "No"
)

// Tabla del checklist: una fila por cada acción
@Entity(tableName = "acciones")
data class AccionEntity(
    @PrimaryKey val id: Int,
    val categoria: String,
    val texto: String,
    val completada: Boolean
)

// Tabla del perfil ficticio: siempre hay una sola fila (id = 1)
@Entity(tableName = "perfil")
data class PerfilEntity(
    @PrimaryKey val id: Int = 1,
    val nombre: String,
    val nivel: String     // Básico, Intermedio o Avanzado
)

// Tabla de dispositivos de prueba: una fila por dispositivo
@Entity(tableName = "dispositivos")
data class DispositivoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,   // por ejemplo "Celular de prueba"
    val sistema: String   // por ejemplo "Android 14"
)