package com.kotlin.expensetracker

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

@SpringBootApplication
class ExpenseTrackerApplication

fun main(args: Array<String>) {

	// set up Firebase Authorization
	val resourceStream: InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream("serviceAccountKey.json")
	val serviceAccount = requireNotNull(resourceStream) { "serviceAccountKey.json not found" }

	val options = FirebaseOptions.Builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.setDatabaseUrl("https://expense-tracker-50b03.firebaseio.com/")
			.build()

	FirebaseApp.initializeApp(options)

	runApplication<ExpenseTrackerApplication>(*args)
}
