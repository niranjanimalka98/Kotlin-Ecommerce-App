package com.example.njnstore.tools

import com.example.njnstore.model.UserModel

class Utils {
    companion object{
     fun get_logged_in_user(): UserModel? {
        return try {
            val allUsers: List<UserModel> = UserModel.listAll(UserModel::class.java)
                ?: return null
            if (allUsers.isEmpty()) {
                null
            } else allUsers[0]
        } catch (e: Exception) {
            null
        }
    }
}}