package com.example.currencyconverter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Step 1: Declare all the UI components we need
    EditText etAmount;       // Box where user types the amount
    Spinner spinnerFrom;     // Dropdown to select FROM currency
    Spinner spinnerTo;       // Dropdown to select TO currency
    TextView tvResult;       // Shows the converted amount
    Button btnConvert;       // Convert button
    Button btnSwap;          // Swap currencies button
    Button btnSettings;      // Go to settings button

    // Step 2: Define exchange rates (everything compared to 1 INR)
    // Example: 1 INR = 0.012 USD
    double INR_TO_USD = 0.012;
    double INR_TO_JPY = 1.82;
    double INR_TO_EUR = 0.011;

    // Step 3: List of currencies shown in dropdown
    String[] currencies = {"INR", "USD", "JPY", "EUR"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Load the layout file

        // Step 4: Connect Java variables to XML views using their IDs
        etAmount    = findViewById(R.id.etAmount);
        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo   = findViewById(R.id.spinnerTo);
        tvResult    = findViewById(R.id.tvResult);
        btnConvert  = findViewById(R.id.btnConvert);
        btnSwap     = findViewById(R.id.btnSwap);
        btnSettings = findViewById(R.id.btnSettings);

        // Step 5: Fill the dropdowns with currency names
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                currencies
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);

        // Default: FROM = INR (index 0), TO = USD (index 1)
        spinnerFrom.setSelection(0);
        spinnerTo.setSelection(1);

        // Step 6: Set up button click listeners

        // When Convert button is clicked
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertCurrency();
            }
        });

        // When Swap button is clicked — swap FROM and TO
        btnSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int fromPos = spinnerFrom.getSelectedItemPosition();
                int toPos   = spinnerTo.getSelectedItemPosition();
                spinnerFrom.setSelection(toPos);
                spinnerTo.setSelection(fromPos);
            }
        });

        // When Settings button is clicked — open SettingsActivity
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }