package com.urzaizcoding.subscriber.utils.file;

import java.io.File;

public interface FileStorageService {
    boolean saveFile(String fileName, String directory, File toSave) throws Exception;
    File createFile(String fileName, String directory) throws Exception;
    File getFile(String fileName, String directory) throws Exception;
    void deleteFile(String name,String directory) throws Exception;
}
