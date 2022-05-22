package com.urzaizcoding.subscriber.ui;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.urzaizcoding.subscriber.R;

public class StudentItemTouchHelper extends ItemTouchHelper {

    /**
     * Creates an ItemTouchHelper that will work with the given Callback.
     * <p>
     * You can attach ItemTouchHelper to a RecyclerView via
     * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
     * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
     *
     * @param callback The Callback which controls the behavior of this touch helper.
     */
    private StudentItemTouchHelper(@NonNull Callback callback) {
        super(callback);
    }

    public static StudentItemTouchHelper getInstance(StudentViewModel studentViewModel,StudentRecyclerViewAdapter adapter){
        return new StudentItemTouchHelper(new SimpleCallback(0, LEFT | RIGHT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String message = viewHolder.itemView.getContext().getResources().getString(R.string.student_deleted_message);
                studentViewModel.removeStudent(adapter.getCurrentList().get(viewHolder.getAdapterPosition()));
                Toast.makeText(viewHolder.itemView.getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
