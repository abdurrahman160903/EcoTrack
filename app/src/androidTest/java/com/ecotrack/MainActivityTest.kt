package com.ecotrack

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ecotrack.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Espresso UI tests for the main navigation and log-activity screen.
 *
 * These tests run on a real device or emulator (instrumented tests).
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun dashboardScreenIsDisplayedOnLaunch() {
        onView(withId(R.id.fab_log_activity)).check(matches(isDisplayed()))
    }

    @Test
    fun tappingFabNavigatesToLogActivityScreen() {
        onView(withId(R.id.fab_log_activity)).perform(click())
        onView(withId(R.id.spinner_category)).check(matches(isDisplayed()))
    }

    @Test
    fun bottomNavNavigatesToAnalyticsTab() {
        onView(withId(R.id.analyticsFragment)).perform(click())
        onView(withId(R.id.pie_chart)).check(matches(isDisplayed()))
    }

    @Test
    fun bottomNavNavigatesToTipsTab() {
        onView(withId(R.id.tipsFragment)).perform(click())
        onView(withId(R.id.fab_refresh)).check(matches(isDisplayed()))
    }

    @Test
    fun logActivityShowsValidationErrorOnEmptySubmit() {
        onView(withId(R.id.fab_log_activity)).perform(click())
        onView(withId(R.id.btn_save)).perform(click())
        onView(withText(R.string.invalid_input)).check(matches(isDisplayed()))
    }
}
