

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseTrackerApp extends JFrame implements ActionListener {
    private JTextField expenseField, categoryField, dateField;
    private JButton addButton, editButton, deleteButton;
    private JTable expenseTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    private List<Expense> expenses;

    public ExpenseTrackerApp() {
        setTitle("Expense Tracker");
        setSize(939, 464);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        // Initialize expenses list
        expenses = new ArrayList<>();

        // Expense input field, category field, and buttons
        JPanel inputPanel = new JPanel();
        expenseField = new JTextField(10);
        expenseField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        categoryField = new JTextField(10);
        categoryField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        dateField = new JTextField(10);
        dateField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        addButton = new JButton("Add Expense");
        addButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        addButton.addActionListener(this);
        editButton = new JButton("Edit");
        editButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = expenseTable.getSelectedRow();
                if (selectedRow != -1) { // If a row is selected
                    String amountStr = tableModel.getValueAt(selectedRow, 0).toString();
                    String category = tableModel.getValueAt(selectedRow, 1).toString();
                    String dateStr = tableModel.getValueAt(selectedRow, 2).toString();

                    // Populate fields with selected expense data
                    expenseField.setText(amountStr);
                    categoryField.setText(category);
                    dateField.setText(dateStr);

                    // Remove the selected row from the table
                    expenses.remove(selectedRow);
                    tableModel.removeRow(selectedRow);

                    updateTotalLabel();
                } else {
                    JOptionPane.showMessageDialog(ExpenseTrackerApp.this, "Please select an expense to edit.");
                }
            }
        });
        deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = expenseTable.getSelectedRow();
                if (selectedRow != -1) { // If a row is selected
                    // Remove expense from the list
                    expenses.remove(selectedRow);

                    // Remove row from the table model
                    tableModel.removeRow(selectedRow);

                    updateTotalLabel();
                } else {
                    JOptionPane.showMessageDialog(ExpenseTrackerApp.this, "Please select an expense to delete.");
                }
            }
        });
        JLabel label = new JLabel("Amount:");
        label.setFont(new Font("Tahoma", Font.PLAIN, 14));
        inputPanel.add(label);
        inputPanel.add(expenseField);
        JLabel label_1 = new JLabel("Category:");
        label_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        inputPanel.add(label_1);
        inputPanel.add(categoryField);
        JLabel label_2 = new JLabel("Date (dd-MM-yyyy):");
        label_2.setFont(new Font("Tahoma", Font.PLAIN, 14));
        inputPanel.add(label_2);
        inputPanel.add(dateField);
        inputPanel.add(addButton);
        inputPanel.add(editButton);
        inputPanel.add(deleteButton);

        // Expense table
        String[] columnNames = {"Amount", "Category", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        expenseTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(expenseTable);
        scrollPane.setPreferredSize(new Dimension(580, 150));

        // Total expense label
        totalLabel = new JLabel("Total Expenses: Rs 0.00");
        totalLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu retrieveMenu = new JMenu("Retrieve");
        retrieveMenu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JMenuItem byDateRangeItem = new JMenuItem("By Date Range");
        byDateRangeItem.addActionListener(this);
        retrieveMenu.add(byDateRangeItem);
        JMenuItem byCategoryAndDateRangeItem = new JMenuItem("By Category and Date Range");
        byCategoryAndDateRangeItem.addActionListener(this);
        retrieveMenu.add(byCategoryAndDateRangeItem); // New option
        menuBar.add(retrieveMenu);
        setJMenuBar(menuBar);

        // Add components to the frame
        getContentPane().add(inputPanel, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(totalLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            String expenseStr = expenseField.getText();
            String category = categoryField.getText();
            String dateStr = dateField.getText();
            if (!expenseStr.isEmpty() && !dateStr.isEmpty()) {
                try {
                    double expenseAmount = Double.parseDouble(expenseStr);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = dateFormat.parse(dateStr);
                    Expense expense = new Expense(expenseAmount, category, date);
                    expenses.add(expense);
                    addToTable(expense);
                    updateTotalLabel();
                    expenseField.setText("");
                    categoryField.setText("");
                    dateField.setText("");
                } catch (NumberFormatException | java.text.ParseException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid expense amount and date in the format dd-MM-yyyy.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter an expense amount and date.");
            }
        } else if (e.getActionCommand().equals("Edit")) {
            int selectedRow = expenseTable.getSelectedRow();
            if (selectedRow != -1) {
                String expenseStr = expenseField.getText();
                String category = categoryField.getText();
                String dateStr = dateField.getText();
                if (!expenseStr.isEmpty() && !dateStr.isEmpty()) {
                    try {
                        double expenseAmount = Double.parseDouble(expenseStr);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = dateFormat.parse(dateStr);
                        // Update expense in list
                        Expense selectedExpense = expenses.get(selectedRow);
                        selectedExpense.setAmount(expenseAmount);
                        selectedExpense.setCategory(category);
                        selectedExpense.setDate(date);
                        // Update table
                        tableModel.setValueAt(String.format("%.2f", expenseAmount), selectedRow, 0);
                        tableModel.setValueAt(category, selectedRow, 1);
                        tableModel.setValueAt(dateFormat.format(date), selectedRow, 2);
                        updateTotalLabel();
                        expenseField.setText("");
                        categoryField.setText("");
                        dateField.setText("");
                    } catch (NumberFormatException | java.text.ParseException ex) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid expense amount and date in the format dd-MM-yyyy.");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select an expense to edit.");
                }
            }
        } else if (e.getActionCommand().equals("Delete")) {
            // Existing code to delete expenses
        } else if (e.getActionCommand().equals("By Date Range")) {
            showDialogToRetrieveByDateRange();
        } else if (e.getActionCommand().equals("By Category and Date Range")) {
            showDialogToRetrieveByCategoryAndDateRange();
        }
    }

    private void addToTable(Expense expense) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String[] rowData = {String.format("%.2f", expense.getAmount()), expense.getCategory(), dateFormat.format(expense.getDate())};
        tableModel.addRow(rowData);
    }

    private void updateTotalLabel() {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        totalLabel.setText("Total Expenses: Rs " + String.format("%.2f", total));
    }

    private void showDialogToRetrieveByDateRange() {
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expenses added yet.");
            return;
        }
        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        panel.add(new JLabel("Start Date (dd-MM-yyyy):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (dd-MM-yyyy):"));
        panel.add(endDateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Date Range", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date startDate = dateFormat.parse(startDateField.getText());
                Date endDate = dateFormat.parse(endDateField.getText());

                List<Expense> filteredExpenses = new ArrayList<>();
                for (Expense expense : expenses) {
                    Date expenseDate = expense.getDate();
                    if (expenseDate.compareTo(startDate) >= 0 && expenseDate.compareTo(endDate) <= 0) {
                        filteredExpenses.add(expense);
                    }
                }
                showRetrievedExpenses(filteredExpenses);
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid dates in the format dd-MM-yyyy.");
            }
        }
    }

    private void showDialogToRetrieveByCategoryAndDateRange() {
        if (expenses.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No expenses added yet.");
            return;
        }

        JTextField categoryField = new JTextField(10);
        JTextField startDateField = new JTextField(10);
        JTextField endDateField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        panel.add(new JLabel("Category:"));
        panel.add(categoryField);
        panel.add(new JLabel("Start Date (dd-MM-yyyy):"));
        panel.add(startDateField);
        panel.add(new JLabel("End Date (dd-MM-yyyy):"));
        panel.add(endDateField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Category and Date Range", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date startDate = dateFormat.parse(startDateField.getText());
                Date endDate = dateFormat.parse(endDateField.getText());

                String category = categoryField.getText();

                List<Expense> filteredExpenses = expenses.stream()
                        .filter(expense -> expense.getCategory().equalsIgnoreCase(category))
                        .filter(expense -> expense.getDate().compareTo(startDate) >= 0 && expense.getDate().compareTo(endDate) <= 0)
                        .collect(Collectors.toList());

                if (filteredExpenses.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No expenses found for category '" + category + "' in the specified date range.");
                } else {
                    showRetrievedExpenses(filteredExpenses);
                }
            } catch (java.text.ParseException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid dates in the format dd-MM-yyyy.");
            }
        }
    }
    private void showRetrievedExpenses(List<Expense> retrievedExpenses) {
        JFrame retrieveFrame = new JFrame("Retrieved Expenses");
        retrieveFrame.setSize(600, 400);
        retrieveFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        DefaultTableModel retrieveTableModel = new DefaultTableModel(new String[]{"Amount", "Category", "Date"}, 0);
        JTable retrieveTable = new JTable(retrieveTableModel);

        JScrollPane retrieveScrollPane = new JScrollPane(retrieveTable);

        for (Expense expense : retrievedExpenses) {
            addToTable(expense, retrieveTableModel);
        }

        JLabel retrieveTotalLabel = new JLabel("Total Expenses: Rs " + getTotalExpenses(retrievedExpenses));
        retrieveTotalLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));

        JButton backButton = new JButton("Back to Add Expenses");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retrieveFrame.dispose();
            }
        });

        JPanel retrievePanel = new JPanel();
        retrievePanel.setLayout(new BorderLayout());
        retrievePanel.add(retrieveScrollPane, BorderLayout.CENTER);
        retrievePanel.add(retrieveTotalLabel, BorderLayout.SOUTH);
        retrievePanel.add(backButton, BorderLayout.NORTH);

        retrieveFrame.add(retrievePanel);
        retrieveFrame.setVisible(true);
    }

    private double getTotalExpenses(List<Expense> expenses) {
        double total = 0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    private void addToTable(Expense expense, DefaultTableModel model) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String[] rowData = {String.format("%.2f", expense.getAmount()), expense.getCategory(), dateFormat.format(expense.getDate())};
        model.addRow(rowData);
    }

    private void clearTable() {
        tableModel.setRowCount(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ExpenseTrackerApp();
            }
        });
    }
}

class Expense {
    private double amount;
    private String category;
    private Date date;

    public Expense(double amount, String category, Date date) {
        this.amount = amount;
        this.category = category;
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}