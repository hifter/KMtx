package io.github.hifter.kmtx.module

import net.folivo.trixnity.core.model.UserId
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.Module

expect class RepositoriesModule {
    companion object{
        fun getRepositoriesModule(userId: UserId): Module
        fun getMediaStoreModule ():Module
    }
}