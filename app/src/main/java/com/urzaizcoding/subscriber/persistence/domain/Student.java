package com.urzaizcoding.subscriber.persistence.domain;

import static com.urzaizcoding.subscriber.persistence.domain.Sex.FEMALE;
import static com.urzaizcoding.subscriber.persistence.domain.Sex.MALE;
import static com.urzaizcoding.subscriber.persistence.domain.Sex.OTHER;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "student")
public class Student implements Parcelable {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String firstname;
    private String lastname;
    private String birthDate;
    private Sex sex;
    private String course;
    private short grade;
    private String photoPath;

    @Ignore
    public static Creator<Student> CREATOR;

    static {
        CREATOR = new Creator<Student>() {
            @Override
            public Student createFromParcel(Parcel parcel) {
                return new Student(parcel);
            }

            @Override
            public Student[] newArray(int size) {
                return new Student[size];
            }
        };
    }

    public Student() {
    }

    @Ignore
    private Student(String firstname, String lastname, String birthDate, Sex sex, String course, short grade, String photoPath) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthDate = birthDate;
        this.sex = sex;
        this.course = course;
        this.grade = grade;
        this.photoPath = photoPath;
    }

    @Ignore
    private Student(Parcel parcel){
        this.id = parcel.readLong();
        this.firstname = parcel.readString();
        this.lastname = parcel.readString();
        this.birthDate = parcel.readString();
        String psex = parcel.readString();
        this.sex = psex.equals("MALE")? MALE:psex.equals("FEMALE")? FEMALE:OTHER;
        this.course = parcel.readString();
        this.grade = (short) parcel.readInt();
        this.photoPath = parcel.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public short getGrade() {
        return grade;
    }

    public void setGrade(short grade) {
        this.grade = grade;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student student = (Student) o;
        return getId() == student.getId() && getGrade() == student.getGrade() && getFirstname().equals(student.getFirstname()) && Objects.equals(getLastname(), student.getLastname()) && getBirthDate().equals(student.getBirthDate()) && getSex() == student.getSex() && Objects.equals(getCourse(), student.getCourse()) && Objects.equals(getPhotoPath(), student.getPhotoPath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getFirstname(), getLastname(), getBirthDate(), getSex(), getCourse(), getGrade(), getPhotoPath());
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", sex=" + sex +
                ", course='" + course + '\'' +
                ", grade=" + grade +
                ", photoPath='" + photoPath + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeLong(id);
        parcel.writeString(firstname);
        parcel.writeString(lastname);
        parcel.writeString(birthDate);
        parcel.writeString(sex.name());
        parcel.writeString(course);
        parcel.writeInt(grade);
        parcel.writeString(photoPath);
    }

    public static class StudentBuilder {

        private static StudentBuilder instance;
        private String firstname;
        private String lastname;
        private String birthDate;
        private Sex sex;
        private String course;
        private short grade;
        private String photoPath;

        private StudentBuilder(String firstname, String birthDate, Sex sex) {
            this.firstname = firstname;
            this.birthDate = birthDate;
            this.sex = sex;
        }

        public static StudentBuilder getInstance(String firstname, String birthDate, Sex sex) {
            if (instance == null) {
                instance = new StudentBuilder(firstname, birthDate, sex);
            } else {
                instance.firstname = firstname;
                instance.birthDate = birthDate;
                instance.sex = sex;

            }

            instance.photoPath = "";
            instance.lastname = "";
            instance.grade = 0;
            instance.course = "";

            return instance;
        }

        public StudentBuilder lastName(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public StudentBuilder course(String course) {
            this.course = course;
            return this;
        }

        public StudentBuilder grade(short grade) {
            this.grade = grade;
            return this;
        }

        public StudentBuilder photoPath(String photoPath) {
            this.photoPath = photoPath;
            return this;
        }

        public Student build() {
            return new Student(firstname, lastname, birthDate, sex, course, grade, photoPath);
        }

    }
}
