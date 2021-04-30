package eamato.funn.r6companion.ui.activities

import android.app.Activity
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.ScrollingMovementMethod
import android.text.style.*
import android.util.DisplayMetrics
import eamato.funn.r6companion.R
import eamato.funn.r6companion.utils.glide.GlideDynamicDrawableSpan
import eamato.funn.r6companion.utils.toSpannableContent
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        tv_test_text.movementMethod = ScrollingMovementMethod()

        val content = "### По приказу команды Rainbow...\n\n![[R6S] Vintage Fashion Fanart 1](//staticctf.akamaized.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/4Jeh1aKKbjqK00ShiglwMU/c3a64157864e6624b34912856b622491/1-min.png)\n\nПора начать рисовать! Любите моду и жаждете творить? Тогда хватайте кисти и планшеты — пришло время переосмыслить внешний вид любимого персонажа! \n\nСоздайте старомодные костюмы для любимых оперативников и поделитесь ими с помощью хэштега __#R6Vintage__ — получите шанс выиграть полную коллекцию «The Grand Larceny»!\n\n__Конкурс начнется 5 мая в 19:00 МСК, а закончится 12 мая в 19:00 МСК.__\n\n# Как принять участие?\n\n- Выберите оперативника и придумайте ему старомодный образ с помощью одежды или предметов.\n- Проявите свою фантазию! (Работа должна быть выполнена только вами (без заимствований) и только для этого конкурса.)\n- Создайте файл в формате .png, изобразите оперативника в полный рост. Не добавляйте фон!\n- Выложите работу в Твиттер (@rainbow6game) или Instagram (@Rainbow6game_us), используя хэштег #R6Vintage, и отметьте @rainbow6game (в Твиттере) или @rainbow6game_us (в Instagram).\n\n# Призы\n\n![[R6S] Vintage Fashion Fanart 2](//staticctf.akamaized.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/1shejjns55ZSv1r5y5QkKO/bb896d9bcf62553dca58adab0aed3d03/2-min.png)\n\n### 10 победителей — полная коллекция «The Grand Larceny»\n\nПо итогам конкурса мы выберем самые стильные и эффектные костюмы, создатели которых получат полную коллекцию «The Grand Larceny»! Критерии следующие:\n\n- уровень дизайна и стиля,\n- оригинальность,\n- соблюдение темы конкурса и события.\n\n# Победители:\n\nМы свяжемся с победителями в социальных сетях, чтобы рассказать, как они смогут получить награду.\n\nИх работы будут опубликованы, когда событие подойдет к концу.\n\nЕсли хотите узнать больше, ознакомьтесь с официальными правилами конкурса.\n\n[![[R6S] Vintage Fashion Fanart 3](//staticctf.akamaized.net/J3yJr34U2pZ2Ieem48Dwy9uqj5PNUQTn/1oG7uTpiTflPeBiTJ0lPhN/9ffa39a45612b7a3b7b33af23cf9be71/3-min.png)](http://static2.cdn.ubi.com/pxm/RainbowSix/Rules/r6_vintagefashioncontest_US%20and%20Worldwide%20Promotion%20Official%20Rules.pdf)\n\n\n---\n\n\nAs part of the competition, Ubisoft collects and processes your personal data to allow your participation in the competition and to share the content you have published. The processing of your personal data is necessary to perform the contract you have entered with Ubisoft by accepting the rules of the competition. Your personal data will be accessible by Ubisoft, its affiliates and sub-processors. The content you have published as part of the competition will remain publicly available until you choose to delete it.\n\nUbisoft may transfer your personal data to non-European countries that ensure an adequate level of protection according to the EU Commission or within the framework of the standard data protection clauses adopted by the EU Commission [here](https://ec.europa.eu/info/law/law-topic/data-protection/data-transfers-outside-eu/model-contracts-transfer-personal-data-third-countries_fr).\n\nYou can request a copy of your data, its deletion or rectification, object to the processing of your data, request the restriction of its processing, and/or receive your information in portable form by contacting Ubisoft's data protection officers [here](https://support.ubi.com/faqs/35367/Reviewing-the-data-that-Ubisoft-holds-about-me/). After contacting us, if you are not satisfied with the way we handled your request, you may address a complaint to the supervisory authority of your country.\n"

        val displayMetrics = getDisplayMetrics()

        tv_test_text?.text = content.toSpannableContent(displayMetrics, tv_test_text)
    }

    fun getDisplayMetrics(): DisplayMetrics {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

}