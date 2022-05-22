package com.urzaizcoding.subscriber.persistence.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.urzaizcoding.subscriber.persistence.domain.Student;

import java.util.List;
import java.util.Optional;

@Dao
public abstract class StudentRoomDao implements StudentDao {

    @Query(value = "SELECT * FROM student")
    public abstract LiveData<List<Student>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long addOrUpdate(Student student);

    @Query(value = "SELECT * FROM student WHERE id=:id")
    public abstract Optional<Student> getById(long id);

    @Query(value = "DELETE FROM student WHERE id=:id")
    public abstract void delete(long id);

    @Query(value = "DELETE FROM student")
    public abstract void deleteAll();
}
