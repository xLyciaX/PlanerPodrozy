# Etap 7: film i notatka głosowa

Gałąź: `feat/video-and-voice-note` (utworzona z `main` po scaleniu etapu 6).

| Commit | Opis |
|---|---|
| `79d1795` | `feat(media): add video and voice note storage and audio recorder` |
| `2d392b1` | `feat(data): save trip video and voice note replacing previous files` |
| `f281ada` | `feat(media): decode video thumbnails with Coil video frame decoder` |
| `24ce83b` | `feat(media): add ExoPlayer video player and voice note player` |
| `ecc12cd` | `fix(media): render video frames with Compose PlayerSurface` |
| `e16e8f9` | `feat(trips): add media sheet, video picking and voice recording` |

Skrót `…/planerpodrozy/` oznacza `app/src/main/java/pl/wieczorek/planerpodrozy/`.
Numery linii dotyczą stanu po commicie `e16e8f9`.

---

## 1. Pliki i nagrywanie (commit `79d1795`) — `…/planerpodrozy/data/media/`

### `MediaStorage.kt` (zmieniony)
- l. 17 `importVideo(source: Uri): String`: kopia filmu do pamięci aplikacji.
- l. 23 `createVoiceNoteFile(): String`: ścieżka nowego pliku na nagranie.

### `FileMediaStorage.kt` (przebudowany)
- l. 13 `importPhoto` i l. 15 `importVideo` korzystają ze wspólnej l. 32 `copyToAppStorage` (kopiowanie w `Dispatchers.IO`).
- l. 24 `createVoiceNoteFile`; l. 39 `newFile(directory, extension)`: unikalna nazwa `UUID.rozszerzenie`.
- l. 45–47 katalogi `filesDir/photos`, `filesDir/videos`, `filesDir/audio`.

### `AudioRecorder.kt` (nowy)
- l. 4 `interface AudioRecorder`: l. 6 `start(outputPath)`, l. 9 `stop()`, l. 12 `cancel()`.
  ViewModel zna tylko ten interfejs, nie `MediaRecorder` (ani `Context`).

### `AndroidAudioRecorder.kt` (nowy)
- l. 8 `class AndroidAudioRecorder(context)`: systemowy `MediaRecorder` (od Androida 12 konstruktor z `Context`).
- l. 12 `start`: mikrofon, kontener MPEG-4, kodek AAC 128 kb/s, 44,1 kHz → plik `.m4a`; błąd przygotowania zwalnia rejestrator.
- l. 37 `stop` (rzuca wyjątek przy pustym nagraniu), l. 47 `cancel` (przerywa bez zachowania wyniku).

### `app/src/main/AndroidManifest.xml` (zmieniony)
- l. 6 `<uses-permission android:name="android.permission.RECORD_AUDIO" />`.
- l. 7–9 `<uses-feature android:name="android.hardware.microphone" android:required="false" />`
  (aplikację da się zainstalować także na urządzeniu bez mikrofonu).

### `…/planerpodrozy/AppContainer.kt` (zmieniony)
- Nowe pole `audioRecorder: AudioRecorder = AndroidAudioRecorder(context)`.

---

## 2. Zapis w podróży (commit `2d392b1`)

### `…/planerpodrozy/data/local/TripDao.kt` (zmieniony)
- l. 19–20 `findById(tripId)`; l. 22–23 `updateVideo(tripId, videoPath)`; l. 25–26 `updateVoiceNote(tripId, voiceNotePath)`.

### `…/planerpodrozy/data/repository/TripRepository.kt` (zmieniony)
- l. 22 `setVideo(tripId, filePath)`, l. 25 `setVoiceNote(tripId, filePath)`.

### `…/planerpodrozy/data/repository/RoomTripRepository.kt` (zmieniony)
- l. 20 konstruktor przyjmuje teraz `database` i `mediaStorage` (`AppContainer`: `RoomTripRepository(database, mediaStorage)`).
- l. 70 `setVideo`, l. 76 `setVoiceNote`: zapis nowej ścieżki, a potem usunięcie pliku poprzedniego filmu/notatki,
  czyli nowe zastępuje stare.

---

## 3. Miniatura filmu (commit `f281ada`)

### `…/planerpodrozy/PlanerApp.kt` (zmieniony)
- l. 13 `PlanerApp : Application(), SingletonImageLoader.Factory`.
- l. 24 `newImageLoader(context)`: globalny loader Coil z l. 26 `VideoFrameDecoder.Factory()`.
  Dzięki temu `AsyncImage(model = File("film.mp4"))` pokazuje pierwszą klatkę filmu (reguła 6).

---

## 4. Odtwarzacze (commity `24ce83b` i `ecc12cd`) — `…/planerpodrozy/ui/media/`

