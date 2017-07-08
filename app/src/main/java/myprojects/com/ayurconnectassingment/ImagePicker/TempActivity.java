package myprojects.com.ayurconnectassingment.ImagePicker;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import myprojects.com.ayurconnectassingment.Activities.SuggestionActivity;

import static com.yalantis.ucrop.UCrop.REQUEST_CROP;

public class TempActivity extends AppCompatActivity {

    PickerManager pickerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.pickerManager = GlobalHolder.getInstance().getPickerManager();
        this.pickerManager.setActivity(TempActivity.this);
        this.pickerManager.pickPhotoWithPermission();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != SuggestionActivity.RESULT_OK) {
            finish();
            return;
        }
        switch (requestCode) {
            case PickerManager.REQUEST_CODE_SELECT_IMAGE:
                if (data != null) {
                    final Uri uri = data.getData();
                    pickerManager.setUri(uri);
                    pickerManager.startCropActivity();
                }
                else finish();
                break;
            case REQUEST_CROP:
                if (data != null) {
                    pickerManager.handleCropResult(data);
                } else finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == PickerManager.REQUEST_CODE_IMAGE_PERMISSION)
                pickerManager.handlePermissionResult(grantResults);
        else
            finish();

    }

}
