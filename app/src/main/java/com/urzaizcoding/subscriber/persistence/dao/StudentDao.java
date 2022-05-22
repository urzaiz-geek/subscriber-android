package com.urzaizcoding.subscriber.persistence.dao;

import androidx.lifecycle.LiveData;

import com.urzaizcoding.subscriber.persistence.domain.Student;

import java.util.List;
import java.util.Optional;

public interface StudentDao {
    LiveData<List<Student>> getAll();
    long addOrUpdate(Student student);
    Optional<Student> getById(long id);
    void delete(long id);
    void deleteAll();

    default void delete(Student student){
        delete(student.getId());
    }
}
