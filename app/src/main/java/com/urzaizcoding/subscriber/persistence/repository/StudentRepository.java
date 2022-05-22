package com.urzaizcoding.subscriber.persistence.repository;

import androidx.lifecycle.LiveData;

import com.urzaizcoding.subscriber.persistence.domain.Student;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface StudentRepository {
    LiveData<List<Student>> fetchAllStudents();
    CompletableFuture<Long> saveStudent(Student student);
    void removeStudent(Student student);
    void removeAllStudents();
    CompletableFuture<Optional<Student>> fetchOneStudent(long id);
}
