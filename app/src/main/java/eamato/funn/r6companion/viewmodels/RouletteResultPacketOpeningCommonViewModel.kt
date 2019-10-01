package eamato.funn.r6companion.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import eamato.funn.r6companion.entities.RouletteOperator

class RouletteResultPacketOpeningCommonViewModel : ViewModel() {

    val winner = MutableLiveData<RouletteOperator>()

}