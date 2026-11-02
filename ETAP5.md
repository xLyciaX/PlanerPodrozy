# Etap 5: edycja dnia ze stanem roboczym

Gałąź: `feat/day-editing` (utworzona z `main` po scaleniu etapu 4).

| Commit | Opis |
|---|---|
| `1cd0ce5` | `refactor(photos): extract photo pick tracker and shared day photo row` |
| `c3e8b51` | `feat(data): save day edits and photo changes in one transaction` |
| `6dc63ce` | `feat(days): add day editing screen with draft state` |
| `499fd14` | `feat(nav): animate transition between day view and day editing` |

Skrót `…/planerpodrozy/` oznacza `app/src/main/java/pl/wieczorek/planerpodrozy/`.
Numery linii dotyczą stanu po commicie `499fd14`.

---

## 1. Refaktor: wspólne elementy widoku i edycji dnia (commit `1cd0ce5`)

### `…/planerpodrozy/ui/media/PhotoPickTracker.kt` (nowy)
- l. 8 `data class PickedPhoto(category, filePath)`: zdjęcie wybrane do kadru (plik już w pamięci aplikacji).
- l. 14 `class PhotoPickTracker(mediaStorage)`: pamięta kadr, zanim aparat lub galeria odeślą wynik.
  - l. 20 `prepareCamera(category): Uri`, l. 27 `onCameraResult(success): PickedPhoto?` (po anulowaniu usuwa pusty plik);
  - l. 35 `onGalleryStarted(category)`, l. 43 `onGalleryResult(uri): PickedPhoto?` (kopiuje przez `importPhoto`).
- Logika przeniesiona z `DayViewModel`, żeby nie powtarzać jej w `DayEditViewModel`.

### `…/planerpodrozy/ui/components/DayPhotoRow.kt` (nowy)
- l. 23 `DayPhotoRow(photoPaths, onAddPhoto, modifier, onChangePhoto?, onDeletePhoto?)`: trzy kadry w kolejności
  `PhotoCategory.entries`: `PhotoCard` albo `EmptyPhotoSlot`. Gdy podane `onChangePhoto`/`onDeletePhoto`,
  karty dostają przyciski „zmień”/„usuń”.
- l. 59 `@Preview`.

### `…/planerpodrozy/ui/screens/day/DayViewModel.kt` (zmieniony)
- Pola `pendingCamera` / `pendingGalleryCategory` zastąpione przez `PhotoPickTracker`.
- l. 47 `prepareCameraPhoto`, l. 50 `onCameraResult`, l. 56 `onGalleryPickStarted`, l. 61 `onGalleryResult`
  delegują do trackera; l. 69 `save(photo)` = `setPhoto`. Działanie bez zmian.

### `…/planerpodrozy/ui/screens/day/DayScreen.kt` (zmieniony)
- Usunięta prywatna funkcja `DayPhotos`; l. 124 `DayPhotoRow(photoPaths = detail.photos.mapValues { it.value.filePath }, …)`.

---

## 2. Zapis edycji (commit `c3e8b51`)

### `…/planerpodrozy/domain/model/Trip.kt` (zmieniony)
- l. 49 `data class DraftPhoto(filePath, addedAt)`: zdjęcie w kopii roboczej. `addedAt` = moment wybrania, więc decyduje o okładkach.

### `…/planerpodrozy/data/local/DayDao.kt` (zmieniony)
- l. 19–20 `updateDetails(dayId, description, locations)`: `UPDATE trip_days SET …`
  (lista lokalizacji zamienia się na JSON przez `Converters`).

### `…/planerpodrozy/data/local/PhotoDao.kt` (zmieniony)
- l. 35–36 `findByDay(dayId)`: zdjęcia dnia jako zwykła lista (bez `Flow`), do porównania w transakcji.

### `…/planerpodrozy/data/repository/DayRepository.kt` (zmieniony)
- l. 23 `saveDay(dayId, locations, description, photos: Map<PhotoCategory, DraftPhoto>)`.

### `…/planerpodrozy/data/repository/RoomDayRepository.kt` (zmieniony)
- l. 48 `saveDay`: l. 54 `withTransaction { … }`:
  zapis opisu i lokalizacji; potem dla każdej kategorii porównanie zdjęcia w bazie z kopią roboczą.
  Bez zmian → nic; zmienione lub usunięte → usuń wpis (i zapamiętaj plik); nowe → wstaw z `draft.addedAt`.
