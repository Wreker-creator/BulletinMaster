package com.example.bulletin.model

import java.io.Serializable

data class DataObject(
    val imageUrl : Int,
    val title : String
) : Serializable

//serializable converts data to byte stream so that it can be passed along activities or fragments.