package org.scrobotic.humbank.ui.elements.icons.processed

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Gavel: ImageVector
    get() {
        if (_Gavel != null) {
            return _Gavel!!
        }
        _Gavel = ImageVector.Builder(
            name = "Gavel",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(160f, 840f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(480f)
                verticalLineToRelative(80f)
                lineTo(160f, 840f)
                close()
                moveTo(386f, 646f)
                lineTo(160f, 420f)
                lineToRelative(84f, -86f)
                lineToRelative(228f, 226f)
                lineToRelative(-86f, 86f)
                close()
                moveTo(640f, 392f)
                lineTo(414f, 164f)
                lineToRelative(86f, -84f)
                lineToRelative(226f, 226f)
                lineToRelative(-86f, 86f)
                close()
                moveTo(824f, 800f)
                lineTo(302f, 278f)
                lineToRelative(56f, -56f)
                lineToRelative(522f, 522f)
                lineToRelative(-56f, 56f)
                close()
            }
        }.build()

        return _Gavel!!
    }

@Suppress("ObjectPropertyName")
private var _Gavel: ImageVector? = null
