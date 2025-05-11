package jp.hakuaserver.hakuapay;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import java.util.List;

public class VaultEconomyProvider implements Economy {

    private final DatabaseManager db;

    public VaultEconomyProvider(DatabaseManager db) {
        this.db = db;
    }

    @Override
    public int fractionalDigits() {
        return -1; // 通貨の小数点以下の桁数（例: 2なら「100.00」）
    }
    @Override
    public String currencyNamePlural() {
        return "Coins"; // 複数形の通貨名
    }

    @Override
    public String currencyNameSingular() {
        return "Coin"; // 単数形の通貨名
    }

    @Override
    public boolean isEnabled() { return true; }

    @Override
    public String getName() { return "VaultEconomy"; }

    @Override
    public boolean hasBankSupport() { return false; }

    @Override
    public double getBalance(OfflinePlayer player) {
        return db.getBalance(player.getUniqueId());
    }

    @Override
    public double getBalance(String s, String s1) {
        return 0;
    }

    @Override
    public double getBalance(OfflinePlayer offlinePlayer, String s) {
        return 0;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return db.has(player.getUniqueId(), (int) amount);
    }

    @Override
    public boolean has(String s, String s1, double v) {
        return false;
    }

    @Override
    public boolean has(OfflinePlayer offlinePlayer, String s, double v) {
        return false;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        if (!db.withdraw(player.getUniqueId(), (int) amount)) {
            return new EconomyResponse(0, getBalance(player), EconomyResponse.ResponseType.FAILURE, "残高不足");
        }
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse withdrawPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        db.deposit(player.getUniqueId(), (int) amount);
        return new EconomyResponse(amount, getBalance(player), EconomyResponse.ResponseType.SUCCESS, null);
    }

    @Override
    public EconomyResponse depositPlayer(String s, String s1, double v) {
        return null;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer offlinePlayer, String s, double v) {
        return null;
    }

    // 必要だが無効なメソッドたち（銀行機能を無効化する）
    @Override public List<String> getBanks() { return null; }
    @Override public boolean hasAccount(OfflinePlayer player) { return true; }

    @Override
    public boolean hasAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean hasAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    @Override public boolean createPlayerAccount(OfflinePlayer player) { return true; }

    @Override
    public boolean createPlayerAccount(String s, String s1) {
        return false;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer offlinePlayer, String s) {
        return false;
    }

    // その他の未使用メソッドも適当に実装（オーバーロード含む）
    @Override public EconomyResponse withdrawPlayer(String name, double amount) { return null; }
    @Override public EconomyResponse depositPlayer(String name, double amount) { return null; }
    @Override public boolean has(String name, double amount) { return false; }
    @Override public double getBalance(String name) { return 0; }
    @Override public boolean hasAccount(String name) { return true; }
    @Override public boolean createPlayerAccount(String name) { return true; }
    @Override public String format(double amount) { return String.format("%.2f", amount); }

    // 銀行関連メソッド無効化
    @Override public EconomyResponse createBank(String name, String player) { return notSupported(); }

    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override public EconomyResponse deleteBank(String name) { return notSupported(); }
    @Override public EconomyResponse bankBalance(String name) { return notSupported(); }
    @Override public EconomyResponse bankHas(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankWithdraw(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse bankDeposit(String name, double amount) { return notSupported(); }
    @Override public EconomyResponse isBankOwner(String name, String player) { return notSupported(); }

    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return null;
    }

    @Override public EconomyResponse isBankMember(String name, String player) { return notSupported(); }

    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return null;
    }


    private EconomyResponse notSupported() {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "銀行は未対応です");
    }
}
