package model;

public class money_history_model {
    private String id; // New field for document ID
    private String day;
    private String date;
    private String current_money;
    private String category;
    private String mode_of_payment;
    private String saved_money;
    private String expenses_today;

    // No-argument constructor required for Firestore
    public money_history_model() {
        // Initialize fields if needed
    }

    // Parameterized constructor
    public money_history_model(String day, String date, String current_money, String category, String mode_of_payment, String saved_money, String expenses_today) {
        this.day = day;
        this.date = date;
        this.current_money = current_money;
        this.category = category;
        this.mode_of_payment = mode_of_payment;
        this.saved_money = saved_money;
        this.expenses_today = expenses_today;
    }

    // Getters and setters for fields
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCurrent_money() {
        return current_money;
    }

    public void setCurrent_money(String current_money) {
        this.current_money = current_money;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMode_of_payment() {
        return mode_of_payment;
    }

    public void setMode_of_payment(String mode_of_payment) {
        this.mode_of_payment = mode_of_payment;
    }

    public String getSaved_money() {
        return saved_money;
    }

    public void setSaved_money(String saved_money) {
        this.saved_money = saved_money;
    }

    public String getExpenses_today() {
        return expenses_today;
    }

    public void setExpenses_today(String expenses_today) {
        this.expenses_today = expenses_today;
    }
}
