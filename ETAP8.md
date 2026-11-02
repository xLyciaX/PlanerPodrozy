# Etap 8: usuwanie podróży

Zamiast pierwotnego etapu 8 (dopracowanie wyglądu pod Figmę) — na prośbę użytkowniczki.
Funkcji nie ma w makiecie; usuwanie działa przez przesunięcie karty na liście w lewo i potwierdzenie w dialogu.

Gałąź: `feat/trip-deleting` (utworzona z `main` po scaleniu etapu 7).

| Commit | Opis |
|---|---|
| `bb388b2` | `feat(data): delete trip with its days, photos and media files` |
| `26f5aec` | `feat(trips): delete trip by swiping its card with confirmation` |

Skrót `…/planerpodrozy/` oznacza `app/src/main/java/pl/wieczorek/planerpodrozy/`.
Numery linii dotyczą stanu po commicie `26f5aec`.

---

## 1. Usuwanie w warstwie danych (commit `bb388b2`)

### `…/planerpodrozy/data/local/TripDao.kt` (zmieniony)
- l. 23–24 `deleteById(tripId)`: `DELETE FROM trips WHERE id = :tripId`.
  Dni i zdjęcia znikają same dzięki kluczom obcym z `CASCADE` z etapu 2.

### `…/planerpodrozy/data/local/PhotoDao.kt` (zmieniony)
- l. 44 `findFilePathsByTrip(tripId)`: ścieżki plików wszystkich zdjęć podróży (`INNER JOIN trip_days`).

### `…/planerpodrozy/data/repository/TripRepository.kt` (zmieniony)
- l. 28 `deleteTrip(tripId)`.

### `…/planerpodrozy/data/repository/RoomTripRepository.kt` (zmieniony)
- l. 82 `deleteTrip`: w transakcji zbiera ścieżki plików (l. 85: zdjęcia + film + notatka głosowa)
  i usuwa podróż (l. 87); dopiero po udanym zapisie w bazie usuwa pliki przez `MediaStorage`.

---

## 2. Przesunięcie karty i potwierdzenie (commit `26f5aec`)

### `…/planerpodrozy/ui/components/Dialogs.kt` (zmieniony)
- l. 97 `DeleteTripDialog(tripName, onConfirm, onDismiss)`: `AlertDialog` z ikoną kosza, nazwą podróży w treści
  i przyciskiem „Usuń” w kolorze błędu (`colorScheme.error`), bo operacji nie da się cofnąć.
- l. 137 `@Preview`.

### `…/planerpodrozy/ui/screens/triplist/TripListUiState.kt` (zmieniony)
- l. 14 `tripToDelete: TripSummary?`: podróż czekająca na potwierdzenie (null = dialog zamknięty).

### `…/planerpodrozy/ui/screens/triplist/TripListViewModel.kt` (zmieniony)
- l. 17 `tripToDelete` (`MutableStateFlow`), dołączony w l. 22 do `combine`.
- l. 40 `onRequestDelete(trip)`, l. 44 `onDismissDelete()`, l. 48 `onConfirmDelete()` → l. 51 `repository.deleteTrip(...)`.
  Lista odświeża się sama, bo Room emituje nową listę podróży.

### `…/planerpodrozy/ui/screens/triplist/TripListScreen.kt` (zmieniony)
- l. 71–73 / 84–86: nowe akcje `onRequestDelete`, `onConfirmDelete`, `onDismissDelete`.
- l. 136–137: `DeleteTripDialog`, gdy `tripToDelete != null`.
- l. 158: każda karta w `SwipeToDeleteBox`.
- l. 176 `SwipeToDeleteBox`: M3 `SwipeToDismissBox` z l. 177 `rememberSwipeToDismissBoxState`:
  - przesunięcie od prawej do lewej wywołuje `onDelete`, a `confirmValueChange` zwraca `false`,
    więc karta wraca na miejsce (o usunięciu decyduje dialog);
  - l. 186 `enableDismissFromStartToEnd = false`: tylko jeden kierunek;
  - tło pod kartą (l. 193): `errorContainer` z koszem po prawej, zaokrąglone jak karta (`shapes.large`).
- Podglądy uzupełnione o nowe parametry.

### `app/src/main/res/values/strings.xml` (zmieniony)
- l. 25–28: `trip_delete_title`, `trip_delete_message`, `action_delete`, `cd_delete_trip`.
