package at.aau.serg.websocketbrokerdemo

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameBoardActivityTest {

    /*
    @Test
    fun updateDice_setsDiceTextCorrectly() {
        val scenario = ActivityScenario.launch(GameBoardActivity::class.java)
        scenario.onActivity { activity ->
            activity.updateDice(5, 2)
        }
        onView(withId(R.id.textDice)).check(matches(withText("Gewürfelt: 5 und 2")))
    }



    @Test
    fun updateTile_setsTileTextCorrectly() {
        val scenario = ActivityScenario.launch(GameBoardActivity::class.java)
        scenario.onActivity { activity ->
            activity.updateTile("Bahnhof West", 10)
        }
        onView(withId(R.id.textTile)).check(matches(withText("Gelandet auf: Bahnhof West (10)")))
    }


    @Test
    fun updateCashDisplay_setsCashTextCorrectly() {
        val scenario = ActivityScenario.launch(GameBoardActivity::class.java)
        scenario.onActivity { activity ->
            activity.updateCashDisplay(1500)
        }
        onView(withId(R.id.textCash)).check(matches(withText("Geld: 1500 €")))
    }


    @Test
    fun enableDiceButton_makesDiceButtonVisibleAndEnabled() {
        val scenario = ActivityScenario.launch(GameBoardActivity::class.java)
        scenario.onActivity { activity ->
            activity.enableDiceButton()
        }
        onView(withId(R.id.rollDiceBtn)).check(matches(isDisplayed()))
        onView(withId(R.id.rollDiceBtn)).check(matches(isEnabled()))
    }


    @Test
    fun disableDiceButton_hidesDiceButton() {
        val scenario = ActivityScenario.launch(GameBoardActivity::class.java)
        scenario.onActivity { activity ->
            activity.disableDiceButton()
        }
        onView(withId(R.id.rollDiceBtn)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

     */

    @Test
    fun hideActionButtons_hidesBuyHouseHotelButtons() {
        val scenario = ActivityScenario.launch(GameBoardActivity::class.java)
        scenario.onActivity { activity ->
            activity.hideActionButtons()
        }
        onView(withId(R.id.buybtn)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.buildHouseBtn)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.buildHotelBtn)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}