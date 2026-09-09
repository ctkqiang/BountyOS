# BountyOS ProGuard rules.
#
# Retrofit + kotlinx.serialization rely on runtime reflection over generated
# serializers; keep them and the DTO model classes intact in release builds.

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class com.bountyos.** {
    *** Companion;
}

-keepclasseswithmembers class com.bountyos.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Room entities are accessed reflectively by the generated implementation.
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
