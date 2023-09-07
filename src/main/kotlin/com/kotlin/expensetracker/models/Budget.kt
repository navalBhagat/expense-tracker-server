package com.kotlin.expensetracker.models

import java.util.Date

data class Budget(val id: String?, val name: String, val createdAt: Date, val amount: Double)
