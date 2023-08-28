#-dontwarn org.bouncycastle.jsse.BCSSLSocket
#-dontwarn org.bouncycastle.jsse.BCSSLParameters
#-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.*
#-dontwarn org.openjsse.javax.net.ssl.SSLParameters
#-dontwarn org.openjsse.javax.net.ssl.SSLSocket
#-dontwarn org.openjsse.net.ssl.OpenJSSE
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn sun.misc.**
-dontwarn com.google.errorprone.annotations.MustBeClosed
-dontwarn kotlin.Experimental

-keepattributes Signature
-keepattributes Annotation
-keepattributes Exceptions
-keepattributes *Annotation*

-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class eamato.funn.r6companion.utils.TogglingObject
-keep,allowobfuscation,allowshrinking class eamato.funn.r6companion.utils.NewsDataMixedWithAds
-keep,allowobfuscation,allowshrinking class eamato.funn.r6companion.entities.RouletteOperator
-keep,allowobfuscation,allowshrinking class eamato.funn.r6companion.entities.Updates
-keep,allowobfuscation,allowshrinking class eamato.funn.r6companion.repositories.OperatorsRepository
-keep,allowobfuscation,allowshrinking class eamato.funn.r6companion.entities.Operators

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-optimizations !class/unboxing/enum

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

#-dontobfuscate

-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl

-keep class com.google.gson.examples.android.model.** { <fields>; }

-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

-keep class com.google.gson.** { *; }

-keep class retrofit2.** { *; }

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

-keepnames class kotlinx.parcelize.** { *; }
-keepclassmembers class * implements kotlinx.parcelize.Parcelize { *; }
-keep class kotlin.Metadata { *; }

-keep class android.** { *; }
-keep interface android.** { *; }

-keep class eamato.funn.r6companion.entities.Operators {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Operators$Operator {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators$Operator {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Updates {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Updates {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Updates$Item {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Updates$Item {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Updates$Item$Thumbnail {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Updates$Item$Thumbnail {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.R6StatsOperators {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.R6StatsOperators {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.R6StatsOperators$R6StatsOperatorsItem {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.R6StatsOperators$R6StatsOperatorsItem {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.R6StatsOperators$R6StatsOperatorsItem$Ctu {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.R6StatsOperators$R6StatsOperatorsItem$Ctu {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.dto.RouletteFragmentArgument {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.dto.RouletteFragmentArgument {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.RouletteOperator {
    <fields>;
    <methods>;
    <init>();
}

-keep class eamato.funn.r6companion.entities.dto.UpdateDTO {
    <fields>;
    <methods>;
    <init>();
}

-keep class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity {
    public <init>();
}

-keep class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity$Position {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity$Position {
    public <init>();
}

-keep class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity$En {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity$En {
    public <init>();
}

-keep class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity$Ru {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.LocalizedOurTeamRemoteConfigEntity$Ru {
    public <init>();
}

-keep class eamato.funn.r6companion.firebase.things.LocalizedRemoteConfigEntity {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.LocalizedRemoteConfigEntity {
    public <init>();
}

-keep class eamato.funn.r6companion.firebase.things.ComingSoon {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.ComingSoon {
    public <init>();
}

-keep class eamato.funn.r6companion.firebase.things.OurMission {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.OurMission {
    public <init>();
}

-keep class eamato.funn.r6companion.firebase.things.OurTeam {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.firebase.things.OurTeam {
    public <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.CompanionOperator {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.CompanionOperator {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators$Operator$Equipment {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Operators$Operator$Squad {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators$Operator$Squad {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Operators$Operator$Equipment {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Device {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Device {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Primary {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Primary {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Secondary {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Secondary {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Skill {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.Operators$Operator$Equipment$Skill {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.CompanionOperator$Squad {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.CompanionOperator$Squad {
    <fields>;
    <methods>;
    <init>();
}

-keepclassmembers class eamato.funn.r6companion.entities.CompositeOperator {
    public <init>();
}

-keep class eamato.funn.r6companion.entities.CompositeOperator {
    <fields>;
    <methods>;
    <init>();
}