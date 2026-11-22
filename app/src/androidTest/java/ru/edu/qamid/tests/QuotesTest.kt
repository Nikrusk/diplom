package ru.edu.qamid.tests

import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import io.qameta.allure.android.runners.AllureAndroidJUnit4
import io.qameta.allure.kotlin.Allure.step
import io.qameta.allure.kotlin.Description
import io.qameta.allure.kotlin.Epic
import io.qameta.allure.kotlin.Severity
import io.qameta.allure.kotlin.SeverityLevel
import io.qameta.allure.kotlin.Story
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.edu.qamid.R
import ru.edu.qamid.pages.LoginPage
import ru.edu.qamid.pages.NavigationPage
import ru.edu.qamid.pages.QuotesPage
import ru.edu.qamid.ui.AppActivity
import ru.edu.qamid.utils.waitUntilVisible

@Epic("Тематические цитаты")
@RunWith(AllureAndroidJUnit4::class)
class QuotesTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AppActivity::class.java)

    private val navigationPage = NavigationPage()
    private val quotesPage = QuotesPage()

    private fun openQuotesScreen() {
        step("Ждём главный экран или экран авторизации") {
            val onMain = try {
                waitUntilVisible(withId(R.id.main_menu_image_button), 2000)
                true
            } catch (e: Exception) {
                false
            }
            if (!onMain) {
                LoginPage()
                    .enterLogin("login2")
                    .enterPassword("password2")
                    .clickSignIn()
                waitUntilVisible(withId(R.id.main_menu_image_button))
            }
        }
        step("Нажимаем кнопку «Бабочка» (Тематические цитаты)") {
            navigationPage.clickOurMissionButton()
        }
        step("Отобразился список тематических цитат") {
            waitUntilVisible(withId(R.id.our_mission_item_list_recycler_view), 5000)
            quotesPage.checkQuotesScreenIsVisible()
        }
    }

    @Test
    @Story("Переход во вкладку «Тематические цитаты»")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тап по кнопке «Бабочка» на главном экране. Ожидается: открыт список тематических цитат")
    fun quotesOpenTest() {
        openQuotesScreen()
    }

    @Test
    @Story("Разворачивание и сворачивание цитаты")
    @Severity(SeverityLevel.NORMAL)
    @Description("Тап по карточке цитаты. Ожидается: цитата разворачивается, повторный тап — сворачивается")
    fun expandCollapseQuoteTest() {
        openQuotesScreen()
        step("Разворачиваем первую цитату") {
            quotesPage.clickFirstQuote()
            quotesPage.checkFirstQuoteIsExpanded()
        }
        step("Сворачиваем цитату повторным тапом") {
            quotesPage.clickFirstQuote()
            quotesPage.checkFirstQuoteIsCollapsed()
        }
    }
}
