# ProGuard / R8 rules for RailOne
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.indianrailways.railone.data.model.** { *; }
