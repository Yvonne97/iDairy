package com.example.hp.milkproject;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddFeed extends AppCompatActivity {

    EditText editTextDate, editTextQuantity, editTextName, editTextCost;
    Button buttonDone;
    Spinner spinner;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener dateChangedListener;
    ArrayList<String> stringsCategoryName;
    String cowName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_feed);

        progressDialog = new ProgressDialog(this);

        editTextDate = findViewById(R.id.editTextDate);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        editTextName = findViewById(R.id.editTextName);
        editTextCost = findViewById(R.id.editTextCost);
        buttonDone = findViewById(R.id.buttonDone);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextDate.getText().toString().equals("")) {
                    Toast.makeText(AddFeed.this, "Please add date", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", "date" + editTextDate.getText().toString());
                    map.put("uid", FirebaseAuth.getInstance().getUid());
                    map.put("quantity", "quantity " + editTextQuantity.getText().toString());
                    map.put("feedName", "feed name " + editTextName.getText().toString());
                    map.put("message", "New feed was added");

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    databaseReference.child("Report").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            finish();
                        }
                    });
                }
            }
        });

        myCalendar = Calendar.getInstance();
        dateChangedListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                addDate();
            }
        };

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(AddFeed.this, dateChangedListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void addDate() {
        String myFormat = "dd MMM yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }
}