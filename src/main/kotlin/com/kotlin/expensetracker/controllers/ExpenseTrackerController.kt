package com.kotlin.expensetracker.controllers

import com.kotlin.expensetracker.models.Budget
import com.kotlin.expensetracker.services.ExpenseTrackerServices
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ExpenseTrackerController(val service: ExpenseTrackerServices) {

    @PostMapping("/user")
    fun getUserByUserName(@RequestParam name: String) = service.findOrCreateUser(name)

    @PostMapping("/budgets")
    fun getBudgetsByIds(@RequestBody budgetIds: List<String>) = service.getBudgetsByIds(budgetIds)

    @PostMapping("/budget")
    fun createBudgetForUser(@RequestBody budget: Budget, @RequestParam userId: String) =
        service.createBudgetForUser(budget, userId)
}