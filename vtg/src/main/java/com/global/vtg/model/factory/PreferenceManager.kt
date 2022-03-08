package com.global.vtg.model.factory

import android.content.SharedPreferences
import com.global.vtg.interfaces.IPreferenceManager

class PreferenceManager constructor(private var sharedPreferences: SharedPreferences) :
        IPreferenceManager {

    companion object {
        const val KEY_REMEMBER_ME = "pre.key.rememberMe"
        const val KEY_REMEMBER_ME_USERNAME = "pre.key.rememberMe.username"
        const val KEY_REMEMBER_ME_PHONE = "pre.key.rememberMe.phone"
        const val KEY_REMEMBER_ME_CODE = "pre.key.rememberMe.code"
        const val KEY_REMEMBER_ME_COUNTRY_CODE = "pre.key.rememberMe.country.code"
        const val KEY_REMEMBER_ME_PASSWORD = "pre.key.rememberMe.password"
        const val KEY_USER_NAME = "pre.key.userName"
        const val KEY_PASSWORD = "pre.key.password"
        const val KEY_ROLE = "pre.key.role"
        const val KEY_LAN_CODE = "pre.key.lan_code"
        const val KEY_LOGGED_IN_USER_TYPE = "pre.key.logged_type"
        const val KEY_FIRST_STEP = "pre.key.first_step"
        const val KEY_IS_CLINIC = "pre.key.is.clinic"
        const val KEY_ACCESS_TOKEN = "pre.key.accessToken"
        const val KEY_REFRESH_TOKEN = "pre.key.refreshToken"
        const val KEY_USER_LOGGED_IN = "pre.key.userLoggedIn"
    }

    override fun saveLogin(remember: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_ME, remember).apply()
    }

    override fun clearLogin() {
        sharedPreferences.edit().remove(KEY_REMEMBER_ME).apply()
        sharedPreferences.edit().remove(KEY_USER_NAME).apply()
        sharedPreferences.edit().remove(KEY_PASSWORD).apply()
    }

    override fun isLoginSaved(): Boolean {
        return sharedPreferences.getBoolean(KEY_REMEMBER_ME, false)
    }

    override fun saveUsername(username: String) {
        sharedPreferences.edit().putString(KEY_USER_NAME, username).apply()
    }

    override fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USER_NAME, "")
    }

    override fun savePassword(password: String) {
        sharedPreferences.edit().putString(KEY_PASSWORD, password).apply()
    }

    override fun getPassword(): String? {
        return sharedPreferences.getString(KEY_PASSWORD, "")
    }

    override fun saveAccessToken(accessToken: String?) {
        sharedPreferences.edit().putString(KEY_ACCESS_TOKEN, accessToken).apply()
    }

    override fun getAccessToken(): String? {
        return sharedPreferences.getString(KEY_ACCESS_TOKEN, "")
    }

    override fun clearAccessToken() {
        sharedPreferences.edit().remove(KEY_ACCESS_TOKEN).apply()
    }

    override fun saveRefreshToken(refreshToken: String?) {
        sharedPreferences.edit().putString(KEY_REFRESH_TOKEN, refreshToken).apply()
    }

    override fun saveRole(role: String?) {
        sharedPreferences.edit().putString(KEY_ROLE, role).apply()
    }

    override fun getRole(): String? {
        return sharedPreferences.getString(KEY_ROLE, "user")
    }

    override fun saveIsClinic(isClinic: Boolean?) {
        sharedPreferences.edit().putBoolean(KEY_IS_CLINIC, isClinic == true).apply()
    }

    override fun getIsClinic(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_CLINIC, false)
    }

}