- Po transakcji usuwa pliki zdjęć usuniętych lub zastąpionych.

---

## 3. Ekran 05b: edycja dnia (commit `6dc63ce`) — `…/planerpodrozy/ui/screens/dayedit/`

### `DayEditUiState.kt` (nowy)
- l. 8 `DayEditUiState`: kopia robocza (`locations`, `description`, `photos: Map<PhotoCategory, DraftPhoto>`),
  dane do nagłówka arkusza (`dayNumber`, `date`), stan dialogu lokalizacji, arkusza zdjęć, błędu, zapisu
  oraz `isFinished` (powrót do widoku dnia).

### `DayEditViewModel.kt` (nowy)
- l. 25 `DayEditViewModel(dayId, dayRepository, mediaStorage)`.
- l. 39 `init`: kopia robocza tworzona **raz** z bazy (`observeDay(dayId).first()`).
- l. 37 `filesAddedInSession`: pliki zdjęć dodanych w tej edycji.
- Lokalizacje: l. 58 `onOpenAddLocation`, l. 62 `onDismissAddLocation`, l. 66 `onAddLocation`, l. 70 `onRemoveLocation(index)`.
- Opis: l. 78 `onDescriptionChange`, l. 79 `take(TripDay.MaxDescriptionLength)` (maks. 500 znaków).
- Zdjęcia: l. 85 `onChoosePhoto` (pusty kadr i „zmień” otwierają ten sam arkusz), l. 93 `onDeletePhoto`,
  l. 97–117 aparat i galeria przez `PhotoPickTracker`, l. 122 `putInDraft` (nowe zdjęcie zastępuje kadr tylko w kopii roboczej).
- l. 131 `onSave`: `saveDay(...)`, usunięcie plików dodanych w tej edycji, ale już niepotrzebnych, `isFinished = true`.
- l. 144 `onCancel`: usuwa wszystkie pliki dodane w tej edycji, nic nie zapisuje.
- l. 151 `deleteSessionFiles(except)`.

### `DayEditScreen.kt` (nowy)
- l. 57 `DayEditRoute(dayId, onClose)`:
  - l. 66 `BackHandler(onBack = viewModel::onCancel)`: systemowe „wstecz” odrzuca zmiany jak „Anuluj”;
  - l. 67 `LaunchedEffect(state.isFinished)`: powrót po zapisie/anulowaniu;
  - te same `rememberPhotoSourceLaunchers` co w widoku dnia.
- l. 94 `DayEditScreen(…)`: pasek „Edycja dnia” (strzałka = Anuluj), snackbar błędu (l. 113),
  kolumna: chipy lokalizacji, „Kadry dnia”, l. 145 `DayPhotoRow` z „zmień”/„usuń”, opis, `CancelSaveButtons`.
  l. 157 `AddLocationDialog`, l. 162 `PhotoSourceSheet` (wspólne komponenty).
- l. 175 `LocationChips`: `FlowRow` z l. 185 `InputChip` (pinezka, nazwa, „×” usuwa lokalizację; tło
  `surfaceContainerHighest`, bez ramki) i l. 210 `AssistChip` „+ Dodaj lokalizację” z przerywaną ramką
  (l. 222 `dashedBorder`, bo M3 jej nie ma).
- l. 228 `DescriptionField`: `OutlinedTextField` (min. 4 linie, kształt `extraLarge`) z l. 240 `supportingText`
  „n / 500” wyrównanym do prawej.
- l. 260 `@Preview`.

### `…/planerpodrozy/ui/navigation/PlanerNavHost.kt` (zmieniony)
- `composable<DayEditDestination>`: zamiast szkieletu `DayEditRoute(dayId, onClose = goBack)`.

---

## 4. Animacja 05 ↔ 05b (commit `499fd14`)

### `…/planerpodrozy/ui/navigation/PlanerNavHost.kt` (zmieniony)
- l. 21 `EditTransitionMillis = 300`.
- l. 57 `composable<DayEditDestination>(enterTransition, popExitTransition)`:
  - l. 58 wejście: `slideIntoContainer(Up)` + `fadeIn`, czyli edycja wysuwa się od dołu;
  - l. 62 wyjście przy powrocie: `slideOutOfContainer(Down)` + `fadeOut`.
- Doszły importy `AnimatedContentTransitionScope`, `tween`, `fadeIn`, `fadeOut`.
