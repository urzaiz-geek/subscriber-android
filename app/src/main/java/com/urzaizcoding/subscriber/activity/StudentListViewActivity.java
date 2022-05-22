package com.urzaizcoding.subscriber.activity;


import static com.urzaizcoding.subscriber.activity.ActivitiesContracts.RESULT_DATA;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.urzaizcoding.subscriber.R;
import com.urzaizcoding.subscriber.databinding.ActivityStudentListViewBinding;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.ui.StudentItemTouchHelper;
import com.urzaizcoding.subscriber.ui.StudentRecyclerViewAdapter;
import com.urzaizcoding.subscriber.ui.StudentViewModel;
import com.urzaizcoding.subscriber.ui.StudentViewModelFactory;
import com.urzaizcoding.subscriber.utils.common.Injection;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManager;

public class StudentListViewActivity extends AppCompatActivity {

    private StudentViewModel studentViewModel;
    private final ActivityResultLauncher<Student> addingEditingLauncher;

    {
        addingEditingLauncher = registerForActivityResult(new ActivitiesContracts.CreateEditStudentContract()
                ,result -> {
                    if(result != null){
                        studentViewModel.saveStudent(result);
                        Toast.makeText(this,
                                getString(R.string.student_added_message),
                                Toast.LENGTH_SHORT).show();
                    }

                });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.urzaizcoding.subscriber.databinding.ActivityStudentListViewBinding ui = ActivityStudentListViewBinding.inflate(getLayoutInflater());
        setContentView(ui.getRoot());
        ui.floatingActionButtonAdd.setOnClickListener(
                (btn) -> addingEditingLauncher.launch(null)
        );
        ui.recyclerViewListStudent.setLayoutManager(new LinearLayoutManager(this));
        ui.recyclerViewListStudent.setHasFixedSize(true);

        try {
            studentViewModel = new ViewModelProvider(this,new StudentViewModelFactory(Injection.localRepositoryProvider(getApplicationContext())))
                    .get(StudentViewModel.class);
        } catch (Exception e) {
            Log.e(getClass().getName(),"Unable To create repository due to : "+e.getMessage());
            Toast.makeText(this, getString(R.string.fatal_listview_message_error), Toast.LENGTH_SHORT).show();
            finish();
        }

        //create adapter

        StudentPhotoManager studentPhotoManager = null;

        try {
            studentPhotoManager = Injection.studentPhotoManagerProvider(this);
        } catch (Exception e) {
            Log.e(getClass().getName(),"Unable to create the photoManager : "+e.getMessage());
        }

        StudentRecyclerViewAdapter studentRecyclerViewAdapter = new StudentRecyclerViewAdapter(studentPhotoManager);
        studentRecyclerViewAdapter.setItemClickListener(addingEditingLauncher::launch);

        StudentItemTouchHelper studentItemTouchHelper = StudentItemTouchHelper.getInstance(studentViewModel,studentRecyclerViewAdapter);
        studentItemTouchHelper.attachToRecyclerView(ui.recyclerViewListStudent);

        ui.recyclerViewListStudent.setAdapter(studentRecyclerViewAdapter);
        studentViewModel.getAllStudents().observe(this, studentRecyclerViewAdapter::submitList);

        Intent intent = getIntent();
        if(intent.hasExtra(RESULT_DATA)){
            Student student = intent.getParcelableExtra(RESULT_DATA);
            studentViewModel.saveStudent(student);
            Toast.makeText(this,
                    getString(R.string.student_added_message),
                    Toast.LENGTH_SHORT).show();
        }

    }
}