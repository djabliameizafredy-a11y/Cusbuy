package com.example

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 0,
    val isRegistered: Boolean = false,
    val email: String = "djabliameizafredy@gmail.com",
    val nickname: String = "FredyD",
    val walletCoins: Int = 600,
    val walletCashUSD: Double = 50.0,
    val defaultMode: String = "Acheteur",
    val currentMode: String = "Acheteur",
    val sellerBusinessName: String = "",
    val sellerCity: String = "",
    val sellerLocation: String = "",
    val sellerGoogleAccount: String = "",
    val sellerCategory: String = "",
    val sellerPhone: String = "",
    val isSellerRegistered: Boolean = false
)

@Entity(tableName = "actions")
data class ActionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "BUY" or "SELL"
    val itemTitle: String,
    val description: String,
    val amountUSD: Double = 0.0,
    val amountCoins: Int = 0,
    val status: String,
    val timestamp: String,
    val trackingNumber: String? = null
)

@Dao
interface CusbuyDao {
    @Query("SELECT * FROM profile WHERE id = 0")
    fun getProfile(): Flow<ProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ProfileEntity)

    @Query("SELECT * FROM actions ORDER BY id DESC")
    fun getAllActions(): Flow<List<ActionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: ActionEntity)

    @Query("DELETE FROM actions")
    suspend fun clearActions()
}

@Database(entities = [ProfileEntity::class, ActionEntity::class], version = 1, exportSchema = false)
abstract class CusbuyDatabase : RoomDatabase() {
    abstract fun dao(): CusbuyDao

    companion object {
        @Volatile
        private var INSTANCE: CusbuyDatabase? = null

        fun getDatabase(context: Context): CusbuyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CusbuyDatabase::class.java,
                    "cusbuy_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
