# Etap 1: motyw, nawigacja, wspólne komponenty

Commity:
- `0d59e9d` `feat: add theme, navigation skeleton and shared components (stage 1)`
- `974c831` `fix(trips): move add trip button to bottom-right floating action button` (gałąź `fix/trip-list-fab`)

Wszystkie ścieżki są względne do katalogu projektu. Skrót `…/planerpodrozy/` oznacza
`app/src/main/java/pl/wieczorek/planerpodrozy/`. Numery linii dotyczą stanu po commicie `974c831`.

---

## 1. Konfiguracja Gradle (biblioteki)

### `gradle/libs.versions.toml` (zmieniony)
- Sekcja `[versions]`: Kotlin `2.0.0` → `2.1.21`, Compose BOM `2024.04.01` → `2025.06.01`,
  `core-ktx` → `1.16.0`, `activity-compose` → `1.10.1`, `lifecycle` → `2.9.1` (wcześniej `lifecycleRuntimeKtx`).
  Nowe wersje: `ksp`, `navigation`, `kotlinxSerialization`, `room`, `coil`, `media3`.
- Sekcja `[libraries]`: nowe wpisy `androidx-lifecycle-runtime-compose`, `androidx-lifecycle-viewmodel-compose`,
  `androidx-material-icons-extended`, `androidx-navigation-compose`, `kotlinx-serialization-json`,
  `androidx-room-runtime`, `androidx-room-ktx`, `androidx-room-compiler`, `coil-compose`, `coil-video`,
  `androidx-media3-exoplayer`, `androidx-media3-ui`.
- Sekcja `[plugins]`: nowe `kotlin-serialization` i `ksp`.

### `build.gradle.kts` (główny, zmieniony)
- Blok `plugins { … }`: dopisane `alias(libs.plugins.kotlin.serialization) apply false` i `alias(libs.plugins.ksp) apply false`.

### `app/build.gradle.kts` (zmieniony)
- Blok `plugins { … }`: dopisane `alias(libs.plugins.kotlin.serialization)` i `alias(libs.plugins.ksp)`.
- Blok `dependencies { … }`, zaraz po `implementation(libs.androidx.material3)`: 13 nowych linii
  (`material.icons.extended`, `lifecycle.runtime.compose`, `lifecycle.viewmodel.compose`, `navigation.compose`,
  `kotlinx.serialization.json`, `room.runtime`, `room.ktx`, `ksp(room.compiler)`, `coil.compose`, `coil.video`,
  `media3.exoplayer`, `media3.ui`).
- `minSdk`, `compileSdk` i `targetSdk` bez zmian (26 / 35 / 35).

---

## 2. Zasoby (`app/src/main/res`)

