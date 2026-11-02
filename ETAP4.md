# Etap 4: dodawanie zdjęć, reguła 3 zdjęć, okładki

Gałąź: `feat/photo-adding` (utworzona z `main` po scaleniu etapu 3).

| Commit | Opis |
|---|---|
| `ae61b4b` | `feat(photos): add media storage and FileProvider for photo files` |
| `f46318f` | `feat(photos): save one photo per category and replace the previous file` |
| `d34a09e` | `feat(photos): add photo source sheet with camera and gallery` |

Skrót `…/planerpodrozy/` oznacza `app/src/main/java/pl/wieczorek/planerpodrozy/`.
Numery linii dotyczą stanu po commicie `d34a09e`.

---

## 1. Pliki zdjęć (`…/planerpodrozy/data/media/`)

### `MediaStorage.kt` (nowy)
- l. 6 `data class CameraPhoto(path, uri)`: plik na zdjęcie z aparatu. `uri` (`content://…`) dostaje aparat, `path` zapisujemy w bazie.
- l. 12 `interface MediaStorage`:
  - l. 14 `importPhoto(source: Uri): String`: kopiuje zdjęcie z galerii do pamięci aplikacji, zwraca ścieżkę kopii;
  - l. 17 `createCameraPhoto(): CameraPhoto`: tworzy pusty plik dla aparatu;
  - l. 20 `delete(path)`: usuwa plik.

### `FileMediaStorage.kt` (nowy)
- l. 11 `class FileMediaStorage(context) : MediaStorage`. Pliki trafiają do `filesDir/photos/` (l. 13 `photosDir`, l. 39 `PHOTOS_DIR`).
- l. 17 `importPhoto`: `contentResolver.openInputStream(uri)` → `copyTo` do nowego pliku, w `withContext(Dispatchers.IO)` (wątek w tle).
- l. 25 `createCameraPhoto`: nowy plik + l. 28 `FileProvider.getUriForFile(…)`, czyli adres `content://`, pod który aparat może zapisać.
- l. 32 `delete`: `File(path).delete()` w tle.
- l. 36 `newPhotoFile()`: unikalna nazwa `UUID.jpg`.

### `app/src/main/res/xml/file_paths.xml` (nowy)
- l. 4 `<files-path name="photos" path="photos/" />`: katalog z `filesDir`, który `FileProvider` może udostępnić aparatowi.

### `app/src/main/AndroidManifest.xml` (zmieniony)
- l. 29–37 `<provider android:name="androidx.core.content.FileProvider" …>`:
  `authorities="${applicationId}.fileprovider"`, `exported="false"`, `grantUriPermissions="true"`
  (aparat dostaje tymczasowe prawo zapisu do jednego pliku), `meta-data` wskazuje `@xml/file_paths`.

### `…/planerpodrozy/AppContainer.kt` (zmieniony)
- l. 19 nowe pole `mediaStorage: MediaStorage = FileMediaStorage(context)`.
- l. 22 `RoomDayRepository(database, mediaStorage)`: repozytorium dnia dostaje `MediaStorage`, żeby usuwać pliki zastąpionych zdjęć.

---

## 2. Zapis zdjęcia w kadrze

### `…/planerpodrozy/data/local/PhotoDao.kt` (zmieniony)
- l. 35–36 `find(dayId, category)`: zdjęcie w danym kadrze (albo `null`).
- l. 38–39 `@Insert insert(photo)`, l. 41–42 `@Delete delete(photo)`.

### `…/planerpodrozy/data/repository/DayRepository.kt` (zmieniony)
- l. 16 `setPhoto(dayId, category, filePath)`: zapis zdjęcia w kadrze; zajęty kadr zostaje zastąpiony.

### `…/planerpodrozy/data/repository/RoomDayRepository.kt` (zmieniony)
- l. 14–17 konstruktor przyjmuje teraz `database` i `mediaStorage`.
- l. 33 `setPhoto`:
  - l. 35 `withTransaction { … }`: znajdź poprzednie zdjęcie kategorii, usuń jego wpis, wstaw nowe z `addedAt = Instant.now()`;
  - l. 44: plik poprzedniego zdjęcia usuwany dopiero po udanym zapisie w bazie.
