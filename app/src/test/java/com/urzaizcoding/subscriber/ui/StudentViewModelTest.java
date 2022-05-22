package com.urzaizcoding.subscriber.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.urzaizcoding.subscriber.persistence.domain.Sex;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.persistence.domain.Student.StudentBuilder;
import com.urzaizcoding.subscriber.persistence.repository.StudentRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RunWith(MockitoJUnitRunner.class)
public class StudentViewModelTest {
    private StudentViewModel underTest;

    @Mock
    private Observer<List<Student>> observer;

    @Mock
    private StudentRepository studentRepository;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MutableLiveData<List<Student>> datas;

    @Before
    public void setUp() throws Exception {
        datas = new MutableLiveData<>();
        when(studentRepository.fetchAllStudents()).thenReturn(datas);
        doNothing().when(observer).onChanged(any());
        underTest = new StudentViewModel(studentRepository);
        underTest.getAllStudents().observeForever(observer);
    }

    @Test
    public void testInitialStateOfViewModel(){
        assertNotNull(underTest.getAllStudents());
        assertTrue(underTest.getAllStudents().hasObservers());
    }

    @Test
    public void shouldTestIfWhenSaveStudentIsCalledRepoIsCalledToo() throws ExecutionException, InterruptedException {
        //Given
        final Student student = StudentBuilder.getInstance("Tresor","13/11/1997", Sex.MALE)
                .lastName("Urzaiz")
                .build();
        when(studentRepository.saveStudent(student)).then(
                invocation -> {
                    CompletableFuture<Long> retour = new CompletableFuture<>();
                    retour.complete(1L);
                    return retour;
                }
        );

        //When
        final CompletableFuture<Long> result = underTest.saveStudent(student);

        //Then
        assertNotNull(result);
        assertEquals(Optional.of(1L), Optional.ofNullable(result.get()));
        verify(studentRepository,times(1)).saveStudent(student);
    }

    @Test
    public void shouldTestIfWhenGettingAStudentRepoFetchIsCalled() throws InterruptedException, ExecutionException, TimeoutException {
        //Given
        final Student student = StudentBuilder.getInstance("Tresor","13/11/1997", Sex.MALE)
                .lastName("Urzaiz")
                .build();
        when(studentRepository.fetchOneStudent(anyLong())).then(invocation -> {
            CompletableFuture<Optional<Student>> retour = new CompletableFuture<>();
            retour.complete(Optional.ofNullable(student));
            return retour;
        });

        //When
        final Optional<Student> result = underTest.fetchOneStudent(1).get(1, TimeUnit.SECONDS);

        //Then
        assertTrue(result.isPresent());
        assertEquals(student,result.get());
        verify(studentRepository,times(1)).fetchOneStudent(anyLong());
    }

    @Test
    public void shouldTestIfWhenRemovingStudentCorrectRepoMethodisCalled(){
        //Given
        final Student student = StudentBuilder.getInstance("Tresor","13/11/1997", Sex.MALE)
                .lastName("Urzaiz")
                .build();
        doNothing().when(studentRepository).removeStudent(student);
        //When
        underTest.removeStudent(student);

        //Then
        verify(studentRepository,times(1)).removeStudent(student);
    }

    @Test
    public void shouldTestIfRepoIsCorrectlyObservedOnDataChange() throws Exception{
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
                /*when we save a new student we should report to the mocked repo that fake data had changed*/
        when(studentRepository.saveStudent(any(Student.class))).then(invocation -> {
            datas.postValue(Arrays.asList(invocation.getArgument(0,Student.class)));
            return new CompletableFuture<Long>();
        });

        //When
        for(Student student: students){
            studentRepository.saveStudent(student);
        }
        //Then
        verify(observer, atLeastOnce()).onChanged(any(List.class));
    }
}