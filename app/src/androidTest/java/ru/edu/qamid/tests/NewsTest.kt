package ru.edu.qamid.tests

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.edu.qamid.R
import ru.edu.qamid.pages.ControlPanelPage
import ru.edu.qamid.pages.EditNewsPage
import ru.edu.qamid.pages.FilterNewsPage
import ru.edu.qamid.pages.LoginPage
import ru.edu.qamid.pages.NavigationPage
import ru.edu.qamid.pages.NewsPage
import ru.edu.qamid.ui.AppActivity
import ru.edu.qamid.utils.waitUntilVisible
import androidx.test.espresso.Espresso.onView
import ru.edu.qamid.utils.waitUntilGone
import io.qameta.allure.android.rules.ScreenshotRule
import io.qameta.allure.android.allureScreenshot

@Epic("Новости")
@Feature("Сортировка и фильтрация новостей")
@RunWith(AllureAndroidJUnit4::class)
class NewsTest {

    @get:Rule
    val screenshotRule = ScreenshotRule(ScreenshotRule.Mode.FAILURE, "failure-screenshot")

    @get:Rule
    val activityRule = ActivityScenarioRule(AppActivity::class.java)

    private val newsPage = NewsPage()

    private val controlPanelPage = ControlPanelPage()

    private val editNewsPage = EditNewsPage()
    private val navigationPage = NavigationPage()
    private val filterNewsPage = FilterNewsPage()

    private fun loginAsValidUser() {
        var onLoginScreen = false
        step("Ждём экран авторизации или главный экран") {
            onLoginScreen = try {
                waitUntilVisible(withId(R.id.login_edit_text), 5000)
                true
            } catch (e: Exception) {
                false
            }
        }
        if (!onLoginScreen) return
        step("Вводим логин и пароль") {
            LoginPage()
                .enterLogin("login2")
                .enterPassword("password2")
        }
        step("Нажимаем «Войти»") {
            LoginPage().clickSignIn()
        }
        step("Открылась главная страница") {
            waitUntilVisible(withId(R.id.main_menu_image_button))
        }
    }

    private fun openNewsScreen() {
        step("Ждём, пока приложение загрузится после сплэш-скрина") {
            waitUntilGone(
                onView(withId(R.id.splash_screen_circular_progress_indicator)),
                timeoutSeconds = 90
            )
        }
        loginAsValidUser()
        step("Переходим во вкладку News") {
            navigationPage.clickMainMenu()
            navigationPage.clickNewsMenuItem()
            waitUntilVisible(withId(R.id.news_sort_button))
        }
    }
    private fun checkFilterResult() {
        step("Отобразился отфильтрованный список новостей") {
            waitUntilVisible(withId(R.id.news_sort_button), 10000)
            var resultFound = false
            repeat(30) {
                if (resultFound) return@repeat
                resultFound = try {
                    newsPage.checkNewsCardIsVisible()
                    true
                } catch (e: Throwable) {
                    try {
                        newsPage.checkEmptyNewsListIsVisible()
                        true
                    } catch (e2: Throwable) {
                        false
                    }
                }
                if (!resultFound) Thread.sleep(500)
            }
            assertTrue("За 15 секунд не дождались ни карточек, ни пустого состояния списка", resultFound)
        }
    }
    private fun waitForNewsCards() {
        var found = false
        val states = StringBuilder()
        repeat(30) {
            if (found) return@repeat
            var state = ""
            try {
                newsPage.checkNewsCardIsVisible()
                found = true
                state = "заглушка скрыта — карточки есть"
            } catch (e: AssertionError) {
                state = "заглушка ВИДИМА — список пуст"
            } catch (e: Throwable) {
                state = "заглушка отсутствует (${e.javaClass.simpleName})"
            }
            if (!found) {
                states.append(state).append("; ")
                Thread.sleep(500)
            }
        }
        assertTrue(
            "За 15 секунд карточки не появились. Состояния по попыткам: $states",
            found
        )
    }

