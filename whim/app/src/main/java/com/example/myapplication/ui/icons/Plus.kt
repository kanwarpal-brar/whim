import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
fun Plus(color: Int = MaterialTheme.colorScheme.onBackground.toArgb()): ImageVector {
    return remember {
        ImageVector.Builder(
                name = "Plus",
                defaultWidth = 32.dp,
                defaultHeight = 32.dp,
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
    				pathFillType = PathFillType.NonZero
				) {
					moveTo(12f, 8.3273f)
					verticalLineTo(15.6537f)
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
					moveTo(15.6667f, 11.9905f)
					horizontalLineTo(8.33333f)
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
    				pathFillType = PathFillType.EvenOdd
				) {
					moveTo(16.6857f, 2f)
					horizontalLineTo(7.31429f)
					curveTo(4.0476f, 2f, 2f, 4.3121f, 2f, 7.5852f)
					verticalLineTo(16.4148f)
					curveTo(2f, 19.6879f, 4.0381f, 22f, 7.3143f, 22f)
					horizontalLineTo(16.6857f)
					curveTo(19.9619f, 22f, 22f, 19.6879f, 22f, 16.4148f)
					verticalLineTo(7.58516f)
					curveTo(22f, 4.3121f, 19.9619f, 2f, 16.6857f, 2f)
					close()
}
}.build()
    }
}

