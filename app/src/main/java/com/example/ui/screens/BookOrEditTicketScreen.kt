package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.StationCatalog
import com.example.data.TicketCategory
import com.example.data.TicketEntity
import com.example.data.TicketStatus
import com.example.ui.theme.CancelledRed
import com.example.ui.theme.CompletedGreen
import com.example.ui.theme.DividerLight
import com.example.ui.theme.PageBackground
import com.example.ui.theme.RailBlue
import com.example.ui.theme.RailNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.UserProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookOrEditTicketScreen(
  editTicket: TicketEntity?,
  initialCategory: TicketCategory,
  prefillSource: String,
  prefillDestination: String,
  prefillAdults: Int,
  prefillChildren: Int,
  prefillTrainType: String,
  prefillTravelClass: String,
  userProfile: UserProfile,
  onBack: () -> Unit,
  onConfirmBook: (
    category: TicketCategory,
    source: String,
    destination: String,
    via: String,
    distanceKm: Int,
    adults: Int,
    children: Int,
    ticketType: String,
    trainType: String,
    travelClass: String,
    fare: Double,
    passengerName: String,
    mobileNumber: String,
  ) -> Unit,
  onConfirmUpdate: (TicketEntity) -> Unit,
  onDeleteTicket: (Long) -> Unit,
) {
  val isEditMode = editTicket != null

  var category by
    remember(editTicket?.id) {
      mutableStateOf(
        editTicket?.category?.let { runCatching { TicketCategory.valueOf(it) }.getOrNull() }
          ?: initialCategory
      )
    }
  var sourceStation by
    remember(editTicket?.id) {
      mutableStateOf(
        editTicket?.sourceStation ?: prefillSource.ifEmpty { "WARDHA JN." }
      )
    }
  var destinationStation by
    remember(editTicket?.id) {
      mutableStateOf(
        editTicket?.destinationStation ?: prefillDestination.ifEmpty { "PULGAON JN." }
      )
    }
  var viaRoute by remember(editTicket?.id) { mutableStateOf(editTicket?.viaRoute ?: "---") }
  var ticketType by
    remember(editTicket?.id) { mutableStateOf(editTicket?.ticketType ?: "JOURNEY") }
  var trainType by
    remember(editTicket?.id) { mutableStateOf(editTicket?.trainType ?: prefillTrainType) }
  var travelClass by
    remember(editTicket?.id) { mutableStateOf(editTicket?.travelClass ?: prefillTravelClass) }
  var adults by remember(editTicket?.id) { mutableIntStateOf(editTicket?.adults ?: prefillAdults) }
  var children by
    remember(editTicket?.id) { mutableIntStateOf(editTicket?.children ?: prefillChildren) }
  var status by
    remember(editTicket?.id) {
      mutableStateOf(editTicket?.status ?: TicketStatus.UPCOMING.name)
    }
  var passengerName by
    remember(editTicket?.id) {
      mutableStateOf(editTicket?.passengerName ?: userProfile.name)
    }
  var mobileNumber by
    remember(editTicket?.id) {
      mutableStateOf(editTicket?.mobileNumber ?: userProfile.mobileNumber)
    }
  var bookingDateLabel by
    remember(editTicket?.id) { mutableStateOf(editTicket?.bookingDateLabel ?: "") }
  var bookingDateTimeFull by
    remember(editTicket?.id) { mutableStateOf(editTicket?.bookingDateTimeFull ?: "") }

  var manualOverride by remember(editTicket?.id) { mutableStateOf(isEditMode) }
  var distanceText by
    remember(editTicket?.id) {
      mutableStateOf(
        (editTicket?.distanceKm
          ?: StationCatalog.estimateDistanceKm(sourceStation, destinationStation, category))
          .toString()
      )
    }
  var fareText by
    remember(editTicket?.id) {
      val initialFare =
        editTicket?.fareAmount
          ?: StationCatalog.calculateFare(
            StationCatalog.estimateDistanceKm(sourceStation, destinationStation, category),
            adults,
            children,
            category,
            ticketType,
            trainType,
            travelClass,
          )
      mutableStateOf(String.format("%.2f", initialFare))
    }

  var pickingForSource by remember { mutableStateOf<Boolean?>(null) }
  var validationError by remember { mutableStateOf<String?>(null) }
  var showDeleteDialog by remember { mutableStateOf(false) }

  // Auto-recompute distance & fare whenever route/passengers change unless manual override is enabled
  val computedDistance =
    remember(sourceStation, destinationStation, category) {
      StationCatalog.estimateDistanceKm(sourceStation, destinationStation, category)
    }
  val effectiveDistance =
    if (manualOverride) distanceText.toIntOrNull()?.coerceAtLeast(0) ?: computedDistance
    else computedDistance
  val computedFare =
    remember(
      effectiveDistance,
      adults,
      children,
      category,
      ticketType,
      trainType,
      travelClass,
    ) {
      StationCatalog.calculateFare(
        effectiveDistance,
        adults,
        children,
        category,
        ticketType,
        trainType,
        travelClass,
      )
    }

  LaunchedEffect(computedDistance, computedFare, manualOverride) {
    if (!manualOverride) {
      distanceText = computedDistance.toString()
      fareText = String.format("%.2f", computedFare)
    }
  }

  var finalFare by remember { mutableDoubleStateOf(computedFare) }
  finalFare =
    if (manualOverride) fareText.toDoubleOrNull()?.coerceAtLeast(0.0) ?: computedFare
    else computedFare

  Scaffold(
    containerColor = PageBackground,
    topBar = {
      Surface(color = RailBlue, modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier =
            Modifier.fillMaxWidth()
              .windowInsetsPadding(WindowInsets.statusBars)
              .padding(horizontal = 16.dp, vertical = 12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              onClick = onBack,
              shape = CircleShape,
              color = Color.Transparent,
              border = BorderStroke(1.5.dp, Color.White),
              modifier = Modifier.size(40.dp).testTag("booking_form_back"),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = "Navigate back",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp),
                )
              }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Text(
              text =
                if (isEditMode) "Edit Ticket"
                else "Book ${category.displayLabel} Ticket",
              color = Color.White,
              fontSize = 19.sp,
              fontWeight = FontWeight.SemiBold,
            )
          }

          if (isEditMode && editTicket != null) {
            IconButton(
              onClick = { showDeleteDialog = true },
              modifier = Modifier.size(48.dp).testTag("edit_delete_ticket_button"),
            ) {
              Icon(
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = "Delete ticket",
                tint = Color.White,
              )
            }
          }
        }
      }
    },
  ) { innerPadding ->
    Column(
      modifier =
        Modifier.fillMaxSize()
          .padding(innerPadding)
          .windowInsetsPadding(WindowInsets.navigationBars)
          .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 600.dp).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
      ) {
        // Ticket Category Selector
        Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
          Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
              text = "Ticket Category",
              color = RailNavy,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              TicketCategory.entries.forEach { cat ->
                FilterChip(
                  selected = category == cat,
                  onClick = { category = cat },
                  label = { Text(cat.displayLabel, fontSize = 13.sp) },
                  colors =
                    FilterChipDefaults.filterChipColors(
                      selectedContainerColor = RailBlue,
                      selectedLabelColor = Color.White,
                    ),
                  modifier = Modifier.testTag("category_chip_${cat.name.lowercase()}"),
                )
              }
            }
          }
        }

        // Station Selection Card
        Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
          Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(
              text = if (category == TicketCategory.PLATFORM) "Station" else "Route Stations",
              color = RailNavy,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))

            StationSelectorBox(
              label = if (category == TicketCategory.PLATFORM) "Platform Station" else "Source Station",
              stationValue = sourceStation,
              onClick = { pickingForSource = true },
              testTag = "source_station_selector",
            )

            if (category != TicketCategory.PLATFORM) {
              Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                  onClick = {
                    val tmp = sourceStation
                    sourceStation = destinationStation
                    destinationStation = tmp
                  },
                  shape = CircleShape,
                  color = Color(0xFFEAF2FF),
                  modifier = Modifier.padding(vertical = 6.dp).size(42.dp).testTag("swap_stations_button"),
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Filled.SwapVert,
                      contentDescription = "Swap source and destination stations",
                      tint = RailBlue,
                    )
                  }
                }
              }

              StationSelectorBox(
                label = "Destination Station",
                stationValue = destinationStation,
                onClick = { pickingForSource = false },
                testTag = "destination_station_selector",
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
              value = viaRoute,
              onValueChange = { viaRoute = it },
              label = { Text("Via Route (optional)") },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("via_route_input"),
            )
          }
        }

        // Journey Options (Ticket Type, Train Type, Class)
        Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            if (category != TicketCategory.PLATFORM) {
              ChipOptionGroup(
                title = "Ticket Type",
                options = listOf("JOURNEY", "RETURN", "SEASON"),
                selected = ticketType,
                onSelect = { ticketType = it },
              )
              ChipOptionGroup(
                title = "Train Types",
                options = listOf("ORDINARY", "MAIL/EXP", "SUPERFAST"),
                selected = trainType,
                onSelect = { trainType = it },
              )
              ChipOptionGroup(
                title = "Class",
                options = listOf("SECOND", "SLEEPER", "FIRST", "AC CHAIR"),
                selected = travelClass,
                onSelect = { travelClass = it },
              )
            }

            HorizontalDivider(color = DividerLight)

            // Adult & Child Steppers
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              PassengerStepper(
                label = "Adults",
                count = adults,
                minCount = 1,
                onCountChange = { adults = it },
                testTagPrefix = "adults",
              )
              PassengerStepper(
                label = "Children",
                count = children,
                minCount = 0,
                onCountChange = { children = it },
                testTagPrefix = "children",
              )
            }
          }
        }

        // Edit-specific fields: Status, Date/Time, Passenger Name & Mobile, Custom Fare/Distance
        Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
          Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Column(modifier = Modifier.weight(1f)) {
                Text(
                  text = "Custom Distance & Fare",
                  color = TextPrimary,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.SemiBold,
                )
                Text(
                  text = "Auto-calculates unless toggled on",
                  color = TextSecondary,
                  fontSize = 12.sp,
                )
              }
              Switch(
                checked = manualOverride,
                onCheckedChange = { manualOverride = it },
                modifier = Modifier.testTag("manual_override_switch"),
              )
            }

            if (manualOverride) {
              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                  value = distanceText,
                  onValueChange = { distanceText = it.filter(Char::isDigit) },
                  label = { Text("Distance (km)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  singleLine = true,
                  modifier = Modifier.weight(1f).testTag("distance_input"),
                )
                OutlinedTextField(
                  value = fareText,
                  onValueChange = { fareText = it },
                  label = { Text("Fare (₹)") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                  singleLine = true,
                  modifier = Modifier.weight(1f).testTag("fare_input"),
                )
              }
            }

            if (isEditMode) {
              ChipOptionGroup(
                title = "Ticket Status",
                options = TicketStatus.entries.map { it.name },
                selected = status,
                onSelect = { status = it },
              )
              Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                  value = bookingDateLabel,
                  onValueChange = { bookingDateLabel = it },
                  label = { Text("Short Date") },
                  singleLine = true,
                  modifier = Modifier.weight(1f).testTag("short_date_input"),
                )
                OutlinedTextField(
                  value = bookingDateTimeFull,
                  onValueChange = { bookingDateTimeFull = it },
                  label = { Text("Full Date & Time") },
                  singleLine = true,
                  modifier = Modifier.weight(1f).testTag("full_date_input"),
                )
              }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              OutlinedTextField(
                value = passengerName,
                onValueChange = { passengerName = it },
                label = { Text("Passenger Name") },
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("passenger_name_input"),
              )
              OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it.filter(Char::isDigit).take(10) },
                label = { Text("Mobile") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.weight(1f).testTag("mobile_input"),
              )
            }
          }
        }

        // Fare Summary Banner
        Surface(
          color = Color(0xFFEAF2FF),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth(),
        ) {
          Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text("Total Fare", color = TextSecondary, fontSize = 12.sp)
              Text(
                text = StationCatalog.formatRupees(finalFare),
                color = RailNavy,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
              )
            }
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = if (category == TicketCategory.PLATFORM) "Platform Ticket" else "$effectiveDistance km",
                color = RailBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
              )
              Text(
                text = "$adults Adult, $children Child",
                color = TextSecondary,
                fontSize = 13.sp,
              )
            }
          }
        }

        validationError?.let { err ->
          Text(
            text = err,
            color = CancelledRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
          )
        }

        Button(
          onClick = {
            val s = sourceStation.trim().uppercase()
            val d = destinationStation.trim().uppercase()
            if (s.isEmpty()) {
              validationError = "Please choose a source station."
              return@Button
            }
            if (category != TicketCategory.PLATFORM && (d.isEmpty() || d == s)) {
              validationError = "Destination must be different from source station."
              return@Button
            }
            validationError = null

            if (isEditMode && editTicket != null) {
              onConfirmUpdate(
                editTicket.copy(
                  category = category.name,
                  sourceStation = s,
                  destinationStation = if (category == TicketCategory.PLATFORM) "PLATFORM" else d,
                  viaRoute = viaRoute.trim().ifEmpty { "---" },
                  distanceKm = if (category == TicketCategory.PLATFORM) 0 else effectiveDistance,
                  adults = adults,
                  children = children,
                  ticketType = if (category == TicketCategory.PLATFORM) "PLATFORM" else ticketType,
                  trainType = trainType,
                  travelClass = travelClass,
                  fareAmount = finalFare,
                  status = status,
                  passengerName = passengerName.trim().ifEmpty { userProfile.name },
                  mobileNumber = mobileNumber.trim().ifEmpty { userProfile.mobileNumber },
                  bookingDateLabel =
                    bookingDateLabel.trim().ifEmpty { editTicket.bookingDateLabel },
                  bookingDateTimeFull =
                    bookingDateTimeFull.trim().ifEmpty { editTicket.bookingDateTimeFull },
                )
              )
            } else {
              onConfirmBook(
                category,
                s,
                d,
                viaRoute,
                if (category == TicketCategory.PLATFORM) 0 else effectiveDistance,
                adults,
                children,
                ticketType,
                trainType,
                travelClass,
                finalFare,
                passengerName,
                mobileNumber,
              )
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = if (isEditMode) CompletedGreen else RailBlue),
          shape = RoundedCornerShape(50),
          modifier = Modifier.fillMaxWidth().height(52.dp).testTag("confirm_ticket_button"),
        ) {
          Text(
            text = if (isEditMode) "Save Ticket Changes" else "Confirm & Book Ticket",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }

  pickingForSource?.let { isSource ->
    StationPickerSheet(
      title = if (isSource) "Select Source Station" else "Select Destination Station",
      currentStation = if (isSource) sourceStation else destinationStation,
      onSelect = { selected ->
        if (isSource) sourceStation = selected else destinationStation = selected
        pickingForSource = null
      },
      onDismiss = { pickingForSource = null },
    )
  }

  if (showDeleteDialog && editTicket != null) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("Delete Ticket?", fontWeight = FontWeight.Bold) },
      text = {
        Text("Remove UTS ticket ${editTicket.utsNumber} permanently from this device?")
      },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            onDeleteTicket(editTicket.id)
          }
        ) {
          Text("Delete", color = CancelledRed)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) { Text("Keep") }
      },
    )
  }
}

