# Add project specific ProGuard rules here.
-keep class com.kutirakushala.** { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.AppGlideModule { *; }

# Prevent stripping of model classes
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable