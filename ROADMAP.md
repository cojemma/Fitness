# Fitness App - Feature Roadmap

This document tracks all features in the Fitness SDK and Sample App, including their current status.

---

## Legend

| Status | Meaning |
|--------|---------|
| ✅ | Completed and tested |
| 🚧 | In progress |
| 📋 | Planned (not started) |
| 💡 | Future idea |

---

## Fitness SDK Features

### Core Features

| Feature | Status | Description |
|---------|--------|-------------|
| Workout CRUD | ✅ | Create, read, update, delete workouts |
| Exercise management | ✅ | Add exercises to workouts with sets/reps/weight |
| Local storage (Room) | ✅ | Persist data to SQLite via Room |
| Reactive observation | ✅ | Observe workouts with Kotlin Flow |
| Workout types | ✅ | STRENGTH, CARDIO, FLEXIBILITY, HIIT, SPORTS, OTHER |
| Date range filtering | ✅ | Get workouts between two dates |
| Type filtering | ✅ | Filter workouts by type |
| SDK configuration | ✅ | Custom database name, logging toggle |
| Input validation | ✅ | Validate workout/exercise data before saving |
| Error handling | ✅ | Result<T> pattern for all operations |
| Exercise library | ✅ | Predefined exercises (55+) with muscle/category |
| Exercise search | ✅ | Search and filter exercises by name/muscle/category |
| Workout templates | ✅ | Save and reuse workout routines, update from completed workouts |
| Per-set recording | ✅ | Store individual set records (weight, reps) per exercise |
| Last Session Data | ✅ | Pre-load previous performance data for progressive overload |

### SDK - To Develop

| Feature | Status | Priority | Description |
|---------|--------|----------|-------------|
| Export data | 📋 | Medium | Export workouts to JSON/CSV |
| Import data | 📋 | Medium | Import workouts from backup |
| Statistics API | 📋 | Medium | Weekly/monthly stats calculations |
| Workout history | 📋 | Low | Track workout completion streaks |
| Body measurements | 💡 | Low | Track weight, body fat, etc. |
| Goals & achievements | 💡 | Low | Set fitness goals and track progress |

---

## Sample App Features

### Screens

| Screen | Status | Description |
|--------|--------|-------------|
| Home screen | ✅ | Workout list with weekly stats summary |
| Add workout | ✅ | Form to create new workout |
| Workout details | ✅ | View workout info and exercises |
| Edit workout | ✅ | Modify existing workout |
| Add exercise dialog | ✅ | Modal to add exercises |
| Exercise picker | ✅ | Browse and select from exercise library |
| Template list | ✅ | List of saved workout templates |
| Add template | ✅ | Create/Edit workout template |
| Active workout | ✅ | Session view with timer and set logging |

### UI Components

| Component | Status | Description |
|--------|--------|-------------|
| Workout card | ✅ | Card with type icon, duration, calories |
| Exercise item | ✅ | Row showing sets × reps, weight with per-set details |
| Stats summary | ✅ | Weekly totals (workouts, calories, duration) |
| Empty state | ✅ | Placeholder when no workouts |
| Workout type dropdown | ✅ | Select workout category |
| Exercise library cards | ✅ | Cards showing exercise details with category emoji |
| Muscle group filter chips | ✅ | Filter exercises by muscle group |
| Template card | ✅ | Card with muscle groups and start button |
| Template exercise card | ✅ | Editor for template sets and rests |

### App - To Develop

| Feature | Status | Priority | Description |
|---------|--------|----------|-------------|
| Swipe to delete | 📋 | High | Swipe gesture on workout cards |
| Search workouts | 📋 | High | Search by name or type |
| Calendar view | 📋 | Medium | View workouts on calendar |
| Charts/graphs | 📋 | Medium | Visualize progress over time |
| Workout timer | 📋 | Medium | Built-in rest timer |
| Settings screen | 📋 | Low | App preferences |
| Dark mode toggle | 📋 | Low | Manual theme switching |
| Onboarding | 💡 | Low | First-time user guide |
| Notifications | 💡 | Low | Workout reminders |
| Widget | 💡 | Low | Home screen widget |

---

## Demo/Test Features

These features exist for testing or demonstration purposes:

| Feature | Location | Purpose |
|---------|----------|---------|
| Sample workout creation | `MainActivity.kt` (old) | Demo SDK usage |
| Unit tests | `fitness-sdk/src/test/` | Test use cases and mappers |
| In-memory database | `FitnessDatabase.kt` | Testing without persistence |

---

## Known Issues

| Issue | Status | Notes |
|-------|--------|-------|
| Schema export warning | ⚠️ | Room schema export not configured (non-blocking) |
| Deprecated icon imports | ⚠️ | Some Material icons show deprecation warnings |

---

## Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.4.0 | 2026-02-04 | Execution & UX: Progressive overload tracking (last session data), "Save as Template" with replacement logic, Rest Timer auto-start. |
| 1.3.0 | 2026-02-04 | Per-Set Recording: Store individual set records (weight, reps) for detailed tracking. Display per-set data in workout details. |
| 1.2.0 | 2026-02-03 | Workout Template System: Create, edit, and start workouts from templates. Active workout session UI. |
| 1.1.0 | 2026-02-03 | Exercise library with 55+ predefined exercises, picker screen |
| 1.0.0 | 2026-02-03 | Initial release with core features |

---

## Next Steps (Recommended Order)

1. **Swipe to delete** - Better UX for deleting workouts
2. **Search workouts** - Find workouts quickly
3. **Charts** - Visualize workout progress
4. **Workout timer** - Built-in rest timer between sets
