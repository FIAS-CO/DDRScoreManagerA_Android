package jp.linanfine.dsma.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "memo")
public class Memo {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "updated_at")
    public Long updatedAt = System.currentTimeMillis();

    public Memo(int id, String text) {
        this.id = id;
        this.text = text;
    }
}
