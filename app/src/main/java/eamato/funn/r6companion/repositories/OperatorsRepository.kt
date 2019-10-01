package eamato.funn.r6companion.repositories

import android.content.res.AssetManager
import com.google.gson.Gson
import eamato.funn.r6companion.entities.Operators
import io.reactivex.Single
import java.io.BufferedReader
import java.io.InputStreamReader

object OperatorsRepository {

    private var operators: List<Operators.Operator>? = null

    fun getOperators(assetManager: AssetManager): Single<List<Operators.Operator>> {
        return if (operators.isNullOrEmpty()) {
                try {
                    val operators = Gson().fromJson<Operators?>(
                        BufferedReader(InputStreamReader(assetManager.open("operators.json"))),
                        Operators::class.java
                    )
                    val temp = (operators?.operators?.filterNotNull() ?: emptyList())
                    this.operators = temp
                    Single.just(temp)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Single.just(emptyList<Operators.Operator>())
                }
            } else {
                Single.just(operators)
            }
    }

    fun clearOperators() {
        operators = null
    }

}