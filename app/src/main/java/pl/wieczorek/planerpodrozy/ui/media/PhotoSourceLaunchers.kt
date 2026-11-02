package pl.wieczorek.planerpodrozy.ui.media

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

/**
 * Uruchamia systemowy aparat i systemowy wybór zdjęć (Photo Picker).
 * Wspólne dla widoku dnia i edycji dnia.
 */
class PhotoSourceLaunchers(
    private val camera: ManagedActivityResultLauncher<Uri, Boolean>,
    private val gallery: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    private val onPhotoTaken: (Boolean) -> Unit,
) {
    /** Aparat zapisze zdjęcie pod adresem [target]; wynik (sukces/anulowanie) wróci do onPhotoTaken. */
    fun takePhoto(target: Uri) {
        try {
            camera.launch(target)
        } catch (e: ActivityNotFoundException) {
            // Na urządzeniu nie ma aplikacji aparatu.
            onPhotoTaken(false)
        }
    }

    /** Otwiera wybór jednego zdjęcia (tylko obrazy, bez filmów). */
    fun pickFromGallery() {
        gallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }
}

/**
 * Rejestruje oba „launchery”. Wynik z innej aplikacji wraca do podanych funkcji,
 * także po obrocie ekranu (dlatego rejestracja musi być w composable, a nie w ViewModelu).
 */
@Composable
fun rememberPhotoSourceLaunchers(
    onPhotoTaken: (success: Boolean) -> Unit,
    onPhotoPicked: (Uri?) -> Unit,
): PhotoSourceLaunchers {
    val camera = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture(), onPhotoTaken)
    val gallery = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia(), onPhotoPicked)
    return remember(camera, gallery) { PhotoSourceLaunchers(camera, gallery, onPhotoTaken) }
}
