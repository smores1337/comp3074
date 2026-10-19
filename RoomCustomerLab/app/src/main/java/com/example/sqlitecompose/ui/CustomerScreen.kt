package com.example.sqlitecompose.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sqlitecompose.data.Customer

@Composable
fun CustomerApp(
    viewModel: CustomerViewModel
) {
    val customers by viewModel.customers
        .collectAsStateWithLifecycle(initialValue = emptyList())
    val activeSearch by viewModel.activeSearch.collectAsStateWithLifecycle()

    CustomerScreen(
        customers = customers,
        activeSearch = activeSearch,
        onAddCustomer = viewModel::addCustomer,
        onUpdateCustomer = viewModel::updateCustomer,
        onDeleteCustomer = viewModel::deleteCustomer,
        onSearch = viewModel::search,
        onClearSearch = viewModel::clearSearch
    )
}

@Composable
fun CustomerScreen(
    customers: List<Customer>,
    activeSearch: String,
    onAddCustomer: (String, Int, Boolean) -> Unit,
    onUpdateCustomer: (Customer) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var ageText by rememberSaveable { mutableStateOf("") }
    var isActive by rememberSaveable { mutableStateOf(false) }
    var message by rememberSaveable { mutableStateOf<String?>(null) }

    // Id of the customer being edited, or null when the form adds a new customer.
    var editingId by rememberSaveable { mutableStateOf<Int?>(null) }
    var searchText by rememberSaveable { mutableStateOf(activeSearch) }

    fun resetForm() {
        name = ""
        ageText = ""
        isActive = false
        editingId = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Room Customer Demo",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Customer name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = ageText,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    ageText = newValue
                }
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Active customer")
            Spacer(Modifier.width(8.dp))
            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it }
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    val age = ageText.toIntOrNull()
                    val id = editingId

                    if (name.isBlank() || age == null) {
                        message = "Enter a valid name and age."
                    } else if (id == null) {
                        onAddCustomer(
                            name.trim(),
                            age,
                            isActive
                        )
                        resetForm()
                        message = "Customer submitted."
                    } else {
                        onUpdateCustomer(
                            Customer(
                                id = id,
                                name = name.trim(),
                                age = age,
                                isActive = isActive
                            )
                        )
                        resetForm()
                        message = "Customer $id updated."
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(if (editingId == null) "Add Customer" else "Save Changes")
            }

            if (editingId != null) {
                OutlinedButton(
                    onClick = {
                        resetForm()
                        message = "Edit cancelled."
                    }
                ) {
                    Text("Cancel")
                }
            }
        }

        message?.let {
            Spacer(Modifier.height(8.dp))
            Text(it)
        }

        Spacer(Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Search by name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { onSearch(searchText) },
                modifier = Modifier.weight(1f)
            ) {
                Text("Search")
            }
            OutlinedButton(
                onClick = {
                    searchText = ""
                    onClearSearch()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Clear")
            }
        }

        if (activeSearch.isNotBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Showing customers matching \"$activeSearch\"",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(Modifier.height(12.dp))

        CustomerList(
            customers = customers,
            onEditCustomer = { customer ->
                name = customer.name
                ageText = customer.age.toString()
                isActive = customer.isActive
                editingId = customer.id
                message = "Editing customer ${customer.id}."
            },
            onDeleteCustomer = { customer ->
                if (customer.id == editingId) {
                    resetForm()
                }
                onDeleteCustomer(customer)
            },
            emptyMessage = if (activeSearch.isBlank()) {
                "No customers in the database."
            } else {
                "No customers match \"$activeSearch\"."
            },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun CustomerList(
    customers: List<Customer>,
    onEditCustomer: (Customer) -> Unit,
    onDeleteCustomer: (Customer) -> Unit,
    modifier: Modifier = Modifier,
    emptyMessage: String = "No customers in the database."
) {
    if (customers.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(emptyMessage)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = customers,
                key = { customer -> customer.id }
            ) { customer ->
                CustomerRow(
                    customer = customer,
                    onEdit = {
                        onEditCustomer(customer)
                    },
                    onDelete = {
                        onDeleteCustomer(customer)
                    }
                )
            }
        }
    }
}

@Composable
fun CustomerRow(
    customer: Customer,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text("ID: ${customer.id}")
                Text("Age: ${customer.age}")
                Text(
                    if (customer.isActive)
                        "Active customer"
                    else
                        "Inactive customer"
                )
            }

            TextButton(onClick = onEdit) {
                Text("Edit")
            }
            TextButton(onClick = onDelete) {
                Text("Delete")
            }
        }
    }
}
