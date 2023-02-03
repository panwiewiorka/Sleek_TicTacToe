package com.example.mytictactoe

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mytictactoe.ui.TicApp
import com.example.mytictactoe.ui.theme.MyTicTacToeTheme

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
class TicUiTests {
    /*
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.mytictactoe", appContext.packageName)
    }
     */

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setAppContent() {
        composeTestRule.setContent {
            MyTicTacToeTheme { TicApp() }
        }
    }

    @Test
    fun verifyMenuIsVisible() {
        composeTestRule.onNodeWithTag("Menu Window").assertExists("No Menu")
        // same, without Modifier.testTag("Menu"):
//        val backText = composeTestRule.activity.getString(R.string.back_button)
//        composeTestRule.onNodeWithContentDescription(backText).assertExist("No node with this text was found.")
    }

    @Test
    fun setSizeSliderTo4_ChangesBoardSizeTextTo4_andMakeWinRowSliderEnable() {
        //verifyMenuIsVisible()
        composeTestRule.onNodeWithTag("winRow Slider").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Board size: 3").performTouchInput {
            swipe(start = Offset(0f, 0f) , end = Offset(150f, 0f))
        }
        composeTestRule.onNodeWithTag("Board Size").assertTextEquals("Board size: 4")
        composeTestRule.onNodeWithTag("winRow Slider").assertExists("No winRow Slider is visible")
    }

    @Test
    fun setWinRowSliderTo4_ChangesWinRowText_AndWinRowIconTo4() {
        //verifyMenuIsVisible()
        //setSizeSliderTo4_ChangesBoardSizeTextTo4_andMakeWinRowSliderEnable()
        composeTestRule.onNodeWithTag("winRow Slider").performTouchInput {
            swipe(start = Offset(0f, 0f) , end = Offset(150f, 0f))
        }
        composeTestRule.onNodeWithTag("Win Row").assertTextEquals("Win row: 4")
        composeTestRule.onNodeWithTag("winRow square", true).assertTextEquals("4")
    }

    @Test
    fun tappingStart_MenuDisappears() {
        //setWinRowSliderTo4_ChangesWinRowText_AndWinRowIconTo4()
        composeTestRule.onNodeWithText("START").performClick()
        composeTestRule.onNodeWithTag("Menu Window").assertDoesNotExist()
    }

    @Test
    fun tappingCell_FillsCellWithX_AndChangesCurrentMoveIconTo0_AndCancelAppears() {
        //tappingStart_MenuDisappears()
        composeTestRule.onNodeWithTag("Cancel Icon").assertDoesNotExist()
        composeTestRule.onNodeWithTag("currentMove: X").assertExists("CurrentMove Icon is not X")
        composeTestRule.onNodeWithTag("Cell 0 0").performClick()
        composeTestRule.onNodeWithTag("Text 0 0", true).assertTextEquals("X")
        composeTestRule.onNodeWithTag("currentMove: 0").assertExists("CurrentMove Icon is not 0")
        composeTestRule.onNodeWithTag("Cancel Icon", true).assertExists("No Cancel Icon is visible")
    }

    @Test
    fun tappingCancel_CellXDisappears_AndCancelDisappears_AndChangesCurrentMoveIconToX() {
        composeTestRule.onNodeWithTag("Cancel Button").performClick()
        composeTestRule.onNodeWithTag("Text 0 0", true).assertTextEquals(" ")
        composeTestRule.onNodeWithTag("Cancel Icon").assertDoesNotExist()
        composeTestRule.onNodeWithTag("currentMove: X").assertExists("CurrentMove Icon is not X")
    }

    @Test
    fun tappingMenuAndChangingWinRowTo3AndCancellingMenu_ChecksWinRowCancel() {
        composeTestRule.onNodeWithTag("Cell 0 0").performClick()
        composeTestRule.onNodeWithTag("Menu Button").performClick()
        composeTestRule.onNodeWithTag("winRow Slider").performTouchInput {
            swipe(start = Offset(150f, 0f) , end = Offset(0f, 0f))
        }
        composeTestRule.onNodeWithTag("winRow square", true).assertTextEquals("3")
        pressBack()
        composeTestRule.onNodeWithTag("Menu Window").assertDoesNotExist()
        composeTestRule.onNodeWithTag("winRow square", true).assertTextEquals("4")
    }

    @Test
    fun secondTappingCell_FillsCell_AndChangesCurrentMoveIconTo0() {
        composeTestRule.onNodeWithTag("Cell 1 0").performClick()
        composeTestRule.onNodeWithTag("Cell 1 0").assertTextEquals("0")
        composeTestRule.onNodeWithTag("currentMove: X").assertExists("CurrentMove Icon is not X")
    }

    @Test
    fun fillCellsUntilWin_showsGameOverScreen_AndThenMenu() {
        composeTestRule.onNodeWithTag("Cell 0 1").performClick()
        composeTestRule.onNodeWithTag("Cell 1 1").performClick()
        composeTestRule.onNodeWithTag("Cell 0 2").performClick()
        composeTestRule.onNodeWithTag("Cell 1 2").performClick()
        composeTestRule.onNodeWithTag("Game Over Screen").assertDoesNotExist()
        composeTestRule.onNodeWithTag("Cell 0 3").performClick()
        composeTestRule.onNodeWithTag("Game Over Screen").assertExists("No Game Over Screen")
        composeTestRule.onNodeWithTag("Game Over Screen").performClick()
        composeTestRule.onNodeWithTag("Menu Window").assertExists("No Menu Window")
    }

    @Test
    fun testAll() {
        verifyMenuIsVisible()
        setSizeSliderTo4_ChangesBoardSizeTextTo4_andMakeWinRowSliderEnable()
        setWinRowSliderTo4_ChangesWinRowText_AndWinRowIconTo4()
        tappingStart_MenuDisappears()
        tappingCell_FillsCellWithX_AndChangesCurrentMoveIconTo0_AndCancelAppears()
        tappingCancel_CellXDisappears_AndCancelDisappears_AndChangesCurrentMoveIconToX()
        tappingMenuAndChangingWinRowTo3AndCancellingMenu_ChecksWinRowCancel()
        secondTappingCell_FillsCell_AndChangesCurrentMoveIconTo0()
        fillCellsUntilWin_showsGameOverScreen_AndThenMenu()
    }

}