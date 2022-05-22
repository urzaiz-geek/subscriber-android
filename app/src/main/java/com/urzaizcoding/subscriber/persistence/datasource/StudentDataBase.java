package com.urzaizcoding.subscriber.persistence.datasource;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.urzaizcoding.subscriber.persistence.dao.StudentRoomDao;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.task.AppExecutors;

@Database(entities = {Student.class},exportSchema = false,version = 1)
public abstract class StudentDataBase extends RoomDatabase {
    private static final String DATABASE_NAME = "student.db";
    private static final RoomDatabase.Callback MANAGEMENT_CALL_BACK;

    static {
        MANAGEMENT_CALL_BACK = new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                AppExecutors.getInstance().diskIO().execute(
                        () -> {
                            instance.studentDao().deleteAll();
                        }
                );
            }
        };
    }

    public abstract StudentRoomDao studentDao();

    private static volatile StudentDataBase instance;

    public static StudentDataBase getInstance(Context context){
        if(instance == null){
            synchronized (StudentDataBase.class){
                instance = Room.databaseBuilder(context,StudentDataBase.class,DATABASE_NAME)
                        .addCallback(MANAGEMENT_CALL_BACK)
                        .build();
            }
        }
        return instance;
    }


}
