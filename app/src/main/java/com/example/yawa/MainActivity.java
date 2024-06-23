package com.example.yawa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Field;
import java.util.ArrayList;

import adapter.money_history;
import model.money_history_model;
import model.integerList;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private money_history adapter;
    private ArrayList<money_history_model> emergencyList = new ArrayList<>();
    private ArrayList<Integer> integerListSavings = new ArrayList<>();
    private ArrayList<Integer> integerListAllowance = new ArrayList<>();
    private ArrayList<Integer> integerListExpenses = new ArrayList<>();
    private RecyclerView emergencyRecyclerView;
    int sumSavings, sumExpenses, sumAllowance;
    private FloatingActionButton add_button, chatbot_button;
    TextView savings, overall_allowance, overall_expenses;

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize Firebase Auth and get the current user
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize UI elements
        add_button = findViewById(R.id.add_button);
        emergencyRecyclerView = findViewById(R.id.recycle_view_history_day);
        overall_allowance = findViewById(R.id.total_allowance);
        overall_expenses = findViewById(R.id.overall_expenses);

        chatbot_button = findViewById(R.id.chatbot_icons);
        savings = findViewById(R.id.savings);

        // Setup RecyclerView
        emergencyRecyclerView.setHasFixedSize(true);
        emergencyRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Start listening to Firestore data
        listenToDocumentLocal();

        // Setup FloatingActionButton click listener
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(MainActivity.this, add_button);
            }
        });
        chatbot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MazenChatbot.class);
                startActivity(intent);
            }
        });


    }

    @SuppressLint("NotifyDataSetChanged")
    public void listenToDocumentLocal() {
        final CollectionReference docRef = db.collection("Daily Money History");
        docRef.addSnapshotListener((querySnapshot, e) -> {
            if (e != null) {
                Log.e("MainActivity", "Listen failed.", e);
                return;
            }

            if (querySnapshot != null) {
                adapter = new money_history(emergencyList, MainActivity.this);
                emergencyRecyclerView.setAdapter(adapter);

                for (DocumentChange dc : querySnapshot.getDocumentChanges()) {
                    DocumentSnapshot snapshot = dc.getDocument();
                    String source = snapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";

                    if (snapshot.exists()) {
                        db.collection("Daily Money History").get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            emergencyList.clear();
                                            for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                                                money_history_model model = d.toObject(money_history_model.class);
                                                emergencyList.add(model);


                                                // Use the sum as needed

                                            }
                                            convertAndSumData();
                                            adapter.notifyDataSetChanged();
                                            savings.setText(sumSavings+" PHP");
                                            overall_allowance.setText(sumAllowance+" PHP");
                                            overall_expenses.setText(sumExpenses+" PHP");


                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.e("MainActivity", "Error retrieving data: " + e.getMessage());
                                    }
                                });
                    } else {
                        Log.d("MainActivity", source + " data: null");
                    }
                }
            }
        });
    }
    private void updateUI() {
        // Update RecyclerView adapter and other UI elements
        if (adapter == null) {
            adapter = new money_history(emergencyList, MainActivity.this);
            emergencyRecyclerView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

        savings.setText(sumSavings + " PHP");
        overall_allowance.setText(sumAllowance + " PHP");
        overall_expenses.setText(sumExpenses + " PHP");
    }

    private void showPopupMenu(Context context, View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.inflate(R.menu.popup_menu); // Ensure you have this menu in res/menu/popup_menu.xml
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.icon_edit) {
                    Toast.makeText(context, "Edited", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.icon_delete) {
                    Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.icon_add) {
                    Intent intent = new Intent(context, creating_tracker.class);
                    startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        });

        try {
            Field popup = PopupMenu.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            Object menu = popup.get(popupMenu);
            menu.getClass()
                    .getDeclaredMethod("setForceShowIcon", boolean.class)
                    .invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            popupMenu.show();
        }

    }
    private void convertAndSumData() {
        integerListSavings.clear();
        integerListAllowance.clear();
        integerListExpenses.clear();
        for (money_history_model model : emergencyList) {
            try {
                int savings = Integer.parseInt(model.getSaved_money());
                int total_allowance = Integer.parseInt(model.getCurrent_money());
                int over_expenses = Integer.parseInt(model.getExpenses_today());
                integerListSavings.add(savings);
                integerListAllowance.add(total_allowance);
                integerListExpenses.add(over_expenses);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        sumSavings = sumIntegerSavings(integerListSavings);
        sumAllowance = sumIntegerAllowance(integerListAllowance);
        sumExpenses = sumIntegerExpenses(integerListExpenses);
    }
    private int sumIntegerSavings(ArrayList<Integer> integerListSavings) {
        int sum = 0;
        for (int value : integerListSavings) {
            sum += value;
        }
        return sum;
    }
    private int sumIntegerAllowance(ArrayList<Integer> integerListAllowance) {
        int sum = 0;
        for (int value : integerListAllowance) {
            sum += value;
        }
        return sum;
    }
    private int sumIntegerExpenses(ArrayList<Integer> integerListExpenses) {
        int sum = 0;
        for (int value : integerListExpenses) {
            sum += value;
        }
        return sum;
    }
}
