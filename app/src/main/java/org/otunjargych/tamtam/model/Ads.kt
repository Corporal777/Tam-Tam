package org.otunjargych.tamtam.model

data class Work(
    var id: String = "",
    var title: String = "",
    var text: String = "",
    var salary: String = "",
    var timeStamp: Any = "",
    var station: String? = "",
    var category: String = "",
    var likes: Int = 0,
    var viewings : Int = 0,
    var imageURL: String = "",
    val phone_number: String = ""
)


data class LikedAds(
    var id : String = "",
    val title: String = "",
    val text: String = "",
    val salary: String = "",
    val station: String? = "",
    val timeStamp: Any = "",
    val category: String = "",
    var likes: Int = 0,
    var viewings : Int = 0,
    val firstImageURL: String = "",
    val secondImageURL: String = "",
    val thirdImageURL: String = "",
    val fourthImageURL: String = "",
    val phone_number: String = ""
)


data class Flats(
    var id: String = "",
    var title: String = "",
    var text: String = "",
    var salary: String = "",
    var timeStamp: Any = "",
    var station: String? = "",
    var category: String = "",
    var likes: Int = 0,
    var viewings : Int = 0,
    var imageURL: String = "",
    val phone_number: String = ""
)


class Beauty(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val salary: String = "",
    val station: String? = "",
    val timeStamp: Any = "",
    val category: String = "",
    var likes: Int = 0,
    var viewings : Int = 0,
    val firstImageURL: String = "",
    val secondImageURL: String = "",
    val thirdImageURL: String = "",
    val fourthImageURL: String = "",
    val phone_number: String = ""
)
data class BuySell(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val salary: String = "",
    val station: String? = "",
    val timeStamp: Any = "",
    val category: String = "",
    var likes: Int = 0,
    var viewings : Int = 0,
    val firstImageURL: String = "",
    val secondImageURL: String = "",
    val thirdImageURL: String = "",
    val fourthImageURL: String = "",
    val phone_number: String = ""
)



data class Transportation(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val salary: String = "",
    val station: String? = "",
    val timeStamp: Any = "",
    val category: String = "",
    var likes: Int = 0,
    var viewings : Int = 0,
    val imageURL: String = "",
    val phone_number: String = ""

)
data class Services(
    val id: String = "",
    val title: String = "",
    val text: String = "",
    val salary: String = "",
    val station: String? = "",
    val timeStamp: Any = "",
    val category: String = "",
    var likes: Int = 0,
    var viewings : Int = 0,
    val imageURL: String = "",
    val phone_number: String = ""
)