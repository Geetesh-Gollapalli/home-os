# R8 / Proguard Rules for Google Play Release

# Room Database Keep Rules
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Moshi & JSON Codegen Keep Rules
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
}

# Coroutines
-keepclassmembers class kotlinx.coroutines.** {
    public *;
}

# Retain Android Architecture Components
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    public <init>(...);
}
