package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

// A persistent expense manager that keeps the data created by the app
// in an embedded database
public class PersistentDemoExpenseManager extends ExpenseManager{
    private final Context context;

    public PersistentDemoExpenseManager(Context context){
        this.context = context;
        try {
            setup();
        }catch (ExpenseManagerException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void setup() throws ExpenseManagerException {
        // create data base
        SQLiteDatabase myDatabase = context.openOrCreateDatabase("200193U", context.MODE_PRIVATE, null);

        // create table for Account
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS Account(" +
                "account_no VARCHAR PRIMARY KEY," +
                "bank_name VARCHAR," +
                "holder VARCHAR," +
                "initial_amount REAL" +
                " );");

        // create table for transactionLog
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS Transact(" +
                "transaction_id INTEGER PRIMARY KEY," +
                "account_no VARCHAR," +
                "type INT," +
                "amount REAL," +
                "date DATE," +
                "FOREIGN KEY (account_no) REFERENCES Account(account_no)" +
                ");");

        // set tha database for Account details
        PersistentAccountDAO accountDAO = new PersistentAccountDAO(myDatabase);
        setAccountsDAO(accountDAO);

        // set the database for transactions
        PersistentTransactionDAO transactionDAO = new PersistentTransactionDAO(myDatabase);
        setTransactionsDAO(transactionDAO);
    }
}
