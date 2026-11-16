package com.example.data

import kotlinx.coroutines.flow.Flow

class TicketRepository(private val ticketDao: TicketDao) {
  val allTickets: Flow<List<TicketEntity>> = ticketDao.getAllTickets()

  fun getTicketById(id: Long): Flow<TicketEntity?> = ticketDao.getTicketById(id)

  suspend fun getTicketByIdOnce(id: Long): TicketEntity? = ticketDao.getTicketByIdOnce(id)

  suspend fun insertTicket(ticket: TicketEntity): Long = ticketDao.insertTicket(ticket)

  suspend fun updateTicket(ticket: TicketEntity) = ticketDao.updateTicket(ticket)

  suspend fun deleteTicket(id: Long) = ticketDao.deleteTicketById(id)

  suspend fun ensureSeeded() {
    if (ticketDao.countTickets() == 0) {
      ticketDao.insertAll(defaultScreenshotTickets())
    }
  }

  private fun defaultScreenshotTickets(): List<TicketEntity> =
    listOf(
      TicketEntity(
        utsNumber = "XYGDEHJ01C",
        category = TicketCategory.UNRESERVED.name,
        ticketType = "JOURNEY",
        trainType = "ORDINARY",
        travelClass = "SECOND",
        sourceStation = "WARDHA JN.",
        destinationStation = "PULGAON JN.",
        viaRoute = "---",
        distanceKm = 30,
        adults = 1,
        children = 0,
        fareAmount = 10.00,
        bookingTimestampMillis = 1793075100000L, // Tue, 27 Oct 26, 09:55
        bookingDateLabel = "Tue, 27 Oct 26",
        bookingDateTimeFull = "27 Oct 2026, 09:55",
        securityCode = "R18103",
        irCode = "IR:27AAAGM0289C2ZI",
        mobileNumber = "9405937428",
        passengerName = "tushar wasnik",
        status = TicketStatus.UPCOMING.name,
      ),
      TicketEntity(
        utsNumber = "XYGEEGJ04D",
        category = TicketCategory.UNRESERVED.name,
        ticketType = "JOURNEY",
        trainType = "ORDINARY",
        travelClass = "SECOND",
        sourceStation = "WARDHA JN.",
        destinationStation = "NAGPUR",
        viaRoute = "---",
        distanceKm = 79,
        adults = 1,
        children = 0,
        fareAmount = 20.00,
        bookingTimestampMillis = 1789974900000L, // Mon, 21 Sep 26
        bookingDateLabel = "Mon, 21 Sep 26",
        bookingDateTimeFull = "21 Sep 2026, 08:15",
        securityCode = "R14209",
        irCode = "IR:27AAAGM0289C2ZI",
        mobileNumber = "9405937428",
        passengerName = "tushar wasnik",
        status = TicketStatus.COMPLETED.name,
      ),
      TicketEntity(
        utsNumber = "X76CEGV003",
        category = TicketCategory.UNRESERVED.name,
        ticketType = "JOURNEY",
        trainType = "ORDINARY",
        travelClass = "SECOND",
        sourceStation = "HINGANGHAT",
        destinationStation = "WARORA",
        viaRoute = "---",
        distanceKm = 38,
        adults = 1,
        children = 0,
        fareAmount = 15.00,
        bookingTimestampMillis = 1791021600000L, // Sat, 3 Oct 26
        bookingDateLabel = "Sat, 3 Oct 26",
        bookingDateTimeFull = "03 Oct 2026, 14:20",
        securityCode = "R16488",
        irCode = "IR:27AAAGM0289C2ZI",
        mobileNumber = "9405937428",
        passengerName = "tushar wasnik",
        status = TicketStatus.COMPLETED.name,
      ),
      TicketEntity(
        utsNumber = "XYGEEGZ026",
        category = TicketCategory.UNRESERVED.name,
        ticketType = "JOURNEY",
        trainType = "ORDINARY",
        travelClass = "SECOND",
        sourceStation = "PULGAON JN.",
        destinationStation = "WARDHA JN.",
        viaRoute = "---",
        distanceKm = 30,
        adults = 1,
        children = 0,
        fareAmount = 10.00,
        bookingTimestampMillis = 1792066800000L, // Thu, 15 Oct 26
        bookingDateLabel = "Thu, 15 Oct 26",
        bookingDateTimeFull = "15 Oct 2026, 17:40",
        securityCode = "R17310",
        irCode = "IR:27AAAGM0289C2ZI",
        mobileNumber = "9405937428",
        passengerName = "tushar wasnik",
        status = TicketStatus.COMPLETED.name,
      ),
    )
}
