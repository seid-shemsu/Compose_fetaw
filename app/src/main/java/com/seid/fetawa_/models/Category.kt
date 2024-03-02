package com.seid.fetawa_.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "categories")
data class Category(
    @ColumnInfo(name = "name") var name: String = "",
    @PrimaryKey @ColumnInfo(name = "uuid") var uuid: String = "",
): Serializable