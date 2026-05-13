package org.example.project

import android.content.Context // Importante agregar este import
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.example.project.data.GymDatabase

class MainActivity : ComponentActivity() {

    // ESTO ES NUEVO: Guardamos el contexto para que ExportUtils pueda usarlo
    companion object {
        var appContext: Context? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Asignamos el contexto de la aplicación
        appContext = applicationContext

        val driver = AndroidSqliteDriver(GymDatabase.Schema, applicationContext, "gym_v2.db")
        val database = GymDatabase(driver)

        // Llenar catálogos
        if (database.gymDatabaseQueries.contarRoles().executeAsOne() == 0L) {
            database.gymDatabaseQueries.transaction {
                database.gymDatabaseQueries.insertarRol(1, "Administrador")
                database.gymDatabaseQueries.insertarRol(2, "Socio")
                database.gymDatabaseQueries.insertarEstado(1, "Activo")
                database.gymDatabaseQueries.insertarEstado(2, "Vencido")
                database.gymDatabaseQueries.insertarEstado(3, "Suspendido")
            }
        }

        // Crear admin
        val adminExiste = database.gymDatabaseQueries.verificarLogin("admin", "123").executeAsList()
        if (adminExiste.isEmpty()) {
            database.gymDatabaseQueries.insertarUsuario("admin", "123", "master_token", 1, 1)
        }

        setContent {
            App(database = database)
        }
    }
}