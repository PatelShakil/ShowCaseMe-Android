package com.techsavvy.showcaseme.common

/**
 * Single source of truth for branding and environment URLs.
 *
 * PROD is active below. To point the app at a local backend while developing,
 * comment the PROD constant and uncomment the matching DEV one (one flip per URL).
 */
object Brand {
    const val NAME = "આપदिWebsite"
    const val NAME_LATIN = "AapdiWebsite"

    // Public profile site (QR codes, share links)
    const val PUBLIC_URL = "https://aapdi.website/"
    // DEV (Android emulator -> host machine):
//     const val PUBLIC_URL = "http://localhost:3000/"

    // Authenticated dashboard (WebView + jwt-verify handoff)
    const val DASHBOARD_URL = "https://app.aapdi.website/"
    // DEV (Android emulator -> host machine):
//     const val DASHBOARD_URL = "http://localhost:5173/"

    // API base
    const val API_URL = "https://api.aapdi.website/api/"
    // DEV (Android emulator -> host machine):
//     const val API_URL = "http://localhost:8000/api/"
}
