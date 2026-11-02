# Etap 3: widok podróży i widok dnia

Gałąź: `feat/trip-and-day-views` (utworzona z `main` po scaleniu etapu 2).

| Commit | Opis |
|---|---|
| `112981f` | `feat(data): add trip detail and day queries with covers` |
| `aac2a71` | `feat(trips): add trip view with banner and day tiles` |
| `96c0be3` | `fix(ui): draw dashed border above surface background` |
| `93ad4ab` | `feat(days): add day view with locations, photo slots and description` |

Skrót `…/planerpodrozy/` oznacza `app/src/main/java/pl/wieczorek/planerpodrozy/`.
Numery linii dotyczą stanu po commicie `93ad4ab`.

---

## 1. Modele domenowe

### `…/planerpodrozy/domain/model/TripDetails.kt` (nowy)
- l. 4 `DaySummary(day, coverPath, photoCount)`: dane kafelka dnia (okładka dnia + licznik n/3).
- l. 11 `TripDetail(trip, coverPath, days)`: wszystko dla widoku podróży (ekran 04).
- l. 18 `DayDetail(day, photos: Map<PhotoCategory, Photo>)`: dzień ze zdjęciami po kategoriach (ekran 05).

---

## 2. Zapytania Room (`…/planerpodrozy/data/local/`)

| Plik | Nowe zapytanie (linia) |
|---|---|
| `TripDao.kt` | l. 16–17 `observeById(tripId)`: jedna podróż (`Flow<TripEntity?>`) |
| `DayDao.kt` | l. 13–14 `observeByTrip(tripId)`: dni podróży posortowane po `dayNumber`; l. 16–17 `observeById(dayId)`: jeden dzień. Doszły importy `Query` i `Flow`. |
| `PhotoDao.kt` | l. 20–27 `observeByTrip(tripId)`: zdjęcia ze wszystkich dni podróży (`INNER JOIN trip_days`); l. 29–30 `observeByDay(dayId)`: zdjęcia jednego dnia |

---

## 3. Repozytoria (`…/planerpodrozy/data/repository/`)

### `TripRepository.kt` (zmieniony)
- l. 14 nowa metoda `observeTripDetail(tripId): Flow<TripDetail?>`.

### `RoomTripRepository.kt` (zmieniony)
- l. 35 `observeTripDetail(tripId)`: `combine` trzech strumieni (podróż, dni, zdjęcia podróży).
  l. 41: brak podróży daje `null`. Okładkę podróży i okładkę każdego dnia liczy ta sama funkcja
  `coverPhoto` z `domain/Covers.kt`; `photoCount` = liczba zdjęć dnia.

### `DayRepository.kt` (nowy)
- l. 7 `interface DayRepository`, l. 9 `observeDay(dayId): Flow<DayDetail?>`.
  Osobny, mały interfejs dla dnia (zasada I z SOLID); w etapach 4–5 dojdzie do niego dodawanie zdjęć i zapis dnia.

### `RoomDayRepository.kt` (nowy)
- l. 9 `class RoomDayRepository(database)`; l. 14 `observeDay`: `combine` dnia i jego zdjęć,
  zdjęcia zamienione na mapę `associateBy { it.category }`.

### `…/planerpodrozy/AppContainer.kt` (zmieniony)
- Nowe pole `dayRepository: DayRepository = RoomDayRepository(database)` (+ 2 importy).

---

## 4. Nawigacja

### `…/planerpodrozy/ui/navigation/PlanerNavHost.kt` (zmieniony)
- l. 32 `composable<TripDetailDestination>`: zamiast szkieletu `TripDetailRoute(tripId, onBack, onOpenGallery, onOpenDay)`;
  galeria → `GalleryDestination(tripId)`, kafelek dnia → `DayDestination(dayId)`.
- l. 41 `composable<DayDestination>`: zamiast szkieletu `DayRoute(dayId, onBack, onEdit)`; „Edytuj” → `DayEditDestination(dayId)`.
- `DayEditDestination` i `GalleryDestination` nadal pokazują tymczasowy `SkeletonScreen` (etapy 5 i 6).

