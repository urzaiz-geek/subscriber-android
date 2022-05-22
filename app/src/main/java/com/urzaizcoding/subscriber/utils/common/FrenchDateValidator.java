package com.urzaizcoding.subscriber.utils.common;


import android.icu.text.SimpleDateFormat;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.util.Date;


@RequiresApi(api = Build.VERSION_CODES.N)
public class FrenchDateValidator implements DateValidatorService {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private SimpleDateFormat dateTimeFormatter;

    @Override
    public boolean validate(@NonNull String date) {
        dateTimeFormatter = dateTimeFormatter = new SimpleDateFormat(DATE_FORMAT);
        dateTimeFormatter.setLenient(false);
        Date oDate = null;
        try {
            oDate = dateTimeFormatter.parse(date);
        }catch (ParseException e){
            return false;
        }
        if(!dateTimeFormatter.format(oDate).equals(date)){
            return false;
        }
        return true;
    }
}