### `TripVideoPlayer.kt` (nowy, poprawiony w `ecc12cd`)
- l. 36 `PlaybackTickMillis = 200L`: co ile odświeżamy czas na osi (wspólne z notatką).
- l. 46 `TripVideoPlayer(videoPath)`:
  - l. 49 `ExoPlayer.Builder(context).build()` + `setMediaItem` + `prepare()` (w `remember`, jeden odtwarzacz na plik);
  - l. 59 `rememberPresentationState(player)`: rozmiar obrazu filmu;
  - l. 62 `DisposableEffect`: nasłuch `onIsPlayingChanged` / `onPlaybackStateChanged` (czas trwania; po końcu powrót
    na początek i do miniatury), a przy zejściu z ekranu `release()`;
  - l. 84 `LifecycleEventEffect(ON_STOP)`: pauza, gdy aplikacja schodzi w tło;
  - l. 87 `LaunchedEffect`: w trakcie odtwarzania co 200 ms odczyt pozycji;
  - podgląd: l. 117 `PlayerSurface(…, SURFACE_TYPE_TEXTURE_VIEW)` przycięty jak `ContentScale.Crop`,
    a do pierwszego odtworzenia przykryty l. 124 `AsyncImage` z miniaturą.
- **Poprawka `ecc12cd`**: pierwsza wersja (`24ce83b`) osadzała `PlayerView` z `media3-ui` i tworzyła go dopiero
  po kliknięciu play, przez co obraz był czarny (dźwięk i oś czasu działały). Teraz jest `PlayerSurface` z
  `media3-ui-compose`, tworzony od początku.

### `VoiceNotePlayer.kt` (nowy)
- l. 23 `VoiceNotePlayer(audioPath, onUnavailable)`: l. 29 systemowy `MediaPlayer` (`setDataSource` + `prepare`);
  plik nieczytelny → `onUnavailable()` (pusta karta).
- l. 39 `DisposableEffect`: po końcu nagrania powrót na początek; `release()` przy zejściu z ekranu.
- l. 47 `LifecycleEventEffect(ON_STOP)`: pauza w tle; l. 51 `LaunchedEffect`: odczyt pozycji w trakcie odtwarzania.
- Rysuje bezstanowy `VoiceNotePlayerCard` z etapu 1 (play/pauza, fala, „0:42 / 1:30”).

### `gradle/libs.versions.toml`, `app/build.gradle.kts` (zmienione w `ecc12cd`)
- `androidx-media3-ui` → `androidx-media3-ui-compose` (ta sama wersja `media3 = 1.6.1`).

---

## 5. Widok podróży (commit `e16e8f9`) — `…/planerpodrozy/ui/screens/tripdetail/`

### `TripDetailUiState.kt` (zmieniony)
- l. 6 `enum class MediaError { VideoImport, Recording, MicrophoneDenied }`.
- l. 8 nowe pola: `isMediaSheetOpen`, `isImportingVideo`, `recordingElapsedMs` (null = brak nagrywania), `error`.

### `TripDetailViewModel.kt` (zmieniony)
- l. 21 konstruktor: `tripId`, `tripRepository`, `mediaStorage`, `audioRecorder`.
- `mediaState` (lokalny stan multimediów) łączony z danymi podróży przez `combine`.
- l. 43 / 47 otwarcie i zamknięcie arkusza „+”.
- l. 54 `onVideoPicked(uri)`: `isImportingVideo`, kopia `importVideo`, l. 59 `setVideo`; błąd → `MediaError.VideoImport`.
- l. 67 `onStartRecording()`: nowy plik, `audioRecorder.start`, licznik czasu na `SystemClock.elapsedRealtime()` (l. 78, 84).
- l. 89 `onStopRecording()`: `stop()`, a potem l. 94 `setVoiceNote`; puste nagranie → usunięcie pliku i błąd.
- l. 103 `onCancelRecording()`: przerwanie i usunięcie pliku; l. 109 `onMicrophoneDenied()`; l. 113 `onErrorShown()`.
- l. 118 `finishRecording()`: zatrzymanie licznika.
- l. 128 `onCleared()`: przy zniszczeniu ViewModelu w trakcie nagrywania wyłącza mikrofon i usuwa plik (l. 131 `runBlocking`).

### `TripDetailScreen.kt` (zmieniony)
- l. 71 `TripDetailRoute`:
  - l. 84 `videoPicker`: `PickVisualMedia` z typem `VideoOnly`;
  - l. 89 `microphonePermission`: `RequestPermission`; zgoda → nagrywanie, odmowa → komunikat;
  - l. 106: jeśli zgoda już jest (`checkSelfPermission`), od razu nagrywa.
- l. 118 `TripDetailScreen`: nowe parametry akcji multimediów; l. 135 snackbar z błędem.
  - l. 188: przycisk „+” (`IconButton`) w etykiecie „FILM I NOTATKA GŁOSOWA”;
  - l. 207: wspólny `OptionsBottomSheet` z opcjami „Dodaj film” / „Nagraj notatkę głosową”;
  - l. 227: `RecordVoiceDialog` z licznikiem, gdy trwa nagrywanie.
- l. 233 `TripVideo`: „Kopiowanie filmu…”, `TripVideoPlayer` (l. 241, `key` = nowy odtwarzacz po zmianie filmu) albo pusta karta.
- l. 252 `TripVoiceNote`: `VoiceNotePlayer` (l. 263) albo pusta karta.
- l. 267 `MediaError.messageRes`: tekst komunikatu dla każdego błędu.

### `app/src/main/res/values/strings.xml` (zmieniony)
- l. 53–56: `media_video_importing`, `media_video_error`, `media_record_error`, `media_mic_denied`.