| Plik | Zmiana |
|---|---|
| `res/font/dm_sans_regular.ttf` | nowy: krój DM Sans 400 (Google Fonts, licencja OFL) |
| `res/font/dm_sans_medium.ttf` | nowy: krój DM Sans 500 |
| `res/values/strings.xml` | przepisany: nazwa aplikacji „Planer Podróży” i wszystkie teksty ekranów 01–07 po polsku, pogrupowane komentarzami (`<!-- 01 Lista podróży -->` itd.), plural `trip_duration_days` („1 dzień”, „3 dni”, „7 dni”) |
| `res/values/colors.xml` | przepisany: zamiast kolorów szablonu jeden kolor `window_background` (#FDFBFF) |
| `res/values/themes.xml` | w stylu `Theme.PlanerPodrozy` dodane `android:windowBackground`, żeby przy starcie nie mignęło białe tło |

---

## 3. Motyw (`…/planerpodrozy/ui/theme/`)

### `Color.kt` (przepisany)
- Linie 8–31: stałe kolorów z Figmy (`Primary`, `PrimaryContainer`, `OnPrimaryContainer`, `Secondary`,
  `Background`, `OutlineVariant`, `SurfaceContainer…` itd.). Komentarze przy `OnPrimaryContainer` (l. 12–14)
  i `Secondary` (l. 16–17) tłumaczą nietypowe mapowanie.

### `Theme.kt` (przepisany)
- Linie 9–29: `LightColorScheme`, czyli przypisanie kolorów z `Color.kt` do slotów Material 3.
- Linie 31–39: `PlanerPodrozyTheme(content)`. Usunięte z szablonu: ciemny motyw i dynamiczne kolory (Android 12+).
  Dodane `shapes = Shapes`.

### `Type.kt` (przepisany)
- Linie 12–15: `DmSans`, czyli rodzina czcionek z plików `res/font`.
- Linie 21–42: `Typography` z rozmiarami z makiety. Przy każdym stylu komentarz mówi, gdzie jest używany.
- Linie 47–53: dwa dodatkowe style jako rozszerzenia: `Typography.caption` (11 sp) i `Typography.captionSmall` (10 sp).

### `Shape.kt` (nowy)
- Linie 6–15: `Shapes` z promieniami 8 / 12 / 16 / 24 dp, pobranymi z `Dimens`.

### `Dimens.kt` (nowy)
- `object Dimens` (od l. 6): odstępy (`SpaceXs`…`SpaceXl`), promienie (`Corner…`), margines ekranu,
  wysokości z makiety (nagłówek listy, karta podróży, baner, kafelek dnia, kadry, galeria, odtwarzacze),
  rozmiary ikon i słupków fali.

---

## 4. Model domenowy (`…/planerpodrozy/domain/model/`)

### `PhotoCategory.kt` (nowy)
- `enum class PhotoCategory { LANDSCAPE, FOOD, SELFIE }` oraz `PhotosPerDay = 3` (liczba kategorii),
  używane przez licznik `n/3` na kafelku dnia.

---

## 5. Formatowanie dat (`…/planerpodrozy/ui/format/`)

### `DateFormatter.kt` (nowy)
- `object DateFormatter`, jedyne miejsce formatowania dat:
  - l. 24 `range(from, to)`: „12–18 lipca 2026”, „30 czerwca – 2 lipca 2026”, „30 grudnia 2025 – 2 stycznia 2026”;
  - l. 33 `numeric(date)`: „12.07.2026”;
  - l. 36 `dayMonth(date)`: „12 lipca”;
  - l. 39 `duration(millis)`: „0:42”, „2:18”.

### `app/src/test/java/pl/wieczorek/planerpodrozy/ui/format/DateFormatterTest.kt` (nowy)
- 6 testów JUnit dla powyższych funkcji (l. 10–55).

---

## 6. Wspólne komponenty (`…/planerpodrozy/ui/components/`)

Każdy plik kończy się funkcją `@Preview` (podgląd w Android Studio).

| Plik | Co zawiera (linia) |
|---|---|
| `PlanerTopAppBar.kt` | `PlanerTopAppBar(title, onBack, centered)` (l. 27): pasek ze strzałką wstecz; `centered = false` daje tytuł do lewej (galeria) |
| `PhotoImage.kt` | `PhotoImage(path, placeholder)` (l. 31): zdjęcie z pliku przez Coil albo liliowy placeholder; `TripCoverImage` (l. 59): okładka podróży; `EmptyTripPlaceholder` (l. 70): walizka na liliowym tle (reguła 4) |
| `SmallLabels.kt` | `DurationPill` (l. 34): pigułka „X dni”; `SectionLabel` (l. 56): „DNI PODRÓŻY” z miejscem na „+”; `LocationRow` (l. 77): pinezka + tekst |
| `FormFields.kt` | `planerTextFieldColors()` (l. 30): wspólne kolory pól; `DateField` (l. 44): pole daty tylko do odczytu z ikoną kalendarza; `CancelSaveButtons` (l. 79): para Anuluj/Zapisz |
| `DashedBorder.kt` | `Modifier.dashedBorder(…)` (l. 18): przerywana ramka (pusty kadr, chip „Dodaj lokalizację”) |
| `PhotoCards.kt` | `planerCardBorder()` (l. 39): ramka kart; `PhotoCard` (l. 46): kadr z podpisem i opcjonalnymi przyciskami zmień/usuń (`PhotoActionButton`, l. 95); `EmptyPhotoSlot` (l. 110): „Selfie / Dodaj zdjęcie” |
| `TripCards.kt` | `TripCard` (l. 34): karta podróży na liście; `DayTile` (l. 84): kafelek dnia z licznikiem n/3 |
| `OptionsBottomSheet.kt` | `SheetOption` (l. 44): opis jednej opcji; `OptionsBottomSheet` (l. 58): `ModalBottomSheet` z opcjami i „Anuluj”, chowa się przed wykonaniem akcji (`hideThen`, l. 66); `OptionsSheetContent` (l. 91): treść arkusza |
| `MediaCards.kt` | `VideoPlayerCard` (l. 82): podgląd filmu, play, oś czasu; `VoiceNotePlayerCard` (l. 153): play/pauza, fala, czas; `EmptyMediaCard` (l. 207): pusta karta; `AudioWaveform` (l. 239): słupki rysowane na `Canvas` wg wzoru z makiety (`WaveformPattern`, l. 229) |
| `Dialogs.kt` | `AddLocationDialog` (l. 28): dialog z polem tekstowym; `RecordVoiceDialog` (l. 64): licznik nagrywania z „Anuluj” / „Zatrzymaj i zapisz” |
| `PhotoCategoryLabels.kt` | `PhotoCategory.labelRes` (l. 9): „Krajobraz/Jedzenie/Selfie”; `PhotoCategory.filterLabelRes` (l. 18): „Krajobrazy/…” do filtrów galerii |

---

## 7. Nawigacja (`…/planerpodrozy/ui/navigation/`)

### `Routes.kt` (nowy)
- Linie 9–24: typowane trasy `TripListRoute`, `NewTripRoute`, `TripDetailRoute(tripId)`, `DayRoute(dayId)`,
  `DayEditRoute(dayId)`, `GalleryRoute(tripId)`.

### `PlanerNavHost.kt` (nowy)
- `PlanerNavHost()` (l. 20): `NavHost` ze startem na liście i sześcioma `composable<…>`.
  Lista ma prawdziwy ekran, pozostałe trasy pokazują tymczasowy `SkeletonScreen`.

---

## 8. Ekrany (`…/planerpodrozy/ui/screens/`)

### `triplist/TripListScreen.kt` (nowy, poprawiony w `974c831`)
- l. 40 `enum class SortOrder`.
- l. 47 `TripListScreen(sortOrder, onAddTrip, onSortChange, …)`: `Scaffold` z nagłówkiem, przyciskami sortowania i pustym stanem.
- **Poprawka `974c831`:** przycisk „+” przeniesiony z wiersza nad listą do parametru `floatingActionButton`
  w `Scaffold` (l. 57–66): `FloatingActionButton` w prawym dolnym rogu, z tą samą akcją `onAddTrip`.
  Wiersz z sortowaniem (l. 77–96) ma teraz `Arrangement.End`, więc strzałki zostały po prawej.
  Import `FilledIconButton` zamieniony na `FloatingActionButton`.
- l. 104 `TripListHeader()`: zaokrąglony liliowy nagłówek „Moje podróże” z `statusBarsPadding()`.
- l. 130 `SortButton(…)`: aktywny kierunek jako `FilledTonalIconButton`, nieaktywny jako `IconButton`.
- l. 154 `EmptyTripList(…)`: ikona walizki i komunikat zachęcający do dodania podróży.

### `SkeletonScreen.kt` (nowy, **tymczasowy**)
- `SkeletonScreen` (l. 25) i `SkeletonLink` (l. 53): szkielet ekranu z przyciskami przejść,
  służy tylko do sprawdzenia nawigacji. Zniknie, gdy powstaną prawdziwe ekrany (etapy 2–7).

---

## 9. Punkt wejścia

### `…/planerpodrozy/MainActivity.kt` (przepisany)
- Usunięte z szablonu: `Greeting` i `GreetingPreview`.
- `onCreate`: `enableEdgeToEdge()` + `PlanerPodrozyTheme { PlanerNavHost() }`.
