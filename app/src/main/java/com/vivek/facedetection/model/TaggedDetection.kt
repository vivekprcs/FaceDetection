package com.vivek.facedetection.model

import com.google.mediapipe.tasks.components.containers.Detection

data class TaggedDetection(
    val detection: Detection,
    var tag: String = ""
)
