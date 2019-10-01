package eamato.funn.r6companion.utils.open_pack

class MyGestureDetectorImplementation2(
    private val player2: Player2,
    private val detectingViewSize: MySize
) : MyGestureDetector {

    var isScrollDetected = false

    override fun myOnSwipe(startingX: Float) {
        isScrollDetected = false
        player2.middlePlayRoadPlayer.playRoad.playbackStatus.value = PlaybackStatus.PLAYING
    }

    override fun myOnScrollForXAxis(distanceX: Float, startingX: Float) {
        isScrollDetected = true
        getCalculations(startingX)?.let { nonNullCalculations ->
            val frameCount = distanceX / nonNullCalculations.pixelsPerFrame
            val currentFrame = Math.min(frameCount.toInt(), nonNullCalculations.frameCount - 1)
            if (distanceX >= (nonNullCalculations.autoPlayThreshold - startingX)) {
                player2.middlePlayRoadPlayer.lastGivenFrameIndex.set(currentFrame)
                player2.middlePlayRoadPlayer.playRoad.playbackStatus.value = PlaybackStatus.PLAYING
            } else {
                player2.middlePlayRoadPlayer.lastGivenFrameIndex.set(currentFrame)
                player2.middlePlayRoadPlayer.playRoad.playbackStatus.value = PlaybackStatus.PLAYINGFBF
            }
        }
    }

    private fun getCalculations(startingPosition: Float): Calculations? {
        val frameCount = player2.middlePlayRoadPlayer.playRoad.slides.size
        val remainingPixels = detectingViewSize.width - startingPosition.toInt()
        if (frameCount < remainingPixels)
            return Calculations(frameCount, remainingPixels - (remainingPixels / 5), remainingPixels / frameCount)
        return null
    }

}