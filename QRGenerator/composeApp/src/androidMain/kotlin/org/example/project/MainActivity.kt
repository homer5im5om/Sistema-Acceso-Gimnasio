package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.example.project.data.GymDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Cámbialo para que quede exactamente así:
        val driver = AndroidSqliteDriver(GymDatabase.Schema, applicationContext, "gym_v2.db")
        val database = GymDatabase(driver)

        // 1. LLENAR CATÁLOGOS SI ESTÁN VACÍOS
        if (database.gymDatabaseQueries.contarRoles().executeAsOne() == 0L) {
            database.gymDatabaseQueries.transaction {
                database.gymDatabaseQueries.insertarRol(1, "Administrador")
                database.gymDatabaseQueries.insertarRol(2, "Socio")

                database.gymDatabaseQueries.insertarEstado(1, "Activo")
                database.gymDatabaseQueries.insertarEstado(2, "Vencido")
                database.gymDatabaseQueries.insertarEstado(3, "Suspendido")
            }
        }

        // 2. CREAR ADMIN SI NO EXISTE
        val adminExiste = database.gymDatabaseQueries.verificarLogin("admin", "123").executeAsList()
        if (adminExiste.isEmpty()) {
            database.gymDatabaseQueries.insertarUsuario("admin", "123", "master_token", 1, 1)
        }

        setContent {
            App(database = database)
        }
    }
}