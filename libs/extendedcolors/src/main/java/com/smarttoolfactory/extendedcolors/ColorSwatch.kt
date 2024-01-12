package com.smarttoolfactory.extendedcolors

import androidx.compose.ui.graphics.Color

object ColorSwatch {

    val primaryHeaderColors by lazy {
        listOf(
            Color(0xffF44336),
            Color(0xffE91E63),
            Color(0xff9C27B0),
            Color(0xff673AB7),
            Color(0xff3F51B5),
            Color(0xff2196F3),
            Color(0xff03A9F4),
            Color(0xff00BCD4),
            Color(0xff009688),
            Color(0xff4CAF50),
            Color(0xff8BC34A),
            Color(0xffCDDC39),
            Color(0xffFFEB3B),
            Color(0xffFFC107),
            Color(0xffFF9800),
            Color(0xffFF5722),
            Color(0xff795548),
            Color(0xff9E9E9E),
            Color(0xff607D8B)
        )
    }

    val accentHeaderColors by lazy {
        listOf(
            Color(0xffF44336),
            Color(0xffE91E63),
            Color(0xff9C27B0),
            Color(0xff673AB7),
            Color(0xff3F51B5),
            Color(0xff2196F3),
            Color(0xff03A9F4),
            Color(0xff00BCD4),
            Color(0xff009688),
            Color(0xff4CAF50),
            Color(0xff8BC34A),
            Color(0xffCDDC39),
            Color(0xffFFEB3B),
            Color(0xffFFC107),
            Color(0xffFF9800),
            Color(0xffFF5722),
        )
    }

    val primaryColorSwatches by lazy {
        listOf(
            red,
            pink,
            purple,
            deepPurple,
            indigo,
            blue,
            lightBlue,
            cyan,
            teal,
            green,
            lightGreen,
            lime,
            yellow,
            amber,
            orange,
            deepOrange,
            brown,
            grey,
            blueGrey
        )
    }


    val accentColorSwatches by lazy {
        listOf(
            redAccent,
            pinkAccent,
            purpleAccent,
            deepPurpleAccent,
            indigoAccent,
            blueAccent,
            lightBlueAccent,
            cyanAccent,
            tealAccent,
            greenAccent,
            lightGreenAccent,
            limeAccent,
            yellowAccent,
            amberAccent,
            orangeAccent,
            deepOrangeAccent,
        )
    }

    val red by lazy {
        linkedMapOf(
            50 to Color(0xffFFEBEE),
            100 to Color(0xffFFCDD2),
            200 to Color(0xffEF9A9A),
            300 to Color(0xffE57373),
            400 to Color(0xffEF5350),
            500 to Color(0xffF44336),
            600 to Color(0xffE53935),
            700 to Color(0xffD32F2F),
            800 to Color(0xffC62828),
            900 to Color(0xffB71C1C)
        )
    }

    val redAccent by lazy {
        linkedMapOf(
            100 to Color(0xffFF8A80),
            200 to Color(0xffFF5252),
            400 to Color(0xffFF1744),
            700 to Color(0xffD50000)
        )
    }

    val pink by lazy {
        linkedMapOf(
            50 to Color(0xffFCE4EC),
            100 to Color(0xffF8BBD0),
            200 to Color(0xffF48FB1),
            300 to Color(0xffF06292),
            400 to Color(0xffEC407A),
            500 to Color(0xffE91E63),
            600 to Color(0xffD81B60),
            700 to Color(0xffC2185B),
            800 to Color(0xffAD1457),
            900 to Color(0xff880E4F)
        )
    }

    val pinkAccent by lazy {
        linkedMapOf(
            100 to Color(0xffFF80AB),
            200 to Color(0xffFF4081),
            400 to Color(0xffF50057),
            700 to Color(0xffC51162)
        )
    }

