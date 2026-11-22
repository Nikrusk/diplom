package ru.edu.qamid.utils

import android.view.View
import android.view.accessibility.AccessibilityEvent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.platform.app.InstrumentationRegistry
import io.qameta.allure.android.allureScreenshot
import org.hamcrest.Matcher
import org.hamcrest.Matchers.not
import java.util.concurrent.atomic.AtomicBoolean

fun waitUntilVisible(matcher: Matcher<View>, timeoutMs: Long = 60000) {
    val deadline = System.currentTimeMillis() + timeoutMs
    while (System.currentTimeMillis() < deadline) {
        try {
            onView(matcher).check(matches(isDisplayed()))
            return
        } catch (e: Throwable) {
            Thread.sleep(500)
        }
    }
    onView(matcher).check(matches(isDisplayed()))
}

fun waitUntilGone(viewAction: ViewInteraction, timeoutSeconds: Long = 60) {
    val deadline = System.currentTimeMillis() + timeoutSeconds * 1000
    while (System.currentTimeMillis() < deadline) {
        try {
            viewAction.check(matches(not(isDisplayed())))
            return // элемент исчез — выходим
        } catch (e: NoMatchingViewException) {
            return // элемента нет вообще — сплэш уже ушёл
        } catch (e: AssertionError) {
            // элемент всё ещё виден — ждём дальше
        }
        Thread.sleep(250)
    }
    // вышли по таймауту — финальная проверка уронит тест, если сплэш так и не исчез
    viewAction.check(matches(not(isDisplayed())))
}

fun attachScreenshot(name: String = "Скриншот при падении") {
    allureScreenshot(name = name)
}

class ToastProbe(private val expectedText: String) {
    private val found = AtomicBoolean(false)

    fun start() {
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .setOnAccessibilityEventListener { event ->
                if (event.eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                    val className = event.className?.toString() ?: ""
                    val text = event.text.joinToString(" ")
                    if (className.contains("Toast") && text.contains(expectedText)) {
                        found.set(true)
                    }
                }
            }
    }

    fun waitForToast(timeoutMs: Long = 5000): Boolean {
        val deadline = System.currentTimeMillis() + timeoutMs
        try {
            while (System.currentTimeMillis() < deadline) {
                if (found.get()) return true
                Thread.sleep(100)
            }
        } finally {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .setOnAccessibilityEventListener(null)
        }
        return false
    }
}
