package ru.edu.qamid.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.hamcrest.Matchers.not
import ru.edu.qamid.R
import androidx.test.espresso.matcher.ViewMatchers.withText

class NewsPage {

    fun clickSortButton(): NewsPage {
        onView(withId(R.id.news_sort_button)).perform(click())
        return this
    }

    fun clickFilterButton(): NewsPage {
        onView(withId(R.id.news_filter_button)).perform(click())
        return this
    }

    // надёжный признак «список не пустой»: заглушка пустого списка скрыта.
    // Заголовок карточки здесь не годится — у свёрнутых карточек он GONE
    fun checkNewsCardIsVisible(): NewsPage {
        onView(withId(R.id.empty_news_list_text_view))
            .check(matches(not(isDisplayed())))
        return this
    }

    fun checkEmptyNewsListIsVisible(): NewsPage {
        onView(withId(R.id.empty_news_list_text_view))
            .check(matches(isDisplayed()))
        return this
    }

    fun clickEditNewsButton(): NewsPage {
        onView(withId(R.id.news_edit_button))
            .perform(click())
        return this
    }

    fun checkEmptyStateIsVisible(): NewsPage {
        var visible = false
        repeat(10) {
            if (visible) return@repeat
            visible = try {
                onView(withText(org.hamcrest.Matchers.containsString("There is nothing here yet")))
                    .check(matches(isDisplayed()))
                true
            } catch (e: Throwable) {
                Thread.sleep(500)
                false
            }
        }
        org.junit.Assert.assertTrue("Пустое состояние не появилось за 5 секунд", visible)
        return this
    }
}
