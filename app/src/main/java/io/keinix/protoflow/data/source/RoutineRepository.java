package io.keinix.protoflow.data.source;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import javax.inject.Inject;

import io.keinix.protoflow.data.Routine;
import io.keinix.protoflow.data.source.local.RoutineDao;

public class RoutineRepository {

    private RoutineDao mRoutineDao;

    @Inject
    RoutineRepository(RoutineDao routineDao) {
        mRoutineDao = routineDao;
    }

    void insertRoutine(Routine routine) {
        new insertRoutineAsync(mRoutineDao).execute(routine);
    }

    void deleteRoutine(Routine routine) {
        new deleteRoutineAsyncTask(mRoutineDao).execute(routine);
    }

    LiveData<List<Routine>> getAllRoutines() {
        return mRoutineDao.getAllRoutines();
    }

    //---------------AsyncTasks---------------

    // INSERT ASYNC
    private static class insertRoutineAsync extends AsyncTask<Routine, Void, Void> {

        private RoutineDao asyncRoutineDao;

        public insertRoutineAsync(RoutineDao routineDao) {
            asyncRoutineDao = routineDao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            asyncRoutineDao.insertRoutine(routines[0]);
            return null;
        }
    }

    //DELETE ASYNC
    private static class deleteRoutineAsyncTask extends AsyncTask<Routine, Void, Void> {

        private RoutineDao mAsyncDao;

        public deleteRoutineAsyncTask(RoutineDao asyncDao) {
            mAsyncDao = asyncDao;
        }

        @Override
        protected Void doInBackground(Routine... routine) {
            mAsyncDao.deleteRooutine(routine[0]);
            return null;
        }
    }
}