- **Reguła 3 zdjęć**: jedno zdjęcie na kategorię (unikalny indeks z etapu 2 + zastępowanie), więc najwyżej 3 na dzień.
- **Okładki**: bez nowego kodu. `observeTrips` i `observeTripDetail` (etapy 2–3) liczą je funkcją `coverPhoto`
  z nowych `addedAt`, a Room sam odświeża listy po zapisie.

---

## 3. Interfejs dodawania zdjęć

### `…/planerpodrozy/ui/media/PhotoSourceLaunchers.kt` (nowy)
- l. 16 `class PhotoSourceLaunchers`:
  - l. 22 `takePhoto(target)`: uruchamia aparat (`TakePicture`); brak aplikacji aparatu (`ActivityNotFoundException`) = wynik `false`;
  - l. 32 `pickFromGallery()`: systemowy Photo Picker, tylko obrazy (`PickVisualMedia.ImageOnly`).
- l. 42 `rememberPhotoSourceLaunchers(onPhotoTaken, onPhotoPicked)`: rejestruje oba `rememberLauncherForActivityResult`.
  Wspólne dla widoku dnia i (w etapie 5) edycji dnia.

### `…/planerpodrozy/ui/media/PhotoSourceSheet.kt` (nowy)
- l. 20 `PhotoSourceSheet(category, dayNumber, date, onTakePhoto, onPickFromGallery, onDismiss)`: ekran 06.
  Używa wspólnego `OptionsBottomSheet`: tytuł „Dodaj zdjęcie”, kontekst „Selfie · Dzień 1 · 12.07.2026”
  (`photo_source_context` + `DateFormatter.numeric`), opcje „Zrób zdjęcie” / „Wybierz z galerii”.
- l. 57 `@Preview` (arkusz modalny widać w trybie interaktywnym podglądu).

### `…/planerpodrozy/ui/screens/day/DayUiState.kt` (zmieniony)
- Nowe pola: `photoSourceCategory: PhotoCategory?` (dla którego kadru otwarty jest arkusz) i `showPhotoError`.

### `…/planerpodrozy/ui/screens/day/DayViewModel.kt` (zmieniony)
- l. 16–20 konstruktor: `dayId`, `dayRepository`, `mediaStorage`.
- l. 22–23 `photoSourceCategory`, `showPhotoError` (`MutableStateFlow`); l. 30 `uiState` łączy je z danymi dnia (`combine`).
- l. 27–28 `pendingCamera`, `pendingGalleryCategory`: zapamiętana kategoria kadru, zanim aparat/galeria odeślą wynik.
- l. 39 `onAddPhoto`, l. 43 `onDismissPhotoSource`: otwarcie/zamknięcie arkusza.
- l. 48 `prepareCameraPhoto(category)`: tworzy plik i zwraca `Uri` dla aparatu.
- l. 54 `onCameraResult(success)`: sukces → l. 59 `setPhoto`; anulowanie → l. 62 usunięcie pustego pliku.
- l. 67 `onGalleryPickStarted`, l. 72 `onGalleryResult(uri)`: l. 77 kopia przez `importPhoto`, l. 78 `setPhoto`;
  błąd kopiowania → `showPhotoError = true`.
- l. 83 `onPhotoErrorShown`.

### `…/planerpodrozy/ui/screens/day/DayScreen.kt` (zmieniony)
- l. 54 `DayRoute`: ViewModel z `mediaStorage`; l. 61 `rememberPhotoSourceLaunchers(…)` z wynikami do ViewModelu.
  `onTakePhoto` = `launchers.takePhoto(viewModel.prepareCameraPhoto(category))`;
  `onPickFromGallery` = `onGalleryPickStarted(category)` + `launchers.pickFromGallery()`.
- l. 82 `DayScreen`: nowe parametry `onDismissPhotoSource`, `onTakePhoto`, `onPickFromGallery`, `onPhotoErrorShown`.
  - l. 95–106: `SnackbarHostState` + `LaunchedEffect(state.showPhotoError)` pokazują komunikat „Nie udało się dodać zdjęcia.”;
  - l. 133–143: gdy `photoSourceCategory != null`, pokazuje `PhotoSourceSheet`.
- Kliknięcie pustego kadru (`EmptyPhotoSlot`) otwiera teraz arkusz (koniec `TODO etap 4`).

### `app/src/main/res/values/strings.xml` (zmieniony)
- l. 84 `photo_add_error` = „Nie udało się dodać zdjęcia.”
