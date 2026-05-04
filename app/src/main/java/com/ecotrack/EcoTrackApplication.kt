package com.ecotrack

import android.app.Application
import com.ecotrack.data.db.EcoTrackDatabase

/**
 * Application class that initialises app-wide singletons such as the
 * Room database instance.
 */
class EcoTrackApplication : Application() {

    /** Lazily initialised Room database, shared across the whole app. */
    val database: EcoTrackDatabase by lazy {
        EcoTrackDatabase.getInstance(this)
    }
}
