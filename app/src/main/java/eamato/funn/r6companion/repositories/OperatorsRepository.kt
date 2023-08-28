package eamato.funn.r6companion.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.InstanceCreator
import eamato.funn.r6companion.entities.Operators
import eamato.funn.r6companion.utils.IRemoteDataFetcher
import eamato.funn.r6companion.utils.IRepository
import eamato.funn.r6companion.utils.OPERATORS_CACHE_FILE_NAME
import eamato.funn.r6companion.utils.isCurrentlyConnectedToInternet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.reflect.Type


class OperatorsRepository(
    private val context: Context?,
    private val remoteDataFetcher: IRemoteDataFetcher
) : IRepository<List<Operators.Operator>?> {

    override suspend fun getRepository(): List<Operators.Operator>? {
        return getOperators()
    }

    private suspend fun getOperators(): List<Operators.Operator>? {
        if (context == null)
            return null

        val result = if (context.isCurrentlyConnectedToInternet())
            getRemoteOperators(context, remoteDataFetcher)
        else
            getLocalOperators(context)

        if (result == null)
            return getOperatorsFromAssets(context)

        return parseResult(result)
    }

    private suspend fun getRemoteOperators(
        context: Context,
        remoteDataFetcher: IRemoteDataFetcher
    ): String? = withContext(Dispatchers.IO) {
        try {
            var fetchedOperators = withTimeout(5 * 1_000L) { remoteDataFetcher.fetch() }
            if (fetchedOperators != null)
                changeLocalDataIfNeeded(context, fetchedOperators)
            else
                fetchedOperators = getLocalOperators(context)
            return@withContext fetchedOperators
        } catch (e: Exception) {
            getLocalOperators(context)
        }
    }

    private suspend fun getLocalOperators(context: Context): String? {
        return withContext(Dispatchers.IO) {
            try {
                val operatorsFile = getLocalFile(context) ?: return@withContext null

                val isNewFile = operatorsFile.createNewFile()

                if (isNewFile)
                    return@withContext null
                else
                    return@withContext operatorsFile.readText()
                        .takeIf { it.isNotEmpty() && it.isNotBlank() }
            } catch (e: Exception) {
                return@withContext null
            }
        }
    }

    private fun getLocalFile(context: Context): File? {
        val filesDir = context.filesDir
        if (!filesDir.exists() || !filesDir.isDirectory)
            return null
        return File(filesDir, OPERATORS_CACHE_FILE_NAME)
    }

    private suspend fun changeLocalDataIfNeeded(context: Context, data: String) {
        val localOperators = getLocalOperators(context)
        if (localOperators == null || localOperators != data)
            getLocalFile(context)?.writeText(data)
    }

    private suspend fun parseResult(result: String): List<Operators.Operator> {
        return withContext(Dispatchers.IO) {
            try {
                val operators = Gson().fromJson(result, Operators::class.java)
                return@withContext operators?.operators?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }
        }
    }

    private suspend fun getOperatorsFromAssets(context: Context): List<Operators.Operator> {
        return withContext(Dispatchers.IO) {
            try {
                val operators = Gson().fromJson(
                    BufferedReader(InputStreamReader(context.assets.open("operators.json"))),
                    Operators::class.java
                )
                return@withContext operators?.operators?.filterNotNull() ?: emptyList()
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext emptyList()
            }
        }
    }

}