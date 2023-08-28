package eamato.funn.r6companion.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import eamato.funn.r6companion.api.requests.IOperatorsRequests
import eamato.funn.r6companion.entities.CompanionOperator
import eamato.funn.r6companion.entities.CompositeOperator
import eamato.funn.r6companion.entities.Operators
import eamato.funn.r6companion.utils.IRepository
import eamato.funn.r6companion.utils.toCompanionOperator
import eamato.funn.r6companion.utils.toCompositeOperators
import io.reactivex.Completable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class OperatorsViewModel : RetryableViewModel() {

    private val pOperators = MutableLiveData<List<CompositeOperator>?>(null)
    val operators: LiveData<List<CompositeOperator>?> get() = pOperators

    private val _companionOperators = MutableLiveData<List<CompanionOperator>?>(null)
    val companionOperators: LiveData<List<CompanionOperator>?> = _companionOperators

    fun requestOperators(operators: Operators) {
        viewModelScope.launch {
            flow {
                val r6StatsOperators = IOperatorsRequests.getR6StatsRequestCoroutines().getOperatorsCoroutines()
                emit(r6StatsOperators)
            }
                .flowOn(Dispatchers.IO)
                .onStart {
                    pIsRequestActive.value = true
                }
                .catch {
                    pIsRequestActive.value = false
                    pRequestError.value = it
                    errorAction = Completable.fromAction {
                        requestOperators(operators)
                    }
                }
                .map {
                    it.toCompositeOperators(operators)
                }
                .collect {
                    pIsRequestActive.value = false
                    pRequestError.value = null
                    errorAction = null
                    pOperators.value = it
                }
        }
    }

    fun getAllOperators(iRepository: IRepository<List<Operators.Operator>?>) {
        pIsRequestActive.value = true

        viewModelScope.launch {
            flow {
                emit(iRepository.getRepository())
            }
                .flowOn(Dispatchers.IO)
                .onStart { pIsRequestActive.value = true }
                .catch {
                    pIsRequestActive.value = false
                    pRequestError.value = it
                    errorAction = Completable.fromAction {
                        getAllOperators(iRepository)
                    }
                }
                .map {
                    it.toCompanionOperator()
                }
                .collect {
                    pIsRequestActive.value = false
                    pRequestError.value = null
                    errorAction = null
                    _companionOperators.value = it
                }
        }
    }

}