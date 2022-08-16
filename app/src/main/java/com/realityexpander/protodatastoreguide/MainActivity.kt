package com.realityexpander.protodatastoreguide

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import com.realityexpander.protodatastoreguide.ui.theme.ProtoDataStoreGuideTheme
import kotlinx.coroutines.launch

val Context.dataStore by dataStore("app-settings.json", AppSettingsSerializer)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProtoDataStoreGuideTheme {
                val appSettings = dataStore.data.collectAsState( // .data is a Flow
                    initial = AppSettings()
                ).value

                val scope = rememberCoroutineScope()

                val latState = remember { mutableStateOf(TextFieldValue()) }
                val lonState = remember { mutableStateOf(TextFieldValue()) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colors.background),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Show Language Radio Buttons
                    for(i in 0 until Language.values().size) {
                        val language = Language.values()[i]

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = language == appSettings.language,
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = MaterialTheme.colors.onSurface,
                                    unselectedColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                    disabledColor = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                                ),
                                onClick = {
                                    scope.launch {
                                        dataStore.setLanguage(language)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))


                            Text(text = language.toString(),
                                color = MaterialTheme.colors.onSurface,
                                modifier = Modifier.clickable { // make text clickable
                                    scope.launch {
                                        dataStore.setLanguage(language)
                                    }
                                }
                            )
                        }
                    }

                    // Location input fields
                    Spacer(modifier = Modifier.height(16.dp))
                    Row (
                        modifier = Modifier
                            .padding(start = 16.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        TextField(
                            value = latState.value,
                            onValueChange = { latState.value = it },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.25f),
                                cursorColor = MaterialTheme.colors.onSurface,
                                textColor = MaterialTheme.colors.onSurface,
                                disabledTextColor = MaterialTheme.colors.background.copy(alpha = 0.5f),
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            modifier = Modifier
                                .weight(1f),
                            label = { Text(text = "Latitude") },
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        TextField(
                            value = lonState.value,
                            onValueChange = { lonState.value = it },
                            colors = TextFieldDefaults.textFieldColors(
                                backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.25f),
                                cursorColor = MaterialTheme.colors.onSurface,
                                textColor = MaterialTheme.colors.onSurface,
                                disabledTextColor = MaterialTheme.colors.background.copy(alpha = 0.5f),
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                            ),
                            modifier = Modifier
                                .weight(1f),
                            label = { Text(text = "Longitude") },
                        )
                    }

                    // Add known location
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if(latState.value.text.isNotEmpty() && lonState.value.text.isNotEmpty()) {
                                scope.launch {
                                    dataStore.addKnownLocation(
                                        Location(
                                            latState.value.text.toDouble(),
                                            lonState.value.text.toDouble()
                                        )
                                    )
                                }
                            }
                        },
                    ) {
                        Text(text = "Add Known Location (PersistentList)")
                    }

                    // List known locations (to persistentList)
                    Spacer(modifier = Modifier.height(8.dp))
                    for(knownLocation in appSettings.knownLocations) {

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "lat=${knownLocation.lat}, lng=${knownLocation.lng}",
                                color = MaterialTheme.colors.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        dataStore.removeKnownLocation(knownLocation)
                                    }
                                },
                            ) {
                                Text(text = "Remove")
                            }
                        }
                    }


                    // Add known location (to normal List)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if(latState.value.text.isNotEmpty() && lonState.value.text.isNotEmpty()) {
                                scope.launch {
                                    dataStore.addKnownLocation2(
                                        Location(
                                            latState.value.text.toDouble(),
                                            lonState.value.text.toDouble()
                                        )
                                    )
                                }
                            }
                        },
                    ) {
                        Text(text = "Add Known Location (regular List)")
                    }

                    // List known locations
                    Spacer(modifier = Modifier.height(8.dp))
                    for(knownLocation in appSettings.knownLocations2) {

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "lat=${knownLocation.lat}, lng=${knownLocation.lng}",
                                color = MaterialTheme.colors.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        dataStore.removeKnownLocation2(knownLocation)
                                    }
                                },
                            ) {
                                Text(text = "Remove")
                            }
                        }
                    }

                }
            }
        }
    }

}