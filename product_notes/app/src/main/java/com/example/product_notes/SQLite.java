package com.example.product_notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class SQLite extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CSDLNotes";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTES = "notes";
    private static final String TABLE_NOTES_TRASH = "notes_trash";
    private static final String TABLE_NOTES_ARCHIVE = "notes_archive";
    private static final String TABLE_TAGS = "tags";
    private static final String TABLE_CONTENT = "contents";
    private static final String TABLE_FILE = "files";
    private static final String TABLE_REMINDER = "reminders";

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_CODE = "code";
    private static final String KEY_URI = "uri";
    private static final String KEY_ID_NOTES = "id_notes";
    private static final String KEY_TAG = "tag";
    private static final String KEY_CHECKED = "checked";
    private static final String KEY_REMINDER = "reminder";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_UPDATED_AT = "updated_at";
    private static final String KEY_START= "start";
    private static final String KEY_END = "end";
    private static final String KEY_TYPE = "type";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_IS_ARCHIVE = "is_archive";

    public SQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public int addNote(Notes note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_TAG, note.getTag());
        values.put(KEY_TYPE, note.getType());
        values.put(KEY_LOCATION, note.getLocation());
        values.put(KEY_IS_ARCHIVE, note.getIs_archive());
        values.put(KEY_REMINDER, note.getReminder());
        values.put(KEY_CREATED_AT, note.getCreated_at());
        values.put(KEY_UPDATED_AT, note.getUpdated_at());
        int id = (int) db.insert(TABLE_NOTES, null, values);
        db.close();
        return id;
    }

    public void addFile(File file, int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_URI, file.getUri());
        values.put(KEY_ID_NOTES, id);
        db.insert(TABLE_FILE, null, values);
        db.close();
    }


    public void addTag(Tags tags) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, tags.getTitle());
        values.put(KEY_CODE, tags.getCode());
        db.insert(TABLE_TAGS, null, values);
        db.close();
    }
    public void addContent(Content content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, content.getContent());
        values.put(KEY_CHECKED, content.getChecked());
        values.put(KEY_ID_NOTES, content.getId_note());
        db.insert(TABLE_CONTENT, null, values);
        db.close();
    }

    public void addNoteTrash(Notes note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_TAG, note.getTag());
        values.put(KEY_TYPE, note.getType());
        values.put(KEY_LOCATION, note.getLocation());
        values.put(KEY_IS_ARCHIVE, note.getIs_archive());
        values.put(KEY_REMINDER, note.getReminder());
        values.put(KEY_CREATED_AT, note.getCreated_at());
        values.put(KEY_UPDATED_AT, note.getUpdated_at());
        db.insert(TABLE_NOTES_TRASH, null, values);
        db.close();
    }



    public Notes getNotes(int noteID) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NOTES, null, KEY_ID + " = ?", new String[]{String.valueOf(noteID)}, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
        return notes;
    }


    public ArrayList<Content> getAllContentByIDNote(int id_note) {
        ArrayList<Content> contents = new ArrayList<>();
        String query = "SELECT * FROM contents WHERE id_notes = '"+id_note+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Content content = new Content(cursor.getInt(0),cursor.getString(1), cursor.getInt(2),cursor.getInt(3));
            contents.add(content);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return contents;
    }


    public ArrayList<File> getAllFileByIdNote(int id_note) {
        ArrayList<File> files = new ArrayList<>();
        String query = "SELECT * FROM files WHERE id_notes = '"+id_note+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            File file = new File(cursor.getString(1));
            files.add(file);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return files;
    }



    public ArrayList<Notes> getAllNotes() {
        ArrayList<Notes> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ KEY_IS_ARCHIVE + " =0 ORDER BY "+ KEY_CREATED_AT+ " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            noteList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public ArrayList<Notes> getAllTextNotes() {
        ArrayList<Notes> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ KEY_IS_ARCHIVE +
                "=0 AND " + KEY_TYPE + "='TEXT' ORDER BY "+ KEY_UPDATED_AT+ " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            noteList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public ArrayList<Notes> getAllChecklistNotes() {
        ArrayList<Notes> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ KEY_IS_ARCHIVE +
                    "=0 AND " + KEY_TYPE + "='CHECKLIST' ORDER BY "+ KEY_UPDATED_AT+ " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            noteList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public ArrayList<Notes> getAllNotesOrderByUpdate() {
        ArrayList<Notes> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ KEY_IS_ARCHIVE + " =0 ORDER BY "+ KEY_UPDATED_AT+ " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            noteList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public ArrayList<Notes> getAllNotesOrderByCreate() {
        ArrayList<Notes> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ KEY_IS_ARCHIVE + " =0 ORDER BY "+ KEY_CREATED_AT+ " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            noteList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public ArrayList<Notes> getAllNotesOrderByReminder() {
        ArrayList<Notes> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ KEY_IS_ARCHIVE + " =0 AND "+ KEY_REMINDER + " IS NOT NULL ORDER BY "+ KEY_REMINDER+ " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            noteList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return noteList;
    }

    public ArrayList<Notes> getAllNotesTrash() {
        ArrayList<Notes> noteList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES_TRASH;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            noteList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return noteList;
    }


    public ArrayList<Tags> getAllTags() {
        ArrayList<Tags> tagsArrayList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_TAGS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Tags tags = new Tags(cursor.getInt(0), cursor.getString(1),cursor.getString(2));
            tagsArrayList.add(tags);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return tagsArrayList;
    }

    public ArrayList<Notes> getAllNotesArchive() {
        ArrayList<Notes> notesArrayList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NOTES + " WHERE "+ KEY_IS_ARCHIVE + "= 1 ORDER BY "+ KEY_CREATED_AT +" DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            Notes notes = new Notes(cursor.getInt(0),cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5),cursor.getInt(6), cursor.getString(7),cursor.getString(8),cursor.getString(9));
            notesArrayList.add(notes);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return notesArrayList;
    }


    public void deleteNote(int noteId) {
        Log.d("TAG", "deleteNote: "+noteId);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, KEY_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void deleteFiles(int id_note) {
        Log.d("TAG", "deleteNote: "+id_note);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FILE, KEY_ID_NOTES + " = ?", new String[]{String.valueOf(id_note)});
        db.close();
    }
    public void deleteContent(int id_note) {
        Log.d("TAG", "deleteNote: "+id_note);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTENT, KEY_ID_NOTES + " = ?", new String[]{String.valueOf(id_note)});
        db.close();
    }

    public void deleteTag(int tagID) {
        Log.d("TAG", "deleteNote: "+tagID);
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TAGS, KEY_ID + " = ?", new String[]{String.valueOf(tagID)});
        db.close();
    }

    public void deleteNoteTrash(int noteId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES_TRASH, KEY_ID + " = ?", new String[]{String.valueOf(noteId)});
        db.close();
    }

    public void updateNote(Notes note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_CONTENT, note.getContent());
        values.put(KEY_TAG, note.getTag());
        values.put(KEY_TYPE, note.getType());
        values.put(KEY_LOCATION, note.getLocation());
        values.put(KEY_IS_ARCHIVE, note.getIs_archive());
        values.put(KEY_REMINDER, note.getReminder());
        values.put(KEY_CREATED_AT, note.getCreated_at());
        values.put(KEY_UPDATED_AT, note.getUpdated_at());
        db.update(TABLE_NOTES, values, KEY_ID + " = ?", new String[]{String.valueOf(note.getId())});
        db.close();
    }
    public void updateTags(Tags tags) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, tags.getTitle());
        values.put(KEY_CODE, tags.getCode());
        db.update(TABLE_TAGS, values, KEY_ID + " = ?", new String[]{String.valueOf(tags.getId())});
        db.close();
    }

    public void updateContent(Content content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CONTENT, content.getContent());
        values.put(KEY_CHECKED, content.getChecked());
        db.update(TABLE_CONTENT, values, KEY_ID_NOTES + " = ?", new String[]{String.valueOf(content.getId())});
        db.close();
    }

    public void updateFile(File file) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_URI, file.getUri());
        db.update(TABLE_FILE, values, KEY_ID_NOTES + " = ?", new String[]{String.valueOf(file.getId_note())});
        db.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_notes_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT,%s TEXT,%s TEXT,%s TEXT,%s integer," +
                " reminder TIMESTAMP DEFAULT CURRENT_TIMESTAMP, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP )", TABLE_NOTES, KEY_ID, KEY_TITLE, KEY_CONTENT,KEY_TAG,KEY_TYPE,KEY_LOCATION,KEY_IS_ARCHIVE);
        String create_notes_trash_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT,%s TEXT,%s TEXT,%s TEXT,%s integer," +
                " reminder TIMESTAMP DEFAULT CURRENT_TIMESTAMP, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)", TABLE_NOTES_TRASH, KEY_ID, KEY_TITLE, KEY_CONTENT,KEY_TAG,KEY_TYPE,KEY_LOCATION,KEY_IS_ARCHIVE);
        String create_tags_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT," +
                " created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP , updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)", TABLE_TAGS, KEY_ID, KEY_TITLE,KEY_CODE);
        String create_content_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT,%s integer,%s integer)", TABLE_CONTENT, KEY_ID, KEY_CONTENT,KEY_CHECKED,KEY_ID_NOTES);
        String create_file_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY, %s TEXT,%s integer)", TABLE_FILE, KEY_ID, KEY_URI, KEY_ID_NOTES);
        String create_reminder_table = String.format("CREATE TABLE %s(%s INTEGER PRIMARY KEY,%S TIMESTAMP DEFAULT CURRENT_TIMESTAMP,%S TIMESTAMP DEFAULT CURRENT_TIMESTAMP, %s TEXT,%s integer)", TABLE_REMINDER, KEY_ID, KEY_START,KEY_END,KEY_TYPE, KEY_ID_NOTES);

        db.execSQL(create_notes_table);
        db.execSQL(create_content_table);
        db.execSQL(create_notes_trash_table);
        db.execSQL(create_tags_table);
        db.execSQL(create_file_table);
        db.execSQL(create_reminder_table);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String drop_notes_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NOTES);
        String drop_notes_trash_table = String.format("DROP TABLE IF EXISTS %s", TABLE_NOTES_TRASH);
        String drop_tags_table = String.format("DROP TABLE IF EXISTS %s", TABLE_TAGS);
        db.execSQL(drop_notes_table);
        db.execSQL(drop_notes_trash_table);
        db.execSQL(drop_tags_table);
        onCreate(db);
    }
}
