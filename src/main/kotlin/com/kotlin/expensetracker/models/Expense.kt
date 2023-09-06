package com.kotlin.expensetracker.models

import java.util.Date

data class Expense(val id: String, val name: String, val createdAt: Date, val amount: Float, val budgetId: String)
