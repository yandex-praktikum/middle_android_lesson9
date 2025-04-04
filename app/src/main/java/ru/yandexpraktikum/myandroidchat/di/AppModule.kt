package ru.yandexpraktikum.myandroidchat.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import ru.yandexpraktikum.myandroidchat.data.local.ChatDatabase
import ru.yandexpraktikum.myandroidchat.data.local.MessageDao
import ru.yandexpraktikum.myandroidchat.data.repository.ChatRepositoryImpl
import ru.yandexpraktikum.myandroidchat.data.repository.FakeChatRepositoryImpl
import ru.yandexpraktikum.myandroidchat.data.websocket.ChatWebSocketClient
import ru.yandexpraktikum.myandroidchat.data.websocket.ChatWebSocketListener
import ru.yandexpraktikum.myandroidchat.domain.repository.ChatRepository
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface AppModule {

//    //TODO: Раскомментируйте, чтобы протестировать интерфейс
//    // приложения без обращения к реальному серверу на тестовых данных
//    // (необходимо закомментировать метод bindChatRepository
//    @Binds
//    @Singleton
//    fun bindFakeChatRepository(
//        chatRepositoryImpl: FakeChatRepositoryImpl
//    ): ChatRepository

    @Binds
    @Singleton
    fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    companion object {
        @Provides
        @Singleton
        fun provideOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .pingInterval(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }

        @Provides
        @Singleton
        fun provideChatWebSocketListener(): ChatWebSocketListener {
            return ChatWebSocketListener()
        }

        @Provides
        @Singleton
        fun provideChatDatabase(
            @ApplicationContext context: Context
        ): ChatDatabase {
            return Room.databaseBuilder(
                context,
                ChatDatabase::class.java,
                "chat_database"
            ).build()
        }

        @Provides
        @Singleton
        fun provideMessageDao(database: ChatDatabase): MessageDao {
            return database.messageDao()
        }
    }
} 