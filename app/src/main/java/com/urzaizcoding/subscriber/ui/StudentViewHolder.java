package com.urzaizcoding.subscriber.ui;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.urzaizcoding.subscriber.R;
import com.urzaizcoding.subscriber.databinding.StudentBinding;
import com.urzaizcoding.subscriber.persistence.domain.Sex;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManager;
import com.urzaizcoding.subscriber.utils.task.AppExecutors;

import java.util.concurrent.CompletableFuture;

public class StudentViewHolder extends RecyclerView.ViewHolder {
    private final StudentBinding ui;
    private ItemClickListener<Integer> listener;
    private final StudentPhotoManager studentPhotoManager;

    public StudentViewHolder(StudentBinding ui, StudentPhotoManager studentPhotoManager) {
        super(ui.getRoot());
        this.ui = ui;
        this.studentPhotoManager = studentPhotoManager;
        ui.getRoot().setOnClickListener(view -> onItemClicked());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setStudent(Student student){
        Context context = ui.getRoot().getContext();
        ui.textViewNames.setText(
                String.format(context.getString(R.string.names_format),
                        student.getFirstname(),
                        student.getLastname()
                )
        );

        ui.textViewSexAndBirthDate.setText(
                String.format(context.getString(R.string.date_sex_format),
                        student.getBirthDate(),
                        student.getSex() == Sex.MALE? "Masculin":"Féminin"
                )
        );

        ui.textViewCourseAndGrade.setText(
                String.format(context.getString(R.string.course_grade_format),
                        student.getCourse(),
                        student.getGrade()
                )
        );

        if(student.getPhotoPath().isEmpty() || studentPhotoManager == null){
            ui.imageViewPicStudent.setImageResource(student.getSex() == Sex.MALE || student.getSex() == Sex.OTHER?
                    R.drawable.default_avatar_male:R.drawable.default_avatar_female);
        }else {
            CompletableFuture<Bitmap> futureBitmap = CompletableFuture.supplyAsync(() -> {
                try {
                    return BitmapFactory.decodeFile(studentPhotoManager.getPhoto(student).getAbsolutePath());
                }catch (Exception e){
                    throw new IllegalStateException(e);
                }
            }, AppExecutors.getInstance().diskIO()).handle((bitmap,ex) -> {
                if(ex != null){
                    Log.e(getClass().getName(),"Unable to load student photo : "+ex.getMessage());
                    return null;
                }else {
                    return bitmap;
                }
            });

            futureBitmap.thenAcceptAsync(bitmap -> {
                ui.imageViewPicStudent.setImageBitmap(bitmap);
            },AppExecutors.getInstance().uiThread());
        }
    }

    public void setItemClickListener(ItemClickListener<Integer> itemClickListener) {
        this.listener = itemClickListener;
    }

    public void onItemClicked(){
        listener.OnItemClick(getAdapterPosition());
    }
}
