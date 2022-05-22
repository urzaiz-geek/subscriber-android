package com.urzaizcoding.subscriber.persistence.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.urzaizcoding.subscriber.persistence.dao.StudentDao;
import com.urzaizcoding.subscriber.persistence.domain.Sex;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RunWith(MockitoJUnitRunner.class)
public class LocalStudentRepositoryTest {
    private StudentRepository underTest;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private StudentDao dao;

    @Mock
    private StudentPhotoManager manager;


    @Before
    public void setUp() throws Exception {
        underTest = new LocalStudentRepository(dao,manager);
    }

    @Test
    public void shouldTestIfWhenSaveIsCalledAddFromDaoIsCalled() throws Exception{
        //Given
        final Student student = Student.StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.MALE)
                .lastName("Sehaliah")
                .build();
        when(dao.addOrUpdate(student)).thenReturn(1L);

        //When
        final long result = underTest.saveStudent(student).get(1, TimeUnit.SECONDS);

        //Then
        assertEquals(1L,result);
        verify(dao,times(1)).addOrUpdate(student);
    }

    @Test
    public void shouldTestIfWhenRemoveIsCalledThenTheDaoDeleteIsCalledWithTheStudent() throws Exception{
        //Given
        final Student student = Student.StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.MALE)
                .lastName("Sehaliah")
                .photoPath("salute")
                .build();

        doNothing().when(dao).delete(student);

        //When
        underTest.removeStudent(student);

        //Then
        verify(dao, atLeastOnce()).delete(any(Student.class));
        verify(manager,atLeastOnce()).deletePhoto(any(String.class));


    }

    @Test
    public void shouldTestIfWhenFetchStudentIsCalledThenDaoGetIsCalled() throws Exception{
        //Given
        final long idToFetch = 1;

        when(dao.getById(idToFetch)).thenReturn(Optional.ofNullable(Student.StudentBuilder
                .getInstance("Jeff","3/11/2003", Sex.MALE)
                .lastName("Sehaliah")
                .build()));
        //When
        final Optional<Student> result = underTest.fetchOneStudent(idToFetch).get(1, TimeUnit.SECONDS);

        //Then

        verify(dao,times(1)).getById(idToFetch);
        assertTrue(result.isPresent());
        assertTrue(result.get().getFirstname().equals("Jeff"));
        assertTrue(result.get().getLastname().equals("Sehaliah"));
        assertTrue(result.get().getBirthDate().equals("3/11/2003"));
        assertTrue(result.get().getSex().equals(Sex.MALE));
    }

    @Test
    public void shouldTestIfWhenFetchAllStudentCalledThenDaoGetAllCalled() throws Exception{
        //When
        underTest.fetchAllStudents();
        //Then
        verify(dao,times(1)).getAll();
    }
}