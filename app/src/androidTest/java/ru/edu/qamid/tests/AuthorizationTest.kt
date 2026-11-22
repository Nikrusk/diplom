package ru.edu.qamid.tests

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test
import ru.edu.qamid.R
import ru.edu.qamid.pages.LoginPage
import ru.edu.qamid.ui.AppActivity
import ru.edu.qamid.utils.ToastProbe
import ru.edu.qamid.utils.waitUntilVisible
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Feature
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import org.junit.runner.RunWith

@Epic("Авторизация")
@Feature("Вход в приложение")
@RunWith(AllureAndroidJUnit4::class)
class AuthorizationTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AppActivity::class.java)

    @Test
    @Story("Авторизация валидными данными")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Вход с валидной парой login2/password2. Ожидается переход на главную страницу")
    fun loginWithValidData() {
        step("Открыт экран авторизации") {
            waitUntilVisible(withId(R.id.login_edit_text), 10000)
        }
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
    @Story("Авторизация невалидными данными")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Вход с невалидной парой login/password. Ожидается: пользователь остаётся на экране авторизации, отображается тост \"Something went wrong. Try again later.\"")
    fun loginWithInvalidData() {
        step("Открыт экран авторизации") {
            waitUntilVisible(withId(R.id.login_edit_text), 10000)
        }
        step("Вводим логин и пароль") {
            LoginPage()
                .enterLogin("login")
                .enterPassword("password")
        }
        step("Нажимаем «Войти»") {
            LoginPage().clickSignIn()
        }
        step("Остаёмся на экране авторизации") {
            LoginPage().checkLoginScreenIsVisible()
        }
        step("Отображается тост «Something went wrong. Try again later.»") {
            val toastProbe = ToastProbe("Something went wrong. Try again later.")
            toastProbe.start()

            LoginPage()
                .enterLogin("login")
                .enterPassword("password")
                .clickSignIn()

            if (!toastProbe.waitForToast()) {
                throw AssertionError("Тост \"Something went wrong. Try again later.\" не показан")
            }
        }
    }
}
