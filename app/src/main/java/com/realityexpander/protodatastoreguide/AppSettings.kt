package com.realityexpander.protodatastoreguide

import androidx.datastore.core.DataStore
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.mutate
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.serialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class AppSettings(
    val language: Language = Language.ENGLISH,

    // Note: no @Polymorphic annotation here. Must manually set for each type (AFAIK)
    @Serializable(PersistentListOfLocationSerializer::class)
    val knownLocations: PersistentList<Location> = persistentListOf(),

    // Note: Regular Lists have no need for custom Serializer
    val knownLocations2: List<Location> = listOf()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = PersistentList::class)
class PersistentListOfLocationSerializer(private val dataSerializer: KSerializer<Location>) :
    KSerializer<PersistentList<Location>> {
    private class PersistentListDescriptor : SerialDescriptor by serialDescriptor<List<Location>>() {
        @ExperimentalSerializationApi
        override val serialName: String = "kotlinx.serialization.immutable.persistentList"
    }

    override val descriptor: SerialDescriptor = PersistentListDescriptor()
    override fun serialize(encoder: Encoder, value: PersistentList<Location>) {
        return ListSerializer(dataSerializer).serialize(encoder, value.toList())
    }

    override fun deserialize(decoder: Decoder): PersistentList<Location> {
        return ListSerializer(dataSerializer).deserialize(decoder).toPersistentList()
    }
}


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

// Use persistentList
suspend fun DataStore<AppSettings>.addKnownLocation(location: Location) {
    updateData { appSettings ->
        appSettings.copy( knownLocations = appSettings.knownLocations.mutate {
                it.add(location)
            }
        )
    }
}

// Use persistentList
suspend fun DataStore<AppSettings>.removeKnownLocation(location: Location) {
    updateData { appSettings ->
        appSettings.copy( knownLocations = appSettings.knownLocations.mutate {
            it.remove(location)
        })

    }
}

// Use normal List
suspend fun DataStore<AppSettings>.addKnownLocation2(location: Location) {
    updateData { appSettings ->
        appSettings.copy( knownLocations2 =
            appSettings.knownLocations2.toMutableList().apply {
                add(location)
            }.toList()
        )
    }
}

// Use normal List
suspend fun DataStore<AppSettings>.removeKnownLocation2(location: Location) {
    updateData { appSettings ->

        // Use normal List
        appSettings.copy( knownLocations2 =
            appSettings.knownLocations2.toMutableList().apply {
                remove(location)
            }.toList()
        )
    }
}






















