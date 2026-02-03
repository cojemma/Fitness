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

### SDK - To Develop

| Feature | Status | Priority | Description |
|---------|--------|----------|-------------|
| Workout templates | 📋 | High | Save and reuse workout routines |
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

### UI Components

| Component | Status | Description |
|-----------|--------|-------------|
| Workout card | ✅ | Card with type icon, duration, calories |
| Exercise item | ✅ | Row showing sets × reps, weight |
| Stats summary | ✅ | Weekly totals (workouts, calories, duration) |
| Empty state | ✅ | Placeholder when no workouts |
| Workout type dropdown | ✅ | Select workout category |

### App - To Develop

| Feature | Status | Priority | Description |
|---------|--------|----------|-------------|
| Swipe to delete | 📋 | High | Swipe gesture on workout cards |
| Search workouts | 📋 | High | Search by name or type |
| Calendar view | 📋 | Medium | View workouts on calendar |
| Charts/graphs | 📋 | Medium | Visualize progress over time |
| Workout timer | 📋 | Medium | Built-in rest timer |
| Exercise library | 📋 | Medium | Predefined exercises to choose from |
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
| 1.0.0 | 2026-02-03 | Initial release with core features |

---

## Next Steps (Recommended Order)

1. **Swipe to delete** - Better UX for deleting workouts
2. **Search workouts** - Find workouts quickly
3. **Workout templates** - Save and reuse routines
4. **Charts** - Visualize workout progress
5. **Exercise library** - Predefined exercise database
