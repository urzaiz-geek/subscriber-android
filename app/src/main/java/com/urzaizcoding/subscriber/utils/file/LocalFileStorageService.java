package com.urzaizcoding.subscriber.utils.file;

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class LocalFileStorageService implements FileStorageService{

    @Override
    public boolean saveFile(String fileName, String directory,@NonNull File toSave) throws Exception {

        //how do we save the file?
        String name = fileName;
        String directoryPath = directory;
        //check if required args are not null

        if(toSave == null){
            throw new IllegalArgumentException("Cannot Save a null file");
        }

        if(name == null && directoryPath == null){
            throw new IllegalArgumentException("The directory path must not be null when the file name is null too");
        }else if(name == null){
            name = toSave.getName();
        }

        if(directoryPath == null && name.equals(toSave.getName())){
            throw new IllegalArgumentException("The directory path must not be null when the file name is the same as the one to save");
        }else if(directoryPath == null) {
            directoryPath = toSave.getParent();
        }





        //first check if directory is a valid and accessible directory by creating a File object based on it

        File dir = new File(directoryPath);

        try {
            if(!(dir.exists() && dir.isDirectory() && dir.canWrite())){
                throw new IllegalArgumentException("The given directory doesn't exists or is not a directory");
            }


            //check if toSave is a file and exists and we can read it

            if(!(toSave.exists() && toSave.isFile() && toSave.canRead())){
                throw new IllegalArgumentException("The given file doesn't exists or is not a file");
            }

        }catch (SecurityException e){
            throw new IllegalArgumentException("The directory cannot be written or the file cannot be read");
        }

        //create a File object describing the file

        File newFile = new File(dir,name);

        //move the given file to the new location using File.renameTo
        moveFile(toSave,newFile);
        return true;
    }

    @Override
    public File createFile(String fileName, String directory) throws Exception {
        //how to create a new file

        //create a File object describing the directory and check if it exists, is a directory and is writable

        File dir = new File(directory);

        try {
            if(!(dir.exists() && dir.isDirectory() && dir.canWrite())){
                throw new IllegalArgumentException("The directory doesn't exists or is not a directory");
            }
        }catch (SecurityException e){
            throw new IllegalArgumentException("The directory cannot be written");
        }

        //get name and extension if extension is absent affect null
        String name;
        String fileExtension = null;
        if(fileName.contains(".")){
            name = fileName.substring(0,fileName.lastIndexOf("."));
            fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);
        }else {
            name = fileName;
        }



        if(name.length() < 3){
            throw new IllegalArgumentException("The given filename is too short");
        }
        //create the new file

        File tfile =  File.createTempFile("placeholder",fileExtension,dir);

        //rename it to good name

        File finale = new File(dir,String.format("%s.%s",
                name,
                fileExtension == null? "tmp":fileExtension));


        moveFile(tfile,finale);
        return finale;
    }

    private void moveFile(File tfile, File finale) throws Exception{

        if(!finale.exists()){
            finale.createNewFile();
        }

        try(
                InputStream in = new BufferedInputStream(new FileInputStream(tfile));
                OutputStream out = new BufferedOutputStream(new FileOutputStream(finale))

                )
        {
            byte [] buffer = new byte[1024];
            int readBytes;
            while((readBytes = in.read(buffer)) > 0){
                out.write(buffer, 0,readBytes);
                out.flush();
            }
        }
        tfile.delete();
    }

    @Override
    public File getFile(String fileName, String directory) throws IllegalArgumentException {
        //how the get the file?

        //create a File object to check if the directory exists, is a directory and is readable

        File dir = new File(directory);

        try {
            if(!(dir.exists() && dir.isDirectory() && dir.canWrite())){
                throw new IllegalArgumentException("The given directory doesn't exists or is not a directory ");
            }
        }catch (SecurityException e){
            throw new IllegalArgumentException("The given directory cannot be read");
        }

        //create the File object describing the file and check if it exists and is a file

        File file = new File(dir,fileName);

        if(!(file.exists() && file.isFile())){
            throw new IllegalArgumentException("The given filename doesn't exists or is not a file ");
        }

        //return the file
        return file;
    }

    @Override
    public void deleteFile(@NonNull String name,@NonNull String directory) throws Exception {
        if(name == null || directory == null){
            throw new IllegalArgumentException("The file name and directory name cannot be null");
        }

        File dir = new File(directory);

        if(!(dir.exists() && dir.isDirectory() && dir.canWrite())){
            throw new IllegalArgumentException("The directory must exist, be a valid directory an be writable");
        }

        File toDelete = new File(dir,name);

        if(!(toDelete.exists() && toDelete.isFile())){
            throw new IllegalArgumentException("The file must exist, be a valid directory an be writable");
        }

        toDelete.delete();
    }
}
