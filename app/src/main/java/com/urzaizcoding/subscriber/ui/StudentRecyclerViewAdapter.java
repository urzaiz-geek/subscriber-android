package com.urzaizcoding.subscriber.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.urzaizcoding.subscriber.databinding.StudentBinding;
import com.urzaizcoding.subscriber.persistence.domain.Student;
import com.urzaizcoding.subscriber.utils.file.StudentPhotoManager;

public class StudentRecyclerViewAdapter extends ListAdapter<Student,StudentViewHolder> {
    private ItemClickListener<Student> itemClickListener;
    public static final DiffUtil.ItemCallback<Student> DIFF_CALLBACK = new DiffUtil.ItemCallback<Student>() {
        @Override
        public boolean areItemsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Student oldItem, @NonNull Student newItem) {
            return oldItem.equals(newItem);
        }
    };

    private StudentPhotoManager studentPhotoManager;

    public StudentRecyclerViewAdapter(StudentPhotoManager studentPhotoManager) {
        super(DIFF_CALLBACK);
        this.studentPhotoManager = studentPhotoManager;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StudentViewHolder(
                StudentBinding.inflate(
                        LayoutInflater.from(parent.getContext()),parent,false
                ),studentPhotoManager
        );
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            holder.setStudent(getItem(position));
            holder.setItemClickListener((positionItem) -> {
                itemClickListener.OnItemClick(getItem(positionItem));
            });
    }

    public void setItemClickListener(ItemClickListener<Student> listener){
        this.itemClickListener = listener;
    }
}
