package com.example.bec_client.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.adapter.LeaderboardAdapter
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {
    @Test
     fun isPageVisible() {
        val bundle = Bundle()
        val scenario = launchFragmentInContainer<ProfileFragment>(fragmentArgs=bundle)

        onView(withId(R.id.amIAuthenticatedButton)).check(matches(isDisplayed()))
        //onView(withId(R.id.recyclerView)).check(matches(hasChildCount(5)))

    }

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
    fun testRecyclerViewItemCount() {
        // Set up
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.profile)).perform(ViewActions.click())
        onView(withId(R.id.loginLogoutButton)).check(matches(isDisplayed()))
        onView(isRoot()).perform(waitFor(2000));

        onView(withId(R.id.recyclerView)).check(matches(hasChildCount(5)))
    }

    @Test
    fun testRedirectUserPage() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        onView(withId(R.id.profile)).perform(ViewActions.click())
        onView(withId(R.id.loginLogoutButton)).check(matches(isDisplayed()))
        onView(isRoot()).perform(waitFor(2000));

        onView(withId(R.id.recyclerView)).perform(
            RecyclerViewActions.actionOnItemAtPosition<LeaderboardAdapter.ViewHolder>(0, click()))
        onView(withId(R.id.followButton)).check(matches(isDisplayed()))
    }
}