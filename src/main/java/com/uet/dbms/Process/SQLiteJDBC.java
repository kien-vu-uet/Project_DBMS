package com.uet.dbms.Process;

import com.uet.dbms.Entities.Account;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteJDBC {

    public static void insertWord(Word w) throws RuntimeException {
        try {
            Connection connection = null;
            Statement statement = null;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "INSERT INTO EngToVie(word,description,pronounce) " +
                    "VALUES('" + w.getTarget() +"', '" + w.getExplain() + "', \"" + w.getPronounce() + "\");";
            System.out.println(command);
            statement.executeUpdate(command);
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Insert successfully!");
        } catch (Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public static Word targetSearch(String target) throws RuntimeException {
        Word result = null;
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:dictionary.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM EngToVie Where word = \"" + target + "\";" );
            int id = rs.getInt("word_id");
            String word = rs.getString("word");
            String description = rs.getString("description");
            String pronounce = rs.getString("pronounce");
            result = new Word(id, word, description, pronounce);
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return result;
    }

    public static List<Word> patternSearch(String pattern) throws RuntimeException {
        List<Word> result = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM EngToVie WHERE word LIKE '"
                    + pattern + "%' ORDER BY word LIMIT 30;");
            while (rs.next()) {
                int id = rs.getInt("word_id");
                String word = rs.getString("word");
                String description = rs.getString("description");
                String pronounce = rs.getString("pronounce");
                result.add(new Word(id, word, description, pronounce));
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("No result found");
        }
        return result;
    }

    public static void modifyWord(Word word) throws RuntimeException {
//        @SuppressWarnings("unused")
        Connection connection = null;
        Statement statement = null;
        try {

            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE EngToVie SET word =\"" + word.getTarget() + "\" " +
                    "WHERE word_id ='" + word.getId() + "';");
            statement.close();
            connection.commit();
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE EngToVie SET description = \"" + word.getExplain() + "\" " +
                    "WHERE word_id ='" + word.getId() + "';");
            statement.close();
            connection.commit();
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE EngToVie SET pronounce = \"" + word.getPronounce() + "\" " +
                    "WHERE word_id ='" + word.getId() + "';");
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Edit done Successful");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static void deleteWord(int id) throws RuntimeException {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM EngToVie WHERE word_id = " + id + ";");
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Delete successfully!");
        } catch (Exception e) {
            System.err.println("No id Found, Escaped Method!");
        }
    }

    public static boolean checkExistedUsername(String username) throws RuntimeException {
        boolean existance = false;
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT username FROM `User_Account` WHERE username = '" + username + "';");
            String un = rs.getString("username");
            if (username.equals(un)) existance = true;
//            System.out.println(existance);
            statement.close();
            connection.commit();
        } catch (Exception e) {
            System.err.println("No username found!");
            return false;
        }
        return existance;
    }

    public static int checkLoginAccount(String username, String password) throws RuntimeException {
        int res = -1;
        Connection connection = null;
        Statement statement = null;
        try {
            if (!checkExistedUsername(username)) return res;
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT user_id, password FROM `User_Account` WHERE username = '" + username + "';");
            String truePassword = rs.getString("password");
            if (password.equals(truePassword)) {
                res = rs.getInt("user_id");
            }
            statement.close();
            connection.commit();
        } catch (Exception e) {
            System.err.println("Login false!");
            return -1;
        }
        return res;
    }

    public static Account getAccountByID(int id) throws RuntimeException {
        Account account = null;
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM `User_Account` WHERE user_id = " + id + ";");
            String username = rs.getString("username");
            String password = rs.getString("password");
            String avatarPath = rs.getString("avatarPath");
            if (avatarPath != null) account = new Account(id, username, password, avatarPath);
            else account = new Account(id, username, password);
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("No account found!");
            return null;
        }
        return account;
    }

    public static void insertAccount(String username, String password) throws RuntimeException {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "INSERT INTO User_Account (username,password) " +
                    "VALUES ('" + username + "','" + password + "');";
            statement.executeUpdate(command);
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Register successfully");
        } catch (Exception e) {
            System.err.println("Register failed!");
        }
    }

    public static void changeAvatarPath(int id, String path) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "UPDATE User_Account SET avatarPath = '" + path + "' WHERE user_id = " + id + ";";
            statement.executeUpdate(command);
            statement.close();
            connection.commit();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static List<String> getAllUsername() {
        List<String> res = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT username FROM `User_Account`;");
            while (rs.next()) {
                String username = rs.getString("username");
                res.add(username);
            }
            rs.close();
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Get all username!");
        } catch (Exception e) {
            System.err.println("No account signed up!");
            return null;
        }
        return res;
    }

    public static List<String> queryFavouriteTopic(int user_id) throws RuntimeException {
        List<String> result = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "SELECT DISTINCT topic FROM Favourite " +
                    "WHERE user_id = " + user_id + ";";
            ResultSet rs = statement.executeQuery(command);
            while (rs.next()) {
                String topic = rs.getString("topic");
                result.add(topic);
            }
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("Error in query topic: " + e.getMessage());
        }
        return result;
    }

    public static List<Word> queryWordByTopic(String topic) throws RuntimeException {
        List<Word> result = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        List<Integer> idList = new ArrayList<>();
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            ResultSet wordIdRS = statement.executeQuery("SELECT word_id FROM `Favourite` " +
                    "WHERE topic = '" + topic + "';");
            while (wordIdRS.next()) {
                int word_id = wordIdRS.getInt("word_id");
                idList.add(word_id);
            }
            wordIdRS.close();
            statement.close();
            // query word
            statement = connection.createStatement();
            String command = "SELECT * FROM EngToVie WHERE word_id IN (";
            for (int i = 0; i < idList.size() - 1; ++i) {
                command += idList.get(i) + ", ";
            }
            command += idList.get(idList.size() - 1) + ");";
            ResultSet wordRS = statement.executeQuery(command);
            int i = 0;
            while (wordRS.next()) {
                int id = idList.get(i);
                String word = wordRS.getString("word");
                String description = wordRS.getString("description");
                String pronounce = wordRS.getString("pronounce");
                result.add(new Word(id, word, description, pronounce));
                i++;
            }
            wordRS.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    public static void insertWordToFavourite(int user_id, String topic, int word_id) throws RuntimeException {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "INSERT INTO Favourite(user_id, topic, word_id) " +
                    "VALUES(" + user_id + ", '" + topic + "', " + word_id + ");";
            System.out.println(command);
            statement.executeUpdate(command);
            statement.close();
            connection.commit();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void deleteWordOnFavourite(int user_id, String topic, int word_id) throws RuntimeException {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "DELETE FROM Favourite WHERE user_id = " + user_id + " AND topic = '" + topic + "' AND word_id = " + word_id + ";";
            System.out.println(command);
            statement.executeUpdate(command);
            statement.close();
            connection.commit();
            connection.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void changeTopicName(String oldName, String newName) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "UPDATE Favourite " +
                    "SET topic = \"" + newName + "\" " +
                    "WHERE topic = \"" + oldName + "\";";
            statement.executeUpdate(command);
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Edit done!");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void deleteTopic(String topic, int user_id) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            String command = "DELETE FROM Favourite " +
                    "WHERE user_id = " + user_id + " AND topic = \"" + topic + "\";";
            statement.executeUpdate(command);
            statement.close();
            connection.commit();
            connection.close();
            System.out.println("Delete done!");
        } catch (SQLException | ClassNotFoundException throwables) {
            throwables.printStackTrace();
        }
    }
}
