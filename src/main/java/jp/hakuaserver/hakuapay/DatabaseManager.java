package jp.hakuaserver.hakuapay;

import java.io.File;
import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private Connection conn;

    public void connect() throws SQLException {
        File dbDir = new File("plugins/hakuapay");
        if (!dbDir.exists()) {
            dbDir.mkdirs();  // フォルダを自動で作成
        }
        conn = DriverManager.getConnection("jdbc:sqlite:plugins/hakuapay/data.db");
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS balances (uuid TEXT PRIMARY KEY, balance INTEGER, job TEXT)");
        }
    }

    public void close() {
        try {
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBalance(UUID uuid) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT balance FROM balances WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("balance");
            setBalance(uuid, 0);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setBalance(UUID uuid, int balance) {
        try (PreparedStatement ps = conn.prepareStatement("REPLACE INTO balances (uuid, balance, job) VALUES (?, ?, COALESCE((SELECT job FROM balances WHERE uuid = ?), NULL))")) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, balance);
            ps.setString(3, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean addBalance(UUID uuid, int amount) {
        double current = getBalance(uuid);
        setBalance(uuid, current + amount);
        return true;
    }

    public boolean deposit(UUID uuid, int amount) {
        int newBalance = getBalance(uuid) + amount;
        setBalance(uuid, newBalance);
        return true;
    }

    public boolean subtractBalance(UUID uuid, int amount) {
        int current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    public boolean withdraw(UUID uuid, int amount) {
        int current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    public boolean has(UUID uuid, int amount) {
        return getBalance(uuid) >= amount;
    }

    public void setJob(UUID uuid, String job) {
        try (PreparedStatement ps = conn.prepareStatement("REPLACE INTO balances (uuid, balance, job) VALUES (?, COALESCE((SELECT balance FROM balances WHERE uuid = ?), 0), ?)")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, uuid.toString());
            ps.setString(3, job);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getJob(UUID uuid) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT job FROM balances WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("job");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}