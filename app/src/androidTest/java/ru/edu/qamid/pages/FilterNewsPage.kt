package ru.edu.qamid.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import ru.edu.qamid.R
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import org.hamcrest.Matchers.allOf

class FilterNewsPage {

    fun checkFilterScreenIsVisible(): FilterNewsPage {
        onView(withId(R.id.filter_news_title_text_view))
            .check(matches(isDisplayed()))
        return this
    }

    fun typeCategory(category: String): FilterNewsPage {
        // фокус на поле сам раскрывает список подсказок,
        // пункт выбирается в отдельном popup-окне
        onView(withId(R.id.filter_news_category_auto_complete))
            .perform(click())
        onView(withText(category))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())
        // контроль: выбор должен попасть в само поле
        onView(withId(R.id.filter_news_category_auto_complete))
            .check(matches(withText(category)))
        return this
    }



    fun typeDateRange(start: String, end: String): FilterNewsPage {
        onView(withId(R.id.filter_news_date_start_edit_text))
            .perform(replaceText(start), closeSoftKeyboard())
        onView(withId(R.id.filter_news_date_end_edit_text))
            .perform(replaceText(end), closeSoftKeyboard())
        return this
    }

    fun clickApplyButton(): FilterNewsPage {
        onView(withId(R.id.filter_news_apply_button)).perform(click())
        return this
    }

    fun clickCancelButton(): FilterNewsPage {
        onView(withId(R.id.filter_news_cancel_button)).perform(click())
        return this
    }
}
