package ru.edu.qamid.pages

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import ru.edu.qamid.R

class EditNewsPage {

    // с ретраем: форма открывается после навигации, которая происходит
    // не мгновенно; Espresso кидает AssertionError — ловим Throwable
    fun checkEditNewsScreenIsVisible(): EditNewsPage {
        checkAppBar("Editing")
        return this
    }

    fun checkCreateNewsScreenIsVisible(): EditNewsPage {
        checkAppBar("Creating")
        return this
    }

    private fun checkAppBar(title: String) {
        var visible = false
        repeat(10) {
            if (visible) return@repeat
            visible = try {
                onView(withId(R.id.custom_app_bar_title_text_view))
                    .check(matches(withText(title)))
                onView(withId(R.id.custom_app_bar_sub_title_text_view))
                    .check(matches(withText("News")))
                true
            } catch (e: Throwable) {
                Thread.sleep(500)
                false
            }
        }
        org.junit.Assert.assertTrue("Экран «$title» не открылся за 5 секунд", visible)
    }

    fun checkCurrentTitle(title: String): EditNewsPage {
        onView(withId(R.id.news_title_edit_text))
            .check(matches(withText(title)))
        return this
    }

    fun typeTitle(title: String): EditNewsPage {
        onView(withId(R.id.news_title_edit_text))
            .perform(replaceText(title))
        return this
    }

    fun typeDescription(description: String): EditNewsPage {
        onView(withId(R.id.news_description_edit_text))
            .perform(replaceText(description))
        return this
    }

    fun typePublishDate(date: String): EditNewsPage {
        // если поле открывает календарь вместо ввода текста — сменим тактику
        onView(withId(R.id.news_publish_date_edit_text))
            .perform(replaceText(date))
        return this
    }

    fun typePublishTime(time: String): EditNewsPage {
        onView(withId(R.id.news_publish_time_edit_text))
            .perform(replaceText(time))
        return this
    }

    fun switchActive(): EditNewsPage {
        onView(withId(R.id.news_active_switch))
            .perform(click())
        return this
    }

    fun clickSave(): EditNewsPage {
        onView(withId(R.id.news_save_button))
            .perform(click())
        return this
    }

    fun clickCancel(): EditNewsPage {
        onView(withId(R.id.news_cancel_button))
            .perform(click())
        return this
    }

    // выбор категории с ретраем и проверкой результата: клик по полю раскрывает
    // список в popup-окне, а popup разворачивается не мгновенно, поэтому клик
    // по пункту может прийти раньше списка. После выбора проверяем текст поля;
    // если за 5 попыток выбор не удался — вводим категорию текстом напрямую
    fun typeCategory(category: String): EditNewsPage {
        var selected = false
        repeat(5) {
            if (selected) return@repeat
            selected = try {
                onView(withId(R.id.news_category_auto_complete))
                    .perform(click())
                onView(withText(category))
                    .inRoot(RootMatchers.isPlatformPopup())
                    .perform(click())
                // проверяем, что категория действительно установилась —
                // защита от мусорных значений в поле
                onView(withId(R.id.news_category_auto_complete))
                    .check(matches(withText(category)))
                true
            } catch (e: Throwable) {
                Thread.sleep(500)
                false
            }
        }
        if (!selected) {
            // запасной путь: прямой ввод текста в поле автокомплита
            onView(withId(R.id.news_category_auto_complete))
                .perform(click(), clearText(), typeText(category), closeSoftKeyboard())
            org.junit.Assert.assertTrue(
                "Категория «$category» не установилась в поле",
                try {
                    onView(withId(R.id.news_category_auto_complete))
                        .check(matches(withText(category)))
                    true
                } catch (e: Throwable) {
                    false
                }
            )
        }
        return this
    }
}
