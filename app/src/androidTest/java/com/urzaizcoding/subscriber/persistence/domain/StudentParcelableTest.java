package com.urzaizcoding.subscriber.persistence.domain;

import static com.urzaizcoding.subscriber.persistence.domain.Sex.MALE;
import static org.junit.Assert.assertEquals;

import android.os.Parcel;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class StudentParcelableTest {
    private Student underTest;

    @Before
    public void setup(){
        underTest = Student.StudentBuilder.getInstance("Tresor","13/11/1997",MALE)
                .lastName("Urzaiz")
                .course("Genie Logiciel")
                .grade((short) 3)
                .photoPath("/path")
                .build();
    }

    @Test
    public void shouldParcelAStudentAndGetItBackFromParcel() {

        //When
        Parcel parcel = Parcel.obtain();
        underTest.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        final Student result = Student.CREATOR.createFromParcel(parcel);
        Log.d(getClass().getName(),"expected : "+underTest);
        Log.d(getClass().getName(),"result : "+result);
        //Then
        assertEquals(underTest,result);
    }
}
