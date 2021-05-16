package eamato.funn.r6companion

import io.reactivex.Flowable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import okhttp3.internal.toImmutableList
import org.junit.Test

import org.junit.Assert.*
import java.lang.StringBuilder
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.collections.ArrayList
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


    private fun potentialErrorThrower(): String {
        throw Exception("Ebati kopati")
    }

    @Test
    fun flowableFromCallableTest() {
        Flowable.fromCallable { potentialErrorThrower() }
            .subscribe({
                println("")
                potentialErrorThrower()
            }, {
                it.message
            })
    }

    @Test
    fun insertOnEachStep() {
        val list = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9")
        println(list.joinToString())
        val step = 5
        val count = list.size / step
        println(count)
        var iteration = step
        println(list.toMutableList().also {
            for (i in 0 until count) {
                it.add(iteration, "*")
                iteration += step.inc()
            }
        }.toImmutableList().joinToString())
    }

    @Test
    fun getDefaultLocale() {
        Locale.getDefault().toLanguageTag()
    }

    @Test
    fun dateTestISO() {
        val content = "### По приказу команды Rainbow...\n\n![[R6S] Vintage Fashion Fanart 1]" +
                "(//staticctf.akamaized.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/4Jeh1aKKbjqK00ShiglwMU" +
                "/c3a64157864e6624b34912856b622491/1-min.png)\n\nПора начать рисовать! Любите моду " +
                "и жаждете творить? Тогда хватайте кисти и планшеты — пришло время переосмыслить вн" +
                "ешний вид любимого персонажа! \n\nСоздайте старомодные костюмы для любимых операти" +
                "вников и поделитесь ими с помощью хэштега __#R6Vintage__ — получите шанс выиграть " +
                "полную коллекцию «The Grand Larceny»!\n\n__Конкурс начнется 5 мая в 19:00 МСК, а з" +
                "акончится 12 мая в 19:00 МСК.__\n\n# Как принять участие?\n\n- Выберите оперативник" +
                "а и придумайте ему старомодный образ с помощью одежды или предметов.\n- Проявите св" +
                "ою фантазию! (Работа должна быть выполнена только вами (без заимствований) и только " +
                "для этого конкурса.)\n- Создайте файл в формате .png, изобразите оперативника в пол" +
                "ный рост. Не добавляйте фон!\n- Выложите работу в Твиттер (@rainbow6game) или Insta" +
                "gram (@Rainbow6game_us), используя хэштег #R6Vintage, и отметьте @rainbow6game (в Т" +
                "виттере) или @rainbow6game_us (в Instagram).\n\n# Призы\n\n![[R6S] Vintage Fashion " +
                "Fanart 2](//staticctf.akamaized.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/1shejjns55ZSv" +
                "1r5y5QkKO/bb896d9bcf62553dca58adab0aed3d03/2-min.png)\n\n### 10 победителей — полна" +
                "я коллекция «The Grand Larceny»\n\nПо итогам конкурса мы выберем самые стильные и " +
                "эффектные костюмы, создатели которых получат полную коллекцию «The Grand Larceny»! " +
                "Критерии следующие:\n\n- уровень дизайна и стиля,\n- оригинальность,\n- соблюдение " +
                "темы конкурса и события.\n\n# Победители:\n\nМы свяжемся с победителями в социальных " +
                "сетях, чтобы рассказать, как они смогут получить награду.\n\nИх работы будут опубли" +
                "кованы, когда событие подойдет к концу.\n\nЕсли хотите узнать больше, ознакомьтесь " +
                "с официальными правилами конкурса.\n\n[![[R6S] Vintage Fashion Fanart 3](//staticct" +
                "f.akamaized.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/1oG7uTpiTflPeBiTJ0lPhN/9ffa39a4561" +
                "2b7a3b7b33af23cf9be71/3-min.png)](http://static2.cdn.ubi.com/pxm/RainbowSix/Rules/r" +
                "6_vintagefashioncontest_US%20and%20Worldwide%20Promotion%20Official%20Rules.pdf)" +
                "\n\n\n---\n\n\nAs part of the competition, Ubisoft collects and processes your pers" +
                "onal data to allow your participation in the competition and to share the content " +
                "you have published. The processing of your personal data is necessary to perform t" +
                "he contract you have entered with Ubisoft by accepting the rules of the competition" +
                ". Your personal data will be accessible by Ubisoft, its affiliates and sub-processo" +
                "rs. The content you have published as part of the competition will remain publicly " +
                "available until you choose to delete it.\n\nUbisoft may transfer your personal data " +
                "to non-European countries that ensure an adequate level of protection according to " +
                "the EU Commission or within the framework of the standard data protection clauses " +
                "adopted by the EU Commission [here](https://ec.europa.eu/info/law/law-topic/data-pr" +
                "otection/data-transfers-outside-eu/model-contracts-transfer-personal-data-third-cou" +
                "ntries_fr).\n\nYou can request a copy of your data, its deletion or rectification, " +
                "object to the processing of your data, request the restriction of its processing, a" +
                "nd/or receive your information in portable form by contacting Ubisoft's data protec" +
                "tion officers [here](https://support.ubi.com/faqs/35367/Reviewing-the-data-that-Ubi" +
                "soft-holds-about-me/). After contacting us, if you are not satisfied with the way " +
                "we handled your request, you may address a complaint to the supervisory authority " +
                "of your country.\n"
        val headerPrefix = "# "

        "(!\\[+R6S].*?][(])/+(?:/[^/]+)+\\.(?:jpg|gif|png|jpeg)(\\))".toRegex()
            .findAll(content)
            .map {
                it.groupValues[1]
            }
            .forEach {
                println(it)
            }

//        Regex("$headerPrefix(.*?)[\n]").findAll(content).map { it.groupValues[1] }.forEach {
//            println(it)
//        }
    }

    @Test
    fun string_functions_ayout_creator() {
        /*val content = "В новом выпуске «Дизайнерских заметок» мы поговорим о грядущих изменениях в " +
                "балансе, которые вступят в силу с обновлением 1.3 1-го сезона 6-го года, и расскажем " +
                "о причинах их внедрения.\\n\\n# БАЛАНС И ЗАПРЕТ ОПЕРАТИВНИКОВ\\n## ПОКАЗАТЕЛЬ РАЗНИЦЫ " +
                "ПОБЕД И ЗАИНТЕРЕСОВАННОСТЬ\\n\\n![y6s1winattacker](//staticctf.akamaized.net/J3yJr34" +
                "U2pZ2Ieem48Dwy9uqj5PNUQTn/5Zl7ztIIrseXA5RDhQsoL6/fd2a62216a68060d72fbb5a184770947/Y" +
                "6S1_Matrix_Att.png)\\n\\n![y6s1windefender](//staticctf.akamaized.net/J3yJr34U2pZ2I" +
                "eem48Dwy9uqj5PNUQTn/968uD657LDJJFIlSQNsjZ/4c13ad94180e0030391fa4267816e472/Y6S1_Mat" +
                "rix_Def.png)\\n\\n*Показатель заинтересованности помогает лучше оценить популярнос" +
                "ть оперативника. Его ввели, чтобы продемонстрировать работу системы «Выбор/запрет»." +
                "*\\n\\n*Заинтересованность (Presence) — частота выбора оперативника при условии, " +
                "что он не заблокирован. Итоговая разница побед — совокупность этих показателей на " +
                "каждой карте.*\\n\\n## ЧАСТОТА ЗАПРЕТОВ\\n\\n![y6s1banattacker](//staticctf.akamai" +
                "zed.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/3rtCtwsldzhaJsm5pxcTa4/be26d0e53631bdcf27" +
                "b9472d4c4b15e4/BanMatrixAtt_Y6S1v2.png)\\n\\n![Year 6 Season 1 Ban Defender](//sta" +
                "ticctf.akamaized.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/4l7OowPzNJN3fkydUiBaDj/9f809a" +
                "007ec65492ad44796f75088228/BanMatrixdefv2.png) \\n\\n# БАЛАНС ОПЕРАТИВНИКОВ\\n\\n## " +
                "ACE\\n\\n- Дымовые шашки будут заменены на мину\\n- С помощью устройства S.E.L.M.A. " +
                "можно будет уничтожить устройство «Черное зеркало»\\nИзменение ориентировано на: об" +
                "ычных игроков, обладателей высших рангов рейтинговых игр и киберспортсменов.\\n\\nС" +
                "удя по схеме баланса, частота выбора и коэффициент побед оперативника Ace возросли. " +
                "Этот подрывник отличается быстротой и универсальностью, но он не должен идеально по" +
                "дходить для любых ситуаций. Заменив дымовые шашки на мину, мы снизили его потенциал " +
                "на поздних этапах игры, чтобы стимулировать выбор других подрывников, к примеру, " +
                "Thermite.\\n\\nТакже изменилась механика взаимодействия устройства оперативника " +
                "Ace с «Черным зеркалом» оперативника Mira. Игроки уже использовали устройство S.E.L.M.A. " +
                "для противодействия «Черному зеркалу», создавая под ним прореху, чтобы лишить " +
                "защитников преимущества. Теперь с помощью S.E.L.M.A. можно полностью уничтожить " +
                "«Черное зеркало», благодаря чему появится больше способов противостоять оперативнику " +
                "Mira.\\n\\n## JACKAL\\n\\n- Объем магазина C7E снизится до 25+1 ед. (ранее — 30+1)\\n- " +
                "Урон от выстрелов из C7E составит 42 ед. (ранее — 46 ед.)\\n\\nИзменение ориентировано " +
                "на: обычных игроков, обладателей высших рангов рейтинговых игр и киберспортсменов.\\n\\n" +
                "Оперативника Jackal часто выбирают и запрещают ввиду его силы и универсальности. Мы " +
                "проанализировали статистику C7E и посчитали это оружие слишком мощным. После введения " +
                "связанных с ним изменений оперативник Jackal должен будет соответствовать другим по " +
                "силе, лишившись лишь небольшой части прежнего потенциала. Кроме того, теперь у игроков " +
                "появится возможность чаще использовать PDW9 и решать, какой из этих двух видов оружия " +
                "лучше подходит для конкретной карты и стратегии. \\n\\n## JÄGER\\n\\n- Объем магазина " +
                "416-C снизится до 25+1 ед. (ранее — 30+1)\\n- Увеличится вертикальная отдача при " +
                "выстреле из 416-C\\n\\nИзменение ориентировано на: обладателей высших рангов " +
                "рейтинговых игр и киберспортсменов.\\n\\nОперативник Jäger пришелся по душе " +
                "сообществу, и нетрудно понять почему! Но хотя мы любим его не меньше, особенно " +
                "с учетом того, насколько он полезен команде, его показатель заинтересованности в " +
                "95% слишком высок. Мы долго думали над тем, какие изменения лучше внести — этот " +
                "оперативник крайне популярен, и нам хотелось бы сохранить его особенности, при этом " +
                "поощряя разнообразие выбора.\\n\\nПрошлые изменения коснулись его устройства, и " +
                "хотя мы явно пошли в правильном направлении, игроки по-прежнему выбирают оперативника " +
                "Jäger намного чаще прочих. Теперь мы планируем изменить убойную силу штурмовой " +
                "винтовки 416-C, которая по статистике несколько лучше среднего. Из-за уменьшения " +
                "боезапаса и увеличения отдачи это оружие станет сложнее использовать с прежней " +
                "эффективностью. Благодаря этим изменениям должна снизиться частота выбора как " +
                "данного оружия, так и самого оперативника, но в руках умелого игрока 416-C останется " +
                "достаточно сильной винтовкой.\\n\\nВпрочем, мы понимаем, что игроки ценят не столько " +
                "снаряжение оперативника Jäger, сколько возможность противодействия устройствам, " +
                "которую он предоставляет. Мы рассматриваем возможные изменения системы прицеливания, " +
                "но они, разумеется, коснутся не только этого оперативника. Корректировка баланса " +
                "оперативников и оружия не ограничивается одним изменением, так что мы будем следить " +
                "за тем, как новые показатели оружия повлияют на эффективность всеми любимого стрелка, " +
                "и продолжим вносить исправления по мере необходимости. \\n\\n## TACHANKIN\\n\\n- " +
                "ПУ «Шумиха»\\n  - Объем магазина увеличится до 7 ед. (ранее — 5 ед.)\\n  - Длительность " +
                "эффекта составит 7 сек (ранее — 5 сек)\\n  - Снаряды сработают через 0,75 сек (ранее — 1 сек)\\n  - " +
                "Радиус зоны действия эффекта составит 1,9 м (ранее — 1,7 м)\\n  - Расстояние, после " +
                "которого снаряд начнет снижаться, составит 20 м (ранее — 8 м)\\n  - Скорость полета " +
                "снаряда составит 30 ед. (ранее — 20)\\n\\n- DP27\\n  - Время выбора оружия составит " +
                "0,65 сек (ранее — 0,9 сек)\\n  - Время смены оружия составит 0,3 сек (ранее — 0,42 сек)\\n  - " +
                "Изменение ориентировано на: обычных игроков, обладателей высших рангов рейтинговых " +
                "игр и киберспортсменов.\\n\\nУвидев первые отзывы, поступившие после переработки " +
                "оперативника Tachankin, мы опасались, что это нарушит игровой баланс. Но, судя по " +
                "статистике, его выбирают довольно редко, а при выборе не слишком часто используют " +
                "гранаты, в которых по задумке должна была заключаться его основная убойная сила. " +
                "Мы надеемся, что оперативника Tachankin будут использовать на всех уровнях игры, " +
                "и он сможет заменять или дополнять оперативника Smoke в новых стратегиях, основанных " +
                "на зачистке области и сдерживании оппонентов. Мы увеличили мощь, скорость полета и " +
                "боезапас его гранат, а также длительность и радиус зоны действия их эффекта. " +
                "Теперь эти гранаты должны стать смертоноснее и полезнее в бою, но при этом не казаться " +
                "чрезмерно эффективными.\\n\\n## THERMITE\\n\\n- Мина будет заменена дымовыми шашками\\n- " +
                "Изменение ориентировано на: обычных игроков, обладателей высших рангов рейтинговых " +
                "игр и киберспортсменов.\\n\\nМы хотим повысить эффективность оперативника Thermite " +
                "в разных условиях. Благодаря дымовым шашкам у него появится возможность сильнее " +
                "влиять на ход игры на ее поздних этапах и помогать команде даже после создания " +
                "брешей в стенах.\\n\\n# БАЛАНС ОРУЖИЯ\\n\\n## МАРКСМАНСКИЕ ВИНТОВКИ (ШТУРМОТРЯД)\\n\\n- " +
                "Прицелы с кратностью 1 станут недоступны\\n- Появится доступ к прицелам с кратностями " +
                "1,5, 2 и 2,5\\n- Прицел с кратностью 3 будет выбран по умолчанию\\n\\nИзменение " +
                "ориентировано на: обычных игроков, обладателей высших рангов рейтинговых игр и " +
                "киберспортсменов.\\n\\nМарксманские винтовки должны быть эффективны на дальних дистанциях. " +
                "Но на данный момент для этого можно было использовать только прицелы с кратностью 3, " +
                "которые подходили не для всех карт и ситуаций. Мы добавили доступ к нескольким " +
                "типам прицелов для марксманских винтовок, чтобы дать игрокам свободу выбора, " +
                "не меняя при этом назначение оружия.\\n\\n---\\n\\nИзучите последние изменения в " +
                "Rainbow Six Осада на тестовом сервере и получите эксклюзивный значок благодаря " +
                "программе [«Охота за ошибками»](http://rainbow6.com/bughunterprogram).\\n\\nДелитесь " +
                "отзывами и читайте нас в [Твиттере](https://twitter.com/Rainbow6Game), на " +
                "[Reddit](https://www.reddit.com/r/Rainbow6/), [Facebook](https://www.facebook.com/Rainbow6/) " +
                "и наших [форумах](https://forums.ubi.com/forumdisplay.php/64-Rainbow-Six).\",\n" +
                "\"trackingPageValue\": \" Y6S1.3 DESIGNER'S NOTES"*/

        val content = "Jackal's Elite Set is now available. Includes the Rastreador uniform, headgear, victory dance, weapon skin for the C7E, PDW9, ITA 12L, USP40 and ITA12S, as well as the Elite Jackal Chibi charm.\\n\\n#R6S\\n\\nPlease SUBSCRIBE: http://bit.ly/UbisoftYouTubeChannel\\n \\nVisit our official channels to stay up to date with Rainbow Six Siege:\\nhttps://rainbow6.ubisoft.com/\\nhttps://www.facebook.com/rainbow6usa/\\nhttps://www.instagram.com/rainbow6gam...\\nhttps://twitter.com/Rainbow6Game\\nhttps://www.youtube.com/UbisoftNA\\n \\nDiscover all our Rainbow Six products and exclusive items on the Ubisoft Store: https://ubi.li/DZNaP\\n \\nABOUT RAINBOW SIX SIEGE:\\nRainbow Six Siege is an exciting, new approach to the first-person shooter experience that puts tactical combat and masterful destruction at the center of the action. Lead your team of unique, counter-terrorist Rainbow operators through tense and thrilling combat scenarios, and achieve victory through smart preparation and strategic improvisation. Rainbow Six Siege is available now on Xbox One, PlayStation® 4 and PC.\\n  \\n KEY FEATURES:\\n  \\n -THE RULES OF SIEGE: Five versus Five. Attack versus Defend. Infiltrate versus Fortify. Team-based strategy meets intense, tactical combat.\\n -WORLD'S ELITE COUNTER-TERRORIST OPERATORS: Choose your Operator and wield their unique ability to breach or defend the objective as a part of an elite team.\\n -DESTRUCTION AS A TOOL: Walls can be shattered; floors and ceilings can be breached. Mastering the tactical use of destruction is the key to victory.\\n -CLOSE-QUARTERS COMBAT: With tight spaces shaping all combat arenas, tense encounters and up-close-and-personal firefights abound within every Siege.\\n\\nABOUT UBISOFT:\\nUbisoft is a leading creator, publisher and distributor of interactive entertainment and services, with a rich portfolio of world-renowned brands, including Assassin’s Creed, Far Cry, For Honor, Just Dance, Watch Dogs, Tom Clancy’s video game series including Ghost Recon, Rainbow Six and The Division. The teams throughout Ubisoft’s worldwide network of studios and business offices are committed to delivering original and memorable gaming experiences across all popular platforms, including consoles, mobile phones, tablets and PCs. For the 2019-20 fiscal year, Ubisoft generated net bookings of €1,534 million. To learn more, please visit: www.ubisoftgroup.com.  \\n\\n© 2021 Ubisoft Entertainment. All Rights Reserved. Ubisoft and the Ubisoft logo are registered trademarks in the US and/or other countries.\\n\\nRainbow Six Siege: Jackal Elite Set - New on the Six | Ubisoft [NA]\\nhttp://www.youtube.com/UbisoftNA"

        val mainContentSplitter = "\\n\\n"
        val innerContentSplitter = "\\n"
        val mainContent = content.split(mainContentSplitter)
        val viewContent = ArrayList<Cont>()

        mainContent.forEach {
            if (it.contains(innerContentSplitter))
                it.split(innerContentSplitter).map { str -> getCont(str) }.filterNotNullTo(viewContent)
            else
                getCont(it)?.let { nonNullCont -> viewContent.add(nonNullCont) }
        }
        println(viewContent.joinToString("\n"))
    }

    private fun getCont(str: String): Cont? {
        val header1 = "(?<=[^#]|^)# (.*)".toRegex()
        val header2 = "(?<=[^#]|^)#{2} (.*)".toRegex()
        val imagePrefix = "(/{2}.*?\\.(?:jpg|gif|png|jpeg))".toRegex()
        val captionPrefix = "(?<=__)(.*?)(?=__)".toRegex()

        return when {
            str.startsWith("##") -> {
                header2.find(str)?.groups?.get(1)?.value?.let { nonNullValue ->
                    Cont("Text - header2", nonNullValue)
                }
            }
            str.startsWith("#") -> {
                header1.find(str)?.groups?.get(1)?.value?.let { nonNullValue ->
                    Cont("Text - header1", nonNullValue)
                }
            }
            str.startsWith("![") -> {
                imagePrefix.find(str)?.groups?.get(1)?.value?.let { nonNullValue ->
                   Cont("Image", "http:$nonNullValue")
                }
            }
            str.startsWith("--") -> {
                captionPrefix.find(str)?.groups?.get(1)?.value?.let { nonNullValue ->
                    Cont("Text - paragraph", nonNullValue)
                }
            }
            else -> Cont("Text - simple", str)
        }
    }

    data class Cont(val type: String, val value: String) {
        override fun toString(): String {
            return "Type: $type Value: $value"
        }
    }

}