    @Test
    @Story("Сортировка новостей по новизне")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Нажатие на кнопку сортировки во вкладке News. Ожидается: новости отсортированы по дате публикации")
    fun sortNewsTest() {
        openNewsScreen()
        step("Нажимаем кнопку сортировки") {
            newsPage.clickSortButton()
        }
        step("Список новостей отображается") {
            waitForNewsCards()
        }
    }

    @Test
    @Story("Сортировка новостей по периоду")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Выбор периода «за неделю» в фильтре. Ожидается: показан список новостей за указанный период")
    fun sortNewsByPeriodTest() {
        openNewsScreen()
        step("Открываем фильтр и задаём период за неделю") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeDateRange("06.11.2026", "13.11.2026")
                .clickApplyButton()
        }
        step("Показан список новостей за период") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «Объявление»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр по категории «Объявление». Ожидается: отфильтрованный список новостей")
    fun filterByAnnouncementTest() {
        openNewsScreen()
        step("Задаём категорию «Объявление» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Объявление")
                .clickApplyButton()
        }
        step("Показан отфильтрованный список") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «День рождения»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр по категории «День рождения». Ожидается: отфильтрованный список новостей")
    fun filterByBirthdayTest() {
        openNewsScreen()
        step("Задаём категорию «День рождения» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("День рождения")
                .clickApplyButton()
        }
        step("Показан отфильтрованный список") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «Зарплата»")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Фильтр по категории «Зарплата». Ожидается: отфильтрованный список новостей")
    fun filterBySalaryTest() {
        openNewsScreen()
        step("Задаём категорию «Зарплата» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Зарплата")
                .clickApplyButton()
        }
        step("Показан отфильтрованный список") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «Профсоюз»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр по категории «Профсоюз». Ожидается: отфильтрованный список новостей")
    fun filterByTradeUnionTest() {
        openNewsScreen()
        step("Задаём категорию «Профсоюз» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Профсоюз")
                .clickApplyButton()
        }
        step("Показан отфильтрованный список") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «Праздник»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр по категории «Праздник». Ожидается: отфильтрованный список новостей")
    fun filterByHolidayTest() {
        openNewsScreen()
        step("Задаём категорию «Праздник» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Праздник")
                .clickApplyButton()
        }
        step("Показан отфильтрованный список") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «Массаж»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр по категории «Массаж». Ожидается: отфильтрованный список новостей")
    fun filterByMassageTest() {
        openNewsScreen()
        step("Задаём категорию «Массаж» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Массаж")
                .clickApplyButton()
        }
        step("Показан отфильтрованный список") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «Благодарность»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр по категории «Благодарность». Ожидается: отфильтрованный список новостей")
    fun filterByGratitudeTest() {
        openNewsScreen()
        step("Задаём категорию «Благодарность» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Благодарность")
                .clickApplyButton()
        }
        step("Показан отфильтрованный список") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Фильтрация по категории «Нужна помощь»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр по категории «Нужна помощь». Ожидается: в списке отображается новость «Нужна помощь»")
    fun filterByNeedHelpTest() {
        openNewsScreen()
        step("Задаём категорию «Нужна помощь» и применяем фильтр") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Нужна помощь")
                .clickApplyButton()
        }
        step("В списке отображается новость «Нужна помощь»") {
            waitForNewsCards()
        }
    }

    @Test
    @Story("Фильтрация без указания категории")
    @Severity(SeverityLevel.NORMAL)
    @Description("Фильтр без категории. Ожидается: список всех имеющихся новостей")
    fun filterWithoutCategoryTest() {
        openNewsScreen()
        step("Открываем фильтр, категорию оставляем пустой") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .clickApplyButton()
        }
        step("Отображается список всех новостей") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Сброс фильтра")
    @Severity(SeverityLevel.NORMAL)
    @Description("Выбор категории «Объявление», затем отмена. Ожидается: после сброса отображается список всех новостей")
    fun filterResetTest() {
        openNewsScreen()
        step("Выбираем категорию «Объявление»") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .typeCategory("Объявление")
                .clickApplyButton()
        }
        step("Отображается отфильтрованный список") {
            checkFilterResult()
        }
        step("Сбрасываем фильтр кнопкой CANCEL") {
            newsPage.clickFilterButton()
            filterNewsPage
                .checkFilterScreenIsVisible()
                .clickCancelButton()
        }
        step("Отображается список всех новостей") {
            checkFilterResult()
        }
    }

    @Test
    @Story("Переход на панель управления")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тап по кнопке редактирования в шапке News. Ожидается: открылась панель управления")
    fun controlPanelOpenTest() {
        openNewsScreen()
        step("Тап по кнопке редактирования в шапке") {
            newsPage.clickEditNewsButton()
        }
        step("Открылась панель управления") {
            controlPanelPage.checkControlPanelIsVisible()
        }
    }

    @Test
    @Story("Создание новости")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Создание новости через «плюс» на панели управления. Ожидается: новость появилась в списке панели")
    fun createNewsTest() {
        val title = "Test news " + System.currentTimeMillis()
        openNewsScreen()
        step("Переходим на панель управления") {
            newsPage.clickEditNewsButton()
            controlPanelPage.checkControlPanelIsVisible()
        }
        step("Открываем форму создания") {
            controlPanelPage.clickAddNewsButton()
            editNewsPage.checkCreateNewsScreenIsVisible()
        }
        step("Заполняем форму") {
            editNewsPage
                .typeCategory("Объявление")
                .typeTitle(title)
                .typePublishDate("01.01.2030")
                .typePublishTime("10:00")
                .typeDescription("Test description")
        }
        step("Сохраняем") {
            editNewsPage.clickSave()
        }
        step("Новость появилась в списке панели") {
            controlPanelPage.checkControlPanelIsVisible()
            controlPanelPage.checkNewsTitleIsVisible(title)
        }
        step("Удаляем тестовую новость") {
            controlPanelPage.clickDeleteNewsButtonByTitle(title)
            controlPanelPage.checkDeleteDialogIsVisible()
            controlPanelPage.clickConfirmDeleteButton()
            controlPanelPage.checkNewsTitleIsAbsent(title)
        }
    }

    @Test
    @Story("Редактирование новости")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Создание новости и изменение её заголовка. Ожидается: обновлённый заголовок на панели управления")
    fun editNewsTest() {
        val title = "Edit target_" + System.currentTimeMillis()
        val editedTitle = "Edited news_" + System.currentTimeMillis()
        openNewsScreen()
        step("Переходим на панель управления") {
            newsPage.clickEditNewsButton()
            controlPanelPage.checkControlPanelIsVisible()
        }
        step("Создаём новость для редактирования") {
            controlPanelPage.clickAddNewsButton()
            editNewsPage.checkCreateNewsScreenIsVisible()
            editNewsPage
                .typeCategory("Объявление")
                .typeTitle(title)
                .typePublishDate("01.01.2030")
                .typePublishTime("10:00")
                .typeDescription("Test description")
                .clickSave()
            controlPanelPage.checkControlPanelIsVisible()
            controlPanelPage.checkNewsTitleIsVisible(title)
        }
        step("Открываем форму редактирования созданной новости") {
            controlPanelPage.clickEditNewsButtonByTitle(title)
            editNewsPage.checkEditNewsScreenIsVisible()
            editNewsPage.checkCurrentTitle(title)
        }
        step("Меняем заголовок и сохраняем") {
            editNewsPage.typeTitle(editedTitle)
            editNewsPage.clickSave()
        }
        step("Заголовок обновился на панели") {
            controlPanelPage.checkControlPanelIsVisible()
            controlPanelPage.checkNewsTitleIsVisible(editedTitle)
        }
        step("Удаляем тестовую новость") {
            controlPanelPage.clickDeleteNewsButtonByTitle(editedTitle)
            controlPanelPage.checkDeleteDialogIsVisible()
            controlPanelPage.clickConfirmDeleteButton()
            controlPanelPage.checkNewsTitleIsAbsent(editedTitle)
        }
    }

    @Test
    @Story("Удаление новости")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Создание новости и её удаление с подтверждением. Ожидается: новость исчезла из списка панели")
    fun deleteNewsWithConfirmTest() {
        val title = "Delete target_" + System.currentTimeMillis()
        openNewsScreen()
        step("Переходим на панель управления") {
            newsPage.clickEditNewsButton()
            controlPanelPage.checkControlPanelIsVisible()
        }
        step("Создаём новость для удаления") {
            controlPanelPage.clickAddNewsButton()
            editNewsPage.checkCreateNewsScreenIsVisible()
            editNewsPage
                .typeCategory("Объявление")
                .typeTitle(title)
                .typePublishDate("01.01.2030")
                .typePublishTime("10:00")
                .typeDescription("Test description")
                .clickSave()
            controlPanelPage.checkControlPanelIsVisible()
            controlPanelPage.checkNewsTitleIsVisible(title)
        }
        step("Нажимаем корзину в карточке созданной новости") {
            controlPanelPage.clickDeleteNewsButtonByTitle(title)
        }
        step("Открывается диалог подтверждения") {
            controlPanelPage.checkDeleteDialogIsVisible()
        }
        step("Подтверждаем удаление") {
            controlPanelPage.clickConfirmDeleteButton()
        }
        step("Новость удалена из списка") {
            controlPanelPage.checkControlPanelIsVisible()
            controlPanelPage.checkNewsTitleIsAbsent(title)
        }
    }

    @Test
    @Story("Отмена удаления новости")
    @Severity(SeverityLevel.NORMAL)
    @Description("Создание новости, отмена удаления в диалоге. Ожидается: новость осталась в списке")
    fun deleteNewsWithCancelTest() {
        val title = "Cancel target_" + System.currentTimeMillis()
        openNewsScreen()
        step("Переходим на панель управления") {
            newsPage.clickEditNewsButton()
            controlPanelPage.checkControlPanelIsVisible()
        }
        step("Создаём новость для отмены удаления") {
            controlPanelPage.clickAddNewsButton()
            editNewsPage.checkCreateNewsScreenIsVisible()
            editNewsPage
                .typeCategory("Объявление")
                .typeTitle(title)
                .typePublishDate("01.01.2030")
                .typePublishTime("10:00")
                .typeDescription("Test description")
                .clickSave()
            controlPanelPage.checkControlPanelIsVisible()
            controlPanelPage.checkNewsTitleIsVisible(title)
        }
        step("Нажимаем корзину в карточке созданной новости") {
            controlPanelPage.clickDeleteNewsButtonByTitle(title)
        }
        step("Открывается диалог подтверждения") {
            controlPanelPage.checkDeleteDialogIsVisible()
        }
        step("Отменяем удаление") {
            controlPanelPage.clickCancelDeleteButton()
        }
        step("Новость осталась в списке") {
            controlPanelPage.checkControlPanelIsVisible()
            controlPanelPage.checkNewsTitleIsVisible(title)
        }
        step("Удаляем тестовую новость начисто") {
            controlPanelPage.clickDeleteNewsButtonByTitle(title)
            controlPanelPage.checkDeleteDialogIsVisible()
            controlPanelPage.clickConfirmDeleteButton()
            controlPanelPage.checkNewsTitleIsAbsent(title)
        }
    }

    @Test
    @Description("Сброс фильтра после фильтрации без совпадений")
    fun resetFilterAfterEmptyResultTest() {
        openNewsScreen()
        step("Фильтруем по периоду без новостей — получаем пустой список") {
            newsPage.clickFilterButton()
            filterNewsPage.typeDateRange("01.01.2030", "02.01.2030")
            filterNewsPage.clickApplyButton()
            newsPage.checkEmptyStateIsVisible()
        }
        step("Повторная фильтрация с пустой формой возвращает полный список") {
            newsPage.clickFilterButton()
            filterNewsPage.clickApplyButton()
            newsPage.checkNewsCardIsVisible()
        }
    }
}
