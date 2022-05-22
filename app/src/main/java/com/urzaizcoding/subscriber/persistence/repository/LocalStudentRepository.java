package com.urzaizcoding.subscriber.persistence.repository;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.urzaizcoding.subscriber.persistence.dao.StudentDao;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManager;
import com.urzaizcoding.subscriber.utils.task.AppExecutors;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class LocalStudentRepository implements StudentRepository{
    private final StudentDao studentRoomDao;
    private final LiveData<List<Student>> allStudentsToObserve;
    private final StudentPhotoManager studentPhotoManager;

    public LocalStudentRepository(StudentDao dao,StudentPhotoManager studentPhotoManager) {
        this.studentPhotoManager = studentPhotoManager;
        studentRoomDao = dao;
        allStudentsToObserve = studentRoomDao.getAll();
    }

    @Override
    public LiveData<List<Student>> fetchAllStudents(){
        return allStudentsToObserve;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CompletableFuture<Long> saveStudent(Student student) {
        return CompletableFuture.supplyAsync(() -> studentRoomDao.addOrUpdate(student), AppExecutors.getInstance().diskIO());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void removeStudent(Student student){
        CompletableFuture<Void> deletionTask = CompletableFuture.runAsync(() -> studentRoomDao.delete(student),
            AppExecutors.getInstance().diskIO()
        );

        if(!student.getPhotoPath().isEmpty()){
            deletionTask.thenRunAsync(() -> {
                try {
                    studentPhotoManager.deletePhoto(student.getPhotoPath());
                } catch (Exception e) {
                    Log.e(getClass().getName(),"Error while attempting to delete student photo : "+e.getMessage());
                }
            },AppExecutors.getInstance().diskIO());
        }
    }

    @Override
    public void removeAllStudents() {
        AppExecutors.getInstance().diskIO().execute(studentRoomDao::deleteAll);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public CompletableFuture<Optional<Student>> fetchOneStudent(long id) {
        return CompletableFuture.supplyAsync(() -> studentRoomDao.getById(id),AppExecutors.getInstance().diskIO());
    }
}
