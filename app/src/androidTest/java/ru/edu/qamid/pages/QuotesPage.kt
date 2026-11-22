package ru.edu.qamid.pages

import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matchers.not
import ru.edu.qamid.R

class QuotesPage {

    // карточки однотипные, поэтому первую находим по уникальному фрагменту цитаты —
    // лобовой onView(withId(...)) упадёт с AmbiguousViewMatcherException
    private val firstQuoteCard = allOf(
        withId(R.id.our_mission_item_material_card_view),
        hasDescendant(withText(containsString("Хоспис для меня")))
    )

    private fun firstQuoteDescription() = onView(
        allOf(
            withId(R.id.our_mission_item_description_text_view),
            isDescendantOfA(firstQuoteCard)
        )
    )

    fun checkQuotesScreenIsVisible(): QuotesPage {
        onView(withId(R.id.our_mission_item_list_recycler_view))
            .check(matches(isDisplayed()))
        return this
    }

    fun checkOurMissionTitleIsVisible(): QuotesPage {
        onView(withId(R.id.our_mission_title_text_view))
            .check(matches(isDisplayed()))
        return this
    }

    // иконка разворачивания не кликабельна (clickable=false), кликаем по самой карточке
    fun clickFirstQuote(): QuotesPage {
        onView(firstQuoteCard).perform(click())
        return this
    }

    // развёрнутость проверяем по появлению текста описания внутри карточки:
    // в свёрнутом дампе узла нет, в развёрнутом — есть и видим
    fun checkFirstQuoteIsExpanded(): QuotesPage {
        var visible = false
        repeat(10) {
            if (visible) return@repeat
            visible = try {
                firstQuoteDescription().check(matches(isDisplayed()))
                true
            } catch (e: Throwable) {
                Thread.sleep(500)
                false
            }
        }
        org.junit.Assert.assertTrue("Текст цитаты не развернулся за 5 секунд", visible)
        return this
    }

    fun checkFirstQuoteIsCollapsed(): QuotesPage {
        // описание может исчезнуть из иерархии совсем (NoMatchingViewException)
        // или остаться невидимым — оба варианта считаем «свернуто»
        try {
            firstQuoteDescription().check(matches(not(isDisplayed())))
        } catch (e: NoMatchingViewException) {
            // узла нет — карточка свернулась
        }
        return this
    }
}
