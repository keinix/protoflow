package io.keinix.protoflow.util;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomTypeConverters {

    @TypeConverter
    public String fromArrayToString(ArrayList<Integer> taskIds) {
        if (taskIds != null) {
            StringBuilder idString = new StringBuilder();
            for (int id : taskIds) {
                idString.append(id).append(",");
            }
            return idString.toString();
        }
        return null;
    }

    @TypeConverter
    public ArrayList<Integer> fromStringToArray(String idString) {
        if (idString != null && idString.length() > 1) {
            ArrayList<Integer> idArray = new ArrayList<>();
            for (String id : idString.split(",")) {
                idArray.add(Integer.parseInt(id));
            }
            return idArray;
        }
        return null;
    }

    /**
     * @param completedDates JSON string containing the dates a repeated task was completed on
     * @return JSON String represented as a MAP
     */
    @TypeConverter
    public HashMap<Integer, List<Long>> fromStringToMap(String completedDates) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<Integer, List<Long>>>() {}.getType();
        return gson.fromJson(completedDates, type);
    }

    @TypeConverter
    public String fromMapToString(Map<Integer, List<Long>> completedDatesMap) {
        Gson gson = new Gson();
        return gson.toJson(completedDatesMap);
    }
}

