package org.scrobotic.humbank.ui.elements.icons.processed

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val ArrowUpward: ImageVector
    get() {
        if (_ArrowUpward != null) {
            return _ArrowUpward!!
        }
        _ArrowUpward = ImageVector.Builder(
            name = "ArrowUpward",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFFE3E3E3))) {
                moveTo(440f, 800f)
                verticalLineToRelative(-487f)
                lineTo(216f, 537f)
                lineToRelative(-56f, -57f)
                lineToRelative(320f, -320f)
                lineToRelative(320f, 320f)
                lineToRelative(-56f, 57f)
                lineToRelative(-224f, -224f)
                verticalLineToRelative(487f)
                horizontalLineToRelative(-80f)
                close()
            }
        }.build()

        return _ArrowUpward!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowUpward: ImageVector? = null
