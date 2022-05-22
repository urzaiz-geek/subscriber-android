package com.urzaizcoding.subscriber.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.urzaizcoding.subscriber.persistence.datasource.StudentDataBase;
import com.urzaizcoding.subscriber.persistence.domain.Sex;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.task.AppExecutors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class LocalStudentRepositoryITest {

    private StudentRepository underTest;
    private CountDownLatch latch;

    @Before
    public void setUp() {
        StudentDataBase testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),StudentDataBase.class)
                .build();
        underTest = new LocalStudentRepository(testDb.studentDao());
    }

    @Test
    public void notnull() {
        assertNotNull(underTest.fetchAllStudents());
    }

    @Test
    public void saveAListAndRemoveAllShouldReturnEmptyTest() throws InterruptedException {
        //Given
        final Student[] students = new Student[] {
                Student.StudentBuilder
                        .getInstance("Nguepnang","21/11/2021", Sex.FEMALE)
                        .lastName("Jaelle")
                        .build(),
                Student.StudentBuilder
                        .getInstance("Tresor","13/11/1997", Sex.FEMALE)
                        .lastName("Urzaiz")
                        .build(),
                Student.StudentBuilder
                        .getInstance("Eyael","21/2/2000", Sex.FEMALE)
                        .lastName("Cheryl")
                        .build(),
                Student.StudentBuilder
                        .getInstance("Jeff","3/11/2003", Sex.FEMALE)
                        .lastName("Sehaliah")
                        .build()
        };
        //When
        LiveData<List<Student>> toObserve = underTest.fetchAllStudents();
        final List [] datas = new List[1];
        AppExecutors.getInstance().uiThread().execute(() -> toObserve.observeForever((list) -> datas[0] = list));
        for(Student student : students){
            underTest.saveStudent(student);
        }
        underTest.removeAllStudents();
        latch = new CountDownLatch(1);
        latch.await(1,TimeUnit.SECONDS); //little delay for operation completion

        //Then
        assertTrue(datas[0].isEmpty());

    }

    @Test
    public void saveAndFetchOfAStudentShouldReturnTheSavedStudentTest() throws Exception {
        //Given
        final Student student = Student.StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.MALE)
                .lastName("Sehaliah")
                .build();

        //When
        final long insertedId = underTest.saveStudent(student).get(1, TimeUnit.SECONDS);
        student.setId(insertedId);
        final Optional<Student> result = underTest.fetchOneStudent(insertedId).get(1, TimeUnit.SECONDS);

        //Then
        assertEquals(student,result.orElseThrow(Exception::new));
    }

    @Test
    public void saveUpdateAndFetchShouldReturnTheUpdatedTest() throws Exception{
        //Given
        final Student student = Student.StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.MALE)
                .lastName("Sehaliah")
                .build();

        //When
        final long insertedId = underTest.saveStudent(student).get(500, TimeUnit.MILLISECONDS);
        student.setId(insertedId);
        student.setCourse("Genie Logiciel");
        student.setGrade((short) 3);
        underTest.saveStudent(student).get(500,TimeUnit.MILLISECONDS);
        final Optional<Student> result = underTest.fetchOneStudent(insertedId).get(500, TimeUnit.MILLISECONDS);

        //Then
        assertEquals(student,result.orElseThrow(Exception::new));
    }

    @Test
    public void saveDeleteAndFetchShouldReturnEmptyTest() throws Exception{
        //Given
        Student student = Student.StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.MALE)
                .lastName("Sehaliah")
                .build();

        //When

        final long insertedId = underTest.saveStudent(student).get(1, TimeUnit.SECONDS);
        student = underTest.fetchOneStudent(insertedId).get(1, TimeUnit.SECONDS).get();
        latch = new CountDownLatch(1);
        underTest.removeStudent(student);
        latch.await(500, TimeUnit.MILLISECONDS);
        final Optional<Student> result = underTest.fetchOneStudent(student.getId()).get(1,TimeUnit.SECONDS);

        //Then
        assertFalse(result.isPresent());

    }

    @Test
    public void saveAListAndFetchAllShouldReturnTheSavedList() throws Exception {
        //Given
        final Student [] students = new Student[] {
                Student.StudentBuilder
                        .getInstance("Nguepnang","21/11/2021", Sex.FEMALE)
                        .lastName("Jaelle")
                        .build(),
                Student.StudentBuilder
                        .getInstance("Tresor","13/11/1997", Sex.FEMALE)
                        .lastName("Urzaiz")
                        .build(),
                Student.StudentBuilder
                        .getInstance("Eyael","21/2/2000", Sex.FEMALE)
                        .lastName("Cheryl")
                        .build(),
                Student.StudentBuilder
                        .getInstance("Jeff","3/11/2003", Sex.FEMALE)
                        .lastName("Sehaliah")
                        .build()
        };

        //When

        final List<Student>[] datas = new List[1];

        latch = new CountDownLatch(4);

        final LiveData<List<Student>> toObserve = underTest.fetchAllStudents();
        AppExecutors.getInstance().uiThread().execute(() -> toObserve.observeForever(list -> {
            latch.countDown();
            datas[0] = list;
        }));


        for(Student student : students){
            underTest.saveStudent(student);
        }

        latch.await(2, TimeUnit.SECONDS);

        //Then
        assertEquals(students.length,datas[0].size());
        assertTrue(datas[0].stream().allMatch(student -> Arrays.stream(students).anyMatch(innerStudent -> student.getBirthDate().equals(innerStudent.getBirthDate()) &&
                student.getFirstname().equals(innerStudent.getFirstname()) &&
                student.getLastname().equals(innerStudent.getLastname()) &&
                student.getSex().equals(innerStudent.getSex()))));
    }


}