    val purple by lazy {
        linkedMapOf(
            50 to Color(0xffF3E5F5),
            100 to Color(0xffE1BEE7),
            200 to Color(0xffCE93D8),
            300 to Color(0xffBA68C8),
            400 to Color(0xffAB47BC),
            500 to Color(0xff9C27B0),
            600 to Color(0xff8E24AA),
            700 to Color(0xff7B1FA2),
            800 to Color(0xff6A1B9A),
            900 to Color(0xff4A148C)
        )
    }

    val purpleAccent by lazy {
        linkedMapOf(
            100 to Color(0xffEA80FC),
            200 to Color(0xffE040FB),
            400 to Color(0xffD500F9),
            700 to Color(0xffAA00FF)
        )
    }

    val deepPurple by lazy {
        linkedMapOf(
            50 to Color(0xffEDE7F6),
            100 to Color(0xffD1C4E9),
            200 to Color(0xffB39DDB),
            300 to Color(0xff9575CD),
            400 to Color(0xff7E57C2),
            500 to Color(0xff673AB7),
            600 to Color(0xff5E35B1),
            700 to Color(0xff512DA8),
            800 to Color(0xff4527A0),
            900 to Color(0xff311B92)
        )
    }

    val deepPurpleAccent by lazy {
        linkedMapOf(
            100 to Color(0xffB388FF),
            200 to Color(0xff7C4DFF),
            400 to Color(0xff651FFF),
            700 to Color(0xff6200EA)
        )
    }

    val indigo by lazy {
        linkedMapOf(
            50 to Color(0xffE8EAF6),
            100 to Color(0xffC5CAE9),
            200 to Color(0xff9FA8DA),
            300 to Color(0xff7986CB),
            400 to Color(0xff5C6BC0),
            500 to Color(0xff3F51B5),
            600 to Color(0xff3949AB),
            700 to Color(0xff303F9F),
            800 to Color(0xff283593),
            900 to Color(0xff1A237E)
        )
    }

    val indigoAccent by lazy {
        linkedMapOf(
            100 to Color(0xff8C9EFF),
            200 to Color(0xff536DFE),
            400 to Color(0xff3D5AFE),
            700 to Color(0xff304FFE)
        )
    }

    val blue by lazy {
        linkedMapOf(
            50 to Color(0xffE3F2FD),
            100 to Color(0xffBBDEFB),
            200 to Color(0xff90CAF9),
            300 to Color(0xff64B5F6),
            400 to Color(0xff42A5F5),
            500 to Color(0xff2196F3),
            600 to Color(0xff1E88E5),
            700 to Color(0xff1976D2),
            800 to Color(0xff1565C0),
            900 to Color(0xff0D47A1)
        )
    }

    val blueAccent by lazy {
        linkedMapOf(
            100 to Color(0xff82B1FF),
            200 to Color(0xff448AFF),
            400 to Color(0xff2979FF),
            700 to Color(0xff2962FF)
        )
    }
    val lightBlue by lazy {
        linkedMapOf(
            50 to Color(0xffE1F5FE),
            100 to Color(0xffB3E5FC),
            200 to Color(0xff81D4FA),
            300 to Color(0xff4FC3F7),
            400 to Color(0xff29B6F6),
            500 to Color(0xff03A9F4),
            600 to Color(0xff039BE5),
            700 to Color(0xff0288D1),
            800 to Color(0xff0277BD),
            900 to Color(0xff01579B)
        )
    }

    val lightBlueAccent by lazy {
        linkedMapOf(
            100 to Color(0xff80D8FF),
            200 to Color(0xff40C4FF),
            400 to Color(0xff00B0FF),
            700 to Color(0xff0091EA)
        )
    }

    val cyan by lazy {
        linkedMapOf(
            50 to Color(0xffE0F7FA),
            100 to Color(0xffB2EBF2),
            200 to Color(0xff80DEEA),
            300 to Color(0xff4DD0E1),
            400 to Color(0xff26C6DA),
            500 to Color(0xff00BCD4),
            600 to Color(0xff00ACC1),
            700 to Color(0xff0097A7),
            800 to Color(0xff00838F),
            900 to Color(0xff006064)
        )
    }

