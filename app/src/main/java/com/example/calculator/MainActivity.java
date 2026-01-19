package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText input;
    EditText result;

    String operator = "";
    String oldNumber = "";
    double memory = 0.0;
    boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input_expression);
        result = findViewById(R.id.result);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // обработка нажатий цифр, точки и +-
    public void ClickNumber(View view) {
        String number = input.getText().toString();

        if (isNewInput || number.equals("0") || number.endsWith("=")) {
            if (view.getId() != R.id.ButtonForDot){
                number = "";
            }
            if (number.endsWith("=")) result.setText("0");
            isNewInput = false;
        }

        int id = view.getId();

        if (id == R.id.ButtonFor0) number += "0";
        else if (id == R.id.ButtonFor1) number += "1";
        else if (id == R.id.ButtonFor2) number += "2";
        else if (id == R.id.ButtonFor3) number += "3";
        else if (id == R.id.ButtonFor4) number += "4";
        else if (id == R.id.ButtonFor5) number += "5";
        else if (id == R.id.ButtonFor6) number += "6";
        else if (id == R.id.ButtonFor7) number += "7";
        else if (id == R.id.ButtonFor8) number += "8";
        else if (id == R.id.ButtonFor9) number += "9";

        else if (id == R.id.ButtonForDot) {
            if (!number.contains(".")) number += ".";
        }
        else if (id == R.id.ButtonPlusDiff) {
            if (!number.startsWith("-")) number = "-" + number;
            else number = number.substring(1);
        }

        input.setText(number);
    }

    // обработка нажатий на арифметические операции
    public void Operation(View view) {
        String number = input.getText().toString();

        if (number.endsWith("=")){
            number = result.getText().toString();
        }

        int id = view.getId();

        if (id == R.id.ButtonPlus) operator = "+";
        else if (id == R.id.ButtonDiff) operator = "-";
        else if (id == R.id.ButtonMultiply) operator = "*";
        else if (id == R.id.ButtonDivide) operator = "/";

        input.setText(number + " " + operator + " ");
        oldNumber = number;
    }

    // подсчет результата
    public void ClickEqual(View view) {
        String expression = input.getText().toString();
        double r = 0.0;

        try {
            double a = Double.parseDouble(oldNumber);
            String[] parts = expression.split(" ");
            double b = Double.parseDouble(parts[parts.length - 1]);

            switch (operator) {
                case "+": r = a + b; break;
                case "-": r = a - b; break;
                case "*": r = a * b; break;
                case "/":
                    if (b == 0) {
                        Toast.makeText(this, "Ошибка: деление на ноль!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    r = a / b;
                    break;
            }
            input.setText(expression + " =");
            result.setText(String.valueOf(r));
            isNewInput = true;

        } catch (Exception e) {
            Toast.makeText(this, "Ошибка вычисления", Toast.LENGTH_SHORT).show();
        }
    }


    // нажатие очистки
    public void ClickClear(View view) {
        input.setText("0");
        result.setText("0");
        oldNumber = "";
        operator = "";
        isNewInput = true;
    }


    // стереть 1 значение
    public void ClickBackspace(View view) {
        String s = input.getText().toString();
        if (s.length() > 1) s = s.substring(0, s.length() - 1);
        else s = "0";
        input.setText(s);
    }


    // 1 разделить на число
    public void ClickReverse(View view) {
        try {
            double n = Double.parseDouble(input.getText().toString());
            if (n == 0) {
                Toast.makeText(this, "Нельзя делить на ноль", Toast.LENGTH_SHORT).show();
                return;
            }
            result.setText(String.valueOf(1 / n));
        } catch (Exception ignored) {}
    }


    // обработка нажатий сохранения в памяти
    public void ClickMemory(View view) {
        double n = Double.parseDouble(result.getText().toString());
        int id = view.getId();

        if (id == R.id.ButtonMS) memory = n;
        else if (id == R.id.ButtonMR) input.setText(String.valueOf(memory));
        else if (id == R.id.ButtonMC) memory = 0;
        else if (id == R.id.ButtonMPlus) memory += n;
        else if (id == R.id.ButtonMDiff) memory -= n;
    }
}
