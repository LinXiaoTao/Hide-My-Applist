# Xposed
-keepclassmembers class com.box.android.black.MyApp {
    boolean isHooked;
}

# Enum class
-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.box.android.black.data.UpdateData { *; }
-keep class com.box.android.black.data.UpdateData$* { *; }

-keep,allowoptimization class * extends androidx.preference.PreferenceFragmentCompat
-keepclassmembers class com.box.android.black.databinding.**  {
    public <methods>;
}
