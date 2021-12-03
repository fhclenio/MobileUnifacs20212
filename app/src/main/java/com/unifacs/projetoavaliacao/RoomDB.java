package com.unifacs.projetoavaliacao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {History.class}, version = 1)
public abstract class RoomDB extends RoomDatabase {
    public abstract HistoryDAO hisDAO();
}
