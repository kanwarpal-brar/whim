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
fun People(color: Int = MaterialTheme.colorScheme.onBackground.toArgb()): ImageVector {
    return remember {
        ImageVector.Builder(
                name = "People",
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
					moveTo(16.0201f, 10.9134f)
					curveTo(17.8411f, 10.9134f, 19.3171f, 9.4374f, 19.3171f, 7.6164f)
					curveTo(19.3171f, 5.7964f, 17.8411f, 4.3194f, 16.0201f, 4.3194f)
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
					moveTo(17.5363f, 14.4964f)
					curveTo(18.0803f, 14.5334f, 18.6203f, 14.6114f, 19.1533f, 14.7294f)
					curveTo(19.8923f, 14.8764f, 20.7823f, 15.1794f, 21.0983f, 15.8424f)
					curveTo(21.3003f, 16.2674f, 21.3003f, 16.7624f, 21.0983f, 17.1874f)
					curveTo(20.7833f, 17.8504f, 19.8923f, 18.1534f, 19.1533f, 18.3054f)
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
					moveTo(9.59143f, 15.2063f)
					curveTo(13.2814f, 15.2063f, 16.4334f, 15.7653f, 16.4334f, 17.9983f)
					curveTo(16.4334f, 20.2323f, 13.3014f, 20.8103f, 9.5914f, 20.8103f)
					curveTo(5.9014f, 20.8103f, 2.7504f, 20.2523f, 2.7504f, 18.0183f)
					curveTo(2.7504f, 15.7843f, 5.8814f, 15.2063f, 9.5914f, 15.2063f)
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
    				pathFillType = PathFillType.EvenOdd
				) {
					moveTo(9.5914f, 12.0188f)
					curveTo(7.1574f, 12.0188f, 5.2074f, 10.0678f, 5.2074f, 7.6338f)
					curveTo(5.2074f, 5.2008f, 7.1574f, 3.2498f, 9.5914f, 3.2498f)
					curveTo(12.0254f, 3.2498f, 13.9764f, 5.2008f, 13.9764f, 7.6338f)
					curveTo(13.9764f, 10.0678f, 12.0254f, 12.0188f, 9.5914f, 12.0188f)
					close()
}
}.build()
    }
}

