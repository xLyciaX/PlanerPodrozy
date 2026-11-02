# Etap 6: galeria z filtrami

Gałąź: `feat/gallery` (utworzona z `main` po scaleniu etapu 5).

| Commit | Opis |
|---|---|
| `38ece98` | `feat(data): add trip gallery query with photos ordered by day` |
| `d0d972c` | `feat(gallery): add gallery screen with category filters` |
| `b3c94e2` | `chore: remove temporary skeleton screen` |

Skrót `…/planerpodrozy/` oznacza `app/src/main/java/pl/wieczorek/planerpodrozy/`.
Numery linii dotyczą stanu po commicie `b3c94e2`.

---

## 1. Dane galerii (commit `38ece98`)

### `…/planerpodrozy/domain/model/TripDetails.kt` (zmieniony)
- l. 18 `data class TripGallery(tripName, photos: List<Photo>)`: wszystko dla ekranu 07.

### `…/planerpodrozy/data/local/PhotoDao.kt` (zmieniony)
- l. 28 w zapytaniu `observeByTrip` dopisane `ORDER BY trip_days.dayNumber, photos.addedAt`:
  zdjęcia w galerii idą po kolei dniami, a w obrębie dnia wg czasu dodania.
  (Kolejność nie wpływa na okładki, bo `coverPhoto` sam wybiera najwcześniejsze.)

### `…/planerpodrozy/data/repository/TripRepository.kt` (zmieniony)
- l. 18 `observeGallery(tripId): Flow<TripGallery?>`.

### `…/planerpodrozy/data/repository/RoomTripRepository.kt` (zmieniony)
- l. 61 `observeGallery`: `combine` podróży (nazwa) i jej zdjęć; brak podróży daje `null`.

---

## 2. Ekran 07: galeria (commit `d0d972c`) — `…/planerpodrozy/ui/screens/gallery/`

### `GalleryUiState.kt` (nowy)
- l. 6 `GalleryUiState(tripName, photos, selectedCategory: PhotoCategory?, isLoading)`.
  `photos` są już przefiltrowane; `selectedCategory = null` oznacza „Wszystkie”.

### `GalleryViewModel.kt` (nowy)
- l. 13 `GalleryViewModel(tripId, repository)`: `combine` galerii z wybranym filtrem (`MutableStateFlow`) → `stateIn`.
- l. 21 filtrowanie: `filter { category == null || it.category == category }`.
- l. 32 `onSelectCategory(category?)`: zawsze jeden wybrany filtr.

### `GalleryScreen.kt` (nowy)
- l. 47 `GalleryRoute(tripId, onBack)`.
- l. 55 `GalleryScreen(…)`: `PlanerTopAppBar` z tytułem „Galeria – {nazwa}”, wyrównanym do lewej
  (l. 66 `centered = false`) i ze strzałką wstecz (decyzja z planu). Pod nim filtry i siatka; przy braku zdjęć
  w wybranej kategorii komunikat „Brak zdjęć w tej kategorii.” (l. 83).
- l. 97 `CategoryFilters`: l. 99 lista filtrów `listOf(null) + PhotoCategory.entries` (null = „Wszystkie”),
  l. 100 `LazyRow` (przewijanie w poziomie), l. 106 `FilterChip`:
  wybrany = tło `primary`, biały tekst i ikona ✓; niewybrany = `primaryContainer`, ramka `secondary`.
  Nazwy filtrów z `filterLabelRes` („Krajobrazy”, „Jedzenie”, „Selfie”).
- l. 138 `PhotoGrid`: l. 139 `LazyVerticalGrid` z l. 140 `GridCells.Fixed(3)`, odstępy 8 dp,
  miniatury `PhotoImage` 80 dp przycięte do `shapes.large` (16 dp).
- l. 165 `@Preview` z 9 przykładowymi zdjęciami.

### `…/planerpodrozy/ui/navigation/PlanerNavHost.kt` (zmieniony)
- l. 68 `composable<GalleryDestination>`: zamiast szkieletu `GalleryRoute(tripId, onBack = goBack)`.
- Usunięte importy `stringResource` i `R`, potrzebne tylko szkieletom.

---

## 3. Porządki (commit `b3c94e2`)

### `…/planerpodrozy/ui/screens/SkeletonScreen.kt` (usunięty)
- Tymczasowy szkielet z etapu 1. Po etapie 6 każdy ekran ma prawdziwą implementację, więc nic go już nie używa.
