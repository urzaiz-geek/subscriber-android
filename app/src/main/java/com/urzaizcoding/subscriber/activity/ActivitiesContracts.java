package com.urzaizcoding.subscriber.activity;

import static java.util.Objects.requireNonNull;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.urzaizcoding.subscriber.persistence.domain.Student;

public class ActivitiesContracts {
    public static final String CREATE_DATA = "Create_data";
    public static final String EDIT_DATA = "Edit_data";
    public static final String RESULT_DATA = "Result_data";
    public static final String PHOTO_TAKEN_STUDENT = "Photo_taken_stud";
    public static final String STUDENT_DATA = "Student_data";

    public static class CreateEditStudentContract extends ActivityResultContract<Student, Student> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Student input) {
            Intent intent = new Intent(context,RegisterStudentActivity.class);
            if(input != null) {
                intent.putExtra(EDIT_DATA, input);
            }else {
                intent.putExtra(CREATE_DATA,"");
            }
            return intent;
        }

        @Override
        public Student parseResult(int resultCode, @Nullable Intent result) {
            if(resultCode == Activity.RESULT_OK){
                return requireNonNull(result).getParcelableExtra(RESULT_DATA);
            }
            return null;
        }
    }

    public static class TakePhotoContract extends ActivityResultContract<Student,Student> {

        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, @NonNull Student input) {
            Intent intent = new Intent(context, TakePhotoActivity.class);
            intent.putExtra(STUDENT_DATA,input);
            return intent;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public Student parseResult(int resultCode, @Nullable Intent intent) {
            if(resultCode == Activity.RESULT_OK){
                return requireNonNull(intent).getParcelableExtra(PHOTO_TAKEN_STUDENT);
            }

            return null;
        }
    }
}
