package com.kotlin.expensetracker.services

import com.google.cloud.firestore.Firestore
import com.google.firebase.cloud.FirestoreClient
import com.kotlin.expensetracker.models.User
import org.springframework.stereotype.Service
import java.util.*
import kotlin.collections.HashMap

@Service
class ExpenseTrackerServices {

    val db: Firestore = FirestoreClient.getFirestore()

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
        }
        return null
    }

}
