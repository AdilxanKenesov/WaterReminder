-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

-keepclassmembers enum com.visionsystems.waterreminder.** { *; }

-keep class com.visionsystems.waterreminder.navigation.** implements androidx.navigation3.runtime.NavKey { *; }

-keep class * implements androidx.glance.appwidget.action.ActionCallback { <init>(); }

-if class androidx.credentials.CredentialManager
-keep class androidx.credentials.playservices.** { *; }
