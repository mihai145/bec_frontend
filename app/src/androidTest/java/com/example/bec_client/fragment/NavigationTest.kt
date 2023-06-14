package com.example.bec_client.fragment

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @Test
    fun testMenuNavigation() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.search)).perform(click())
        onView(withId(R.id.SearchView)).check(matches(isDisplayed()))

        onView(withId(R.id.trending)).perform(click())
        onView(withId(R.id.genreSpinner)).check(matches(isDisplayed()))

        onView(withId(R.id.nearby)).perform(click())
        onView(withId(R.id.mapFragment)).check(matches(isDisplayed()))

        onView(withId(R.id.profile)).perform(click())
        onView(withId(R.id.loginLogoutButton)).check(matches(isDisplayed()))
    }
}