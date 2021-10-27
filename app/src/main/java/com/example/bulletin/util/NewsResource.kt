package com.example.bulletin.util

//It is recommended by google to wrap around the response we got
//it will be a generic lass and is useful to differentiate between
//successful and unsuccessful responses and also helps us to handle
// the loading state.
//As stated before it helps us know whether the response was successful
//or not and depending on that we can handle the error or show the response

sealed class NewsResource <T> (
    val data : T? = null,
    val message : String? = null
){

    class Success<T>(data: T?) : NewsResource<T>(data)

    class Error<T>(message: String?, data: T? = null) : NewsResource<T>(data, message)

    class Loading<T> : NewsResource<T>()

}