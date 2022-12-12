package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final SQLiteDatabase database;

    public PersistentTransactionDAO(SQLiteDatabase db) {
        this.database = db;
    }

    // Insert transaction into the embedded database
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String sql = "INSERT INTO Transact (account_no,type,amount,date) VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1, accountNo);
        statement.bindLong(2, (expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        statement.bindDouble(3, amount);
        statement.bindLong(4, date.getTime());

        statement.executeInsert();
    }

    // get all transaction logs from the embedded database
    @Override
    public List<Transaction> getAllTransactionLogs() {
        Cursor data = database.rawQuery("SELECT * FROM Transact", null);
        List<Transaction> transactions = new ArrayList<>();

        if(data.moveToFirst()){
            do{
                Transaction t = new Transaction(new Date(data.getLong(data.getColumnIndex("date"))),
                        data.getString(data.getColumnIndex("account_no")),
                        (data.getInt(data.getColumnIndex("type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        data.getDouble(data.getColumnIndex("amount")));
                transactions.add(t);
            }while(data.moveToNext());
        }

        data.close();
        return transactions;

    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor data = database.rawQuery("SELECT * FROM Transact LIMIT"+limit, null);
        List<Transaction> transactions = new ArrayList<>();

        if(data.moveToFirst()){
            do{
                Transaction t = new Transaction(new Date(data.getLong(data.getColumnIndex("date"))),
                        data.getString(data.getColumnIndex("account_no")),
                        (data.getInt(data.getColumnIndex("type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        data.getDouble(data.getColumnIndex("amount")));
                transactions.add(t);
            }while(data.moveToNext());
        }

        data.close();
        return transactions;
    }
}
