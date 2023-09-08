package com.kotlin.expensetracker.controllers

import com.kotlin.expensetracker.models.Budget
import com.kotlin.expensetracker.models.Expense
import com.kotlin.expensetracker.services.ExpenseTrackerServices
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ExpenseTrackerController(val service: ExpenseTrackerServices) {

    @PostMapping("/user")
    fun getUserByUserName(@RequestParam name: String) = service.findOrCreateUser(name)

    @PostMapping("/budget")
    fun createBudgetForUser(@RequestBody budget: Budget, @RequestParam userId: String): String =
        service.createBudgetForUser(budget, userId)

    @DeleteMapping("/user")
    fun deleteUser(@RequestParam userId: String) = service.deleteUserAndAssociatedBudgets(userId)

    @PostMapping("/budgets")
    fun getBudgetsByIds(@RequestBody budgetIds: List<String>) = service.getBudgetsByIds(budgetIds)

    @DeleteMapping("/budget")
    fun deleteBudget(@RequestParam budgetId: String) = service.deleteBudgetAndAssociatedExpenses(budgetId)

    @PostMapping("/expense")
    fun createExpense(@RequestBody expense: Expense) = service.createExpense(expense)

    @PostMapping("/expenses")
    fun getExpensesByBudgetIds(@RequestBody budgetIds: List<String>) = service.getExpensesByBudgetIds(budgetIds)

    @DeleteMapping("/expense")
    fun deleteExpense(@RequestParam expenseId: String) = service.deleteExpense(expenseId)
}