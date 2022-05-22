package com.urzaizcoding.subscriber.utils.file;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;


public class LocalFileStorageServiceTest {
    private LocalFileStorageService underTest;
    private final String testDirectory = "I:/test/";

    @Before
    public void setUp() throws Exception {
        underTest = new LocalFileStorageService();
    }

    @Test
    public void shouldSaveAGivenFileWhenAllArsAreCorrect() throws Exception {
        //Given
        File toSave = new File(testDirectory,"testFile.txt");
        toSave.createNewFile();
        final String fileName = "saveName.jpg";

        //When
        final boolean result = underTest.saveFile(fileName,testDirectory,toSave);

        //Then
        assertTrue(result);
        assertTrue(new File(testDirectory,fileName).exists());
        assertTrue(new File(testDirectory,fileName).isFile());
        toSave.delete();
        new File(testDirectory,fileName).delete();
    }

    @Test
    public void saveShouldThrowExceptionIfNullArgIsProvided() throws Exception {
        //Given
        File toSave = new File(testDirectory,"testFile.txt");
        toSave.createNewFile();

        //When-Then

        final Exception e1 = assertThrows(IllegalArgumentException.class,() -> {
            underTest.saveFile("testFile.txt",testDirectory,null); //file is null
        });

        final Exception e2 = assertThrows(IllegalArgumentException.class,() -> {
            underTest.saveFile(null,null,toSave); //file name and directory are both null
        });

        final Exception e3 = assertThrows(IllegalArgumentException.class,() -> {
            underTest.saveFile("testFile.txt",null,toSave); //file name is the same as the file to save and directory is null
        });

        assertThat(e1).hasMessageThat().contains("Cannot Save a null file");
        assertThat(e2).hasMessageThat().contains("The directory path must not be null when the file name is null too");
        assertThat(e3).hasMessageThat().contains("The directory path must not be null when the file name is the same as the one to save");

        toSave.delete();
    }

    @Test
    public void saveShouldSaveTheFileWithTheSameNameWhenTheFileNameIsNull() throws Exception {
        //Given

        final String fileNameToSave = "testFile.txt";
        final String subDirName = "sub";
        final File subDir = new File(testDirectory,subDirName);
        final File toSave = new File(testDirectory,fileNameToSave);

        subDir.mkdir();
        toSave.createNewFile();

        //When

        final boolean result = underTest.saveFile(null,subDir.getAbsolutePath(),toSave);

        //Then

        assertThat(result).isTrue();
        assertThat(new File(subDir,toSave.getName()).exists()).isTrue();


        new File(subDir,toSave.getName()).delete();
        subDir.delete();
        toSave.delete();
    }

    @Test
    public void saveShouldGivenANullDirButAValidNameDifferentToTheFileToSaveSaveItInSameDir() throws Exception{
        //Given
        final String fileNameToSave = "testFile.txt";
        final String newName = "newName.txt";
        final File toSave = new File(testDirectory,fileNameToSave);
        toSave.createNewFile();

        //When

        final boolean result = underTest.saveFile(newName,null,toSave);

        //Then

        assertThat(result).isTrue();
        assertThat(new File(toSave.getParent(),newName).exists()).isTrue();
        assertThat(new File(toSave.getParent(),newName).isFile()).isTrue();

        toSave.delete();
        new File(toSave.getParent(),newName).delete();
    }

    @Test
    public void saveShouldThrowExceptionWhenDirectoryDoesntExists() throws IOException {
        //Given
        File toSave = new File(testDirectory,"testFile.txt");
        toSave.createNewFile();
        final String fileName = "saveName.txt";

        //When
        //Then
        final Exception e1 = assertThrows(IllegalArgumentException.class,() -> {
            underTest.saveFile(fileName,"bonjour",toSave);    //directory doesn't exists
        });

        assertThat(e1).hasMessageThat()
                .containsMatch("The given directory doesn't exists or is not a directory");

        toSave.delete();
    }

    @Test
    public void saveFileShouldThrowExceptionWhenDirectoryIsAFile() throws Exception {
        //Given
        File toSave = new File(testDirectory,"testFile.txt");
        toSave.createNewFile();
        final String fileName = "saveName.txt";

        final Exception e4 = assertThrows(IllegalArgumentException.class, () ->{
            underTest.saveFile(fileName,toSave.getAbsolutePath(),toSave); //directory is a file
        });

        assertThat(e4).hasMessageThat()
                .containsMatch("The given directory doesn't exists or is not a directory");
        toSave.delete();
    }

    @Test
    public void saveFileShouldThrowAndExceptionWhenTheFileIsADirectory() throws Exception{
        //Given
        File toSave = new File(testDirectory,"testFile.txt");
        toSave.createNewFile();
        final String fileName = "saveName.txt";

        //when-then
        final Exception e3 = assertThrows(IllegalArgumentException.class, () ->{
            underTest.saveFile(fileName,testDirectory,new File(testDirectory)); //file is a directory
        });

        assertThat(e3).hasMessageThat()
                .containsMatch("The given file doesn't exists or is not a file");
        toSave.delete();
    }

