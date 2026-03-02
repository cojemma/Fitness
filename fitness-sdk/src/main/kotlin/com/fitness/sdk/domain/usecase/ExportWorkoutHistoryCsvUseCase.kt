package com.fitness.sdk.domain.usecase

import com.fitness.sdk.domain.model.Workout
import com.fitness.sdk.domain.repository.WorkoutRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Use case for exporting workout history as CSV content.
 * Flattens Workout → Exercise → ExerciseSet into one row per set.
 * If an exercise has no set records, a single summary row is produced.
 */
class ExportWorkoutHistoryCsvUseCase(
    private val workoutRepository: WorkoutRepository
) {

    companion object {
        private const val CSV_HEADER =
            "Date,Workout Name,Workout Type,Duration (min),Exercise Name,Set #,Weight (kg),Reps,Warmup,Volume (kg)"

        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    }

    /**
     * Export workout history within the given date range as CSV text.
     *
     * @param startTime Start of the date range (inclusive, millis since epoch)
     * @param endTime End of the date range (inclusive, millis since epoch)
     * @return CSV content string including the header row
     */
    suspend operator fun invoke(startTime: Long, endTime: Long): String {
        require(startTime <= endTime) { "startTime must be <= endTime" }

        val workouts = workoutRepository.getWorkoutsByDateRange(startTime, endTime)
        return buildCsv(workouts)
    }

    internal fun buildCsv(workouts: List<Workout>): String {
        val sb = StringBuilder()
        sb.appendLine(CSV_HEADER)

        for (workout in workouts) {
            val date = DATE_FORMAT.format(Date(workout.startTime))
            val workoutName = escapeCsvField(workout.name)
            val workoutType = workout.type.name
            val duration = workout.durationMinutes

            for (exercise in workout.exercises) {
                val exerciseName = escapeCsvField(exercise.name)

                if (exercise.setRecords.isNotEmpty()) {
                    // One row per set record
                    for (set in exercise.setRecords) {
                        val weight = set.weight ?: 0f
                        val volume = set.calculateVolume()
                        val warmup = if (set.isWarmupSet) "Yes" else "No"
                        sb.appendLine("$date,$workoutName,$workoutType,$duration,$exerciseName,${set.setNumber},$weight,${set.reps},$warmup,$volume")
                    }
                } else if (exercise.sets > 0) {
                    // Fallback: single summary row from aggregate fields
                    val weight = exercise.weight ?: 0f
                    val volume = exercise.calculateVolume()
                    sb.appendLine("$date,$workoutName,$workoutType,$duration,$exerciseName,1-${exercise.sets},$weight,${exercise.reps},No,$volume")
                }
            }
        }

        return sb.toString()
    }

    /**
     * Escape a field for CSV: wrap in quotes if it contains comma, quote, or newline.
     */
    private fun escapeCsvField(field: String): String {
        return if (field.contains(',') || field.contains('"') || field.contains('\n')) {
            "\"${field.replace("\"", "\"\"")}\""
        } else {
            field
        }
    }
}
