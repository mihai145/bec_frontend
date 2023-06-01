package com.example.bec_client.fragment

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TrendingFragmentTest {
    fun waitFor(millis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints() : Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "Wait for $millis milliseconds."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadForAtLeast(millis)
            }
        }
    }

    @Test
    fun testSpinner() {
        // Set up
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.trending)).perform(click())
        onView(isRoot()).perform(waitFor(2000))

        onView(withId(R.id.genreSpinner)).perform(click())
        onData(anything()).atPosition(1).perform(click())
        onView(withId(R.id.genreSpinner)).check(matches(withSpinnerText("Adventure")))
    }
}