package com.fitness.sdk

/**
 * Configuration class for the Fitness SDK.
 * Use the Builder pattern to create configuration.
 */
class FitnessSDKConfig private constructor(
    val databaseName: String,
    val enableLogging: Boolean
) {
    /**
     * Builder for creating FitnessSDKConfig.
     */
    class Builder {
        private var databaseName: String = DEFAULT_DATABASE_NAME
        private var enableLogging: Boolean = false

        /**
         * Set a custom database name.
         * Default is "fitness_sdk_database".
         */
        fun databaseName(name: String) = apply {
            require(name.isNotBlank()) { "Database name cannot be blank" }
            this.databaseName = name
        }

        /**
         * Enable or disable debug logging.
         * Default is false.
         */
        fun enableLogging(enable: Boolean) = apply {
            this.enableLogging = enable
        }

        /**
         * Build the configuration.
         */
        fun build(): FitnessSDKConfig {
            return FitnessSDKConfig(
                databaseName = databaseName,
                enableLogging = enableLogging
            )
        }
    }

    companion object {
        const val DEFAULT_DATABASE_NAME = "fitness_sdk_database"

        /**
         * Create a default configuration.
         */
        fun default(): FitnessSDKConfig = Builder().build()
    }
}
