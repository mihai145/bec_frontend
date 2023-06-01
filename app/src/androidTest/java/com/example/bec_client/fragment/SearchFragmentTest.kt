package com.example.bec_client.fragment

import android.view.View
import android.widget.SearchView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.adapter.LeaderboardAdapter
import com.example.bec_client.adapter.RecyclerAdapter
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {
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

    fun typeSearchViewText(text: String): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Change view text"
            }

            override fun getConstraints(): Matcher<View> {
                return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
            }

            override fun perform(uiController: UiController?, view: View?) {
                (view as SearchView).setQuery(text, true)
            }
        }
    }

    @Test
    fun testSearch() {
        // Set up
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.search)).perform(click())
        onView(withId(R.id.SearchView)).perform(typeSearchViewText("fane"))
        onView(isRoot()).perform(waitFor(2000))

        onView(withId(R.id.recyclerView)).check(matches(hasMinimumChildCount(1)))

    }

    @Test
    fun testClickCard() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.search)).perform(click())
        onView(withId(R.id.SearchView)).perform(typeSearchViewText("fane"))
        onView(isRoot()).perform(waitFor(2000))

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerAdapter.ViewHolder>(3, click()))
        onView(withId(R.id.reviewButton)).check(matches(isDisplayed()))
    }
}