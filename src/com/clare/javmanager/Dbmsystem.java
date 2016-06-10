package com.clare.javmanager;
import java.sql.*;

/**
 * Created by chenz on 6/9/2016.
 */
public class Dbmsystem {
    public static final int Number=0;
    public final static int Name=1;
    public final static int company=2;
    public final static int filepath=3;
    public final static int imagePath=4;
    public final static int UID=5;
    private final static String[] keywords={"Numbers","movieName","Company","Path","imagePath","UID"};
    public Dbmsystem(){
        initilizeTable();
    }
    public int updateString(int options, String content,int id){
        String sql="UPDATE Movies SET "+keywords[options]+" WHERE UID="+id;
        ResultSet result=constumupdate(sql);
        return 0;
    }
    public int insertNewMovie(String name,String Company, String filepath, String releasedate, boolean mosic,String actors, String numbers,String imagePath){
        String sql="INSERT INTO Movies(movieName,Numbers,Actors,Company,ReleaseDate,Path,mosic,imagePath) VALUES (\""+name+"\", \""+numbers+"\", \""+actors+"\", "+Company+"\", \""+releasedate+"\", \""+filepath+"\", "+mosic+", \""+imagePath+"\");";
        ResultSet result=constumupdate(sql);
        return 0;
    }
    public ResultSet constumupdate(String content){
        Connection c=null;
        Statement stmt=null;
        ResultSet result=null;
        try {
            Class.forName("org.sqlite.JDBC");
            c=DriverManager.getConnection("jdbc:sqllite:userDate.db");
            System.out.println("Got connection of Database");
            stmt.execute(content);
            result=stmt.getResultSet();
            stmt.close();
            c.close();

        }catch (ClassNotFoundException e){

        }catch (SQLException e){
        }
        return result;

    }
    public int initilizeTable(){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:userData.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
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
                    "    imagePath TEXT\n"+
                    ");\n" +
                    "CREATE UNIQUE INDEX Movies_UID_uindex ON Movies (UID);";
            stmt.executeUpdate(sql);
            String sql2="CREATE TABLE RepkeyWord\n" +
                    "(\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    orginalName TEXT,\n" +
                    "    numberKey TEXT\n" +
                    ");\n" +
                    "CREATE UNIQUE INDEX RepkeyWord_id_uindex ON RepkeyWord (id);\n" +
                    "CREATE UNIQUE INDEX RepkeyWord_orginalName_uindex ON RepkeyWord (orginalName);";
            stmt.close();
            stmt.execute(sql2);
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Table created successfully");
        return 0;
    }
    public ResultSet getMovieInfo(int type, String content){
        String sql="SELECT * FROM Movies WHERE "+keywords[type]+"="+content+";";
        ResultSet resultSet=constumupdate(sql);
        return resultSet;
    }
    public static void main(String args[]){
        Dbmsystem d=new Dbmsystem();
    }
}
