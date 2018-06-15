package io.keinix.protoflow.util;

import android.arch.persistence.room.TypeConverter;

import java.util.ArrayList;

public class RoomTypeConverters {

    @TypeConverter
    public String fromArrayToString(ArrayList<Integer> taskIds) {
        StringBuilder idString = new StringBuilder();
        for (int id : taskIds) {
            idString.append(id + ",");
        }
        return idString.toString();
    }

    @TypeConverter
    public ArrayList<Integer> fromStringToArray(String idString) {
        ArrayList<Integer> idArray= new ArrayList<>();
        for (String id : idString.split(",")) {
            idArray.add(Integer.parseInt(id));
        }
        return idArray;
    }
}

