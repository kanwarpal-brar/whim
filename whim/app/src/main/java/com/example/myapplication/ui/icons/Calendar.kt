import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp


@Composable
fun Calendar(color: Int = MaterialTheme.colorScheme.onBackground.toArgb()): ImageVector {
    return remember {
        ImageVector.Builder(
                name = "Calendar",
                defaultWidth = 30.dp,
                defaultHeight = 30.dp,
                viewportWidth = 16f,
                viewportHeight = 16f
            ).apply {
				path(
    				fill = SolidColor(Color(color)),
    				fillAlpha = 1.0f,
    				stroke = null,
    				strokeAlpha = 1.0f,
    				strokeLineWidth = 1.0f,
    				strokeLineCap = StrokeCap.Butt,
    				strokeLineJoin = StrokeJoin.Miter,
    				strokeLineMiter = 1.0f,
    				pathFillType = PathFillType.EvenOdd
				) {
					moveTo(14f, 4f)
					verticalLineToRelative(-0.994f)
					curveTo(14f, 2.45f, 13.55f, 2f, 12.994f, 2f)
					horizontalLineTo(11f)
					verticalLineToRelative(1f)
					horizontalLineToRelative(-1f)
					verticalLineTo(2f)
					horizontalLineTo(6f)
					verticalLineToRelative(1f)
					horizontalLineTo(5f)
					verticalLineTo(2f)
					horizontalLineTo(3.006f)
					curveTo(2.45f, 2f, 2f, 2.45f, 2f, 3.006f)
					verticalLineToRelative(9.988f)
					curveTo(2f, 13.55f, 2.45f, 14f, 3.006f, 14f)
					horizontalLineToRelative(9.988f)
					curveTo(13.55f, 14f, 14f, 13.55f, 14f, 12.994f)
					verticalLineTo(5f)
					horizontalLineTo(2f)
					verticalLineTo(4f)
					horizontalLineToRelative(12f)
					close()
					moveToRelative(-3f, -3f)
					horizontalLineToRelative(1.994f)
					curveTo(14.102f, 1f, 15f, 1.897f, 15f, 3.006f)
					verticalLineToRelative(9.988f)
					arcTo(2.005f, 2.005f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12.994f, 15f)
					horizontalLineTo(3.006f)
					arcTo(2.005f, 2.005f, 0f, isMoreThanHalf = false, isPositiveArc = true, 1f, 12.994f)
					verticalLineTo(3.006f)
					curveTo(1f, 1.898f, 1.897f, 1f, 3.006f, 1f)
					horizontalLineTo(5f)
					verticalLineTo(0f)
					horizontalLineToRelative(1f)
					verticalLineToRelative(1f)
					horizontalLineToRelative(4f)
					verticalLineTo(0f)
					horizontalLineToRelative(1f)
					verticalLineToRelative(1f)
					close()
					moveTo(4f, 7f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineTo(4f)
					verticalLineTo(7f)
					close()
					moveToRelative(3f, 0f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineTo(7f)
					verticalLineTo(7f)
					close()
					moveToRelative(3f, 0f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineToRelative(-2f)
					verticalLineTo(7f)
					close()
					moveTo(4f, 9f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineTo(4f)
					verticalLineTo(9f)
					close()
					moveToRelative(3f, 0f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineTo(7f)
					verticalLineTo(9f)
					close()
					moveToRelative(3f, 0f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineToRelative(-2f)
					verticalLineTo(9f)
					close()
					moveToRelative(-6f, 2f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineTo(4f)
					verticalLineToRelative(-1f)
					close()
					moveToRelative(3f, 0f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineTo(7f)
					verticalLineToRelative(-1f)
					close()
					moveToRelative(3f, 0f)
					horizontalLineToRelative(2f)
					verticalLineToRelative(1f)
					horizontalLineToRelative(-2f)
					verticalLineToRelative(-1f)
					close()
}
}.build()
    }
}