---

## 5. Ekran 04: widok podróży (`…/planerpodrozy/ui/screens/tripdetail/`)

### `TripDetailUiState.kt` (nowy)
- l. 5 `TripDetailUiState(detail: TripDetail?, isLoading)`.

### `TripDetailViewModel.kt` (nowy)
- l. 11 `TripDetailViewModel(tripId, repository)`: `observeTripDetail(tripId)` → `map` na `UiState` → `stateIn`.
  `tripId` przychodzi przez konstruktor z trasy nawigacji.

### `TripDetailScreen.kt` (nowy)
- l. 52 `TripDetailRoute(…)`: tworzy ViewModel (`planerViewModel`) i przekazuje stan do ekranu.
- l. 70 `TripDetailScreen(…)`: przewijana kolumna (`verticalScroll`). Tytuł paska = nazwa podróży.
  l. 83: do czasu załadowania treść jest pusta.
  - `TripBanner` (l. 144): okładka 132 dp (`TripCoverImage`, walizka bez zdjęć), pod nią biały pasek
    z nazwą, ikoną kalendarza, terminem (`DateFormatter.range`) i liliową `DurationPill`.
  - l. 92 `Button` „Zobacz galerię” (wyśrodkowany, jak na zrzucie z Figmy).
  - `SectionLabel` „DNI PODRÓŻY” + l. 104 `LazyRow` kafelków `DayTile` (data przez `DateFormatter.dayMonth`).
  - `SectionLabel` „FILM I NOTATKA GŁOSOWA” + dwie `EmptyMediaCard` (l. 127, 132).
    l. 122: `TODO etap 7` – odtwarzacze i przycisk „+”.
- l. 191 `previewDetail()` + l. 209 `@Preview` (360 × 1020, jak ramka w Figmie).

---

## 6. Ekran 05: widok dnia (`…/planerpodrozy/ui/screens/day/`)

### `DayUiState.kt` (nowy)
- l. 5 `DayUiState(detail: DayDetail?, isLoading)`.

### `DayViewModel.kt` (nowy)
- l. 11 `DayViewModel(dayId, repository: DayRepository)`: ten sam schemat co `TripDetailViewModel`.

### `DayScreen.kt` (nowy)
- l. 48 `DayRoute(dayId, onBack, onEdit)`. l. 59: `TODO etap 4` – kliknięcie pustego kadru jeszcze nic nie robi.
- l. 66 `DayScreen(…)`: tytuł „Dzień N”, kolumna z odstępami 16 dp i marginesami 16/24 dp jak w Figmie.
  - l. 90 `FlowRow` z `LocationRow`: lokalizacje zawijają się do kolejnej linii; ukryte, gdy dzień ich nie ma.
  - l. 105 `PhotosHeader`: „Trzy kadry dnia” + l. 117 `FilledTonalButton` „Edytuj” z ikoną
    (kolory nadpisane na `primaryContainer`/`primary`, bo domyślny tonal M3 używa `secondaryContainer`).
  - l. 135 `DayPhotos`: `Row` 200 dp, trzy sloty w kolejności `PhotoCategory.entries`
    (Krajobraz, Jedzenie, Selfie); jest zdjęcie → `PhotoCard`, brak → `EmptyPhotoSlot`.
  - l. 158 `DayDescription`: „Opis dnia” + `OutlinedCard` z tekstem albo komunikatem o braku opisu.
- l. 183 `@Preview` z dwiema lokalizacjami i opisem.

---

## 7. Poprawka z etapu 1

### `…/planerpodrozy/ui/components/DashedBorder.kt` (zmieniony, commit `96c0be3`)
- **Błąd**: ramka pustego kadru była niewidoczna. `drawBehind` rysuje pod zawartością, a `Surface` malował na niej swoje tło.
- **Poprawka**: `drawBehind` → `drawWithContent` (l. 24); najpierw `drawContent()` (l. 26), potem ramka na wierzchu.
