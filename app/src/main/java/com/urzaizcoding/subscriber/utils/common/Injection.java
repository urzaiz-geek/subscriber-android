package com.urzaizcoding.subscriber.utils.common;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.urzaizcoding.subscriber.persistence.dao.StudentDao;
import com.urzaizcoding.subscriber.persistence.datasource.StudentDataBase;
import com.urzaizcoding.subscriber.persistence.repository.LocalStudentRepository;
import com.urzaizcoding.subscriber.persistence.repository.StudentRepository;
import com.urzaizcoding.subscriber.utils.file.LocalFileStorageService;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManager;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManagerImpl;
import com.urzaizcoding.subscriber.utils.file.SubscriberFileProvider;

public class Injection {
    private static StudentRepository repoInstance;
    private static DateValidatorService dateValidatorInstance;
    private static SubscriberFileProvider fileProviderInstance;
    private static StudentPhotoManager studentPhotoManagerInstance;

    public static StudentDao daoProvider(Context applicationContext) {
        return StudentDataBase.getInstance(applicationContext).studentDao();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static StudentRepository localRepositoryProvider(Context applicationContext) throws Exception {
        if(repoInstance == null){
            synchronized (Injection.class){
                repoInstance = new LocalStudentRepository(daoProvider(applicationContext),studentPhotoManagerProvider(applicationContext));
            }
        }
        return repoInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static DateValidatorService dateValidatorProvider(){
        if(dateValidatorInstance == null){
            synchronized (Injection.class){
                dateValidatorInstance = new FrenchDateValidator();
            }
        }
        return dateValidatorInstance;
    }

    public static SubscriberFileProvider subscriberFileProvider() {
        if(fileProviderInstance == null){
            synchronized (Injection.class){
                fileProviderInstance = new SubscriberFileProvider();
            }
        }
        return fileProviderInstance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static StudentPhotoManager studentPhotoManagerProvider(Context context) throws Exception {
        if(studentPhotoManagerInstance == null){
            synchronized (Injection.class){
                studentPhotoManagerInstance = new StudentPhotoManagerImpl(new LocalFileStorageService(),context);
            }
        }
        return studentPhotoManagerInstance;
    }
}
