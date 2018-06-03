package io.keinix.protoflow.addeddittask;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.keinix.protoflow.R;
import io.keinix.protoflow.data.Task;

public class AddEditTaskActivity extends AppCompatActivity {

    @BindView(R.id.button_submit) Button btn;
    @BindView(R.id.editText) EditText editText;

    private AddEditTaskViewModel mViewModel;

    @OnClick(R.id.button_submit)
    void submit() {
        Task task = new Task(editText.getText().toString());
       mViewModel.addTask(task);
       finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(AddEditTaskViewModel.class);
    }
}
