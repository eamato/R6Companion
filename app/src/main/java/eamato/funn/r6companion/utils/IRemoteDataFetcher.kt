package eamato.funn.r6companion.utils

interface IRemoteDataFetcher {

    suspend fun fetch(): String?

}