package ru.edu.qamid.tests

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.edu.qamid.R
import ru.edu.qamid.pages.ControlPanelPage
import ru.edu.qamid.pages.EditNewsPage
import ru.edu.qamid.pages.LoginPage
import ru.edu.qamid.pages.NavigationPage
import ru.edu.qamid.pages.NewsPage
import ru.edu.qamid.ui.AppActivity
import ru.edu.qamid.utils.waitUntilVisible

data class SeedNews(val title: String, val category: String, val date: String)

@Severity(SeverityLevel.MINOR)
@RunWith(AllureAndroidJUnit4::class)
class SeedNewsTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AppActivity::class.java)

    private val newsPage = NewsPage()
    private val controlPanelPage = ControlPanelPage()
    private val editNewsPage = EditNewsPage()
    private val navigationPage = NavigationPage()

    private val baseNews = listOf(
        SeedNews("Seed news Announcement", "Объявление", "21.11.2026"),
        SeedNews("Seed news Birthday", "День рождения", "21.11.2026"),
        SeedNews("Seed news Salary", "Зарплата", "21.11.2026"),
        SeedNews("Seed news Trade union", "Профсоюз", "21.11.2026"),
        SeedNews("Seed news Holiday", "Праздник", "21.11.2026"),
        SeedNews("Seed news Massage", "Массаж", "21.11.2026"),
        SeedNews("Seed news Gratitude", "Благодарность", "21.11.2026"),
        SeedNews("Seed news Need help", "Нужна помощь", "21.11.2026"),
        SeedNews("Seed news Period", "Объявление", "10.11.2026")
    )

    private fun openControlPanel() {
        step("Авторизуемся при необходимости") {
            val onLoginScreen = try {
                waitUntilVisible(withId(R.id.login_edit_text), 5000)
                true
            } catch (e: Exception) {
                false
            }
            if (onLoginScreen) {
                LoginPage()
                    .enterLogin("login2")
                    .enterPassword("password2")
                    .clickSignIn()
                waitUntilVisible(withId(R.id.main_menu_image_button))
            }
        }
        step("Переходим во вкладку News") {
            navigationPage.clickMainMenu()
            navigationPage.clickNewsMenuItem()
            waitUntilVisible(withId(R.id.news_sort_button))
        }
        step("Открываем панель управления") {
            newsPage.clickEditNewsButton()
            controlPanelPage.checkControlPanelIsVisible()
        }
    }

    @Test
    @Description("Проверяет наличие базового набора новостей и создаёт недостающие. " +
            "Запускается перед NewsTest, чтобы фильтры и сортировка всегда имели данные")
    fun seedNewsDataTest() {
        openControlPanel()
        for (news in baseNews) {
            if (controlPanelPage.isNewsTitlePresent(news.title)) {
                step("«${news.title}» уже есть — пропускаем") {
                    // существующие новости не трогаем
                }
            } else {
                step("Создаём «${news.title}» (${news.category}, ${news.date})") {
                    controlPanelPage.clickAddNewsButton()
                    editNewsPage.checkCreateNewsScreenIsVisible()
                    editNewsPage
                        .typeCategory(news.category)
                        .typeTitle(news.title)
                        .typePublishDate(news.date)
                        .typePublishTime("12:00")
                        .typeDescription("Seed data for NewsTest")
                        .clickSave()
                    controlPanelPage.checkControlPanelIsVisible()
                    controlPanelPage.checkNewsTitleIsVisible(news.title)
                }
            }
        }
    }
}
