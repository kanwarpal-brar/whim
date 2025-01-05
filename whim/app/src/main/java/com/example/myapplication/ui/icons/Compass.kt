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
fun Compass(color: Int = MaterialTheme.colorScheme.onBackground.toArgb()): ImageVector {
    return remember {
        ImageVector.Builder(
                name = "Compass",
                defaultWidth = 34.dp,
                defaultHeight = 34.dp,
                viewportWidth = 24f,
                viewportHeight = 24f
            ).apply {
				path(
    				fill = null,
    				fillAlpha = 1.0f,
    				stroke = SolidColor(Color(color)),
    				strokeAlpha = 1.0f,
    				strokeLineWidth = 1.5f,
    				strokeLineCap = StrokeCap.Round,
    				strokeLineJoin = StrokeJoin.Round,
    				strokeLineMiter = 1.0f,
    				pathFillType = PathFillType.EvenOdd
				) {
					moveTo(8.27002f, 14.9519f)
					lineTo(9.8627f, 9.8627f)
					lineTo(14.9519f, 8.27002f)
					lineTo(13.3593f, 13.3593f)
					lineTo(8.27002f, 14.9519f)
					close()
}
				path(
    				fill = null,
    				fillAlpha = 1.0f,
    				stroke = SolidColor(Color(color)),
    				strokeAlpha = 1.0f,
    				strokeLineWidth = 1.5f,
    				strokeLineCap = StrokeCap.Round,
    				strokeLineJoin = StrokeJoin.Round,
    				strokeLineMiter = 1.0f,
    				pathFillType = PathFillType.NonZero
				) {
					moveTo(21.221980000000002f, 11.611f)
					arcTo(9.61098f, 9.61098f, 0f, isMoreThanHalf = false, isPositiveArc = true, 11.611f, 21.221980000000002f)
					arcTo(9.61098f, 9.61098f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.000020000000001f, 11.611f)
					arcTo(9.61098f, 9.61098f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21.221980000000002f, 11.611f)
					close()
}
}.build()
    }
}

