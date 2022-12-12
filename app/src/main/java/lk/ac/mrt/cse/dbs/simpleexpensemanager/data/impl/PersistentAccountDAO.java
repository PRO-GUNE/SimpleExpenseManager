package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidTransactionException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


public class PersistentAccountDAO implements AccountDAO {
    private final SQLiteDatabase database;

    public PersistentAccountDAO(SQLiteDatabase db) {
        this.database = db;
    }

    // read all the account numbers stored in the embedded database
    @Override
    public List<String> getAccountNumbersList() {
        Cursor data = database.rawQuery("SELECT account_no FROM Account", null);
        List<String> accounts = new ArrayList<>();

        if (data.moveToFirst()) {
            do {
                accounts.add(data.getString(data.getColumnIndex("account_no")));
            }while(data.moveToNext());
        }
        data.close();
        return accounts;
    }

    // read all the account details stored in the embedded database
    @Override
    public List<Account> getAccountsList() {
        Cursor data = database.rawQuery("SELECT * FROM Account", null);
        List<Account> accounts = new ArrayList<>();

        if (data.moveToFirst()) {
            do {
                Account account = new Account(data.getString(data.getColumnIndex("account_no")),
                        data.getString(data.getColumnIndex("bank_name")),
                        data.getString(data.getColumnIndex("holder")),
                        data.getDouble(data.getColumnIndex("initial_amount")));
                accounts.add(account);
            }while(data.moveToNext());
        }

        data.close();
        return accounts;
    }

    // read the details of a given account number stored in the database
    @Override
    public Account getAccount(String accountNo)
            throws InvalidAccountException {
        Cursor data = database.rawQuery("SELECT * FROM Account WHERE account_no = " + accountNo, null);
        // if data is empty we have an invalid account number
        if(data.getCount()==0){
            throw new InvalidAccountException("Invalid Account");
        }

        Account account = null;
        if (data.moveToFirst()) {
            do {
                account = new Account(data.getString(data.getColumnIndex("account_no")),
                        data.getString(data.getColumnIndex("bank_name")),
                        data.getString(data.getColumnIndex("holder")),
                        data.getDouble(data.getColumnIndex("initial_amount")));
            }while (data.moveToNext());
        }
        data.close();
        return account;
    }

    // insert a new account into the embedded database
    @Override
    public void addAccount(Account account) {
        String sql = "INSERT INTO Account (account_no,bank_name,holder,initial_amount) VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, account.getAccountNo());
        statement.bindString(2, account.getBankName());
        statement.bindString(3, account.getAccountHolderName());
        statement.bindDouble(4, account.getBalance());

        statement.executeInsert();
    }

    // delete an account from the embedded database
    @Override
    public void removeAccount(String accountNo)
            throws InvalidAccountException {
        String sql = "DELETE FROM Account WHERE account_no = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, accountNo);

        statement.executeUpdateDelete();
    }

    // update the balance of a given account stored in the embedded database
    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount)
            throws InvalidAccountException, InvalidTransactionException {
        double balance = getAccount(accountNo).getBalance();
        String sql = "UPDATE Account SET initial_amount = initial_amount + ?";
        SQLiteStatement statement = database.compileStatement(sql);
        if (expenseType == ExpenseType.EXPENSE) {
            if(balance > amount){
                statement.bindDouble(1, -amount);
            }else{
                throw new InvalidTransactionException("Insufficient Balance");
            }

        } else {
            statement.bindDouble(1, amount);
        }

        statement.executeUpdateDelete();
    }
}
