package com.urzaizcoding.subscriber.utils.file;

import static com.google.common.truth.Truth.assertThat;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.urzaizcoding.subscriber.persistence.domain.Sex;
import com.urzaizcoding.subscriber.persistence.domain.Student;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(AndroidJUnit4.class)
public class StudentPhotoManagerImplTest {
    private StudentPhotoManager underTest;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        underTest = new StudentPhotoManagerImpl(new LocalFileStorageService(),
                ApplicationProvider.getApplicationContext());
    }

    @Test
    public void generateUriForStudentPhotoShouldGenerateUriWhenArgsAreCorrect() throws Exception {
        //Given
        final Student student = Student.StudentBuilder.getInstance("Tresor",
                "13/11/1998",
                Sex.MALE).build();
        student.setId(9);

        //When

        final FileBundle result = underTest.generateUriForStudentPhoto(student);

        //Then

        assertThat(new File(result.getLocaleAbsolutePath()).exists()).isTrue();
        assertThat(new File(result.getLocaleAbsolutePath()).isFile()).isTrue();
        assertThat(result.getFileName()).contains(student.getFirstname());
        assertThat(result.getFileName()).contains(String.valueOf(student.getId()));
        new File(result.getLocaleAbsolutePath()).delete();

    }

    @Test
    public void generateUriForStudentPhotoShouldThrowUsefulExceptionIfStudentIsNull() throws Exception {
        //Given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Received Student is null");

        //When-Then
        underTest.generateUriForStudentPhoto(null);

    }

    @Test
    public void savePhotoShouldSaveAGivenPhotoFile() throws Exception {
        //Given
        final File toSave = new File(ApplicationProvider.getApplicationContext().getCacheDir(), "dummy.jpg");
        toSave.createNewFile();
        final File photoStore = new File(ApplicationProvider.getApplicationContext().getFilesDir(), "ppstore");


        //When
        final boolean result = underTest.savePhoto(toSave);

        //Then
        final File resultFile = new File(photoStore,toSave.getName());
        assertThat(result).isTrue();
        assertThat(resultFile.exists()).isTrue();
        assertThat(resultFile.isFile()).isTrue();
        assertThat(resultFile.getName().substring(resultFile.getName().lastIndexOf("."))).containsMatch("jpg");
        assertThat(toSave.exists()).isFalse();
        resultFile.delete();
    }

    @Test
    public void shouldSaveAndGetPhoto() throws Exception{
        //Given
        final Student student = Student.StudentBuilder
                .getInstance("Tresor","13/11/1998",Sex.MALE)
                .build();
        final File photoStore = new File(ApplicationProvider.getApplicationContext().getFilesDir(), "ppstore");
        final File pic = new File(photoStore,"tresor_urzaiz.jpg");

        pic.createNewFile();

        student.setPhotoPath(pic.getName());

        //When
        final File result = underTest.getPhoto(student);

        //Then

        assertThat(result).isNotNull();
        assertThat(result.exists()).isTrue();
        assertThat(result.isFile()).isTrue();
        assertThat(result.getName()).contains(student.getPhotoPath());
        pic.delete();
    }


}