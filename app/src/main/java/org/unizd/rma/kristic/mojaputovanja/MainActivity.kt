package org.unizd.rma.kristic.mojaputovanja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.unizd.rma.kristic.mojaputovanja.data.PutovanjeDatabase
import org.unizd.rma.kristic.mojaputovanja.repository.PutovanjeRepository
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModelFactory
import org.unizd.rma.kristic.mojaputovanja.ui.PutovanjeScreen
import org.unizd.rma.kristic.mojaputovanja.ui.DodajPutovanjeScreen

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PutovanjeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Povežemo bazu i repository
        val dao = PutovanjeDatabase.getDatabase(application).putovanjeDao()
        val repository = PutovanjeRepository(dao)
        val factory = PutovanjeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PutovanjeViewModel::class.java]

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "lista") {
                    composable("lista") {
                        PutovanjeScreen(viewModel = viewModel) {
                            navController.navigate("dodaj")
                        }
                    }
                    composable("dodaj") {
                        DodajPutovanjeScreen(viewModel = viewModel) {
                            navController.popBackStack() // Vrati se na listu nakon spremanja
                        }
                    }
                }
            }
        }
    }
}
