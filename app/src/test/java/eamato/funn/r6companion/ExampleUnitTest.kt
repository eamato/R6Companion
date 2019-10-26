package eamato.funn.r6companion

import io.reactivex.Flowable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.properties.Delegates

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

// test master

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun autoScroll() {
        val listSize = 5
        val countDownLatch = CountDownLatch(1)

        val scrollToRight: (Long) -> Int = {
            (it % listSize).toInt() + 1
        }

        val scrollToLeft: (Long) -> Int = {
            listSize - (it % listSize).toInt() - 1
        }

        val scrollToRightEndless: (Long) -> Int = {
            if (it < listSize)
                it.toInt()
            else
                listSize
        }

        var currentScroller = scrollToRight

        val scroller = Flowable.interval(1L, TimeUnit.SECONDS)
            .map {
                val res = currentScroller.invoke(it)
                if (currentScroller == scrollToRight) {
                    if (res == listSize)
                        currentScroller = scrollToLeft
                } else {
                    if (res == 0)
                        currentScroller = scrollToRight
                }
                res
            }

        val scroller2 = Flowable.interval(1L, TimeUnit.SECONDS)
            .map {
                scrollToRightEndless.invoke(it)
            }

        val scroller3 = Flowable.interval(1L, TimeUnit.SECONDS)
            .map {
                val res = currentScroller.invoke(it + 2)
                if (currentScroller == scrollToRight) {
                    if (res == listSize)
                        currentScroller = scrollToLeft
                } else {
                    if (res == 0)
                        currentScroller = scrollToRight
                }
                res
            }

        scroller3.subscribe({
            println("New position: $it")
        }, {
            println("Error: $it")
        })

        countDownLatch.await()
    }

    @Test
    fun modifyList() {
        val data = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9).also { println(it.joinToString()) }
        val firstVisibleItemPosition = 0
        val lastVisibleItemPosition = 8
        val spanCount = 3
        if (lastVisibleItemPosition >= data.size - spanCount) {
            data
                .drop(spanCount)
                .toMutableList()
                .also { it.addAll(data.take(spanCount)) }
                .toList()
            println(data.joinToString())
        }

        val input = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).also { println(it.joinToString()) }
        input.addAll(input)
        val halfOfList = Math.floor(((input.size - 1) / 2).toDouble()).toInt()
        for (i in 0..halfOfList) {
            input.removeAt(0)
        }
        println(input.joinToString())
    }

    @Test
    fun math() {
        val size = 7
        val newSize = size * 3
        assertEquals(3, Math.floor(((size - 1) / 2).toDouble()).toInt())
        assertEquals(3, newSize / size)
    }

    data class DummyObject(val name: String, var isSelected: Boolean)

    @Test
    fun updateListContent() {
        var data = listOf(
            DummyObject("1", false), DummyObject("2", false), DummyObject("3", false),
            DummyObject("4", false), DummyObject("5", false), DummyObject("6", false)
        )
        println(data.joinToString())
        data = data.toMutableList().also {
            it[3] = DummyObject("new", true)
        }.toList()
        println(data.joinToString())
    }

    @Test
    fun delegatesTest() {
        var observableProperty: List<DummyObject> by Delegates.observable(emptyList()) {
            property, oldValue, newValue ->
            println("Property: $property\noldValue: ${oldValue.joinToString()}\nnewValue: ${newValue.joinToString()}")
        }
        observableProperty = listOf(
            DummyObject("1", false), DummyObject("2", false), DummyObject("3", false),
            DummyObject("4", false), DummyObject("5", false), DummyObject("6", false),
            DummyObject("7", false), DummyObject("8", false), DummyObject("9", false)
        )

        observableProperty[2].isSelected = true.also { observableProperty = observableProperty }

    }

    @Test
    fun copyListTest() {
        val data = listOf(
            DummyObject("1", false), DummyObject("2", false), DummyObject("3", false), DummyObject("4", false)
        )
        println("Data: ${data.joinToString()}")
        val dataCopy = ArrayList(data.map { it.copy() })
        println("Data copy: ${dataCopy.joinToString()}")
        data[2].isSelected = true
        println("Data: ${data.joinToString()}")
        println("Data copy: ${dataCopy.joinToString()}")
    }

    @Test
    fun sequence() {
        val countDownLatch = CountDownLatch(1)
        var currentPosition = 0
        Flowable
            .interval(1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .map {
                val newPosition = (it.toInt() + currentPosition) % 6
            }
            .subscribe({
                println("Position: $currentPosition")
            }, {}, {})
        countDownLatch.await()
    }

    private class TestClass(var number: Int) {

        val numberPlusOne = number + 1

        init {
            number += 10
            println("init: $number")
        }

    }

    @Test
    fun classInitialization() {
        val tc = TestClass(12)
        println("Number: ${tc.number}")
        println("Number plus one: ${tc.numberPlusOne}")
    }

    @Test
    fun player2() {
        val currentPosition = AtomicInteger(0)

        val play: (iteration: Long) -> Long = {
            currentPosition.set(it.toInt())
            it
        }

        val pause: (iteration: Long) -> Long = {
            currentPosition.get().toLong()
        }

        val countDownLatch = CountDownLatch(1)

        val flow = Flowable.interval(1, TimeUnit.SECONDS)
            .map(play)

        flow.subscribe({
            println("Next: $it")
        }, {
            println("Error: $it")
        }, {
            println("Complete!")
        })

        thread {
            TimeUnit.SECONDS.sleep(5)
            flow.map(pause)
            TimeUnit.SECONDS.sleep(5)
            flow.map(play)
        }

        countDownLatch.await()
    }

    @Test
    fun changeTest() {
        val countDownLatch = CountDownLatch(1)

        val atomicCondition = AtomicBoolean(true)

        var flow1 = Flowable.interval(
            1L, TimeUnit.SECONDS
        ).map { "Flow 1 = $it" }

        var flow2 = Flowable.interval(
            1L, TimeUnit.SECONDS
        ).map { "Flow 2 = $it" }
            .skipWhile {
                !atomicCondition.get()
            }

        var flow3 = Flowable.interval(
            1L, TimeUnit.SECONDS
        ).map { "Flow 3 = $it" }

        val zip = Flowable.zip(
            flow1, flow2, flow3, Function3<String, String, String, String> { t1, t2, t3 ->
                "PLAY: $t1, $t2, $t3"
            }
        )
        zip.subscribe({
            println("Next: $it")
        }, {
            println("Error: $it")
        }, {
            println("Complete!")
        })

        thread {
            TimeUnit.SECONDS.sleep(5)
            atomicCondition.set(false)
            TimeUnit.SECONDS.sleep(5)
            atomicCondition.set(true)
        }

        countDownLatch.await()
    }

    private enum class testPlaybackStatus {
        PAUSED, PLAYING, STOPPED
    }

    private enum class testPlayMode(val addition: Int) {
        STRAIGHT(1), REVERSED(-1)
    }

    private data class testPlayRoad(
        val frames: List<String>,
        var isLooped: Boolean,
        var playbackStatus: testPlaybackStatus
    )

    private data class testPlaylist(
        val bottomPlayRoad: testPlayRoad,
        val middlePlayRoad: testPlayRoad,
        val topPlayRoad: testPlayRoad,
        val duration: Long
    )

    private data class testResultRoad(val bottom: String, val middle: String, val top: String)

    private fun createPlaylist(): testPlaylist {
        return testPlaylist(
            testPlayRoad(
                listOf(
                    "Bottom 1", "Bottom 2", "Bottom 3", "Bottom 4", "Bottom 5", "Bottom 6", "Bottom 7",
                    "Bottom 8", "Bottom 9", "Bottom 10", "Bottom 11", "Bottom 12", "Bottom 13", "Bottom 14"
                ),
                true,
                testPlaybackStatus.PLAYING
            ),
            testPlayRoad(
                listOf(
                    "Middle 1", "Middle 2", "Middle 3", "Middle 4", "Middle 5", "Middle 6", "Middle 7",
                    "Middle 8", "Middle 9", "Middle 10", "Middle 11", "Middle 12", "Middle 13", "Middle 14",
                    "Middle 15", "Middle 16", "Middle 17", "Middle 18", "Middle 19", "Middle 20", "Middle 21",
                    "Middle 22", "Middle 23", "Middle 24", "Middle 25", "Middle 26", "Middle 27", "Middle 28"
                ),
                false,
                testPlaybackStatus.PAUSED
            ),
            testPlayRoad(
                listOf(
                    "Top 1", "Top 2", "Top 3", "Top 4", "Top 5", "Top 6", "Top 7",
                    "Top 8", "Top 9", "Top 10", "Top 11", "Top 12", "Top 13", "Top 14",
                    "Top 15", "Top 16", "Top 17", "Top 18", "Top 19", "Top 20", "Top 21"
                ),
                true,
                testPlaybackStatus.PLAYING
            ),
            1000
        )
    }

    private inner class testPlayRoadPlayer(val playRoad: testPlayRoad) {

        private val lastGivenFrameIndex = AtomicInteger(0)
        val playMode = AtomicReference<testPlayMode>(testPlayMode.STRAIGHT)

        fun getFrame(): String {
            return when (playRoad.playbackStatus) {
                testPlaybackStatus.PLAYING -> {
                    playRoad.frames[lastGivenFrameIndex.get()].also {
                        val last = lastGivenFrameIndex.get()

                        if (playMode.get() == testPlayMode.STRAIGHT) {
                            if (last == playRoad.frames.lastIndex) {
                                if (playRoad.isLooped)
                                    lastGivenFrameIndex.set(0)
                                else
                                    playRoad.playbackStatus = testPlaybackStatus.PAUSED
                            } else {
                                lastGivenFrameIndex.set(last + playMode.get().addition)
                            }
                        } else {
                            if (last == 0) {
                                if (playRoad.isLooped)
                                    lastGivenFrameIndex.set(playRoad.frames.lastIndex)
                                else
                                    playRoad.playbackStatus = testPlaybackStatus.PAUSED
                            } else {
                                lastGivenFrameIndex.set(last + playMode.get().addition)
                            }
                        }

                    }
                }
                testPlaybackStatus.STOPPED -> {
                    lastGivenFrameIndex.set(0)
                    playRoad.frames[lastGivenFrameIndex.get()]
                }
                testPlaybackStatus.PAUSED -> {
                    playRoad.frames[lastGivenFrameIndex.get()]
                }
            }
        }

    }

    private inner class testPlayer(playlist: testPlaylist) {
        val bottomPlayRoadPlayer = testPlayRoadPlayer(playlist.bottomPlayRoad)
        val middlePlayRoadPlayer = testPlayRoadPlayer(playlist.middlePlayRoad)
        val topPlayRoadPlayer = testPlayRoadPlayer(playlist.topPlayRoad)

        val playback = Flowable.interval(playlist.duration, TimeUnit.MILLISECONDS)
            .map { testResultRoad(bottomPlayRoadPlayer.getFrame(), middlePlayRoadPlayer.getFrame(), topPlayRoadPlayer.getFrame()) }

    }

    @Test
    fun testPlayer() {
        val countDownLatch = CountDownLatch(1)

        val playlist = createPlaylist()
        val player = testPlayer(playlist)
        player.playback.subscribe({
            println("Next: ${it.bottom} -> ${it.middle} -> ${it.top}")
        }, {
            println("Error: $it")
        }, {
            println("Complete!")
        })

        thread {
            TimeUnit.SECONDS.sleep(5)
            playlist.middlePlayRoad.playbackStatus = testPlaybackStatus.PLAYING
            TimeUnit.SECONDS.sleep(10)
            player.middlePlayRoadPlayer.playMode.set(testPlayMode.REVERSED)
            TimeUnit.SECONDS.sleep(5)
            player.middlePlayRoadPlayer.playMode.set(testPlayMode.STRAIGHT)
            TimeUnit.SECONDS.sleep(5)
            playlist.middlePlayRoad.playbackStatus = testPlaybackStatus.PAUSED
            TimeUnit.SECONDS.sleep(5)
            playlist.middlePlayRoad.playbackStatus = testPlaybackStatus.PLAYING
            TimeUnit.SECONDS.sleep(10)
            playlist.middlePlayRoad.playbackStatus = testPlaybackStatus.STOPPED
        }

        countDownLatch.await()
    }

    @Test
    fun centerShrinkTest() {
        val (viewWidth, viewHeight) = run { 10 to 20 }
        val (imageWidth,imageHeight) = run { 5 to 5 }

        val scale: Float
        var (dx, dy) = run { 0f to 0f }

        // 5 * 20 > 10 * 5 = 100 > 50
        if (imageWidth * viewHeight > viewWidth * imageHeight) {
            // 20.0 / 5.0 = 4.0
            scale = viewHeight.toFloat() / imageHeight.toFloat()
            dx = (viewWidth - imageWidth * scale) * 0.5f
        } else {
            // 10.0 / 5.0 = 2.0
            scale = viewWidth.toFloat() / imageWidth.toFloat()
            dy = (viewHeight - imageHeight * scale) * 0.5f
        }


    }

    val akaMainThread = Schedulers.from(Executors.newSingleThreadExecutor { Thread(it, "MainThread") })

    // should work in worker thread
    private fun createPlayer(): Flowable<String> {
        return Flowable.fromCallable {
            println("create playlist on: ${Thread.currentThread().name}")
            TimeUnit.SECONDS.sleep(5)
            "Player"
        }
    }

    @Test
    fun threadControl() {
        val countDownLatch = CountDownLatch(1)

        createPlayer()
            .subscribeOn(Schedulers.single())
            .observeOn(akaMainThread)
            .flatMap {  player ->
                println("flat map on: ${Thread.currentThread().name}")
                Flowable.interval(1, TimeUnit.SECONDS, Schedulers.single())
                    .onBackpressureDrop()
                    .map {
                        println("map on: ${Thread.currentThread().name}")
                        player
                    }
            }
            .subscribe({
                println("On next on: ${Thread.currentThread().name}: $it")
            }, {
                println("Error on: ${Thread.currentThread().name}: $it")
            })

        countDownLatch.await()
    }

}
