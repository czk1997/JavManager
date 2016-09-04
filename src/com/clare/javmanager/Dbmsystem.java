package com.clare.javmanager;

import javafx.scene.control.Alert;

import java.sql.*;

/**
 * Created by chenz on 6/9/2016.
 */
public class Dbmsystem {
    public static final int Number = 0;
    public final static int Name = 1;
    public final static int company = 2;
    public final static int filepath = 3;
    public final static int imagePath = 4;
    public final static int UID = 5;
    private final static String[] keywords = {"Numbers", "movieName", "Company", "Path", "imagePath", "UID"};
    public Connection c = null;
    public Statement stmt = null;
    public ResultSet result = null;

    public Dbmsystem() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:userData.db");
            System.out.println("Got connection of Database");
            stmt = c.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initilizeTable();
    }

    public static void main(String args[]) {
        Dbmsystem d = new Dbmsystem();
    }

    public int updateString(int options, String content, int id) {
        String sql = "UPDATE Movies SET " + keywords[options] + " WHERE UID=" + id;
        int affectedRows = constumupdate(sql);
        return affectedRows;
    }

    public int insertNewMovie(String name, String Company, String filepath, String releasedate, int mosic, String actors, String numbers, String imagePath, int status) {
        String checkSame = "SELECT * FROM Movies WHERE Path=\"" + filepath + "\"";
        System.out.println(checkSame);
        result = constumQuery(checkSame);
        int rowCount = 0;

        try {
            ResultSetMetaData rsmd = result.getMetaData();

            while (result.next()) {
                rowCount++;

            }
            System.out.println("We got " + rowCount + " same thing");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rowCount == 0) {
            System.out.println("tRUE1");
            String sql = "INSERT INTO Movies(movieName,Numbers,Actors,Company,ReleaseDate,Path,mosic,imagePath) VALUES (\"" + name + "\", \"" + numbers + "\", \"" + actors + "\", \"" + Company + "\", \"" + releasedate + "\", \"" + filepath + "\", " + mosic + ", \"" + imagePath + "\");";
            System.out.println(sql);
            int affectedRows = constumupdate(sql);
            System.out.println("tRUE2");
            if (affectedRows != 0 & status == 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setContentText("Add Completed!");
                alert.showAndWait();

            }

            return affectedRows;
        } else {
            System.out.println("tRUE0");
            if (status == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("File Already Existed");
                alert.setContentText("File Already Existed");
                alert.showAndWait();
            }
            return 0;
        }


    }

    public int constumupdate(String content) {

        int rowNumber = 0;
        try {
            rowNumber = stmt.executeUpdate(content);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rowNumber;
    }

    public ResultSet constumQuery(String content) {
        try {

            result = stmt.executeQuery(content);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public int initilizeTable() {

        try {
            String sql = "CREATE TABLE Movies\n" +
                    "(\n" +
                    "    UID INTEGER UNIQUE PRIMARY KEY  AUTOINCREMENT,\n" +
                    "    movieName TEXT,\n" +
                    "    Numbers TEXT NOT NULL,\n" +
                    "    Actors TEXT,\n" +
                    "    Company TEXT,\n" +
                    "    Path TEXT NOT NULL,\n" +
                    "    ReleaseDate TEXT,\n" +
                    "    mosic BOOLEAN,\n" +
                    "    imagePath TEXT\n" +
                    ");\n" +
                    "CREATE UNIQUE INDEX Movies_UID_uindex ON Movies (UID);";
            stmt.executeUpdate(sql);
            String sql2 = "CREATE TABLE RepkeyWord\n" +
                    "(\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    orginalName TEXT,\n" +
                    "    numberKey TEXT\n" +
                    ");\n" +
                    "CREATE UNIQUE INDEX RepkeyWord_id_uindex ON RepkeyWord (id);\n" +
                    "CREATE UNIQUE INDEX RepkeyWord_orginalName_uindex ON RepkeyWord (orginalName);";
            stmt.close();
            stmt.execute(sql2);

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());

        }
        System.out.println("Table created successfully");
        return 0;
    }

    public ResultSet getMovieInfo(int dbid) {
        String sql = "SELECT * FROM Movies WHERE UID=" + dbid;
        ResultSet resultSet = constumQuery(sql);
        return resultSet;
    }

    public ResultSet getReplaceInfo() {
        String sql = "SELECT * FROM Movies";

        result = constumQuery(sql);
        return result;

    }

    public ResultSet getAllMovies() {
        String sql = "SELECT * FROM Movies";

        result = constumQuery(sql);
        return result;
    }

    public int addRPkey(String orKey, String newKey) {
        String checkSame = "SELECT * FROM RepkeyWord WHERE orginalName=\"" + orKey + "\"";
        System.out.println(checkSame);
        result = constumQuery(checkSame);
        int rowCount = 0;

        try {
            while (result.next()) {
                rowCount++;
            }
            System.out.println("We got " + rowCount + " same thing");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (rowCount == 0) {
            String sql = "INSERT INTO RepkeyWord(orginalName,numberKey) VALUES (\"" + orKey + "\", \"" + newKey + "\")";
            System.out.println(sql);
            int affectedRows = constumupdate(sql);
            if (affectedRows != 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Completed");
                alert.setContentText("Add replace keywords Completed!");
                alert.showAndWait();
            }

            return affectedRows;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Replace keywork Already Existed");
            alert.setContentText("File Already Existed");
            alert.showAndWait();
            return 0;
        }


    }
}
