package com.kotlin.expensetracker.services

import com.google.cloud.firestore.FieldValue
import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.kotlin.expensetracker.models.Budget
import com.kotlin.expensetracker.models.Expense
import com.kotlin.expensetracker.models.User
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class ExpenseTrackerServices {

    val db: Firestore = FirestoreClient.getFirestore()

    //region User
    fun findOrCreateUser(name: String): User {

        // Try to find User
        var newUser = findUserInFirestore(name);
        if (newUser != null) {
            return newUser;
        }

        // User not found
        val newUserId = UUID.randomUUID().toString()
        newUser = User(newUserId, name, emptyList())
        createUserInFirestore(newUser)

        return newUser
    }

    fun deleteUserAndAssociatedBudgets(userId: String) {
        val userCollection = db.collection("users");
        val budgetCollection = db.collection("budgets")

        // extract user to delete
        val userDocument = userCollection.document(userId)

        // extract budgets to delete
        val budgetIds = userDocument.get().get().get("budgets") as List<String>

        // delete user
        userDocument.delete()

        // delete budgets
        budgetIds.forEach { budgetId ->
            val budgetDocument = budgetCollection.document(budgetId)
            deleteBudgetAndAssociatedExpenses(budgetDocument.id)
        }

    }

    private fun createUserInFirestore(user: User) {
        val userCollection = db.collection("users")

        val newUserDocument: MutableMap<String, Any> = HashMap()
        newUserDocument["userName"] = user.userName
        newUserDocument["budgets"] = user.budgets

        user.id?.let { userCollection.document(it).set(newUserDocument) }
    }

    private fun findUserInFirestore(name: String): User? {
        val userCollection = db.collection("users")

        // Try to find user
        val querySnapshot = userCollection.whereEqualTo("userName", name).get().get()

        try {
            for (document in querySnapshot.documents) {
                val id = document.id
                val userName = document.getString("userName")
                val budgets = document.get("budgets") as? List<String> // Assuming budgets is an array

                if (userName != null && budgets != null) {
                    return User(id, userName, budgets)
                }
            }
        } catch (e: Exception) {
            // Handle exceptions (e.g., FirestoreException, NullPointerException)
            // Log the error or take appropriate action
            e.printStackTrace()
            return null
        }
        return null
    }

    //endregion

    //region Budget

    fun getBudgetsByIds(budgetIds: List<String>): List<Budget>? {
        val budgetCollection = db.collection("budgets")
        var budgets = mutableListOf<Budget>()

        try {
            for (budgetId in budgetIds) {
                val budgetSnapshot = budgetCollection.document(budgetId).get().get()
                val id = budgetSnapshot.id
                val name = budgetSnapshot.getString("name")
                val createdAt = budgetSnapshot.getDate("createdAt")
                val amount = budgetSnapshot.getDouble("amount")
                if (name != null && createdAt != null && amount != null) {
                    budgets.add(Budget(id, name, createdAt, amount))
                }
            }
            return budgets;
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null;
    }

    fun createBudgetForUser(budget: Budget, userId: String): String {

        // Create a new budget document
        val budgetCollection = db.collection("budgets")
        val newBudgetDocument = budgetCollection.add(budget).get()

        // Get the ID of the newly created budget document
        val budgetId = newBudgetDocument.id

        // Update the user's budgets list with the new budget ID
        val userCollection = db.collection("users")
        val userDocument = userCollection.document(userId)
        userDocument.update("budgets", FieldValue.arrayUnion(budgetId))

        // Return the updated user object
        return  budgetId;
    }

    fun deleteBudgetAndAssociatedExpenses(budgetId: String) {
        val budgetCollection = db.collection("budgets")

        // delete budget
        budgetCollection.document(budgetId).delete()

        // delete expenses related to budget
        getExpensesByBudgetIds(listOf(budgetId))?.forEach { expense ->
            val expenseId = expense.id
            if (expenseId != null) deleteExpense(expenseId)
        }
    }

    //endregion

    //region Expense

    fun getExpensesByBudgetIds(budgetIds: List<String>): List<Expense>? {
        val expenseCollection = db.collection("expenses")
        var expenses = mutableListOf<Expense>()

        try {
            for (budgetId in budgetIds) {
                val expenseSnapshot = expenseCollection.whereEqualTo("budgetId", budgetId).get().get()
                for (document in expenseSnapshot.documents) {
                    val id = document.id
                    val name = document.getString("name")
                    val createdAt = document.getDate("createdAt")
                    val amount = document.getDouble("amount")
                    val budgetid = document.getString("budgetId")

                    if (name != null && createdAt != null && amount != null && budgetid != null) {
                        expenses.add(Expense(id, name, createdAt, amount, budgetid))
                    }
                }
            }
            return expenses;
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null;
    }

    fun createExpense(expense: Expense): String {
        val expenseCollection = db.collection("expenses")
        val expenseDocument = expenseCollection.add(expense).get()
        return expenseDocument.id;
    }

    fun deleteExpense(expenseId: String) {
        val expenseCollection = db.collection("expenses")
        expenseCollection.document(expenseId).delete()
    }

    //endregion
}
