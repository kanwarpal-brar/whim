import androidx.compose.runtime.Composable
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
fun Profile(color: Int = MaterialTheme.colorScheme.onBackground.toArgb()): ImageVector {
    return remember {
        ImageVector.Builder(
                name = "Profile",
                defaultWidth = 30.dp,
                defaultHeight = 30.dp,
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
					moveTo(16.356930000000002f, 7.27803f)
					arcTo(4.77803f, 4.77803f, 0f, isMoreThanHalf = false, isPositiveArc = true, 11.5789f, 12.05606f)
					arcTo(4.77803f, 4.77803f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6.800870000000001f, 7.27803f)
					arcTo(4.77803f, 4.77803f, 0f, isMoreThanHalf = false, isPositiveArc = true, 16.356930000000002f, 7.27803f)
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
					moveTo(4.00002f, 18.7014f)
					curveTo(3.9987f, 18.3655f, 4.0739f, 18.0337f, 4.2197f, 17.7311f)
					curveTo(4.6774f, 16.8158f, 5.968f, 16.3307f, 7.0389f, 16.111f)
					curveTo(7.8113f, 15.9462f, 8.5943f, 15.836f, 9.3822f, 15.7815f)
					curveTo(10.8408f, 15.6533f, 12.3079f, 15.6533f, 13.7666f, 15.7815f)
					curveTo(14.5544f, 15.8367f, 15.3374f, 15.9468f, 16.1099f, 16.111f)
					curveTo(17.1808f, 16.3307f, 18.4714f, 16.77f, 18.9291f, 17.7311f)
					curveTo(19.2224f, 18.3479f, 19.2224f, 19.064f, 18.9291f, 19.6808f)
					curveTo(18.4714f, 20.6419f, 17.1808f, 21.0812f, 16.1099f, 21.2918f)
					curveTo(15.3384f, 21.4634f, 14.5551f, 21.5766f, 13.7666f, 21.6304f)
					curveTo(12.5794f, 21.7311f, 11.3866f, 21.7494f, 10.1968f, 21.6854f)
					curveTo(9.9222f, 21.6854f, 9.6568f, 21.6854f, 9.3822f, 21.6304f)
					curveTo(8.5966f, 21.5773f, 7.8163f, 21.4641f, 7.0481f, 21.2918f)
					curveTo(5.968f, 21.0812f, 4.6865f, 20.6419f, 4.2197f, 19.6808f)
					curveTo(4.0746f, 19.3747f, 3.9996f, 19.0401f, 4f, 18.7014f)
					close()
}
}.build()
    }
}