    val cyanAccent by lazy {
        linkedMapOf(
            100 to Color(0xff84FFFF),
            200 to Color(0xff18FFFF),
            400 to Color(0xff00E5FF),
            700 to Color(0xff00B8D4)
        )
    }

    val teal by lazy {
        linkedMapOf(
            50 to Color(0xffE0F2F1),
            100 to Color(0xffB2DFDB),
            200 to Color(0xff80CBC4),
            300 to Color(0xff4DB6AC),
            400 to Color(0xff26A69A),
            500 to Color(0xff009688),
            600 to Color(0xff00897B),
            700 to Color(0xff00796B),
            800 to Color(0xff00695C),
            900 to Color(0xff004D40)
        )
    }

    val tealAccent by lazy {
        linkedMapOf(
            100 to Color(0xffA7FFEB),
            200 to Color(0xff64FFDA),
            400 to Color(0xff1DE9B6),
            700 to Color(0xff00BFA5)
        )
    }

    val green by lazy {
        linkedMapOf(
            50 to Color(0xffE8F5E9),
            100 to Color(0xffC8E6C9),
            200 to Color(0xffA5D6A7),
            300 to Color(0xff81C784),
            400 to Color(0xff66BB6A),
            500 to Color(0xff4CAF50),
            600 to Color(0xff43A047),
            700 to Color(0xff388E3C),
            800 to Color(0xff2E7D32),
            900 to Color(0xff1B5E20)
        )
    }

    val greenAccent by lazy {
        linkedMapOf(
            100 to Color(0xffB9F6CA),
            200 to Color(0xff69F0AE),
            400 to Color(0xff00E676),
            700 to Color(0xff00C853)
        )
    }

    val lightGreen by lazy {
        linkedMapOf(
            50 to Color(0xffF1F8E9),
            100 to Color(0xffDCEDC8),
            200 to Color(0xffC5E1A5),
            300 to Color(0xffAED581),
            400 to Color(0xff9CCC65),
            500 to Color(0xff8BC34A),
            600 to Color(0xff7CB342),
            700 to Color(0xff689F38),
            800 to Color(0xff558B2F),
            900 to Color(0xff33691E)
        )
    }

    val lightGreenAccent by lazy {
        linkedMapOf(
            100 to Color(0xffCCFF90),
            200 to Color(0xffB2FF59),
            400 to Color(0xff76FF03),
            700 to Color(0xff64DD17)
        )
    }

    val lime by lazy {
        linkedMapOf(
            50 to Color(0xffF9FBE7),
            100 to Color(0xffF0F4C3),
            200 to Color(0xffE6EE9C),
            300 to Color(0xffDCE775),
            400 to Color(0xffD4E157),
            500 to Color(0xffCDDC39),
            600 to Color(0xffC0CA33),
            700 to Color(0xffAFB42B),
            800 to Color(0xff9E9D24),
            900 to Color(0xff827717)
        )
    }

    val limeAccent by lazy {
        linkedMapOf(
            100 to Color(0xffF4FF81),
            200 to Color(0xffEEFF41),
            400 to Color(0xffC6FF00),
            700 to Color(0xffAEEA00)
        )
    }


    val yellow by lazy {
        linkedMapOf(
            50 to Color(0xffFFFDE7),
            100 to Color(0xffFFF9C4),
            200 to Color(0xffFFF59D),
            300 to Color(0xffFFF176),
            400 to Color(0xffFFEE58),
            500 to Color(0xffFFEB3B),
            600 to Color(0xffFDD835),
            700 to Color(0xffFBC02D),
            800 to Color(0xffF9A825),
            900 to Color(0xffF57F17)
        )
    }

