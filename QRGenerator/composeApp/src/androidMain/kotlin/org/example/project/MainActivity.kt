package org.example.project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.example.project.data.GymDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val driver = AndroidSqliteDriver(GymDatabase.Schema, applicationContext, "gym.db")
        val database = GymDatabase(driver)

        // --- ESTO ES LO QUE NOS FALTABA PARA EL CELULAR ---
        // Intentamos ver si el admin existe, si no, lo creamos
        val adminExiste = database.gymDatabaseQueries.verificarLogin("admin", "123").executeAsList()
        if (adminExiste.isEmpty()) {
            database.gymDatabaseQueries.insertarUsuario(
                nombre = "admin",
                id_rol = 1, // 1 para Admin
                qr_token = "master_token",
                password = "123"
            )
        }
        // --------------------------------------------------

        setContent {
            App(database = database)
        }
    }
}