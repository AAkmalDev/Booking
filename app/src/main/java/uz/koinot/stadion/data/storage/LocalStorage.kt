package uz.koinot.stadion.data.storage

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import uz.koinot.stadion.utils.CONSTANTS
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalStorage @Inject constructor(@ApplicationContext context: Context) {

    private var sharedPref = context.getSharedPreferences("stadium", Context.MODE_PRIVATE)

    var accessToken: String
        set(value) = sharedPref.edit { putString(CONSTANTS.TOKEN, value) }
        get() = sharedPref.getString(CONSTANTS.TOKEN, "")!!

    var firebaseToken: String
        set(value) = sharedPref.edit().putString(CONSTANTS.FIREBASE_TOKEN, value).apply()
        get() = sharedPref.getString(CONSTANTS.FIREBASE_TOKEN, "")!!

    var phoneNumber: String
        set(value) = sharedPref.edit().putString(CONSTANTS.PHONE_NUMBER, value).apply()
        get() = sharedPref.getString(CONSTANTS.PHONE_NUMBER, "")!!

    var firstName: String
        set(value) = sharedPref.edit().putString(CONSTANTS.FIRST_NAME, value).apply()
        get() = sharedPref.getString(CONSTANTS.FIRST_NAME, "")!!

    var lastName: String
        set(value) = sharedPref.edit().putString(CONSTANTS.LAST_NAME, value).apply()
        get() = sharedPref.getString(CONSTANTS.LAST_NAME, "")!!

    var hasAccount: Boolean
        set(value) = sharedPref.edit().putBoolean(CONSTANTS.HAS_ACCOUNT, value).apply()
        get() = sharedPref.getBoolean(CONSTANTS.HAS_ACCOUNT, false)

    var currentStadiumId: Long
        set(value) = sharedPref.edit { putLong(::currentStadiumId.name, value) }
        get() = sharedPref.getLong(::currentStadiumId.name, 0)

    var language: String
        set(value) = sharedPref.edit { putString(::language.name, value) }
        get() = sharedPref.getString(::language.name, "")!!
}