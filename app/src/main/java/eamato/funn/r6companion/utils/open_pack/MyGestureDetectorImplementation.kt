package eamato.funn.r6companion.utils.open_pack

class MyGestureDetectorImplementation(
    private val player: Player,
    var calculations: Player.Calculations?
) : MyGestureDetector {

    var isScrollDetected = false

    override fun myOnSwipe(startingX: Float) {
        isScrollDetected = false
        player.resume()
    }

    override fun myOnScrollForXAxis(distanceX: Float, startingX: Float) {
        isScrollDetected = true
        calculations?.let { nonNullCalculations ->
            val frameCount = distanceX / nonNullCalculations.framePerPixel
            val currentFrame = frameCount.toInt()
            if (distanceX >= (nonNullCalculations.frameByFrameThreshold - startingX)) {
                player.atomicPlayFromPosition.set(currentFrame)
                player.resume()
            } else {
                player.atomicPlayFromPosition.set(currentFrame)
                player.playFrameAtPosition(currentFrame)
            }
        }
    }

}