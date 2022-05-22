package com.urzaizcoding.subscriber.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.persistence.repository.StudentRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class StudentViewModel extends ViewModel {
    private final StudentRepository studentRepository;
    private LiveData<List<Student>> allStudents;

    public StudentViewModel(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        allStudents = studentRepository.fetchAllStudents();
    }

    public CompletableFuture<Long> saveStudent(Student student) {
        return studentRepository.saveStudent(student);
    }

    public void removeStudent(Student student) {
        studentRepository.removeStudent(student);
    }

    public CompletableFuture<Optional<Student>> fetchOneStudent(long id) {
        return studentRepository.fetchOneStudent(id);
    }

    public LiveData<List<Student>> getAllStudents() {
        return allStudents;
    }
}
