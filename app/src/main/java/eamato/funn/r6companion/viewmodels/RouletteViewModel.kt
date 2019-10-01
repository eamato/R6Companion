package eamato.funn.r6companion.viewmodels

import android.content.SharedPreferences
import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eamato.funn.r6companion.entities.RouletteOperator
import eamato.funn.r6companion.repositories.OperatorsRepository
import eamato.funn.r6companion.utils.areThereSavedSelectedOperators
import eamato.funn.r6companion.utils.saveSelectionsPreferencesKey
import eamato.funn.r6companion.utils.toRouletteOperators
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RouletteViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var immutableOperators: List<RouletteOperator> = emptyList()

    private val pVisibleRouletteOperators = MutableLiveData<List<RouletteOperator>>()
    val visibleRouletteOperators: LiveData<List<RouletteOperator>> = pVisibleRouletteOperators

    private val pRollingOperatorsAndWinner = MutableLiveData<Pair<List<RouletteOperator>, RouletteOperator>>()
    val rollingOperatorsAndWinner: LiveData<Pair<List<RouletteOperator>, RouletteOperator>> = pRollingOperatorsAndWinner

    private val pCanRoll = MutableLiveData<Boolean>()
    val canRoll: LiveData<Boolean> = pCanRoll

    init {
        pVisibleRouletteOperators.value = emptyList()

        pCanRoll.value = immutableOperators.any { it.isSelected }
    }

    fun getAllOperators(assetManager: AssetManager, preferences: SharedPreferences) {
        compositeDisposable.add(
            OperatorsRepository.getOperators(assetManager)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.toRouletteOperators() }
                .subscribe({
                    immutableOperators = ArrayList(it.map { operator -> operator.copy() })
                    pVisibleRouletteOperators.value = ArrayList(it.map { operator -> operator.copy() })
                    selectPreviouslySelectedOperators(preferences)
                }, {
                    it.printStackTrace()
                    immutableOperators = emptyList()
                    pVisibleRouletteOperators.value = emptyList()
                })
        )
    }

    fun roll() {
        immutableOperators
            .filter { it.isSelected }
            .takeIf { it.isNotEmpty() }
            ?.let {
                pRollingOperatorsAndWinner.value = Pair(it, it.random())
                pRollingOperatorsAndWinner.value = null
            }
    }

    // TODO REPLACE NAME WITH UNIQUE INDEX
    fun selectUnSelectRouletteOperator(rouletteOperator: RouletteOperator, isSelected: Boolean = !rouletteOperator.isSelected) {
        pVisibleRouletteOperators.value?.let { nonNullOperators ->
            pVisibleRouletteOperators.value = nonNullOperators
                .toMutableList()
                .also { mutableList ->
                    mutableList.indexOf(rouletteOperator)
                        .takeIf { it >= 0 }
                        ?.let { nonNullIndex ->
                            mutableList[nonNullIndex] = rouletteOperator.copy(
                                isSelected = isSelected
                            )
                        }
                }
                .toList()
        }

        immutableOperators.find { it.name == rouletteOperator.name }?.isSelected = isSelected

        pCanRoll.value = immutableOperators.any { it.isSelected }
    }

    fun filter(query: String) {
        pVisibleRouletteOperators.value = ArrayList(immutableOperators.filter { it.name?.contains(query, true) ?: true }.map { it.copy() })
    }

    fun restore() {
        pVisibleRouletteOperators.value = ArrayList(immutableOperators.map { it.copy() })
    }

    fun sortByNameAscending(doAfter: (() -> Unit)? = null) {
        pVisibleRouletteOperators.value = pVisibleRouletteOperators.value
            ?.sortedBy { it.name } ?: emptyList()
        doAfter?.invoke()
    }

    fun sortByNameDescending(doAfter: (() -> Unit)? = null) {
        pVisibleRouletteOperators.value = pVisibleRouletteOperators.value
            ?.sortedByDescending { it.name } ?: emptyList()
        doAfter?.invoke()
    }

    fun selectAll() {
        immutableOperators.forEach { selectUnSelectRouletteOperator(it, true) }
    }

    fun unSelectAll() {
        immutableOperators.forEach { selectUnSelectRouletteOperator(it, false) }
    }

    // TODO REPLACE NAME WITH UNIQUE INDEX
    fun selectPreviouslySelectedOperators(preferences: SharedPreferences) {
        compositeDisposable.add(
            preferences.areThereSavedSelectedOperators()
                .toObservable()
                .flatMap {
                    if (it) {
                        val savedSelectedOperators = preferences.getStringSet(saveSelectionsPreferencesKey, emptySet()) ?: emptySet()
                        Observable.fromIterable(immutableOperators).filter { operator ->
                            savedSelectedOperators.any { savedSelectedOperator ->
                                savedSelectedOperator == operator.name
                            }
                        }
                    } else {
                        Observable.empty()
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    selectUnSelectRouletteOperator(it, true)
                }, {
                    it.printStackTrace()
                }, {})
        )
    }

    fun saveSelectedOperators(preferences: SharedPreferences, doAfter: (() -> Unit)? = null) {
        immutableOperators.filter { it.isSelected }.takeIf { it.isNotEmpty() }?.let { nonNullSelectedOperators ->
            val selectedOperatorsNames = HashSet(nonNullSelectedOperators.map { it.name })
            preferences.edit()
                .putStringSet(saveSelectionsPreferencesKey, selectedOperatorsNames)
                .apply().also { doAfter?.invoke() }
        }
    }

    fun deleteSavedSelectedOperators(preferences: SharedPreferences, doAfter: (() -> Unit)? = null) {
        compositeDisposable.add(
            preferences.areThereSavedSelectedOperators()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    preferences.edit().remove(saveSelectionsPreferencesKey).apply()
                    doAfter?.invoke()
                }, {
                    it.printStackTrace()
                })
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }

}