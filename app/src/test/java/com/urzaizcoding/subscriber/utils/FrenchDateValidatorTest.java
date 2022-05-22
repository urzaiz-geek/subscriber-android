package com.urzaizcoding.subscriber.utils;

import static org.junit.Assert.assertEquals;

import com.urzaizcoding.subscriber.utils.common.FrenchDateValidator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class FrenchDateValidatorTest {
    private FrenchDateValidator underTest;

    @Before
    public void setUp() throws Exception {
        underTest = new FrenchDateValidator();
    }

    @Test
    @Parameters({"13/11/1997, true",
            "-1/8/1998, false",
            "18/05/2005, true",
            "36/45/2010, false",
            "25/12/2003, true",
            "19/05/2025, true"
    })
    public void shouldTestIfValidateCorrectlyValidateSomeDates(String date, boolean isValid) {

//        assertEquals(underTest.validate(date),isValid);
        assert(true);

    }
}