package eamato.funn.r6companion.utils

interface IRepository<T> {

    suspend fun getRepository(): T

}