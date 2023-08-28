package eamato.funn.r6companion.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import eamato.funn.r6companion.R
import eamato.funn.r6companion.adapters.recycler_view_adapters.OurTeamAdapter
import eamato.funn.r6companion.databinding.FragmentAboutBinding
import eamato.funn.r6companion.firebase.things.OUR_MISSION_KEY
import eamato.funn.r6companion.firebase.things.OUR_TEAM
import eamato.funn.r6companion.firebase.things.OurMission
import eamato.funn.r6companion.firebase.things.OurTeam
import eamato.funn.r6companion.ui.fragments.abstracts.BaseFragment
import eamato.funn.r6companion.utils.RUSSIAN_LANGUAGE_CODE
import eamato.funn.r6companion.utils.getFirebaseRemoteConfigEntity
import eamato.funn.r6companion.utils.getText

import android.content.pm.PackageManager.GET_SIGNATURES
import android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES
import android.os.Build
import eamato.funn.r6companion.BuildConfig
import java.lang.StringBuilder
import java.security.MessageDigest
import kotlin.experimental.and

private const val SCREEN_NAME = "About screen"

class AboutFragment : BaseFragment() {

    private var fragmentAboutBinding: FragmentAboutBinding? = null

    private val ourTeamAdapter = OurTeamAdapter()

    private var clickCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentAboutBinding = FragmentAboutBinding.inflate(inflater, container, false)
        return fragmentAboutBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentAboutBinding?.rvTeam?.layoutManager = GridLayoutManager(context, 3)
        fragmentAboutBinding?.rvTeam?.adapter = ourTeamAdapter
        fragmentAboutBinding?.cvVersion?.setOnClickListener {
            if (clickCount >= 10) {
                fragmentAboutBinding?.tvHash?.visibility = View.VISIBLE
                clickCount = 0
            } else {
                fragmentAboutBinding?.tvHash?.visibility = View.GONE
            }

            clickCount++
        }
        fragmentAboutBinding?.tvVersion?.text = "Version: ${BuildConfig.VERSION_CODE}(${BuildConfig.VERSION_NAME})"
        fragmentAboutBinding?.tvHash?.run {
            try {
                val keys = mutableListOf<String>()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, GET_SIGNING_CERTIFICATES)
                    packageInfo.signingInfo
                    packageInfo.signingInfo.apkContentsSigners.forEach { signature ->
                        val sha1 = MessageDigest.getInstance("SHA-1")
                        val sha256 = MessageDigest.getInstance("SHA-256")
                        sha1.update(signature.toByteArray())
                        sha256.update(signature.toByteArray())

                        val digest: ByteArray = sha1.digest()
                        val toRet = StringBuilder()
                        for (i in digest.indices) {
                            if (i != 0)
                                toRet.append(":")
                            val b = (digest[i] and 0xff.toByte()).toInt()
                            val hex = Integer.toHexString(b)
                            if (hex.length == 1)
                                toRet.append("0")
                            toRet.append(hex)
                        }

                        val key = toRet.toString()

                        keys.add(key)
                    }
                } else {
                    val packageInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, GET_SIGNATURES)
                    for (signature in packageInfo.signatures) {
                        val sha1 = MessageDigest.getInstance("SHA-1")
                        val sha256 = MessageDigest.getInstance("SHA-256")
                        sha1.update(signature.toByteArray())
                        sha256.update(signature.toByteArray())

                        val digest: ByteArray = sha1.digest()
                        val toRet = StringBuilder()
                        for (i in digest.indices) {
                            if (i != 0)
                                toRet.append(":")
                            val b = (digest[i] and 0xff.toByte()).toInt()
                            val hex = Integer.toHexString(b)
                            if (hex.length == 1)
                                toRet.append("0")
                            toRet.append(hex)
                        }

                        val key = toRet.toString()
                        keys.add(key)
                    }
                }

                text = keys.joinToString()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        fragmentAboutBinding?.rvTeam?.adapter = null

        super.onDestroyView()

        fragmentAboutBinding = null
    }

    override fun logScreenView() {
        super.logScreenView(this::class.java.simpleName, SCREEN_NAME)
    }

    override fun setLiveDataObservers() {
        mainViewModel.observableFirebaseRemoteConfig.observe(this) {
            it?.let { nonNullFirebaseRemoteConfig ->
                nonNullFirebaseRemoteConfig.getString(OUR_MISSION_KEY)
                    .getFirebaseRemoteConfigEntity(OurMission::class.java)
                    ?.let { nonNullOurMission ->
                        context?.let { nonNullContext ->
                            fragmentAboutBinding?.tvOurMission?.text =
                                nonNullOurMission.getText(nonNullContext)
                        }
                    }

                nonNullFirebaseRemoteConfig.getString(OUR_TEAM)
                    .getFirebaseRemoteConfigEntity(OurTeam::class.java)?.let { nonNullOurTeam ->
                        val positions =
                            if (context?.getString(R.string.lang) == RUSSIAN_LANGUAGE_CODE)
                                nonNullOurTeam.ru?.positions?.filterNotNull()
                            else
                                nonNullOurTeam.en?.positions?.filterNotNull()

                        ourTeamAdapter.submitList(positions)
                    }
            }
        }
    }

    override fun onLiveDataObserversSet() {

    }
}