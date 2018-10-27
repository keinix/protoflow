package io.keinix.protoflow.data.source;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import io.keinix.protoflow.data.Project;
import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.source.local.ProjectDao;

public class ProjectRepository {

    private ProjectDao mProjectDao;

    ProjectRepository(ProjectDao projectDao) {
        mProjectDao = projectDao;
    }

    LiveData<Project> getProject(int id) {
        return mProjectDao.getProject(id);
    }

    LiveData<List<Project>> getAllProjects() {
        return mProjectDao.getAllProjects();
    }

    void insertProject(Project project) {
        new insertProjectAsync(mProjectDao).execute(project);
    }

    void updateProject(Project project) {
        new updateProjectAsync(mProjectDao).execute(project);
    }

    void deleteProject(Project project) {
        new deleteProjectAsyncTask(mProjectDao).execute(project);
    }

    //---------------AsyncTasks---------------

    //DELETE PROJECT ASYNC
    private static class deleteProjectAsyncTask extends AsyncTask<Project, Void, Void> {

        private ProjectDao mAsyncDao;

        public deleteProjectAsyncTask(ProjectDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Project... projects) {
            mAsyncDao.deleteProject(projects[0]);
            return null;
        }
    }

    //INSERT PROJECT ASYNC
    private static class updateProjectAsync extends AsyncTask<Project, Void, Void> {

        private ProjectDao asyncProjectDao;

        public updateProjectAsync(ProjectDao projectDao) {
            asyncProjectDao = projectDao;
        }

        @Override
        protected Void doInBackground(Project... projects) {
            asyncProjectDao.update(projects[0]);
            return null;
        }
    }

    //INSERT PROJECT ASYNC
    private static class insertProjectAsync extends AsyncTask<Project, Void, Void> {

        private ProjectDao asyncProjectDao;

        public insertProjectAsync(ProjectDao projectDao) {
            asyncProjectDao = projectDao;
        }

        @Override
        protected Void doInBackground(Project... projects) {
            asyncProjectDao.insert(projects[0]);
            return null;
        }
    }
}
