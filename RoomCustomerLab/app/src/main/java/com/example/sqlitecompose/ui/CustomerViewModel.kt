package com.example.sqlitecompose.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sqlitecompose.data.Customer
import com.example.sqlitecompose.data.CustomerDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val customerDao: CustomerDao
) : ViewModel() {

    // Challenge: search. A blank query means "show every customer".
    private val searchText = MutableStateFlow("")
    val activeSearch: StateFlow<String> = searchText.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val customers: Flow<List<Customer>> =
        searchText.flatMapLatest { query ->
            if (query.isBlank()) {
                customerDao.getAllCustomers()
            } else {
                customerDao.searchCustomers(query)
            }
        }

    fun search(query: String) {
        searchText.value = query.trim()
    }

    fun clearSearch() {
        searchText.value = ""
    }

    fun addCustomer(
        name: String,
        age: Int,
        isActive: Boolean
    ) {
        viewModelScope.launch {
            customerDao.insert(
                Customer(
                    name = name,
                    age = age,
                    isActive = isActive
                )
            )
        }
    }

    // Challenge: update. The customer keeps its existing id, so Room updates that row.
    fun updateCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.update(customer)
        }
    }

    fun deleteCustomer(customer: Customer) {
        viewModelScope.launch {
            customerDao.delete(customer)
        }
    }
}

class CustomerViewModelFactory(
    private val customerDao: CustomerDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            return CustomerViewModel(customerDao) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
