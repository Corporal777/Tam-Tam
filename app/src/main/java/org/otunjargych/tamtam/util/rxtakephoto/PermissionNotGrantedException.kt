package org.otunjargych.tamtam.util.rxtakephoto

class PermissionNotGrantedException(val permission: String? = null) : Exception()
class GPSNotEnabledException() : Exception()