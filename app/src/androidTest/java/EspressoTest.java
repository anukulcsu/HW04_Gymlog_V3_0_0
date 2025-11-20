package com.example.hw04_gymlog_v300;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testLoginAddLogAndLogout() {
        onView(withId(R.id.username_login_edit_text))
                .perform(typeText("admin1"), closeSoftKeyboard());

        onView(withId(R.id.password_login_edit_text))
                .perform(typeText("admin1"), closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());

        onView(withId(R.id.exercise_label_text_view))
                .check(matches(isDisplayed()));

        String exerciseName = "Shoulder Press";
        onView(withId(R.id.exercise_input_edit_text))
                .perform(typeText(exerciseName), closeSoftKeyboard());

        onView(withId(R.id.weight_input_edit_text))
                .perform(typeText("100"), closeSoftKeyboard());

        onView(withId(R.id.rep_input_edit_text))
                .perform(typeText("10"), closeSoftKeyboard());

        onView(withId(R.id.login_button))
                .perform(click());

        onView(withId(R.id.log_display_recycler_view))
                .perform(RecyclerViewActions.scrollTo(
                        hasDescendant(withText(containsString(exerciseName)))
                ));

        onView(allOf(withId(R.id.recycler_item_text_view), withText(containsString(exerciseName))))
                .check(matches(isDisplayed()));

        onView(withId(R.id.logout_menu_item))
                .perform(click());

        onView(withText("Log Out"))
                .perform(click());

        onView(withId(R.id.username_login_edit_text))
                .check(matches(isDisplayed()));
    }
}