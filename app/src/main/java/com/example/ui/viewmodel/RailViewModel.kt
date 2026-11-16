package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.StationCatalog
import com.example.data.TicketCategory
import com.example.data.TicketEntity
import com.example.data.TicketRepository
import com.example.data.TicketStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class BookingFilter(val label: String) {
  UPCOMING("Upcoming"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled"),
  ALL("All"),
}

data class UserProfile(
  val name: String = "tushar wasnik",
  val mobileNumber: String = "9405937428",
  val isHindi: Boolean = false,
)

data class BookingsUiState(
  val allTickets: List<TicketEntity> = emptyList(),
  val upcomingTickets: List<TicketEntity> = emptyList(),
  val completedTickets: List<TicketEntity> = emptyList(),
  val cancelledTickets: List<TicketEntity> = emptyList(),
  val selectedFilter: BookingFilter = BookingFilter.ALL,
  val sortNewestFirst: Boolean = true,
  val isRefreshing: Boolean = false,
)

class RailViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: TicketRepository

  private val _selectedFilter = MutableStateFlow(BookingFilter.ALL)
  private val _sortNewestFirst = MutableStateFlow(true)
  private val _isRefreshing = MutableStateFlow(false)
  private val _userProfile = MutableStateFlow(UserProfile())
  val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

  private val _statusMessage = MutableStateFlow<String?>(null)
  val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

  val allTicketsFlow: StateFlow<List<TicketEntity>>

  val bookingsUiState: StateFlow<BookingsUiState>

  init {
    val dao = AppDatabase.getDatabase(application).ticketDao()
    repository = TicketRepository(dao)

    viewModelScope.launch { repository.ensureSeeded() }

    allTicketsFlow =
      repository.allTickets.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
      )

    bookingsUiState =
      combine(repository.allTickets, _selectedFilter, _sortNewestFirst, _isRefreshing) {
          tickets,
          filter,
          newestFirst,
          refreshing ->
          val sorted =
            if (newestFirst) tickets.sortedByDescending { it.bookingTimestampMillis }
            else tickets.sortedBy { it.bookingTimestampMillis }

          BookingsUiState(
            allTickets = sorted,
            upcomingTickets = sorted.filter { it.status == TicketStatus.UPCOMING.name },
            completedTickets = sorted.filter { it.status == TicketStatus.COMPLETED.name },
            cancelledTickets = sorted.filter { it.status == TicketStatus.CANCELLED.name },
            selectedFilter = filter,
            sortNewestFirst = newestFirst,
            isRefreshing = refreshing,
          )
        }
        .stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5000),
          initialValue = BookingsUiState(),
        )
  }

  fun observeTicket(id: Long): Flow<TicketEntity?> = repository.getTicketById(id)

  suspend fun getTicketOnce(id: Long): TicketEntity? = repository.getTicketByIdOnce(id)

  fun setFilter(filter: BookingFilter) {
    _selectedFilter.value = filter
  }

  fun toggleSortOrder() {
    _sortNewestFirst.value = !_sortNewestFirst.value
    _statusMessage.value =
      if (_sortNewestFirst.value) "Sorted: Newest first" else "Sorted: Oldest first"
  }

  fun refreshBookings() {
    viewModelScope.launch {
      _isRefreshing.value = true
      repository.ensureSeeded()
      _isRefreshing.value = false
      _statusMessage.value = "Bookings synced locally"
    }
  }

  fun toggleLanguage() {
    val current = _userProfile.value
    _userProfile.value = current.copy(isHindi = !current.isHindi)
  }

  fun updateProfile(name: String, mobileNumber: String) {
    val cleanName = name.trim().ifEmpty { "tushar wasnik" }
    val cleanMobile = mobileNumber.trim().ifEmpty { "9405937428" }
    _userProfile.value = _userProfile.value.copy(name = cleanName, mobileNumber = cleanMobile)
    _statusMessage.value = "Passenger profile updated"
  }

  fun clearStatusMessage() {
    _statusMessage.value = null
  }

  fun bookNewTicket(
    category: TicketCategory,
    sourceStation: String,
    destinationStation: String,
    viaRoute: String,
    distanceKm: Int,
    adults: Int,
    children: Int,
    ticketType: String,
    trainType: String,
    travelClass: String,
    fareAmount: Double,
    passengerName: String,
    mobileNumber: String,
    onBooked: (Long) -> Unit,
  ) {
    viewModelScope.launch {
      val now = System.currentTimeMillis()
      val finalDest =
        if (category == TicketCategory.PLATFORM) "PLATFORM" else destinationStation.trim().uppercase()
      val newTicket =
        TicketEntity(
          utsNumber = StationCatalog.generateUtsCode(now),
          category = category.name,
          ticketType = if (category == TicketCategory.PLATFORM) "PLATFORM" else ticketType,
          trainType = trainType,
          travelClass = travelClass,
          sourceStation = sourceStation.trim().uppercase(),
          destinationStation = finalDest,
          viaRoute = viaRoute.trim().ifEmpty { "---" },
          distanceKm = distanceKm,
          adults = adults,
          children = children,
          fareAmount = fareAmount,
          bookingTimestampMillis = now,
          bookingDateLabel = StationCatalog.formatShortDate(now),
          bookingDateTimeFull = StationCatalog.formatFullDateTime(now),
          securityCode = StationCatalog.generateSecurityCode(now),
          irCode = "IR:27AAAGM0289C2ZI",
          mobileNumber = mobileNumber.trim().ifEmpty { _userProfile.value.mobileNumber },
          passengerName = passengerName.trim().ifEmpty { _userProfile.value.name },
          status = TicketStatus.UPCOMING.name,
        )
      val id = repository.insertTicket(newTicket)
      _statusMessage.value = "Ticket ${newTicket.utsNumber} booked"
      onBooked(id)
    }
  }

  fun updateExistingTicket(updated: TicketEntity, onUpdated: () -> Unit) {
    viewModelScope.launch {
      repository.updateTicket(updated)
      _statusMessage.value = "Ticket ${updated.utsNumber} updated"
      onUpdated()
    }
  }

  fun cancelTicket(ticket: TicketEntity) {
    viewModelScope.launch {
      repository.updateTicket(ticket.copy(status = TicketStatus.CANCELLED.name))
      _statusMessage.value = "Ticket ${ticket.utsNumber} cancelled & refund initiated"
    }
  }

  fun deleteTicket(id: Long, onDeleted: () -> Unit = {}) {
    viewModelScope.launch {
      repository.deleteTicket(id)
      _statusMessage.value = "Ticket removed from device"
      onDeleted()
    }
  }
}
