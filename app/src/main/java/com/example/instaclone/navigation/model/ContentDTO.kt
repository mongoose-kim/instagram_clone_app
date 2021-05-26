package com.example.instaclone.navigation.model

import java.sql.Timestamp

class ContentDTO(var exlain : String? = null,
                 var imageUrl : String? = null,
                 var uid : String? = null,
                 var userId : String? = null,
                 var timestamp : Long? = null,
                 var favoriteCount : Int? = 0,
                 var favorites : MutableMap<String, Boolean> = HashMap()){
    data class Content(var uid : String? = null,
                       var userId : String? = null,
                       var comment : String? = null,
                       var timestamp : Long? = null)

}
