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
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.edu.qamid.R
import ru.edu.qamid.pages.LoginPage
import ru.edu.qamid.pages.NavigationPage
import ru.edu.qamid.ui.AppActivity
import ru.edu.qamid.utils.waitUntilVisible

@Epic("Навигация")
@Feature("Меню навигации")
@RunWith(AllureAndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AppActivity::class.java)

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

    @Test
    @Story("Открытие меню навигации")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Нажатие на кнопку меню на главной странице. Ожидается: открывается меню навигации с пунктом News")
    fun navigationMenuOpenTest() {
        loginAsValidUser()
        step("Нажимаем кнопку меню") {
            NavigationPage().clickMainMenu()
        }
        step("Меню открылось, пункт News виден") {
            NavigationPage().checkNewsMenuItemIsVisible()
        }
    }

    @Test
    @Story("Переход во вкладку News")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Выбор пункта News в меню навигации. Ожидается: открывается экран списка новостей")
    fun newsTabOpenTest() {
        loginAsValidUser()
        step("Нажимаем кнопку меню") {
            NavigationPage().clickMainMenu()
        }
        step("Выбираем пункт News") {
            NavigationPage().clickNewsMenuItem()
        }
        step("Открылся экран списка новостей") {
            waitUntilVisible(withId(R.id.news_sort_button))
            NavigationPage().checkNewsScreenIsVisible()
        }
    }

    @Test
    @Story("Переход на ALL NEWS")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Нажатие на ссылку ALL NEWS на главной странице. Ожидается: открывается экран списка новостей")
    fun allNewsOpenTest() {
        loginAsValidUser()
        step("Нажимаем ALL NEWS") {
            NavigationPage().clickAllNewsButton()
        }
        step("Открылся экран списка новостей") {
            waitUntilVisible(withId(R.id.news_sort_button))
            NavigationPage().checkNewsScreenIsVisible()
        }
    }
}
