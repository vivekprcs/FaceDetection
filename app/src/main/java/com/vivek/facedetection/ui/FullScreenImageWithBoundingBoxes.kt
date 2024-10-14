import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import com.vivek.facedetection.model.TaggedDetection
import com.vivek.facedetection.utils.DimensionUtils

@Composable
fun FullScreenImageWithBoundingBoxes(
    bitmap: Bitmap,
    faceDetections: List<TaggedDetection>,
    onTagUpdate: (Int, String) -> Unit
) {
    Box {
        // Draw the image with bounding boxes
        DrawBoundingBoxes(bitmap, faceDetections)
        // Draw the TextField overlay
        DrawTagBox(bitmap, faceDetections, onTagUpdate)
    }
}

@Composable
fun DrawTagBox(
    bitmap: Bitmap,
    faceDetections: List<TaggedDetection>,
    onTagUpdate: (Int, String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val imageWidthPx = bitmap.width.toFloat()
    val imageHeightPx = bitmap.height.toFloat()

    val scaleX = screenWidthPx / imageWidthPx
    val scaleY = screenHeightPx / imageHeightPx

    faceDetections.forEachIndexed { index, detection ->
        val bbox = detection.detection.boundingBox()

        val scaledLeft = bbox.left * scaleX
        val scaledTop = bbox.top * scaleY

        val textFieldY = (scaledTop - DimensionUtils.TEXT_BOX_OFFSET).coerceAtLeast(0f)
        val textFieldX = scaledLeft.coerceIn(0f, screenWidthPx - with(density) { DimensionUtils.textFieldWidth.toPx() })

        var tagValue by remember { mutableStateOf(detection.tag ?: "Enter name") }

        // Draw TextField
        TextField(
            value = tagValue,
            onValueChange = { newTag ->
                tagValue = newTag
                onTagUpdate(index, newTag)
            },
            modifier = Modifier
                .width(DimensionUtils.textFieldWidth)
                .offset(
                    x = with(density) { textFieldX.toDp() },
                    y = with(density) { textFieldY.toDp() }
                )
                .background(Color.Transparent)
                .padding(DimensionUtils.smallPadding),
            textStyle = LocalTextStyle.current.copy(
                color = Color.White,
                fontSize = DimensionUtils.smallTextSize,
                fontWeight = FontWeight.Bold
            ),
            placeholder = { Text("Tag", color = Color.White, fontSize = DimensionUtils.smallTextSize, fontWeight = FontWeight.Bold) },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}


@Composable
fun DrawBoundingBoxes(bitmap: Bitmap, faceDetections: List<TaggedDetection>) {
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)
    val paint = Paint().apply {
        color = 0xFFFF0000.toInt()
        strokeWidth = DimensionUtils.STROKE_WIDTH
        style = Paint.Style.STROKE
    }

    for (detection in faceDetections) {
        val bbox = detection.detection.boundingBox()
        val rect = RectF(bbox.left, bbox.top, bbox.right, bbox.bottom)
        canvas.drawRect(rect, paint)
    }

    Image(bitmap = mutableBitmap.asImageBitmap(), contentDescription = null)
}