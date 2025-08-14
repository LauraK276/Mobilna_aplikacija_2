package org.unizd.rma.kristic.mojaputovanja

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import org.unizd.rma.kristic.mojaputovanja.data.PutovanjeDatabase
import org.unizd.rma.kristic.mojaputovanja.repository.PutovanjeRepository
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModel
import org.unizd.rma.kristic.mojaputovanja.viewmodel.PutovanjeViewModelFactory
import org.unizd.rma.kristic.mojaputovanja.ui.*

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: PutovanjeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dao = PutovanjeDatabase.getDatabase(application).putovanjeDao()
        val repository = PutovanjeRepository(dao)
        val factory = PutovanjeViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[PutovanjeViewModel::class.java]

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "lista") {
                    composable("lista") {
                        PutovanjeScreen(
                            viewModel = viewModel,
                            onDodajClick = { navController.navigate("dodaj") },
                            onOpenDetalji = { id -> navController.navigate("detalji/$id") }
                        )
                    }
                    composable("dodaj") {
                        DodajPutovanjeScreen(viewModel) { navController.popBackStack() }
                    }
                    composable(
                        route = "detalji/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0
                        DetaljiPutovanjaScreen(
                            id = id,
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() },
                            onDelete = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
