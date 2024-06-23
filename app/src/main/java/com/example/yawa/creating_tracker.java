package com.example.yawa;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class creating_tracker extends AppCompatActivity {

    String[] category = {"Food", "Transportation", "Entertainment", "Necessities", "Miscellaneous", "Others"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    EditText pocket_money, spent;
    String[] mode_of_payment_str = {"Cash", "Gcash", "Paymaya", "Debit Card", "Credit Card", "Apple pay", "Others"};
    AutoCompleteTextView Mode_of_payment;
    ArrayAdapter<String> adapterMode_of_payment;
    private Calendar selectedDate;
    Calendar lastSelectedDate;
    String nice, dayVal, dateVal, item, item2;
    private Timestamp selectedDateToTimestamp;
    Button pickDateButton, create;

    // Map for custom day abbreviations
    private static final Map<String, String> DAY_ABBREVIATIONS = new HashMap<>();
    static {
        DAY_ABBREVIATIONS.put("Monday", "Mon");
        DAY_ABBREVIATIONS.put("Tuesday", "Tues");
        DAY_ABBREVIATIONS.put("Wednesday", "Wed");
        DAY_ABBREVIATIONS.put("Thursday", "Thurs");
        DAY_ABBREVIATIONS.put("Friday", "Fri");
        DAY_ABBREVIATIONS.put("Saturday", "Sat");
        DAY_ABBREVIATIONS.put("Sunday", "Sun");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_creating_tracker);
        pickDateButton = findViewById(R.id.pickDateButton);
        pocket_money = findViewById(R.id.pocket_money);
        spent = findViewById(R.id.spent);
        create = findViewById(R.id.create);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        adapterItems = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, category);

        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                item = adapterView.getItemAtPosition(position).toString();
            }
        });

        Mode_of_payment = findViewById(R.id.mode_of_payment);
        adapterMode_of_payment = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mode_of_payment_str);

        Mode_of_payment.setAdapter(adapterMode_of_payment);
        Mode_of_payment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long Mode_of_payment) {
                item2 = adapterView.getItemAtPosition(position).toString();
            }
        });

        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(item == null) && !(item2 == null) && !(nice == null) && !(dateVal == null) && !(dayVal == null)){
                    createEM();
                } else {
                    Toast.makeText(creating_tracker.this, "Fill up all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar currentDate = Calendar.getInstance();
        int year, month, day;

        if (lastSelectedDate != null) {
            year = lastSelectedDate.get(Calendar.YEAR);
            month = lastSelectedDate.get(Calendar.MONTH);
            day = lastSelectedDate.get(Calendar.DAY_OF_MONTH);
        } else {
            year = currentDate.get(Calendar.YEAR);
            month = currentDate.get(Calendar.MONTH);
            day = currentDate.get(Calendar.DAY_OF_MONTH);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate = Calendar.getInstance();
                    selectedDate.set(year1, monthOfYear, dayOfMonth);
                    selectedDateToTimestamp = new Timestamp(selectedDate.getTimeInMillis());

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault());
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEEE", Locale.getDefault());

                    String formattedDate = dateFormat.format(selectedDateToTimestamp);
                    String formattedDate1 = dateFormat1.format(selectedDateToTimestamp);
                    String formattedDate2 = dateFormat2.format(selectedDateToTimestamp);

                    // Apply custom day abbreviations
                    formattedDate1 = replaceDayWithAbbreviation(formattedDate1);
                    formattedDate2 = replaceDayWithAbbreviation(formattedDate2);

                    pickDateButton.setText(formattedDate1);
                    lastSelectedDate = selectedDate;
                    nice = formattedDate1;
                    dayVal = formattedDate2;
                    dateVal = formattedDate;

                    Toast.makeText(creating_tracker.this, formattedDate1, Toast.LENGTH_SHORT).show();
                }, year, month, day);

        datePickerDialog.show();
    }

    private String replaceDayWithAbbreviation(String dateStr) {
        for (Map.Entry<String, String> entry : DAY_ABBREVIATIONS.entrySet()) {
            dateStr = dateStr.replace(entry.getKey(), entry.getValue());
        }
        return dateStr;
    }

    private void createEM() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference emergencyRef = db.collection("Daily Money History").document();
        Map<String, Object> emergency = new HashMap<>();

        String spent_value = spent.getText().toString();
        String pocket_value = pocket_money.getText().toString();
        int saved_money = (Integer.parseInt(pocket_value)) - (Integer.parseInt(spent_value));
        String saved_moneyVal = String.valueOf(saved_money);

        db.collection("Daily Money History")
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Handle the case where the user document exists
                    } else {
                        // Handle the case where the user document doesn't exist
                    }

                    // Calculate queueNumber separately by counting documents in "Daily Money History" collection
                    db.collection("Daily Money History")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                emergency.put("current_money", pocket_value);
                                emergency.put("expenses_today", spent_value);
                                emergency.put("mode_of_payment", item2);
                                emergency.put("saved_money", saved_moneyVal);
                                emergency.put("category", item);
                                emergency.put("day", dayVal);
                                emergency.put("date", dateVal);
                                emergencyRef.set(emergency)
                                        .addOnSuccessListener(aVoid -> {
                                            // Handle success case
                                            Toast.makeText(creating_tracker.this, "Confirmed", Toast.LENGTH_SHORT).show();
                                            overridePendingTransition(0, 0);
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            // Handle failure case
                                            Toast.makeText(creating_tracker.this, "Failed to add", Toast.LENGTH_SHORT).show();
                                        });
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure case
                                Toast.makeText(creating_tracker.this, "Failed to create emergency", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle failure case
                    Toast.makeText(creating_tracker.this, "Failed to create emergency", Toast.LENGTH_SHORT).show();
                });
    }
}
