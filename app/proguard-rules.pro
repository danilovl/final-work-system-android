# ---------------------------------------------------------------------------
# kotlinx.serialization
# ---------------------------------------------------------------------------
# Serializers are generated at compile time, so field renaming by R8 is safe.
# The library ships its own consumer rules; the rules below additionally keep
# the generated serializers that Retrofit looks up at runtime by type.

-keepattributes RuntimeVisibleAnnotations,AnnotationDefault,InnerClasses

-keepclassmembers @kotlinx.serialization.Serializable class com.finalworksystem.** {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.finalworksystem.**$$serializer { *; }

# ---------------------------------------------------------------------------
# Retrofit service interfaces (suspend functions need generic signatures)
# ---------------------------------------------------------------------------
-keepattributes Signature
-keep,allowobfuscation interface com.finalworksystem.infrastructure.api.Api*Service
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# Keep line numbers for readable crash reports.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
