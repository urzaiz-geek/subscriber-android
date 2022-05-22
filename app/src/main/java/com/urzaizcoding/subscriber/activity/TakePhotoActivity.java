package com.urzaizcoding.subscriber.activity;

import static com.urzaizcoding.subscriber.activity.ActivitiesContracts.PHOTO_TAKEN_STUDENT;
import static com.urzaizcoding.subscriber.activity.ActivitiesContracts.STUDENT_DATA;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.urzaizcoding.subscriber.R;
import com.urzaizcoding.subscriber.databinding.ActivityTakePhotoBinding;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.common.Injection;
import com.urzaizcoding.subscriber.utils.file.FileBundle;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManager;

import java.io.File;

public class TakePhotoActivity extends AppCompatActivity {
    private ActivityTakePhotoBinding ui;
    private final ActivityResultLauncher<Uri> takePhotoLauncher;
    private Student currentStudent;
    private StudentPhotoManager studentPhotoManager;
    private FileBundle bundle;


    {
        takePhotoLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
            Log.d(getClass().getName(),"Got back from taking pic");
            if(result){
                if(bundle != null){
                    Log.d(getClass().getName(),
                            "Photo description : "
                                    + studentPhotoManager.photoDescription(new File(bundle.getLocaleAbsolutePath())));
                    ui.imageViewTakenPhoto.setImageURI(bundle.getProvidedUri());
                }
            }else { //the user didn't take the photo so we must not attempt to save it
                try {
                    studentPhotoManager.deletedCachedPhoto(bundle.getFileName());
                } catch (Exception e) {
                    Log.e(getClass().getName(),"Unable to delete cached photo  : "+e.getMessage());
                }finally {
                    bundle = null;   // prevent a non taken photo from being saved
                }
            }

        });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(getClass().getName(),"TakePhotoActivity is being created");

        ui = ActivityTakePhotoBinding.inflate(getLayoutInflater());
        ui.buttonTakePhoto.setOnClickListener(this::takePhoto);
        ui.buttonSave.setOnClickListener(this::savePhoto);
        Intent intent = getIntent();
        if(intent.hasExtra(STUDENT_DATA)){
            currentStudent = intent.getParcelableExtra(STUDENT_DATA);
        }
        if(savedInstanceState != null){
            bundle = savedInstanceState.getParcelable("bundle");
        }
        try {
            studentPhotoManager = Injection.studentPhotoManagerProvider(this);
        } catch (Exception e) {
            Log.e(getClass().getName(),"unable to create photomanager \n"+e.getMessage());
        }
        setContentView(ui.getRoot());
    }

    private void takePhoto(View view){
        //check if a previous photo has been taken without being deleted an delete it if not
        if(bundle != null){
            try {
                studentPhotoManager.deletedCachedPhoto(bundle.getFileName());
            } catch (Exception e) {
                Toast.makeText(this, R.string.cache_delete_error_message, Toast.LENGTH_SHORT).show();
            }
        }

        //generate the temp picture file with student
        if(currentStudent != null){
            try {
                bundle = studentPhotoManager.generateUriForStudentPhoto(currentStudent);
            } catch (Exception e) {
                Log.e(getClass().getName(),"Error while generating the uri bundle : \n"+e.getMessage());
                return;
            }
            //launch the activity
                takePhotoLauncher.launch(bundle.getProvidedUri());
            //
        }
    }

    private void savePhoto(View view){
        //No photo has been taken yet we quit
        if(bundle != null){
            try {
                if(!studentPhotoManager.savePhoto(new File(bundle.getLocaleAbsolutePath()))){
                    Log.e(getClass().getName(),"Unable to save the photo");
                    return;
                }
                currentStudent.setPhotoPath(bundle.getFileName());

            } catch (Exception e) {
                Toast.makeText(this, R.string.unable_to_save_photo_message, Toast.LENGTH_SHORT).show();
                Log.e(getClass().getName(),"Unable to save the photo : \n"+e.getMessage());
                setResult(RESULT_CANCELED);
                finishAfterTransition();
            }
        }

        Intent resultData = new Intent();
        resultData.putExtra(PHOTO_TAKEN_STUDENT,currentStudent);
        setResult(RESULT_OK,resultData);
        finishAfterTransition();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(bundle != null){
            try {
                studentPhotoManager.deletedCachedPhoto(bundle.getFileName());
            } catch (Exception e) {
                Log.e(getClass().getName(),"Unable to delete cached photo : "+e.getMessage());
            }
        }
        setResult(RESULT_CANCELED);
        finishAfterTransition();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(bundle != null){
            outState.putParcelable("bundle",bundle);
        }
    }
}