@Composable
private fun StationSelectorBox(
  label: String,
  stationValue: String,
  onClick: () -> Unit,
  testTag: String,
) {
  Surface(
    onClick = onClick,
    color = PageBackground,
    shape = RoundedCornerShape(12.dp),
    border = BorderStroke(1.dp, DividerLight),
    modifier = Modifier.fillMaxWidth().testTag(testTag),
  ) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp)) {
      Text(text = label, color = TextSecondary, fontSize = 12.sp)
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = stationValue,
        color = TextPrimary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
      )
    }
  }
}

@Composable
private fun ChipOptionGroup(
  title: String,
  options: List<String>,
  selected: String,
  onSelect: (String) -> Unit,
) {
  Column {
    Text(text = title, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    Spacer(modifier = Modifier.height(4.dp))
    Row(
      modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      options.forEach { option ->
        FilterChip(
          selected = selected == option,
          onClick = { onSelect(option) },
          label = { Text(option, fontSize = 12.sp) },
          colors =
            FilterChipDefaults.filterChipColors(
              selectedContainerColor = Color(0xFFEAF2FF),
              selectedLabelColor = RailBlue,
            ),
        )
      }
    }
  }
}

@Composable
private fun PassengerStepper(
  label: String,
  count: Int,
  minCount: Int,
  onCountChange: (Int) -> Unit,
  testTagPrefix: String,
) {
  Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Text(text = label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    Surface(
      shape = CircleShape,
      color = PageBackground,
      modifier =
        Modifier.size(40.dp)
          .clip(CircleShape)
          .clickable(enabled = count > minCount) { onCountChange(count - 1) }
          .testTag("${testTagPrefix}_minus"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.Remove, contentDescription = "Decrease $label", tint = RailBlue)
      }
    }
    Text(
      text = count.toString(),
      fontSize = 16.sp,
      fontWeight = FontWeight.Bold,
      color = TextPrimary,
    )
    Surface(
      shape = CircleShape,
      color = PageBackground,
      modifier =
        Modifier.size(40.dp)
          .clip(CircleShape)
          .clickable(enabled = count < 9) { onCountChange(count + 1) }
          .testTag("${testTagPrefix}_plus"),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(Icons.Filled.Add, contentDescription = "Increase $label", tint = RailBlue)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StationPickerSheet(
  title: String,
  currentStation: String,
  onSelect: (String) -> Unit,
  onDismiss: () -> Unit,
) {
  var query by remember { mutableStateOf("") }
  val filtered =
    remember(query) {
      val q = query.trim().uppercase()
      if (q.isEmpty()) StationCatalog.stations
      else StationCatalog.stations.filter { it.displayUpper.contains(q) || it.code.contains(q) }
    }

  ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(horizontal = 20.dp)
          .padding(bottom = 28.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
      OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        label = { Text("Search or type custom station name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth().testTag("station_search_input"),
      )

      if (query.trim().isNotEmpty() && filtered.none { it.displayUpper == query.trim().uppercase() }) {
        OutlinedButton(
          onClick = { onSelect(query.trim().uppercase()) },
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text("Use custom station \"${query.trim().uppercase()}\"")
        }
      }

      Column(
        modifier = Modifier.height(280.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        filtered.forEach { station ->
          val selected = station.displayUpper == currentStation
          Surface(
            onClick = { onSelect(station.displayUpper) },
            color = if (selected) Color(0xFFEAF2FF) else PageBackground,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = station.displayUpper,
                color = if (selected) RailBlue else TextPrimary,
                fontWeight = FontWeight.SemiBold,
              )
              Text(
                text = station.code,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
              )
            }
          }
        }
      }
    }
  }
}
