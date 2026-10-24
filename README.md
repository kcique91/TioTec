# TíoTec Preventivo

App Android desarrollada para el caso semestral TíoTec Preventivo (MCmovil SpA) del ramo
DSY1105 Desarrollo de Aplicaciones Móviles, Duoc UC sede Alameda.

La idea de la app es que una persona sin conocimientos técnicos pueda revisar sus hábitos
de seguridad digital (respaldos, actualizaciones, contraseñas y recuperación de cuentas),
ver qué le falta mejorar y llevar un checklist de las acciones que tiene pendientes.

## Integrantes

- Cristopher Moreno
- Daniel Soto

## Tecnologías usadas

- Kotlin y Jetpack Compose con Material 3
- MVVM (ViewModel y StateFlow)
- Navigation Compose
- Room para guardar datos en el teléfono
- Retrofit para la API de consejos
- WorkManager para los recordatorios
- Coil para mostrar la foto de perfil

## Funcionalidades

- Evaluación de 8 preguntas que entrega un puntaje y recomendaciones.
- Checklist con las acciones pendientes según las respuestas, se pueden marcar como completadas.
- Historial de evaluaciones guardado en Room.
- Perfil ficticio y registro de dispositivos de prueba.
- Foto de perfil tomada con la cámara o elegida desde la galería.
- Recordatorios con notificaciones.
- Consejos de seguridad que se cargan desde una API de prueba.
- Solicitudes de asistencia simuladas.
- Formularios de registro, perfil, dispositivos y solicitudes con validaciones, mensajes de error e íconos por campo.
- Animaciones en el checklist (color de las tarjetas y barra de progreso) y en la foto de perfil.

Recursos nativos: notificaciones y cámara/galería.

## Estructura

```
app/src/main/java/duoc/appmoviles/
├── components/      componentes reutilizables
├── data/            Room (entidades, DAOs, base de datos)
├── model/           clases de datos
├── navigation/      rutas de la app
├── network/         Retrofit
├── notifications/   recordatorios
├── screens/         pantallas
├── ui/theme/        colores y tema
└── viewmodels/      lógica y estado de cada pantalla
```

## Cómo ejecutar

1. Clonar el repositorio: `git clone https://github.com/kcique91/TioTec.git`
2. Abrirlo en Android Studio y esperar que termine el Gradle Sync.
3. Crear un emulador desde Device Manager o conectar un celular Android.
4. Darle a Run.

Todos los datos de la app son ficticios.


## Checklist priorizado
Cada pregunta tiene prioridad Alta, Media o Baja. El checklist muestra primero las pendientes más importantes, deja las completadas al final y muestra la prioridad en cada tarjeta.

## Planificación
Tablero de Trello: https://trello.com/b/rR3gBaMs/tiotec