package com.urzaizcoding.subscriber.utils.file;

import com.urzaizcoding.subscriber.persistence.domain.Student;

import java.io.File;

public interface StudentPhotoManager {
    FileBundle generateUriForStudentPhoto(Student student) throws Exception;
    boolean savePhoto(File toSave) throws Exception;

    File getPhoto(Student student) throws Exception;

    void deletedCachedPhoto(String name) throws Exception;

    void deletePhoto(String name) throws Exception;

    String photoDescription(File file);
}
