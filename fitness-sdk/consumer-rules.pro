# Consumer ProGuard rules for FitnessSDK
# Keep all public API classes
-keep class com.fitness.sdk.FitnessSDK { *; }
-keep class com.fitness.sdk.FitnessSDKConfig { *; }
-keep class com.fitness.sdk.api.** { *; }
-keep class com.fitness.sdk.domain.model.** { *; }