    @Test
    public void saveFileShouldThrowExceptionIfFileDoesntExists() throws Exception {
        //Given
        File toSave = new File(testDirectory,"testFile.txt");
        toSave.createNewFile();
        final String fileName = "saveName.txt";

        //when-then
        final Exception e2 = assertThrows(IllegalArgumentException.class, () ->{
            underTest.saveFile(fileName,testDirectory,new File("avion")); //file doesn't exists
        });

        assertThat(e2).hasMessageThat()
                .containsMatch("The given file doesn't exists or is not a file");
        toSave.delete();
    }



    @Test
    public void createFileShouldCreateANewFileWhenAllArgsAreCorrect() throws Exception {
        //Given
        final String fileName = "testFile.txt";

        //When
        final File result = underTest.createFile(fileName,testDirectory);

        //Then
        assertThat(result.exists()).isTrue();
        assertThat(result.isFile()).isTrue();
        assertThat(result.getName()).containsMatch(fileName);
        assertThat(result.getParent()).contains(new File(testDirectory).getName());
        result.delete();
    }

    @Test
    public void createFileShouldThrowAndExceptionWhenDirectoryDoesntExists() throws Exception {
        //Given
        final String fileName = "testFile.txt";

        //When-Then

        final Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
            underTest.createFile(fileName,"salut");
        });

        assertThat(e1).hasMessageThat().contains("The directory doesn't exists or is not a directory");
    }

    @Test
    public void createFileShouldThrowAndExceptionWhenDirectoryIsAFile() throws Exception {
        //Given
        final String fileName = "testFile.txt";
        File dir = new File("dir.tmp");
        dir.createNewFile();

        //When-Then

        final Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
            underTest.createFile(fileName,"dir.tmp");
        });

        assertThat(e1).hasMessageThat().contains("The directory doesn't exists or is not a directory");
        dir.delete();
    }

    @Test
    public void createFileShouldThrowAndExceptionWhenFileNameIsTooShort() throws Exception {
        //Given
        final String fileName = "te";

        //When-Then

        final Exception e1 = assertThrows(IllegalArgumentException.class, () -> {
            underTest.createFile(fileName,testDirectory);
        });

        assertThat(e1).hasMessageThat().contains("too short");
    }

    @Test
    public void getFileShouldGetAFileFromADirectoryWhenArgsAreCorrect() throws Exception {
        //Given
        final String fileName = "test.txt";
        File dir = new File(testDirectory);
        File file = new File(dir,fileName);
        file.createNewFile();

        //When
        final File result = underTest.getFile(fileName,testDirectory);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.isFile()).isTrue();
        assertThat(result.getAbsolutePath()).contains(file.getAbsolutePath());
        file.delete();
    }

    @Test
    public void getFileShouldThrowAndExceptionIfDirectoryDoesntExists() throws Exception {
        //Given
        final String fileName = "test.txt";
        File dir = new File(testDirectory);
        File file = new File(dir,fileName);
        file.createNewFile();

        //When-Then
        final Exception e1 = assertThrows(IllegalArgumentException.class,() -> {
           underTest.getFile(fileName,"dummy");
        });

        assertThat(e1).hasMessageThat().contains("doesn't exists");
        file.delete();
    }

    @Test
    public void getFileShouldThrowAndExceptionIfDirectoryIsAFile() throws Exception {
        //Given
        final String fileName = "test.txt";
        File dir = new File(testDirectory);
        File file = new File(dir,fileName);
        file.createNewFile();

        //When-Then
        final Exception e1 = assertThrows(IllegalArgumentException.class,() -> {
            underTest.getFile(fileName,fileName);
        });

        assertThat(e1).hasMessageThat().contains("not a directory");
        file.delete();
    }

    @Test
    public void getFileShouldThrowAndExceptionIfFileDoesntExists() throws Exception {
        //Given
        final String fileName = "test.txt";
        File dir = new File(testDirectory);
        File file = new File(dir,fileName);
        file.createNewFile();

        //When-Then
        final Exception e1 = assertThrows(IllegalArgumentException.class,() -> {
            underTest.getFile("dummy",testDirectory);
        });

        assertThat(e1).hasMessageThat().contains("doesn't exists");
        file.delete();
    }

    @Test
    public void deleteFileMustDeleteAFileWhenRightArgsAreGiven() throws Exception {
        //Given
        final String fileName = "testFile.txt";
        final File toDelete = new File(testDirectory,fileName);

        toDelete.createNewFile();

        //When

        underTest.deleteFile(fileName,testDirectory);

        //Then

        assertThat(toDelete.exists()).isFalse();
    }
}