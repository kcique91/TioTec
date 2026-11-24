package duoc.appmoviles.viewmodels

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

// Pruebas de la validación del formulario de Registro (UsuarioViewModel).
// No necesita mocks: la lógica no depende de red ni de base de datos.
class UsuarioViewModelTest : StringSpec({

    "un formulario vacío no es válido y marca todos los errores" {
        val vm = UsuarioViewModel()

        vm.validarFormulario() shouldBe false
        vm.estado.value.errores.nombre shouldBe "Campo obligatorio"
        vm.estado.value.errores.correo shouldBe "Correo inválido"
        vm.estado.value.errores.clave shouldBe "Debe tener al menos 6 caracteres"
        vm.estado.value.errores.direccion shouldBe "Campo obligatorio"
    }

    "un formulario completo y correcto es válido" {
        val vm = UsuarioViewModel()
        vm.onNombreChange("Usuario Prueba")
        vm.onCorreoChange("prueba@correo.cl")
        vm.onClaveChange("123456")
        vm.onDireccionChange("Calle Falsa 123")
        vm.onAceptarTerminosChange(true)

        vm.validarFormulario() shouldBe true
        vm.estado.value.errores.nombre.shouldBeNull()
        vm.estado.value.aceptaTerminos shouldBe true
    }

    "un correo sin @ no es válido" {
        val vm = UsuarioViewModel()
        vm.onCorreoChange("correo.cl")

        vm.validarFormulario()

        vm.estado.value.errores.correo shouldBe "Correo inválido"
    }

    "escribir en un campo borra su error" {
        val vm = UsuarioViewModel()
        vm.validarFormulario()

        vm.onNombreChange("U")
        vm.onCorreoChange("a@b")
        vm.onClaveChange("1")
        vm.onDireccionChange("D")

        vm.estado.value.errores.nombre.shouldBeNull()
        vm.estado.value.errores.correo.shouldBeNull()
        vm.estado.value.errores.clave.shouldBeNull()
        vm.estado.value.errores.direccion.shouldBeNull()
    }
})
