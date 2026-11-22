package ru.edu.qamid.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import ru.edu.qamid.R
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed

class LoginPage {

    fun enterLogin(login: String): LoginPage {
        onView(withId(R.id.login_edit_text))
            .perform(replaceText(login), closeSoftKeyboard())
        return this
    }

    fun enterPassword(password: String): LoginPage {
        onView(withId(R.id.password_edit_text))
            .perform(replaceText(password), closeSoftKeyboard())
        return this
    }

    fun clickSignIn(): LoginPage {
        onView(withId(R.id.enter_button)).perform(click())
        return this
    }

    fun checkLoginScreenIsVisible(): LoginPage {
        onView(withId(R.id.login_edit_text)).check(matches(isDisplayed()))
        return this
    }
}
