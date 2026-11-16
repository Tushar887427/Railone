package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
  @Query("SELECT * FROM tickets ORDER BY bookingTimestampMillis DESC")
  fun getAllTickets(): Flow<List<TicketEntity>>

  @Query("SELECT * FROM tickets WHERE id = :id")
  fun getTicketById(id: Long): Flow<TicketEntity?>

  @Query("SELECT * FROM tickets WHERE id = :id LIMIT 1")
  suspend fun getTicketByIdOnce(id: Long): TicketEntity?

  @Query("SELECT COUNT(*) FROM tickets")
  suspend fun countTickets(): Int

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTicket(ticket: TicketEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(tickets: List<TicketEntity>)

  @Update
  suspend fun updateTicket(ticket: TicketEntity)

  @Query("DELETE FROM tickets WHERE id = :id")
  suspend fun deleteTicketById(id: Long)
}
