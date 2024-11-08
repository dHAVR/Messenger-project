package com.dar_hav_projects.messenger.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MessageEntity::class],
    version = 1
)
abstract class MainDb: RoomDatabase() {
    abstract val listItemDao: MessagesDAO
}