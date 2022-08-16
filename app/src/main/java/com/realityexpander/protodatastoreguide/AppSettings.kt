package com.realityexpander.protodatastoreguide

import androidx.datastore.core.DataStore
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val language: Language = Language.ENGLISH,
    val knownLocations: PersistentList<Location> = persistentListOf(),
    val knownLocations2: List<Location> = listOf()
)

@Serializable
data class Location(
    val lat: Double,
    val lng: Double
)

enum class Language {
    ENGLISH, GERMAN, SPANISH
}

suspend fun DataStore<AppSettings>.setLanguage(language: Language) {
    updateData { appSettings ->
        appSettings.copy(language = language)
    }
}

suspend fun DataStore<AppSettings>.addKnownLocation(location: Location) {
    updateData { appSettings ->
        // crashes at runtime
//        appSettings.copy( knownLocations = appSettings.knownLocations.mutate {
//                it.add(location)
//            }
//        )

        // Does not crash at runtime
        appSettings.copy( knownLocations2 =
            appSettings.knownLocations2.toMutableList().apply {
                add(location)
            }.toList()
        )
    }
}

suspend fun DataStore<AppSettings>.removeKnownLocation(location: Location) {
    updateData { appSettings ->
        // crashes at runtime
//        appSettings.copy( knownLocations = appSettings.knownLocations.mutate {
//                it.remove(location)
//            }
//        )

        // Does not crash at runtime
        appSettings.copy( knownLocations2 =
            appSettings.knownLocations2.toMutableList().apply {
                remove(location)
            }.toList()
        )
    }
}
