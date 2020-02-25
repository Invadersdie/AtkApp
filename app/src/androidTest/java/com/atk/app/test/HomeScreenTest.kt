package com.atk.app.test

import androidx.navigation.NavController
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.agoda.kakao.screen.Screen
import com.atk.app.environment.*
import com.atk.app.launchFragmentInHiltContainer
import com.atk.app.screens.createunit.CreateUnitFragment
import com.atk.app.ui.base.UITest
import com.atk.app.ui.screen.HomeScreen
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

@MediumTest
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class HomeScreenTest : UITest() {

    override fun PredicateDispatcher.mockSetup() {
        mockForPathStartsWith(
            "/$TEST_LOCAL_URL/ajax.html?svc=token/login",
            "local_session_response.json"
        )
        mockForPathStartsWith(
            "/$TEST_LOCAL_URL/ajax.html?svc=core/get_hw_types",
            "local_hwtypes_response.json"
        )
        mockForPathStartsWith(
            "/$TEST_HOSTING_URL/ajax.html?svc=token/login",
            "local_session_response.json"
        )
        mockForPathStartsWith(
            "/$TEST_HOSTING_URL/ajax.html?svc=core/get_hw_types",
            "local_hwtypes_response.json"
        )
        mockForPathEndsWith("/getEquipmentList", "atk_equipment_list_response.json")
        mockForPathEndsWith("/getSimList", "atk_sim_list_response.json")
        mockForPathEndsWith("/getCompanyList", "atk_company_list_response.json")
    }

    @Test
    fun qrCode() {
        launchFragmentInHiltContainer<CreateUnitFragment>()
        Screen.onScreen<HomeScreen> {
            imeiQrButton.click()
        }
    }

    @Test
    fun showsHomeScreen() {
        val navController = Mockito.mock(NavController::class.java)
        launchFragment(navController)
        Screen.onScreen<HomeScreen> {

            companiesEditText.typeText("nw")
            onView(withText("NWTrans Poland")).inRoot(RootMatchers.isPlatformPopup())
                .perform(click())
            imeiEditText.typeText("862057")
            onView(withText("862057043832077")).inRoot(RootMatchers.isPlatformPopup())
                .perform(click())
            iccidEditText.typeText("70")
            onView(withText("8937204016205386704")).inRoot(RootMatchers.isPlatformPopup())
                .perform(click())
            objectNameEditText.typeText("testObject")



            phoneEditText.hasText("+37257029708")
            phoneOperatorTextView.hasText("M2M")
//            Espresso.onData(ViewMatchers.withText("NWTrans Poland")).inAdapterView()
//            companiesSpinnerView.click()
        }
    }

    private fun launchFragment(navController: NavController?) {
        launchFragmentInHiltContainer<CreateUnitFragment> {
//            Navigation.setViewNavController(requireView(), navController)
        }
    }
}