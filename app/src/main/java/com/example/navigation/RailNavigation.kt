package com.example.navigation

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.data.TicketCategory
import com.example.data.TicketEntity
import com.example.ui.components.BottomTab
import com.example.ui.screens.BookOrEditTicketScreen
import com.example.ui.screens.BookingDetailsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.MyBookingsScreen
import com.example.ui.screens.YouScreen
import com.example.ui.viewmodel.BookingFilter
import com.example.ui.viewmodel.RailViewModel
import kotlinx.serialization.Serializable

@Serializable object HomeRoute

@Serializable data class MyBookingsRoute(val initialFilter: String = "ALL")

@Serializable data class BookingDetailsRoute(val ticketId: Long)

@Serializable
data class BookOrEditTicketRoute(
  val category: String = TicketCategory.UNRESERVED.name,
  val editTicketId: Long = -1L,
  val prefillSource: String = "",
  val prefillDestination: String = "",
  val prefillAdults: Int = 1,
  val prefillChildren: Int = 0,
  val prefillTrainType: String = "ORDINARY",
  val prefillTravelClass: String = "SECOND",
)

@Serializable object YouRoute

@Serializable object MenuRoute

@Composable
fun RailApp(viewModel: RailViewModel = viewModel()) {
  val navController = rememberNavController()
  val context = LocalContext.current

  val profile by viewModel.userProfile.collectAsStateWithLifecycle()
  val allTickets by viewModel.allTicketsFlow.collectAsStateWithLifecycle()
  val bookingsState by viewModel.bookingsUiState.collectAsStateWithLifecycle()
  val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()

  LaunchedEffect(statusMessage) {
    statusMessage?.let { msg ->
      Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
      viewModel.clearStatusMessage()
    }
  }

  val navigateBottomTab: (BottomTab) -> Unit = { tab ->
    when (tab) {
      BottomTab.HOME -> {
        navController.navigate(HomeRoute) {
          popUpTo(HomeRoute) { inclusive = true }
          launchSingleTop = true
        }
      }
      BottomTab.MY_BOOKINGS -> {
        viewModel.setFilter(BookingFilter.ALL)
        navController.navigate(MyBookingsRoute("ALL")) { launchSingleTop = true }
      }
      BottomTab.YOU -> {
        navController.navigate(YouRoute) { launchSingleTop = true }
      }
      BottomTab.MENU -> {
        navController.navigate(MenuRoute) { launchSingleTop = true }
      }
    }
  }

  NavHost(navController = navController, startDestination = HomeRoute) {
    composable<HomeRoute> {
      HomeScreen(
        profile = profile,
        tickets = allTickets,
        onToggleLanguage = { viewModel.toggleLanguage() },
        onSelectCategory = { cat ->
          val defaultClass =
            if (cat == TicketCategory.RESERVED) "SLEEPER" else "SECOND"
          val defaultTrain =
            if (cat == TicketCategory.RESERVED) "MAIL/EXP" else "ORDINARY"
          navController.navigate(
            BookOrEditTicketRoute(
              category = cat.name,
              prefillTrainType = defaultTrain,
              prefillTravelClass = defaultClass,
            )
          )
        },
        onBookAgain = { ticket ->
          navController.navigate(
            BookOrEditTicketRoute(
              category = ticket.category,
              prefillSource = ticket.sourceStation,
              prefillDestination = ticket.destinationStation,
              prefillAdults = ticket.adults,
              prefillChildren = ticket.children,
              prefillTrainType = ticket.trainType,
              prefillTravelClass = ticket.travelClass,
            )
          )
        },
        onViewDetails = { id -> navController.navigate(BookingDetailsRoute(id)) },
        onBookRoute = { src, dst ->
          navController.navigate(
            BookOrEditTicketRoute(
              category = TicketCategory.UNRESERVED.name,
              prefillSource = src,
              prefillDestination = dst,
            )
          )
        },
        onCancelTicket = { ticket -> viewModel.cancelTicket(ticket) },
        onNavigateTab = navigateBottomTab,
      )
    }

    composable<MyBookingsRoute> { backStackEntry ->
      val route = backStackEntry.toRoute<MyBookingsRoute>()
      LaunchedEffect(route.initialFilter) {
        runCatching { BookingFilter.valueOf(route.initialFilter) }
          .getOrNull()
          ?.let { viewModel.setFilter(it) }
      }

      MyBookingsScreen(
        uiState = bookingsState,
        onBack = { navController.popBackStack() },
        onSelectFilter = { viewModel.setFilter(it) },
        onToggleSort = { viewModel.toggleSortOrder() },
        onRefresh = { viewModel.refreshBookings() },
        onBookAgain = { ticket ->
          navController.navigate(
            BookOrEditTicketRoute(
              category = ticket.category,
              prefillSource = ticket.sourceStation,
              prefillDestination = ticket.destinationStation,
              prefillAdults = ticket.adults,
              prefillChildren = ticket.children,
              prefillTrainType = ticket.trainType,
              prefillTravelClass = ticket.travelClass,
            )
          )
        },
        onViewDetails = { id -> navController.navigate(BookingDetailsRoute(id)) },
        onEditTicket = { id ->
          navController.navigate(BookOrEditTicketRoute(editTicketId = id))
        },
        onBookNewTicket = {
          navController.navigate(BookOrEditTicketRoute(category = TicketCategory.UNRESERVED.name))
        },
      )
    }

    composable<BookingDetailsRoute> { backStackEntry ->
      val route = backStackEntry.toRoute<BookingDetailsRoute>()
      val ticket by
        viewModel.observeTicket(route.ticketId).collectAsStateWithLifecycle(initialValue = null)

      BookingDetailsScreen(
        ticket = ticket,
        onBack = { navController.popBackStack() },
        onBookConnectingJourney = { currentTicket ->
          val nextSource =
            if (currentTicket.destinationStation == "PLATFORM") currentTicket.sourceStation
            else currentTicket.destinationStation
          val nextDest =
            if (nextSource == "PULGAON JN.") "BADNERA JN."
            else if (nextSource == "NAGPUR") "WARDHA JN."
            else "NAGPUR"
          navController.navigate(
            BookOrEditTicketRoute(
              category = TicketCategory.UNRESERVED.name,
              prefillSource = nextSource,
              prefillDestination = nextDest,
              prefillAdults = currentTicket.adults,
              prefillChildren = currentTicket.children,
            )
          )
        },
      )
    }

    composable<BookOrEditTicketRoute> { backStackEntry ->
      val route = backStackEntry.toRoute<BookOrEditTicketRoute>()
      val editTicketState by
        produceState<TicketEntity?>(initialValue = null, route.editTicketId) {
          value =
            if (route.editTicketId > 0L) viewModel.getTicketOnce(route.editTicketId) else null
        }

      if (route.editTicketId > 0L && editTicketState == null) {
        return@composable
      }

      val initialCat =
        runCatching { TicketCategory.valueOf(route.category) }
          .getOrDefault(TicketCategory.UNRESERVED)

      BookOrEditTicketScreen(
        editTicket = editTicketState,
        initialCategory = initialCat,
        prefillSource = route.prefillSource,
        prefillDestination = route.prefillDestination,
        prefillAdults = route.prefillAdults,
        prefillChildren = route.prefillChildren,
        prefillTrainType = route.prefillTrainType,
        prefillTravelClass = route.prefillTravelClass,
        userProfile = profile,
        onBack = { navController.popBackStack() },
        onConfirmBook = {
          category,
          source,
          destination,
          via,
          distanceKm,
          adults,
          children,
          ticketType,
          trainType,
          travelClass,
          fare,
          passengerName,
          mobileNumber ->
          viewModel.bookNewTicket(
            category = category,
            sourceStation = source,
            destinationStation = destination,
            viaRoute = via,
            distanceKm = distanceKm,
            adults = adults,
            children = children,
            ticketType = ticketType,
            trainType = trainType,
            travelClass = travelClass,
            fareAmount = fare,
            passengerName = passengerName,
            mobileNumber = mobileNumber,
            onBooked = { newId ->
              navController.navigate(BookingDetailsRoute(newId)) {
                popUpTo(HomeRoute) { inclusive = false }
              }
            },
          )
        },
        onConfirmUpdate = { updated ->
          viewModel.updateExistingTicket(updated) { navController.popBackStack() }
        },
        onDeleteTicket = { id ->
          viewModel.deleteTicket(id) {
            navController.navigate(MyBookingsRoute("ALL")) {
              popUpTo(HomeRoute) { inclusive = false }
            }
          }
        },
      )
    }

    composable<YouRoute> {
      YouScreen(
        profile = profile,
        tickets = allTickets,
        onSaveProfile = { name, mobile -> viewModel.updateProfile(name, mobile) },
        onOpenBookings = {
          viewModel.setFilter(BookingFilter.ALL)
          navController.navigate(MyBookingsRoute("ALL"))
        },
        onNavigateTab = navigateBottomTab,
      )
    }

    composable<MenuRoute> {
      MenuScreen(
        onBookCategory = { cat ->
          navController.navigate(BookOrEditTicketRoute(category = cat.name))
        },
        onOpenBookings = {
          viewModel.setFilter(BookingFilter.ALL)
          navController.navigate(MyBookingsRoute("ALL"))
        },
        onNavigateTab = navigateBottomTab,
      )
    }
  }
}
