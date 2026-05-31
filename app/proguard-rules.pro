# Add project specific ProGuard rules here.
-keepattributes *Annotation*

# Moshi
-keep class com.squareup.moshi.kotlin.codegen.JsonClassCodegenAdapter { *; }
-keep class com.cuangx.finance.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
