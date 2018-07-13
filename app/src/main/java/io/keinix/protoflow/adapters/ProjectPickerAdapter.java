package io.keinix.protoflow.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.di.ActivityScope;

@ActivityScope
public class ProjectPickerAdapter extends RecyclerView.Adapter<ProjectPickerAdapter.ProjectPickerViewHolder> {

    private List<Project> mProjects;
    private OnProjectSelectedListener mListener;

    public interface OnProjectSelectedListener {
        void onProjectSelected(Project project);
    }

    @Inject
    public ProjectPickerAdapter(OnProjectSelectedListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public ProjectPickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_project_in_picker, parent, false);
        return new ProjectPickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectPickerViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
       if (mProjects == null) {
           return 0;
       } else {
           return mProjects.size();
       }
    }

    public List<Project> getProjects() {
        return mProjects;
    }

    public void setProjects(List<Project> projects) {
        mProjects = projects;
        notifyDataSetChanged();
    }

    public class ProjectPickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_button_project_in_picker) ImageButton mProjectImageButton;
        @BindView(R.id.text_view_project_in_picker) TextView mProjectTextView;

        int mPosition;

        public void bindView(int position) {
            mPosition = position;
            mProjectTextView.setText(mProjects.get(position).getName());
        }

        public ProjectPickerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void onClick(View view) {
            mListener.onProjectSelected(mProjects.get(mPosition));
        }
    }

}
