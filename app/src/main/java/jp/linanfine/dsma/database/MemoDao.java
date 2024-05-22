package jp.linanfine.dsma.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface MemoDao {
    @Query("SELECT * FROM memo WHERE id = :id")
    Memo findMemoById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(Memo memo);
}
