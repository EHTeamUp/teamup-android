//package com.example.teamup.database;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteOpenHelper;
//
//public class DatabaseHelper extends SQLiteOpenHelper {
//
//    private static final String DATABASE_NAME = "TeamUpDB";
//    private static final int DATABASE_VERSION = 1;
//
//    // 사용자 테이블
//    public static final String TABLE_USERS = "users";
//    public static final String COLUMN_ID = "id";
//    public static final String COLUMN_USER_ID = "user_id";
//    public static final String COLUMN_PASSWORD = "password";
//    public static final String COLUMN_NAME = "name";
//    public static final String COLUMN_EMAIL = "email";
//    public static final String COLUMN_LANGUAGES = "languages";
//    public static final String COLUMN_ROLES = "roles";
//    public static final String COLUMN_CONTEST_NAME = "contest_name";
//    public static final String COLUMN_AWARD_TITLE = "award_title";
//    public static final String COLUMN_EXPERIENCE = "experience";
//    public static final String COLUMN_CREATED_AT = "created_at";
//
//    // 사용자 테이블 생성 SQL
//    private static final String CREATE_TABLE_USERS =
//        "CREATE TABLE " + TABLE_USERS + " (" +
//        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
//        COLUMN_USER_ID + " TEXT UNIQUE NOT NULL, " +
//        COLUMN_PASSWORD + " TEXT NOT NULL, " +
//        COLUMN_NAME + " TEXT NOT NULL, " +
//        COLUMN_EMAIL + " TEXT NOT NULL, " +
//        COLUMN_LANGUAGES + " TEXT, " +
//        COLUMN_ROLES + " TEXT, " +
//        COLUMN_CONTEST_NAME + " TEXT, " +
//        COLUMN_AWARD_TITLE + " TEXT, " +
//        COLUMN_EXPERIENCE + " TEXT, " +
//        COLUMN_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
//        ")";
//
//    public DatabaseHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL(CREATE_TABLE_USERS);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // 기존 테이블 삭제 후 새로 생성
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
//        onCreate(db);
//    }
//
//    // 사용자 데이터 삽입
//    public long insertUser(UserData userData) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put(COLUMN_USER_ID, userData.getUserId());
//        values.put(COLUMN_PASSWORD, userData.getPassword());
//        values.put(COLUMN_NAME, userData.getName());
//        values.put(COLUMN_EMAIL, userData.getEmail());
//        values.put(COLUMN_LANGUAGES, userData.getLanguages());
//        values.put(COLUMN_ROLES, userData.getRoles());
//        values.put(COLUMN_CONTEST_NAME, userData.getContestName());
//        values.put(COLUMN_AWARD_TITLE, userData.getAwardTitle());
//        values.put(COLUMN_EXPERIENCE, userData.getExperience());
//
//        long result = db.insert(TABLE_USERS, null, values);
//        db.close();
//        return result;
//    }
//
//    // 사용자 ID로 사용자 조회
//    public UserData getUserById(String userId) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        UserData userData = null;
//
//        Cursor cursor = db.query(TABLE_USERS, null,
//            COLUMN_USER_ID + "=?", new String[]{userId},
//            null, null, null);
//
//        if (cursor.moveToFirst()) {
//            userData = new UserData();
//            userData.setUserId(cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
//            userData.setPassword(cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD)));
//            userData.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
//            userData.setEmail(cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)));
//            userData.setLanguages(cursor.getString(cursor.getColumnIndex(COLUMN_LANGUAGES)));
//            userData.setRoles(cursor.getString(cursor.getColumnIndex(COLUMN_ROLES)));
//            userData.setContestName(cursor.getString(cursor.getColumnIndex(COLUMN_CONTEST_NAME)));
//            userData.setAwardTitle(cursor.getString(cursor.getColumnIndex(COLUMN_AWARD_TITLE)));
//            userData.setExperience(cursor.getString(cursor.getColumnIndex(COLUMN_EXPERIENCE)));
//        }
//
//        cursor.close();
//        db.close();
//        return userData;
//    }
//
//    // 로그인 검증
//    public boolean validateLogin(String userId, String password) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_ID},
//            COLUMN_USER_ID + "=? AND " + COLUMN_PASSWORD + "=?",
//            new String[]{userId, password},
//            null, null, null);
//
//        boolean isValid = cursor.getCount() > 0;
//        cursor.close();
//        db.close();
//        return isValid;
//    }
//}