    val yellowAccent by lazy {
        linkedMapOf(
            100 to Color(0xffFFFF8D),
            200 to Color(0xffFFFF00),
            400 to Color(0xffFFEA00),
            700 to Color(0xffFFD600)
        )
    }

    val amber by lazy {
        linkedMapOf(
            50 to Color(0xffFFF8E1),
            100 to Color(0xffFFECB3),
            200 to Color(0xffFFE082),
            300 to Color(0xffFFD54F),
            400 to Color(0xffFFCA28),
            500 to Color(0xffFFC107),
            600 to Color(0xffFFB300),
            700 to Color(0xffFFA000),
            800 to Color(0xffFF8F00),
            900 to Color(0xffFF6F00)
        )
    }

    val amberAccent by lazy {
        linkedMapOf(
            100 to Color(0xffFFE57F),
            200 to Color(0xffFFD740),
            400 to Color(0xffFFC400),
            700 to Color(0xffFFAB00)
        )
    }

    val orange by lazy {
        linkedMapOf(
            50 to Color(0xffFFF3E0),
            100 to Color(0xffFFE0B2),
            200 to Color(0xffFFCC80),
            300 to Color(0xffFFB74D),
            400 to Color(0xffFFA726),
            500 to Color(0xffFF9800),
            600 to Color(0xffFB8C00),
            700 to Color(0xffF57C00),
            800 to Color(0xffEF6C00),
            900 to Color(0xffE65100)
        )
    }

    val orangeAccent by lazy {
        linkedMapOf(
            100 to Color(0xffFFD180),
            200 to Color(0xffFFAB40),
            400 to Color(0xffFF9100),
            700 to Color(0xffFF6D00)
        )
    }

    val deepOrange by lazy {
        linkedMapOf(
            50 to Color(0xffFBE9E7),
            100 to Color(0xffFFCCBC),
            200 to Color(0xffFFAB91),
            300 to Color(0xffFF8A65),
            400 to Color(0xffFF7043),
            500 to Color(0xffFF5722),
            600 to Color(0xffF4511E),
            700 to Color(0xffE64A19),
            800 to Color(0xffD84315),
            900 to Color(0xffBF360C)
        )
    }

    val deepOrangeAccent by lazy {
        linkedMapOf(
            100 to Color(0xffFF9E80),
            200 to Color(0xffFF6E40),
            400 to Color(0xffFF3D00),
            700 to Color(0xffDD2C00)
        )
    }

    val brown by lazy {
        linkedMapOf(
            50 to Color(0xffEFEBE9),
            100 to Color(0xffD7CCC8),
            200 to Color(0xffBCAAA4),
            300 to Color(0xffA1887F),
            400 to Color(0xff8D6E63),
            500 to Color(0xff795548),
            600 to Color(0xff6D4C41),
            700 to Color(0xff5D4037),
            800 to Color(0xff4E342E),
            900 to Color(0xff3E2723)
        )
    }

    val grey by lazy {
        linkedMapOf(
            50 to Color(0xffFAFAFA),
            100 to Color(0xffF5F5F5),
            200 to Color(0xffEEEEEE),
            300 to Color(0xffE0E0E0),
            400 to Color(0xffBDBDBD),
            500 to Color(0xff9E9E9E),
            600 to Color(0xff757575),
            700 to Color(0xff616161),
            800 to Color(0xff424242),
            900 to Color(0xff212121)
        )
    }

    val blueGrey by lazy {
        linkedMapOf(
            50 to Color(0xffECEFF1),
            100 to Color(0xffCFD8DC),
            200 to Color(0xffB0BEC5),
            300 to Color(0xff90A4AE),
            400 to Color(0xff78909C),
            500 to Color(0xff607D8B),
            600 to Color(0xff546E7A),
            700 to Color(0xff455A64),
            800 to Color(0xff37474F),
            900 to Color(0xff263238)
        )
    }
}
