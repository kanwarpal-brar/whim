import android.content.Context
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.example.myapplication.R

@Composable
fun GlideImage(
    imageUri: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            ShapeableImageView(ctx).apply {
                val shapeAppearance = ShapeAppearanceModel.builder()
                    .setAllCorners(CornerFamily.ROUNDED, 16f)
                    .build()

                val shapeDrawable = MaterialShapeDrawable(shapeAppearance)
                this.shapeAppearanceModel = shapeAppearance
                this.background = shapeDrawable

                val imageToLoad = if (imageUri.isNullOrEmpty()) R.drawable.event_detail else imageUri

                Glide.with(ctx)
                    .load(imageToLoad)
                    .into(this)
            }
        },
        modifier = modifier,
        update = { view ->
            val imageToLoad = if (imageUri.isNullOrEmpty() || imageUri == "null") R.drawable.event_detail else imageUri

            Glide.with(context)
                .load(imageToLoad) // Use a default drawable resource if URI is null or empty
                .into(view as ShapeableImageView)
        }
    )
}
