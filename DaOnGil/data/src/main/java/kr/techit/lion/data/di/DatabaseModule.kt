package kr.techit.lion.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kr.techit.lion.data.database.MainDatabase
import kr.techit.lion.data.database.dao.AreaCodeDao
import kr.techit.lion.data.database.dao.RecentlySearchKeywordDAO
import kr.techit.lion.data.database.dao.SigunguCodeDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {
    @Provides
    @Singleton
    fun provideMainDatabase(@ApplicationContext appContext: Context): MainDatabase =
        Room.databaseBuilder(appContext, MainDatabase::class.java, "main_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideAreaCodeDao(db: MainDatabase): AreaCodeDao = db.areaCodeDao()

    @Provides
    @Singleton
    fun provideSigunguCodeDao(db: MainDatabase): SigunguCodeDao = db.sigunguCodeDao()

    @Provides
    @Singleton
    fun provideRecentlySearchKeywordDao(db: MainDatabase): RecentlySearchKeywordDAO = db.recentlySearchKeywordDao()
}