# SQLCipher ProGuard Rules
-keep class net.sqlcipher.** { *; }
-dontwarn net.sqlcipher.**
-keepclasseswithmembernames class * {
    native <methods>;
}

# Room Database
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Models & Reflection
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class com.aurafit.app.data.model.** { *; }
