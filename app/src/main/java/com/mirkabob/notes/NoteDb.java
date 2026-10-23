package com.mirkabob.notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

class NoteDb extends SQLiteOpenHelper {
    NoteDb(Context c) {
        super(c, "notes.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE notes(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, body TEXT,"
                + " color INTEGER, pinned INTEGER, updated INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
    }

    List<Note> all() {
        List<Note> out = new ArrayList<>();
        try (Cursor c = getReadableDatabase().query("notes", null, null, null, null, null,
                "pinned DESC, updated DESC")) {
            while (c.moveToNext()) out.add(from(c));
        }
        return out;
    }

    Note get(long id) {
        try (Cursor c = getReadableDatabase().query("notes", null, "id=?",
                new String[]{String.valueOf(id)}, null, null, null)) {
            return c.moveToFirst() ? from(c) : null;
        }
    }

    long save(Note n, boolean touch) {
        if (touch) n.updated = System.currentTimeMillis();
        ContentValues v = new ContentValues();
        if (n.id != 0) v.put("id", n.id);
        v.put("title", n.title);
        v.put("body", n.body);
        v.put("color", n.color);
        v.put("pinned", n.pinned ? 1 : 0);
        v.put("updated", n.updated);
        n.id = getWritableDatabase().insertWithOnConflict("notes", null, v, SQLiteDatabase.CONFLICT_REPLACE);
        return n.id;
    }

    void delete(long id) {
        getWritableDatabase().delete("notes", "id=?", new String[]{String.valueOf(id)});
    }

    private static Note from(Cursor c) {
        Note n = new Note();
        n.id = c.getLong(c.getColumnIndexOrThrow("id"));
        n.title = c.getString(c.getColumnIndexOrThrow("title"));
        n.body = c.getString(c.getColumnIndexOrThrow("body"));
        n.color = c.getInt(c.getColumnIndexOrThrow("color"));
        n.pinned = c.getInt(c.getColumnIndexOrThrow("pinned")) == 1;
        n.updated = c.getLong(c.getColumnIndexOrThrow("updated"));
        if (n.title == null) n.title = "";
        if (n.body == null) n.body = "";
        return n;
    }
}
