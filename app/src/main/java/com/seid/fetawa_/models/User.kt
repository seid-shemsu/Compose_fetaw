package com.seid.fetawa_.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "users")
data class User(
    @ColumnInfo(name = "email") var email: String = "",
    @ColumnInfo(name = "photo") var photo: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @PrimaryKey @ColumnInfo(name = "uuid") var uuid: String = "",
) : Serializable