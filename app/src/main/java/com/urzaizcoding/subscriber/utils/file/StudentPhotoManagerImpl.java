package com.urzaizcoding.subscriber.utils.file;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.urzaizcoding.subscriber.persistence.domain.Student;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class StudentPhotoManagerImpl implements StudentPhotoManager {
    private static final String PHOTO_FORMAT = "JPEG";
    private static final String PHOTO_DIR = "ppstore";
    private static final String CACHED_PIC_DIR = "tmppics";
    private static final String FORMAT = "yyyyMMddHHmmss";
    private static final SimpleDateFormat FORMATTER;
    private static final String AUTHORITY = "com.urzaizcoding.fileProvider";
    private static final String EXTENSION = "jpg";
    private final Context context;
    private File cacheDir;
    private File picStore;
    private final FileStorageService fileStorageService;

    static{
        FORMATTER = new SimpleDateFormat(FORMAT);
    }

    private void init() throws Exception {
        //check if all the required folders of app are present if not create them
        cacheDir = Arrays.stream(Objects.requireNonNull(context.getCacheDir().listFiles())).filter(file -> file.isDirectory() && file.getName().equals(CACHED_PIC_DIR))
                .findFirst().orElse(new File(context.getCacheDir(),CACHED_PIC_DIR));

        if(!cacheDir.exists()){
            if(!cacheDir.mkdir()){
                throw new Exception("Unable to create cache picture directory");
            }
        }

        picStore = Arrays.stream(Objects.requireNonNull(context.getFilesDir().listFiles())).filter(file -> file.isDirectory() && file.getName().equals(PHOTO_DIR))
                .findFirst().orElse(new File(context.getFilesDir(),PHOTO_DIR));

        if(!picStore.exists()){
            if(!picStore.mkdir()){
                throw new Exception("Unable to create picture store directory");
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public StudentPhotoManagerImpl(FileStorageService fileStorageService, @NonNull Context context) throws Exception {
        this.context = context;
        this.fileStorageService = fileStorageService;
        init();
    }

    @NonNull
    private static String generatePhotoName(@NonNull Student student) throws IllegalArgumentException{
        if(student == null){
            throw new IllegalArgumentException("Received Student is null");
        }
        return String.format("%s_%s_%d%s.%s", PHOTO_FORMAT, FORMATTER.format(new Date()), student.getId(), student.getFirstname(),EXTENSION);
    }

    @Override
    public FileBundle generateUriForStudentPhoto(@NonNull Student student) throws Exception {
        String filename = generatePhotoName(student);

        File tmpPhoto = fileStorageService.createFile(filename, cacheDir.getAbsolutePath());

        return new FileBundle(SubscriberFileProvider.getUriForFile(context,AUTHORITY,tmpPhoto),
                tmpPhoto.getAbsolutePath());
    }

    @Override
    public boolean savePhoto(@NonNull File toSave) throws Exception {
        if(fileStorageService.saveFile(toSave.getName(),picStore.getAbsolutePath(),toSave)){
            toSave.delete();
        }else {
            return false;
        }

        return true;
    }

    @Override
    public File getPhoto(@NonNull Student student) throws Exception {
        return  fileStorageService.getFile(student.getPhotoPath(),picStore.getAbsolutePath());
    }

    @Override
    public void deletedCachedPhoto(@NonNull String name) throws Exception {
        if(name == null){
            throw new IllegalArgumentException("The picture name cannot be null");
        }

        fileStorageService.deleteFile(name,cacheDir.getAbsolutePath());
    }

    @Override
    public void deletePhoto(String name) throws Exception {
        if(name == null){
            throw new IllegalArgumentException("The picture name cannot be null");
        }

        fileStorageService.deleteFile(name,picStore.getAbsolutePath());
    }

    @Override
    public String photoDescription(@NonNull File file) throws IllegalArgumentException{
        if(file == null){
            throw new IllegalArgumentException("The file cannot be null");
        }
        return String.format("name : %s\npath: %s\nsize: %d bytes\nformat: %s",
                file.getName(),file.getAbsoluteFile(),file.length(),getFormat(file.getName()));
    }

    private String getFormat(String name) {
        String fileExtension = name.substring(name.lastIndexOf(".")+1);
        switch (fileExtension){
            case "jpg":
                return "JPEG";
            case "png":
                return "PNG (Portable Network Graphic)";
            case "bmp":
                return "Bitmap";
        }

        return "";
    }
}
