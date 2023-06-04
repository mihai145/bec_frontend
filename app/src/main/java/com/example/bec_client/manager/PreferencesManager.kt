package com.example.bec_client.manager

object PreferencesManager {
    private val preferences: MutableSet<String> = HashSet()

    fun prefer(title: String?) {
        if(title != null)
            preferences.add(title)
    }

    fun removePreference(title: String?) {
        if(title != null)
            preferences.remove(title)
    }

    fun getPreferences(): Set<String> {
        return preferences
    }
}
