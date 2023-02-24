package org.otunjargych.tamtam.di.data

interface AppPrefs {
    var userToken: String?
    var userId: Int
    var userTown : String
    var uniqueDeviceId: String?
}