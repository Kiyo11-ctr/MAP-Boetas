package com.valentinesgarage.data.repository

import com.valentinesgarage.data.local.UserDao
import com.valentinesgarage.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val userDao: UserDao
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    suspend fun signUp(email: String, password: String): Boolean {
        if (userDao.getUserByEmail(email) != null) return false
        val passwordHash = hashPassword(password)
        val user = User(email = email, passwordHash = passwordHash)
        userDao.insertUser(user)
        return true
    }

    suspend fun login(email: String, password: String): Boolean {
        val user = userDao.getUserByEmail(email) ?: return false
        if (user.passwordHash == hashPassword(password)) {
            _currentUser.value = user
            return true
        }
        return false
    }

    fun logout() {
        _currentUser.value = null
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
