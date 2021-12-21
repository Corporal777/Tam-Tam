package org.otunjargych.tamtam.extensions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var UUID: String
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER : FirebaseAuth

const val NODE_USERS = "users"
const val NODE_WORKS = "works"
const val NODE_TRANSPORT = "transport"
const val NODE_BEAUTY = "beauty"
const val NODE_BUY_SELL = "buy_sell"
const val NODE_HOUSE = "house_room"
const val NODE_SERVICES = "services"
const val FOLDER_USER_IMAGES = "users_profile_images"
const val FOLDER_WORKS_IMAGE = "works_images"
const val FOLDER_TRANSPORT_IMAGE = "transport_images"
const val FOLDER_BEAUTY_AND_MEDICINE_IMAGES = "beauty_and_medicine_images"
const val FOLDER_BUY_SELL_IMAGES = "buy_sell_images"
const val FOLDER_FLATS_IMAGES = "flats_images"
const val FOLDER_SERVICES_IMAGES = "services_images"
const val NODE_LIKED_ADS = "pref_ads"
