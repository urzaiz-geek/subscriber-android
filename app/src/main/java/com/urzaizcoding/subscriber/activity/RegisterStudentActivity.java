package com.urzaizcoding.subscriber.activity;

import static com.urzaizcoding.subscriber.activity.ActivitiesContracts.CREATE_DATA;
import static com.urzaizcoding.subscriber.activity.ActivitiesContracts.EDIT_DATA;
import static com.urzaizcoding.subscriber.activity.ActivitiesContracts.RESULT_DATA;
import static com.urzaizcoding.subscriber.activity.MainActivity.EXTRA_CHAIN;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.urzaizcoding.subscriber.R;
import com.urzaizcoding.subscriber.databinding.ActivityRegisterStudentBinding;
import com.urzaizcoding.subscriber.persistence.domain.Sex;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.common.DateValidatorService;
import com.urzaizcoding.subscriber.utils.common.Injection;

public class RegisterStudentActivity extends AppCompatActivity {

    private ActivityRegisterStudentBinding ui;
    private final ActivityResultLauncher<Student> takePhotoLauncher;
    private DateValidatorService dateValidatorService;
    private Student currentStudent;
    private long currentId;
    private Intent callingIntent;

    {
        takePhotoLauncher = registerForActivityResult(new ActivitiesContracts.TakePhotoContract(), result -> {
            Log.d(getClass().getName(),"Returned from taking photo");
            if(callingIntent.hasExtra(EXTRA_CHAIN)){
                Log.d(getClass().getName(),"Has Extra chain");
                Intent listIntent = new Intent(this,StudentListViewActivity.class);
                listIntent.putExtra(RESULT_DATA,result);
                startActivity(listIntent);
                finishAfterTransition();
            }else if(callingIntent.hasExtra(EDIT_DATA) || callingIntent.hasExtra(CREATE_DATA)) {
                Log.d(getClass().getName(),"Doesn't Have Extra chain");
                Intent resultData = new Intent();
                resultData.putExtra(RESULT_DATA,result);
                setResult(RESULT_OK,resultData);
                finishAfterTransition();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateValidatorService = Injection.dateValidatorProvider();

        ui = ActivityRegisterStudentBinding.inflate(getLayoutInflater());
        ui.buttonNext.setOnClickListener(this::beginEnrollStudent);
        setContentView(ui.getRoot());

        callingIntent = getIntent();
        if(callingIntent.hasExtra(EDIT_DATA)){
            //we want to edit the student
            Student studentToEdit = callingIntent.getParcelableExtra(EDIT_DATA);
            if(studentToEdit != null){
                Log.d(getClass().getName(),"Student is not null");
                fillForm(studentToEdit);
            } else{
              setResult(RESULT_CANCELED);
              finish();
            }

        }
    }

    private void fillForm(Student studentToEdit) {
        ui.editTextFirstName.setText(studentToEdit.getFirstname());
        ui.editTextLastName.setText(studentToEdit.getLastname());
        ui.editTextBirthDate.setText(studentToEdit.getBirthDate());
        ui.editTextCourse.setText(studentToEdit.getCourse());
        ui.editTextGrade.setText(String.valueOf(studentToEdit.getGrade()));
        ui.spinnerSex.setSelection(studentToEdit.getSex() == Sex.MALE?
                0:studentToEdit.getSex() == Sex.FEMALE?
                1:2);
        Log.d(getClass().getName(),"id of edited student : "+studentToEdit.getId());
        currentId = studentToEdit.getId();
    }

    private void beginEnrollStudent(View view) {
        if(!formHasErrors()){
            currentStudent = fromFormParsing();

            if(callingIntent.hasExtra(EDIT_DATA)){
                currentStudent.setId(currentId);
                Log.d(getClass().getName(), "current id : "+currentId);
            }
            takePhotoLauncher.launch(currentStudent);
        }
    }


    private Student fromFormParsing(){
        String firstname = ui.editTextFirstName.getText().toString();
        String birthDate = ui.editTextBirthDate.getText().toString();
        Sex sex = ui.spinnerSex.getSelectedItemPosition() == 0?
                Sex.MALE:ui.spinnerSex.getSelectedItemPosition() == 1?
                Sex.FEMALE:Sex.OTHER;

        return Student.StudentBuilder.getInstance(firstname,birthDate,sex)
                .lastName(ui.editTextLastName.getText().toString())
                .course(ui.editTextCourse.getText().toString())
                .grade(Short.parseShort(ui.editTextGrade.getText().toString()))
                .build();
    }

    private boolean formHasErrors(){
        String firstname = ui.editTextFirstName.getText().toString();
        String birthDate = ui.editTextBirthDate.getText().toString();

        if(firstname.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.error_name_message), Toast.LENGTH_SHORT).show();
            return true;
        }

        if(birthDate.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.error_birth_date_message), Toast.LENGTH_SHORT).show();
            return true;
        }

        if(!dateValidatorService.validate(birthDate)){
            Toast.makeText(this, getResources().getString(R.string.error_birth_date_message_inv), Toast.LENGTH_SHORT).show();
            return true;
        }

        try {
            short number = Short.parseShort(ui.editTextGrade.getText().toString());
            if(number <= 0 || number >10){
                Toast.makeText(this, getResources().getString(R.string.error_grade_message), Toast.LENGTH_SHORT).show();
                return true;
            }
        }catch (NumberFormatException e){
            Toast.makeText(this, getResources().getString(R.string.error_grade_message), Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //TODO delete photo if already taken
        setResult(RESULT_CANCELED);
        finishAfterTransition();
    }
}