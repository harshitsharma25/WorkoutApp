package com.example.a7minutesworkoutapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "history-table")
data class HistoryEntity(

    @PrimaryKey
    val date : String

)
