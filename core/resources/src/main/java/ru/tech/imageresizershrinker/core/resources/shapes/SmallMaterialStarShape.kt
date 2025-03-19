/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.resources.shapes

import android.graphics.Matrix
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

val SmallMaterialStarShape: Shape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val baseWidth = 2695.79f
        val baseHeight = 2680.65f

        val path = Path().apply {
            moveTo(2537.8f, 975.157f)
            cubicTo(2623.02f, 1101.61f, 2665.64f, 1164.84f, 2683.13f, 1233.15f)
            cubicTo(2699.69f, 1297.82f, 2700.13f, 1365.57f, 2684.42f, 1430.45f)
            cubicTo(2667.81f, 1498.98f, 2626.03f, 1562.76f, 2542.46f, 1690.32f)
            cubicTo(2517.24f, 1728.81f, 2504.64f, 1748.05f, 2494.33f, 1768.43f)
            cubicTo(2484.53f, 1787.8f, 2476.3f, 1807.93f, 2469.7f, 1828.61f)
            cubicTo(2462.77f, 1850.37f, 2458.26f, 1872.96f, 2449.25f, 1918.15f)
            cubicTo(2419.3f, 2068.37f, 2404.33f, 2143.48f, 2367.9f, 2204.19f)
            cubicTo(2334.18f, 2260.38f, 2287.43f, 2307.62f, 2231.59f, 2341.92f)
            cubicTo(2171.26f, 2378.98f, 2095.52f, 2394.9f, 1944.04f, 2426.74f)
            cubicTo(1897.48f, 2436.53f, 1874.19f, 2441.42f, 1852.04f, 2448.8f)
            cubicTo(1832.55f, 2455.29f, 1813.57f, 2463.25f, 1795.28f, 2472.6f)
            cubicTo(1774.49f, 2483.22f, 1754.88f, 2496.27f, 1715.65f, 2522.35f)
            cubicTo(1584.84f, 2609.34f, 1519.44f, 2652.83f, 1448.72f, 2669.7f)
            cubicTo(1388.23f, 2684.12f, 1325.25f, 2684.53f, 1264.59f, 2670.89f)
            cubicTo(1193.65f, 2654.95f, 1127.69f, 2612.31f, 995.759f, 2527.04f)
            cubicTo(956.196f, 2501.47f, 936.414f, 2488.68f, 915.487f, 2478.33f)
            cubicTo(897.073f, 2469.21f, 877.998f, 2461.51f, 858.423f, 2455.27f)
            cubicTo(836.176f, 2448.18f, 812.831f, 2443.59f, 766.142f, 2434.41f)
            cubicTo(614.258f, 2404.54f, 538.317f, 2389.61f, 477.51f, 2353.34f)
            cubicTo(421.231f, 2319.77f, 373.865f, 2273.14f, 339.421f, 2217.4f)
            cubicTo(302.205f, 2157.16f, 286.253f, 2082.26f, 254.349f, 1932.44f)
            cubicTo(244.752f, 1887.38f, 239.953f, 1864.84f, 232.735f, 1843.18f)
            cubicTo(225.871f, 1822.58f, 217.375f, 1802.56f, 207.326f, 1783.32f)
            cubicTo(196.758f, 1763.08f, 183.9f, 1744f, 158.185f, 1705.84f)
            cubicTo(72.9637f, 1579.38f, 30.3531f, 1516.16f, 12.8577f, 1447.85f)
            cubicTo(-3.7056f, 1383.18f, -4.1467f, 1315.43f, 11.5731f, 1250.55f)
            cubicTo(28.1775f, 1182.01f, 69.9612f, 1118.24f, 153.529f, 990.681f)
            cubicTo(178.745f, 952.192f, 191.353f, 932.947f, 201.657f, 912.571f)
            cubicTo(211.454f, 893.196f, 219.689f, 873.069f, 226.284f, 852.384f)
            cubicTo(233.219f, 830.629f, 237.724f, 808.034f, 246.734f, 762.845f)
            cubicTo(276.684f, 612.629f, 291.659f, 537.521f, 328.088f, 476.81f)
            cubicTo(361.804f, 420.62f, 408.558f, 373.377f, 464.395f, 339.08f)
            cubicTo(524.724f, 302.023f, 600.465f, 286.102f, 751.947f, 254.26f)
            cubicTo(798.513f, 244.472f, 821.796f, 239.578f, 843.949f, 232.199f)
            cubicTo(863.44f, 225.707f, 882.414f, 217.752f, 900.708f, 208.401f)
            cubicTo(921.498f, 197.775f, 941.112f, 184.732f, 980.339f, 158.647f)
            cubicTo(1111.14f, 71.6626f, 1176.55f, 28.1706f, 1247.27f, 11.3026f)
            cubicTo(1307.75f, -3.122f, 1370.73f, -3.5321f, 1431.4f, 10.1038f)
            cubicTo(1502.34f, 26.0493f, 1568.3f, 68.686f, 1700.23f, 153.959f)
            cubicTo(1739.79f, 179.532f, 1759.57f, 192.318f, 1780.5f, 202.673f)
            cubicTo(1798.91f, 211.784f, 1817.99f, 219.492f, 1837.57f, 225.73f)
            cubicTo(1859.81f, 232.819f, 1883.16f, 237.41f, 1929.85f, 246.591f)
            cubicTo(2081.73f, 276.457f, 2157.67f, 291.391f, 2218.48f, 327.659f)
            cubicTo(2274.76f, 361.227f, 2322.12f, 407.856f, 2356.57f, 463.603f)
            cubicTo(2393.78f, 523.834f, 2409.74f, 598.741f, 2441.64f, 748.554f)
            cubicTo(2451.24f, 793.622f, 2456.04f, 816.156f, 2463.25f, 837.819f)
            cubicTo(2470.12f, 858.417f, 2478.61f, 878.434f, 2488.66f, 897.68f)
            cubicTo(2499.23f, 917.921f, 2512.09f, 937f, 2537.8f, 975.157f)
            close()
        }

        return Outline.Generic(
            path
                .asAndroidPath()
                .apply {
                    transform(
                        Matrix().apply {
                            setScale(size.width / baseWidth, size.height / baseHeight)
                        }
                    )
                }
                .asComposePath()
        )
    }
}