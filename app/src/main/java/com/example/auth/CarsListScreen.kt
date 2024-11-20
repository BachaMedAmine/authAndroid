package com.example.auth


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CarsListScreen(
    navController: NavController,
    viewModel: AuthViewModel = viewModel() // Provide a default ViewModel
) {
    val cars = viewModel.carsState ?: emptyList()

    LazyColumn {
        items(cars) { car ->
            Card(modifier = Modifier.padding(8.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Make: ${car.make}")
                    Text(text = "Model: ${car.carModel}")
                    Text(text = "Year: ${car.year}")
                }
            }
        }
    }
}