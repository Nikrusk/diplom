package ru.edu.qamid.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.CoreMatchers.allOf
import ru.edu.qamid.R

class NavigationPage {

    fun clickMainMenu(): NavigationPage {
        onView(withId(R.id.main_menu_image_button)).perform(click())
        return this
    }

    fun clickNewsMenuItem(): NavigationPage {
        onView(allOf(withId(android.R.id.title), withText("News")))
            .perform(click())
        return this
    }

    fun clickAllNewsButton(): NavigationPage {
        onView(withId(R.id.all_news_text_view)).perform(click())
        return this
    }

    fun checkNewsMenuItemIsVisible(): NavigationPage {
        onView(allOf(withId(android.R.id.title), withText("News")))
            .check(matches(isDisplayed()))
        return this
    }

    fun checkNewsScreenIsVisible(): NavigationPage {
        onView(withId(R.id.news_sort_button)).check(matches(isDisplayed()))
        return this
    }

    fun clickOurMissionButton(): NavigationPage {
        onView(withId(R.id.our_mission_image_button)).perform(click())
        return this
    }

}
