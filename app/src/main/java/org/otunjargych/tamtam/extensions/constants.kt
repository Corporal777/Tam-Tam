package org.otunjargych.tamtam.extensions

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var USER_ID: String
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var USER : FirebaseAuth

internal const val EXTRA_SETUP = "extra.setup"
internal const val EXTRA_IMAGE = "extra.image"
internal const val RESULT_NAME = "images"

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
const val FOLDER_NOTES_IMAGES = "notes_images"
const val FOLDER_BUY_SELL_IMAGES = "buy_sell_images"
const val FOLDER_FLATS_IMAGES = "flats_images"
const val FOLDER_SERVICES_IMAGES = "services_images"
const val NODE_LIKED_ADS = "pref_ads"



const val END_POINT= "works.json"
const val FB_BASE_URL = "https://tam-tam-8b2a7-default-rtdb.firebaseio.com/"

const val NOTES_COLLECTION = "work_notes"
const val NAME_PROPERTY = "timeStamp"
const val PAGE_SIZE = 5