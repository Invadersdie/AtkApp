package com.atk.app.ui.screen

import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.image.KImageView
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.text.KTextView
import com.atk.app.R

class HomeScreen : Screen<HomeScreen>() {
    val companiesEditText = KEditText { withId(R.id.companies) }
    val imeiEditText = KEditText { withId(R.id.imei) }
    val equipmentEditText = KEditText { withId(R.id.hwTypeView) }
    val imeiQrButton = KImageView { withId(R.id.takeQr) }
    val iccidEditText = KEditText { withId(R.id.iccid) }
    val phoneOperatorTextView = KTextView { withId(R.id.phoneOperator) }
    val phoneEditText = KEditText { withId(R.id.phone) }
    val objectNameEditText = KEditText { withId(R.id.name) }
//    val companiesSpinnerView = KSpinnerItem(Espresso.onData(withText("NWTrans Poland")))
}