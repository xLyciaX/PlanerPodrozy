# Etap 2: Room, lista podróży, tworzenie podróży

Gałąź: `feat/room-and-trip-list` (utworzona z `main` po scaleniu `fix/trip-list-fab`).

| Commit | Opis |
|---|---|
| `10b4671` | `feat(data): add domain models, trip day generation and cover rule` |
| `159cd57` | `feat(data): add Room entities, DAOs and database` |
| `cb71e5f` | `feat(data): add trip repository and manual dependency container` |
| `c998476` | `refactor(nav): rename navigation route classes to destinations` |
| `398f505` | `feat(trips): show saved trips on the list with date sorting` |
| `9b00520` | `feat(trips): add new trip form with date range picker` |

Skrót `…/planerpodrozy/` oznacza `app/src/main/java/pl/wieczorek/planerpodrozy/`.
Numery linii dotyczą stanu po commicie `9b00520`.

---

## 1. Warstwa domeny (`…/planerpodrozy/domain/`)

### `model/Trip.kt` (nowy)
- l. 8 `data class Trip(…)`: podróż (id, nazwa, daty, ścieżki filmu i notatki, data utworzenia).
  l. 18 `dayCount`: liczba dni podróży razem z pierwszym i ostatnim.
- l. 23 `data class TripDay(…)`: dzień podróży (numer, data, opis, lista lokalizacji);
  w `companion object` stała `MaxDescriptionLength = 500`.
- l. 37 `data class Photo(…)`: zdjęcie z kategorią i znacznikiem czasu dodania `addedAt`.
- l. 46 `data class TripSummary(trip, coverPath)`: podróż z okładką do pokazania na liście.

### `TripDays.kt` (nowy)
- l. 6 `tripDates(from, to)`: lista kolejnych dat od początku do końca podróży (włącznie).
  Rzuca wyjątek, gdy koniec jest przed początkiem.

### `Covers.kt` (nowy)
- l. 11 `coverPhoto(photos)`: **jedyne miejsce z regułą okładek** (sekcja 6 CLAUDE.md):
  najwcześniej dodane zdjęcie wg `addedAt`, przy remisie mniejsze `id`; brak zdjęć daje `null`.

### Testy (`app/src/test/java/pl/wieczorek/planerpodrozy/domain/`)
- `TripDaysTest.kt` (nowy): 4 testy (l. 10, 18, 24, 31): 7 dni w lipcu, podróż jednodniowa,
  przejście przez koniec roku, odrzucenie odwróconego zakresu.
- `CoversTest.kt` (nowy): 4 testy (l. 21, 26, 34, 44): brak zdjęć, zdjęcie z dnia 3 dodane wcześniej
  wygrywa z dniem 1, po usunięciu okładki przejmuje ją kolejne zdjęcie, remis czasu.

---

## 2. Baza danych Room (`…/planerpodrozy/data/local/`)

### `Entities.kt` (nowy)
- l. 19–28 `TripEntity`: tabela `trips`.
- l. 31–50 `TripDayEntity`: tabela `trip_days`, klucz obcy do `trips` z `CASCADE`
  (usunięcie podróży usuwa jej dni), indeks na `tripId`.
- l. 56–74 `PhotoEntity`: tabela `photos`, klucz obcy do `trip_days` z `CASCADE`,
  **unikalny indeks `(dayId, category)`**, czyli baza sama nie pozwoli na dwa zdjęcia tej samej kategorii w dniu.
- l. 77 `PhotoWithTripId`: wynik złączenia zdjęć z dniami (zdjęcie + id podróży).
- l. 82, 92, 101: `toDomain()`, czyli zamiana encji Room na modele z `domain/model`.

### `Converters.kt` (nowy)
- l. 9 `class Converters`: `LocalDate` ↔ liczba dni (l. 11, 14), `Instant` ↔ milisekundy (l. 17, 20),
  `List<String>` ↔ tekst JSON (l. 24, 27; lokalizacje dnia).

### `TripDao.kt`, `DayDao.kt`, `PhotoDao.kt` (nowe)
- `TripDao` l. 11 `insert(trip)`, l. 14 `observeAll()`: strumień wszystkich podróży.
- `DayDao` l. 9 `insertAll(days)`.
- `PhotoDao` l. 10–17 `observeAllWithTripId()`: zapytanie `SELECT photos.*, trip_days.tripId … INNER JOIN …`.

### `AppDatabase.kt` (nowy)
- l. 17 `abstract class AppDatabase`: trzy tabele, wersja 1, `exportSchema = false`;
  `companion object build(context)` tworzy plik bazy `planer_podrozy.db`.

---

## 3. Repozytorium i wstrzykiwanie zależności

### `…/planerpodrozy/data/repository/TripRepository.kt` (nowy)
- l. 8 `interface TripRepository`: l. 10 `observeTrips()`, l. 13 `createTrip(name, startDate, endDate)`.

### `…/planerpodrozy/data/repository/RoomTripRepository.kt` (nowy)
- l. 16 `class RoomTripRepository(database)`.
- l. 22 `observeTrips()`: łączy (`combine`) strumień podróży ze strumieniem zdjęć, grupuje zdjęcia
  po podróży i wybiera okładkę funkcją `coverPhoto` z `Covers.kt`.
- l. 34 `createTrip(…)`: w transakcji (`withTransaction`, l. 35) zapisuje podróż, a potem jej dni
  wygenerowane przez `tripDates` z numerami 1, 2, 3…

### `…/planerpodrozy/AppContainer.kt` (nowy)
- l. 12 `class AppContainer(context)`: tworzy bazę i `tripRepository` (l. 15).

