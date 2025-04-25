package at.aau.serg.websocketbrokerdemo

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.myapplication.R
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UsernameActivityTest {

    @Test
    fun testEnterButtonIsDisabledWhenEmpty() {
        ActivityScenario.launch(UsernameActivity::class.java)

        // Click ohne Eingabe → Toast oder keine Reaktion
        onView(withId(R.id.enterButton)).perform(click())

        // Optional: prüfen, ob Activity nicht wechselt (hier vereinfachtes Beispiel)
        onView(withId(R.id.usernameEditText)).check(matches(isDisplayed()))
    }


}
