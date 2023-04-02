package com.example.roomdatabasedemo

import android.app.Application

class EmployeeApplication:Application() {
    val db by lazy{
        EmployeeDatabase.getInstance(this)
    }
}