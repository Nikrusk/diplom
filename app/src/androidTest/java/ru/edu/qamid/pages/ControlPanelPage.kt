package ru.edu.qamid.pages

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import ru.edu.qamid.R

class ControlPanelPage {

    // с ретраем: после сохранения новости приложение пишет её в базу
    // асинхронно и только потом возвращает нас на панель — Espresso
    // фоновую запись не ждёт, поэтому проверяем появление панели
    // до 5 секунд
    fun checkControlPanelIsVisible(): ControlPanelPage {
        var visible = false
        repeat(10) {
            if (visible) return@repeat
            visible = try {
                onView(withText("Control panel"))
                    .check(matches(isDisplayed()))
                true
            } catch (e: Throwable) {
                Thread.sleep(500)
                false
            }
        }
        org.junit.Assert.assertTrue("Панель управления не открылась за 5 секунд", visible)
        return this
    }

    fun clickAddNewsButton(): ControlPanelPage {
        onView(withId(R.id.add_news_image_view))
            .perform(click())
        return this
    }

    // работа с карточкой по позиции — только когда порядок гарантирован
    fun clickEditNewsButton(position: Int): ControlPanelPage {
        onView(withId(R.id.news_list_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    clickChildViewWithId(R.id.news_item_edit_image_view)
                )
            )
        return this
    }

    fun clickDeleteNewsButton(position: Int): ControlPanelPage {
        onView(withId(R.id.news_list_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    position,
                    clickChildViewWithId(R.id.news_item_delete_image_view)
                )
            )
        return this
    }

    // надёжная работа с карточкой по заголовку: порядок новостей на панели плавает
    fun clickEditNewsButtonByTitle(title: String): ControlPanelPage {
        onView(withId(R.id.news_list_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(
                        allOf(withId(R.id.news_item_title_text_view), withText(title))
                    ),
                    clickChildViewWithId(R.id.news_item_edit_image_view)
                )
            )
        return this
    }

    fun clickDeleteNewsButtonByTitle(title: String): ControlPanelPage {
        onView(withId(R.id.news_list_recycler_view))
            .perform(
                RecyclerViewActions.actionOnItem<RecyclerView.ViewHolder>(
                    hasDescendant(
                        allOf(withId(R.id.news_item_title_text_view), withText(title))
                    ),
                    clickChildViewWithId(R.id.news_item_delete_image_view)
                )
            )
        return this
    }

    fun checkNewsTitle(position: Int, title: String): ControlPanelPage {
        onView(withId(R.id.news_list_recycler_view))
            .perform(
                RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(position)
            )
        onView(
            allOf(
                withId(R.id.news_item_title_text_view),
                withText(title)
            )
        )
            .check(matches(isDisplayed()))
        return this
    }

    // с ретраем: новость после сохранения появляется в адаптере не мгновенно.
    // Важно: Espresso кидает AssertionError (наследник Error, а не Exception),
    // поэтому ловим Throwable
    fun checkNewsTitleIsVisible(title: String): ControlPanelPage {
        var visible = false
        repeat(10) {
            if (visible) return@repeat
            visible = try {
                onView(withId(R.id.news_list_recycler_view))
                    .perform(
                        RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                            hasDescendant(withText(title))
                        )
                    )
                onView(allOf(withId(R.id.news_item_title_text_view), withText(title)))
                    .check(matches(isDisplayed()))
                true
            } catch (e: Throwable) {
                Thread.sleep(500)
                false
            }
        }
        org.junit.Assert.assertTrue("Новость «$title» не появилась в списке", visible)
        return this
    }

    // с ретраем: карточка исчезает из списка не мгновенно после подтверждения
    fun checkNewsTitleIsAbsent(title: String): ControlPanelPage {
        var absent = false
        repeat(10) {
            if (absent) return@repeat
            absent = try {
                onView(withText(title))
                    .check(doesNotExist())
                true
            } catch (e: Throwable) {
                Thread.sleep(500)
                false
            }
        }
        org.junit.Assert.assertTrue("Новость «$title» всё ещё видна в списке", absent)
        return this
    }

    // тихая проверка для seed-теста: возвращает результат, а не роняет тест
    fun isNewsTitlePresent(title: String): Boolean {
        repeat(3) {
            try {
                onView(withId(R.id.news_list_recycler_view))
                    .perform(
                        RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                            hasDescendant(
                                allOf(withId(R.id.news_item_title_text_view), withText(title))
                            )
                        )
                    )
                return true
            } catch (e: Throwable) {
                Thread.sleep(300)
            }
        }
        return false
    }

    fun checkDeleteDialogIsVisible(): ControlPanelPage {
        // диалог — отдельное окно: ищем сообщение именно в нём через isDialog()
        onView(withId(android.R.id.message))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
        return this
    }

    fun clickConfirmDeleteButton(): ControlPanelPage {
        onView(withId(android.R.id.button1)) // OK
            .inRoot(RootMatchers.isDialog())
            .perform(click())
        return this
    }

    fun clickCancelDeleteButton(): ControlPanelPage {
        onView(withId(android.R.id.button2)) // CANCEL
            .inRoot(RootMatchers.isDialog())
            .perform(click())
        return this
    }

    // клик по дочернему элементу карточки, а не по самой карточке:
    // таких иконок на экране по одной на каждую новость
    private fun clickChildViewWithId(id: Int): ViewAction = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isDisplayed()
        override fun getDescription(): String = "Click on a child view with id $id"
        override fun perform(controller: UiController, view: View) {
            view.findViewById<View>(id).performClick()
        }
    }
}
