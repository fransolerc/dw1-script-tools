package com.dw1demo.model

import kotlinx.serialization.Serializable

@Serializable
data class ScriptManifest(
    val entries: Map<String, ManifestEntry> = emptyMap()
)

@Serializable
data class ManifestEntry(
    val id: Int = -1,
    val info: String = "",
    val dialoguePreviews: List<String> = emptyList(),
    val sectionCount: Int = 0,
    val fileName: String = ""
)