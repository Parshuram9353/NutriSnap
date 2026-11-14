package com.aurafit.app.core.ml

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

data class FoodPrediction(
    val label: String,
    val confidence: Float
)

@Singleton
class FoodClassifier @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var interpreter: Interpreter? = null
    private val labels = mutableListOf<String>()

    private var inputWidth = 224
    private var inputHeight = 224
    private var inputDataType: DataType = DataType.UINT8
    private var outputSize = 2024
    private var outputDataType: DataType = DataType.UINT8

    init {
        initModel()
    }

    private fun initModel() {
        try {
            // Load labels
            context.assets.open("food_labels.txt").bufferedReader().useLines { lines ->
                lines.forEach { labels.add(it.trim()) }
            }

            // Load TFLite model from assets
            val assetFileDescriptor = context.assets.openFd("food_classifier.tflite")
            val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
            val fileChannel = fileInputStream.channel
            val startOffset = assetFileDescriptor.startOffset
            val declaredLength = assetFileDescriptor.declaredLength
            val modelBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)

            val options = Interpreter.Options().apply {
                setNumThreads(4)
                setUseNNAPI(false)
            }
            val interp = Interpreter(modelBuffer, options)

            // Inspect input shape & type
            val inTensor = interp.getInputTensor(0)
            val inShape = inTensor.shape() // [1, height, width, 3]
            if (inShape.size >= 4) {
                inputHeight = inShape[1]
                inputWidth = inShape[2]
            }
            inputDataType = inTensor.dataType()

            // Inspect output shape & type
            val outTensor = interp.getOutputTensor(0)
            val outShape = outTensor.shape()
            outputSize = if (outShape.size >= 2) outShape[1] else labels.size
            outputDataType = outTensor.dataType()

            interpreter = interp
            Log.d("FoodClassifier", "Loaded TFLite Food Classifier: input=(${inputWidth}x${inputHeight}, $inputDataType), output=($outputSize, $outputDataType), labels=${labels.size}")
        } catch (e: Exception) {
            Log.e("FoodClassifier", "Error initializing TFLite food model: ${e.message}", e)
        }
    }

    fun classify(bitmap: Bitmap, topK: Int = 5): List<FoodPrediction> {
        val interp = interpreter ?: run {
            initModel()
            interpreter ?: return emptyList()
        }

        return try {
            // Convert to ARGB_8888 software bitmap if HARDWARE bitmap or other config
            val safeBitmap = if (bitmap.config == Bitmap.Config.HARDWARE || bitmap.config == null) {
                bitmap.copy(Bitmap.Config.ARGB_8888, false) ?: run {
                    val fallback = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(fallback)
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    fallback
                }
            } else {
                bitmap
            }

            // Build image processor matching model input
            val imageProcessor = ImageProcessor.Builder()
                .add(ResizeOp(inputHeight, inputWidth, ResizeOp.ResizeMethod.BILINEAR))
                .apply {
                    if (inputDataType == DataType.FLOAT32) {
                        add(NormalizeOp(127.5f, 127.5f))
                    }
                }
                .build()

            var tensorImage = TensorImage(inputDataType)
            tensorImage.load(safeBitmap)
            tensorImage = imageProcessor.process(tensorImage)

            val inputBuffer = tensorImage.buffer
            inputBuffer.rewind()

            // Prepare output container
            val probabilities: FloatArray
            if (outputDataType == DataType.UINT8) {
                val outputBuffer = ByteBuffer.allocateDirect(outputSize).apply {
                    order(ByteOrder.nativeOrder())
                }
                interp.run(inputBuffer, outputBuffer)
                outputBuffer.rewind()
                val rawBytes = ByteArray(outputSize)
                outputBuffer.get(rawBytes)
                probabilities = FloatArray(outputSize) { i ->
                    (rawBytes[i].toInt() and 0xFF) / 255.0f
                }
            } else {
                val outputArray = Array(1) { FloatArray(outputSize) }
                interp.run(inputBuffer, outputArray)
                probabilities = outputArray[0]
            }

            // Map probabilities to labels
            val predictions = mutableListOf<FoodPrediction>()
            val maxCount = minOf(probabilities.size, labels.size)
            for (i in 0 until maxCount) {
                val label = labels[i]
                if (label.isBlank() || label.startsWith("__") || label.startsWith("/g/")) continue
                val prob = probabilities[i]
                if (prob > 0.01f) {
                    predictions.add(FoodPrediction(label = label, confidence = prob))
                }
            }

            predictions.sortByDescending { it.confidence }
            predictions.take(topK)
        } catch (e: Exception) {
            Log.e("FoodClassifier", "Classification error: ${e.message}", e)
            emptyList()
        }
    }
}
