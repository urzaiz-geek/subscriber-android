package com.urzaizcoding.subscriber.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.urzaizcoding.subscriber.persistence.repository.StudentRepository;

public class StudentViewModelFactory implements ViewModelProvider.Factory {
    private StudentRepository studentRepository;

    public StudentViewModelFactory(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(!modelClass.equals(StudentViewModel.class)) return null;
        return (T) new StudentViewModel(studentRepository);
    }
}
