package io.keinix.protoflow.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.Task;
import io.keinix.protoflow.util.ListItem;


public class AddListItemAdapter extends RecyclerView.Adapter<AddListItemAdapter.AddListItemViewHolder> {

    private List<ListItem> mListItems;

    public AddListItemAdapter() {
        mListItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public AddListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_in_picker, parent, false);
        return new AddListItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddListItemViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        if (mListItems != null) {
            return mListItems.size();
        } else {
            return 0;
        }
    }

    public void setListItems(List<? extends ListItem> listItems) {
        mListItems.clear();
        mListItems.addAll(listItems);
        notifyDataSetChanged();
    }

    class AddListItemViewHolder extends  RecyclerView.ViewHolder {
        @BindView(R.id.image_button_project_in_picker) ImageButton mImageButton;
        @BindView(R.id.text_view_project_in_picker) TextView mTextView;

        @BindDrawable(R.drawable.ic_routines_black_24) Drawable routineIcon;

        public AddListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bindView(int position) {
           if (mListItems.get(position).getItemType() == ListItem.TYPE_TASK) {
               bindTask(position);
           } else {
               bindRoutine(position);
           }
        }

        private void bindTask(int position) {
            Task task = (Task) mListItems.get(position);
            mTextView.setText(task.getName());
            mImageButton.setVisibility(View.INVISIBLE);
        }

        private void bindRoutine(int position) {
            Routine routine = (Routine) mListItems.get(position);
            mImageButton.setVisibility(View.VISIBLE);
            mImageButton.setImageDrawable(routineIcon);
            mTextView.setText(routine.getName());
        }
    }
}