### `…/planerpodrozy/PlanerApp.kt` (nowy)
- l. 6 `class PlanerApp : Application`: w `onCreate` (l. 10) tworzy `AppContainer`.

### `app/src/main/AndroidManifest.xml` (zmieniony)
- l. 6: `android:name=".PlanerApp"` w znaczniku `<application>`.

### `…/planerpodrozy/ui/ViewModelFactory.kt` (nowy)
- l. 15 `planerViewModel { … }`: tworzy ViewModel ekranu i podaje mu zależności z `AppContainer`.

---

## 4. Nawigacja (`…/planerpodrozy/ui/navigation/`)

### `Routes.kt` → `Destinations.kt` (zmiana nazwy pliku i klas, commit `c998476`)
- Klasy tras `TripListRoute`, `NewTripRoute`, … przemianowane na `TripListDestination`, `NewTripDestination`,
  `TripDetailDestination`, `DayDestination`, `DayEditDestination`, `GalleryDestination` (l. 10–25).
  Powód: CLAUDE.md wymaga funkcji `XxxRoute` (ekran z ViewModelem), a jednakowe nazwy by się gryzły.

### `PlanerNavHost.kt` (zmieniony)
- Lista (`composable<TripListDestination>`) wywołuje teraz `TripListRoute(onAddTrip, onOpenTrip)`.
  Usunięty tymczasowy `rememberSaveable` z kierunkiem sortowania i przycisk „Widok podróży”.
- l. 27 `composable<NewTripDestination>`: zamiast szkieletu `NewTripRoute(onClose = goBack)`.

---

## 5. Ekran 01: lista podróży (`…/planerpodrozy/ui/screens/triplist/`)

### `TripListUiState.kt` (nowy)
- l. 5 `enum class SortOrder` (przeniesiony z `TripListScreen.kt`).
- l. 8 `TripListUiState(trips, sortOrder, isLoading)`; `isLoading` zapobiega mignięciu pustego stanu przy starcie.

### `TripListViewModel.kt` (nowy)
- l. 19 `uiState`: `combine` podróży z repozytorium i wybranego sortowania, potem `stateIn`.
- l. 28 `onSortChange(order)`.
- l. 32 `sortedBy(order)`: sortowanie po dacie rozpoczęcia (przy remisie po dacie utworzenia), rosnąco lub malejąco.

### `TripListScreen.kt` (przebudowany)
- l. 53 `TripListRoute(…)`: pobiera ViewModel, obserwuje stan (`collectAsStateWithLifecycle`), przekazuje akcje.
- l. 69 `TripListScreen(state, …)`: bezstanowy ekran. FAB „+” bez zmian (l. 79). W l. 115–118 wybór:
  nic w trakcie ładowania, pusty stan albo lista.
- l. 125 `TripList(…)`: `LazyColumn` z kartami `TripCard`, termin z `DateFormatter.range`,
  dolny odstęp `Dimens.FabClearance`, żeby FAB nie zasłaniał ostatniej karty.
- Usunięte: parametr `onOpenSampleTrip` i tymczasowy przycisk do widoku podróży.
- l. 244 i 263: dwa podglądy (lista z dwiema podróżami i pusty stan).

### `…/planerpodrozy/ui/theme/Dimens.kt` (zmieniony)
- l. 27 `FabClearance = 88.dp`.

---

## 6. Ekrany 02/03: nowa podróż (`…/planerpodrozy/ui/screens/newtrip/`)

### `NewTripUiState.kt` (nowy)
- l. 5 `NewTripUiState(name, startDate, endDate, isDatePickerOpen, isSaving, isSaved)`.
- l. 15 `canSave`: „Zapisz” aktywne tylko z nazwą i obiema datami.

### `NewTripViewModel.kt` (nowy)
- l. 18–32: zmiana nazwy, otwarcie i zamknięcie kalendarza, wybór dat.
- l. 34 `onSave()`: zapis przez `repository.createTrip` (l. 41), potem `isSaved = true`.

### `NewTripScreen.kt` (nowy)
- l. 38 `NewTripRoute(onClose)`: `LaunchedEffect(state.isSaved)` (l. 43) wraca do listy po zapisie.
- l. 59 `NewTripScreen(…)`: pasek „Nowa podróż”, nagłówek „Dokąd tym razem?”, pole „Cel podróży”,
  pola Od/Do (`DateField`), przyciski `CancelSaveButtons`; gdy `isDatePickerOpen`, pokazuje `TripDateRangeDialog`.

### `TripDateRangeDialog.kt` (nowy)
- l. 38 `TripDateRangeDialog(…)`: `DatePickerDialog` z przyciskami „Anuluj” / „OK” (OK aktywne po wyborze obu dat).
- l. 78: `CompositionLocalProvider` z polską konfiguracją **wewnątrz** okna. Tydzień zaczyna się od
  poniedziałku, a nazwy miesięcy i dni są po polsku nawet na telefonie po angielsku.
  (Okno dialogowe ma własną konfigurację, dlatego podmiana na zewnątrz nie działała.)
- l. 86 `DateRangePickerContent(…)`: `DateRangePicker` bez przełącznika trybu, z własnym tytułem
  „Wybierz daty podróży” i nagłówkiem „12.07.2026 – 18.07.2026”.
- l. 121 `tripDatePickerColors()`: kolory z makiety (liliowe tło, końce zakresu w primary, środek jasnoliliowy).
- l. 136, 138: zamiana `LocalDate` ↔ milisekundy UTC, bo na nich działa kalendarz M3.

### `app/src/main/res/values/strings.xml` (zmieniony)
- l. 34–35: `new_trip_pick_dates_title` („Wybierz daty podróży”) i `new_trip_date_range` („%1$s – %2$s”).
