package duoc.appmoviles.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

// Pruebas de la lógica de soporte de VersionAndroid.
// El campo "eol" de la API puede venir como false, true o como una fecha.
class VersionAndroidTest : StringSpec({

    val hoy = "2026-11-23"

    "eol = false significa que tiene soporte" {
        val v = VersionAndroid(cycle = "16", eol = false)
        v.tieneSoporte(hoy) shouldBe true
        v.descripcionSoporte(hoy) shouldBe "Con soporte de seguridad"
    }

    "eol = true significa que ya no tiene soporte" {
        val v = VersionAndroid(cycle = "4.0", eol = true)
        v.tieneSoporte(hoy) shouldBe false
        v.descripcionSoporte(hoy) shouldBe "Sin soporte de seguridad"
    }

    "una fecha futura significa que tiene soporte hasta esa fecha" {
        val v = VersionAndroid(cycle = "15", eol = "2027-01-01")
        v.tieneSoporte(hoy) shouldBe true
        v.descripcionSoporte(hoy) shouldBe "Con soporte hasta 2027-01-01"
    }

    "una fecha pasada significa que perdió el soporte" {
        val v = VersionAndroid(cycle = "12", eol = "2025-03-03")
        v.tieneSoporte(hoy) shouldBe false
        v.descripcionSoporte(hoy) shouldBe "Sin soporte desde 2025-03-03"
    }

    "sin dato de eol no se asume soporte" {
        val v = VersionAndroid(cycle = "1.0")
        v.tieneSoporte(hoy) shouldBe false
        v.descripcionSoporte(hoy) shouldBe "Sin información de soporte"
    }
})
