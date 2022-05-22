package com.urzaizcoding.subscriber.persistence;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.urzaizcoding.subscriber.persistence.dao.StudentDao;
import com.urzaizcoding.subscriber.persistence.datasource.StudentDataBase;
import com.urzaizcoding.subscriber.persistence.domain.Sex;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.persistence.domain.Student.StudentBuilder;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class StudentDataBaseITTest {
    private StudentDataBase underTest;
    private StudentDao dao;
    Handler mainUiHandler;
    Observer<List<Student>> observer;
    private CountDownLatch latch;

    @Before
    public void setUp() throws Exception {
        underTest = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),StudentDataBase.class)
                .allowMainThreadQueries()
                .build();
        dao = underTest.studentDao();
    }

    @After
    public void tearDown() throws Exception {
        underTest.close();
    }

    @Test
    public void insertAndGetStudentFromDataBase() throws Exception{
        //Given
        final Student student = StudentBuilder
                .getInstance("Tresor","13/11/1998", Sex.MALE).build();

        //When
        long insertedId = dao.addOrUpdate(student);
        final Optional<Student> result = dao.getById(insertedId);

        //Then
        assertTrue(result.isPresent());
        assertEquals(student.getFirstname(),result.get().getFirstname());
        assertEquals(student.getBirthDate(), result.get().getBirthDate());
        assertEquals(student.getSex(), result.get().getSex());
    }

    @Test
    public void insertUpdateAndGetStudent() throws Exception{
        //Given
        final Student student = StudentBuilder
                .getInstance("Tresor","13/11/1998", Sex.MALE).build();
        //When
        long insertedId = dao.addOrUpdate(student);
        student.setId(insertedId);
        student.setGrade((short) 3);
        student.setCourse("Genie Logiciel");
        dao.addOrUpdate(student);
        final Optional<Student> result = dao.getById(insertedId);

        //Then
        assertEquals(student,result.orElseThrow(Exception::new));
    }

    @Test
    public void insertAndDeleteOneStudentInAListOfStudents() throws Exception {
        //Given
        final Student student = StudentBuilder
                .getInstance("Nguepnang","21/11/2021", Sex.FEMALE)
                .lastName("Jaelle")
                .build();
        final Student student1 = StudentBuilder
                .getInstance("Tresor","13/11/1997", Sex.FEMALE)
                .lastName("Urzaiz")
                .build();
        final Student student2 = StudentBuilder
                .getInstance("Eyael","21/2/2000", Sex.FEMALE)
                .lastName("Cheryl")
                .build();
        final Student student3 = StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.FEMALE)
                .lastName("Sehaliah")
                .build();

        //When
        long insertedId = dao.addOrUpdate(student);
        dao.addOrUpdate(student1);
        dao.addOrUpdate(student2);
        dao.addOrUpdate(student3);

        Log.d(getClass().getName(),"id : "+insertedId);
        dao.delete(insertedId);
        final Optional<Student> result = dao.getById(insertedId);

        //Then

        assertTrue(!result.isPresent());
    }

    @Test
    public void shouldInsertAListOfStudentAndGetThemAll() throws Exception{
        //Given
        final Student [] students = new Student[] {
        StudentBuilder
                .getInstance("Nguepnang","21/11/2021", Sex.FEMALE)
                .lastName("Jaelle")
                .build(),
        StudentBuilder
                .getInstance("Tresor","13/11/1997", Sex.FEMALE)
                .lastName("Urzaiz")
                .build(),
        StudentBuilder
                .getInstance("Eyael","21/2/2000", Sex.FEMALE)
                .lastName("Cheryl")
                .build(),
        StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.FEMALE)
                .lastName("Sehaliah")
                .build()
        };
        mainUiHandler = new Handler(Looper.getMainLooper());
        latch = new CountDownLatch(4);

        final List<Student> [] datas = new List [1]; //to hold the observed list



        //When

        final LiveData<List<Student>> toObserve = dao.getAll();

        observer = new Observer<List<Student>>() {
            @Override
            public void onChanged(List<Student> studentList) {
                Log.d(getClass().getName(),"fired");
                latch.countDown();
                datas[0] = studentList;
            }
        };

        mainUiHandler.post(() -> {
            toObserve.observeForever(observer);
        });

        for(Student student: students){
            dao.addOrUpdate(student); //populating the table
        }

        latch.await(2, TimeUnit.SECONDS); //wait 2 seconds max





        //Then
        assertEquals(datas[0].size(),students.length);
        assertTrue(datas[0].stream().allMatch(student -> {
            return Arrays.stream(students).anyMatch(innerStudent -> {
                return student.getBirthDate().equals(innerStudent.getBirthDate()) &&
                        student.getFirstname().equals(innerStudent.getFirstname()) &&
                        student.getLastname().equals(innerStudent.getLastname()) &&
                        student.getSex().equals(innerStudent.getSex());
            });
        }));
    }


}