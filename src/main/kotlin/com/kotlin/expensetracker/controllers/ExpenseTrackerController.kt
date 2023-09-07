package com.kotlin.expensetracker.controllers

import com.kotlin.expensetracker.services.ExpenseTrackerServices
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class ExpenseTrackerController(val service: ExpenseTrackerServices) {

    @PostMapping("/user")
    fun getUserByUserName(@RequestParam name: String) = service.findOrCreateUser(name)
}