package com.unifacs.projetoavaliacao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HistoryDAO {

    @Query("SELECT * FROM History")
    List<History> getAll();

    @Query("SELECT * FROM History WHERE id_history IN (:hisIds)")
    List<History> getAllByIds(int[] hisIds);

    @Insert
    void insertSingle(History his);

    @Insert
    void insertAll(History... his);

    @Delete
    void deleteSingle(History his);

    @Delete
    void deleteAll(History... his);
}
