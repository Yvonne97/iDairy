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

public class AddMilking extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText editTextDate, editTextQuantity, editTextAM, editTextNO, editTextPM;
    Button buttonDone;
    Spinner spinner;
    ArrayList<String> stringsCategoryName;
    String cowName;
    ProgressDialog progressDialog;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener dateChangedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_milking);

        progressDialog = new ProgressDialog(this);

        editTextDate = findViewById(R.id.editTextDate);
        editTextQuantity = findViewById(R.id.editTextTotal);
        editTextAM = findViewById(R.id.editText7AM);
        editTextNO = findViewById(R.id.editText6NO);
        editTextPM = findViewById(R.id.editText4PM);
        buttonDone = findViewById(R.id.buttonDone);
        spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cowName.equals("Choose Cow")){
                    Toast.makeText(AddMilking.this, "Please Choose a cow", Toast.LENGTH_SHORT).show();
                }else {
                    Map<String, Object> map = new HashMap<>();
                    map.put("date", "date "+editTextDate.getText().toString());
                    map.put("name", cowName);
                    map.put("uid", FirebaseAuth.getInstance().getUid());
                    map.put("quantity", "quantity "+editTextQuantity.getText().toString()+"ltrs");
                    map.put("milkedTime", "milking time "+editTextAM.getText().toString()+"ltrs AM ,"+editTextNO.getText().toString()+"ltrs NOON ,"+editTextPM.getText().toString()+"ltrs PM");
                    map.put("message", "Milking was done");

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

        getCategoryNames();

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
                new DatePickerDialog(AddMilking.this, dateChangedListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void addDate() {
        String myFormat = "dd MMM yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void getCategoryNames() {

        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (stringsCategoryName != null) {
            stringsCategoryName.clear();
        }

        stringsCategoryName = new ArrayList<String>();
        stringsCategoryName.add("Choose Cow");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Cows").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    GetName getName = postSnapshot.getValue(GetName.class);
                    stringsCategoryName.add(getName.getName());
                }
                final ArrayAdapter<String> adapterCategory = new ArrayAdapter<>(AddMilking.this, android.R.layout.simple_spinner_item, stringsCategoryName);
                adapterCategory.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapterCategory);
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class GetName {
        String name;

        public GetName() {
        }

        public GetName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cowName = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
