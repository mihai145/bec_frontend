package com.example.bec_client.manager

class PreferrencesManager private constructor() {
    var instance: PreferrencesManager? = null
        get() {
            if (field == null) field = PreferrencesManager()
            return field
        }
        private set
    private val preferences: MutableList<String> = ArrayList()
    fun prefer(title: String) {
        preferences.add(title)
    }

    fun removePreference(title: String) {
        preferences.remove(title)
    }

    fun getPreferences(): List<String> {
        return preferences
    }
}