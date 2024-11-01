package com.example.demo.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.stereotype.Service;

import io.github.cdimascio.dotenv.Dotenv;

@Service
public class SqlQueryExecutor {

   
    private String dbUrl;
    private String user;
    private String password;
    
    public SqlQueryExecutor() {
    	
    	final Dotenv dotenv = Dotenv.load();
    	this.dbUrl = dotenv.get("url");
    	this.user = dotenv.get("username");
    	this.password = dotenv.get("password");
    	
    }

    public String executeSqlQuery(String sqlQuery) {
        StringBuilder result = new StringBuilder();
       

        try (Connection conn = DriverManager.getConnection(dbUrl, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlQuery)) {

            int columnCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    result.append(rs.getMetaData().getColumnName(i)).append(": ").append(rs.getString(i)).append("\t");
                }
                result.append("\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error executing query: " + e.getMessage();
        }

        return result.toString();
    }
}

