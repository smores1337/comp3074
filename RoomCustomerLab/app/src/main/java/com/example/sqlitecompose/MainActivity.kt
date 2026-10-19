package com.example.sqlitecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sqlitecompose.data.AppDatabase
import com.example.sqlitecompose.ui.CustomerApp
import com.example.sqlitecompose.ui.CustomerViewModel
import com.example.sqlitecompose.ui.CustomerViewModelFactory
import com.example.sqlitecompose.ui.theme.RoomCustomerLabTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            RoomCustomerLabTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val viewModel: CustomerViewModel = viewModel(
                        factory = CustomerViewModelFactory(
                            database.customerDao()
                        )
                    )

                    CustomerApp(viewModel)
                }
            }
        }
    }
}
