package com.example.bulletin.database

import androidx.room.TypeConverter
import javax.xml.transform.Source

//The reason why i made this class is because when we look at the class Article
//which is our entity, All the values accepted are of primitive type, except for one
//source. Source if of the type Source which is not a primitive datatype.

//hence i created a type converter so that room can know what to do when it accepts
// data of the type Source. By coding this room can know how to convert source into string
//or string into source.

class TypeConverter {

    @TypeConverter
    fun fromSource(source: com.example.bulletin.model.Source) : String{
        return source.name
    }

    //We pass string 2 times here because we only need the name and not the id
    @TypeConverter
    fun toSource(string: String) : com.example.bulletin.model.Source{
        return com.example.bulletin.model.Source(string, string)
    }

}