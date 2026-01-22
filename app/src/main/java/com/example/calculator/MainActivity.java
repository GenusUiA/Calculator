package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView input;  // поле ввода
    TextView result; // поле вывода

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
    // метод для передачи данных горизонтальному состоянию
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("input", input.getText().toString());
        outState.putString("result", result.getText().toString());
        outState.putDouble("memory", memory);
        outState.putString("operator", operator);
        outState.putString("oldNumber", oldNumber);
        outState.putBoolean("isNewInput", isNewInput);
        super.onSaveInstanceState(outState);
    }
    // метод для передачи данных из горизонтального в вертикальное
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        input.setText(savedInstanceState.getString("input"));
        result.setText(savedInstanceState.getString("result"));
        memory = savedInstanceState.getDouble("memory");
        operator = savedInstanceState.getString("operator", "");
        oldNumber = savedInstanceState.getString("oldNumber", "");
        isNewInput = savedInstanceState.getBoolean("isNewInput", true);
    }

    // обработка нажатий цифр, точки и +-
    public void ClickNumber(View view) {
        String current = input.getText().toString();

        int id = view.getId();
        String toAdd = "";

        // замена нуля в случае следующих условий
        boolean replaceZero = isNewInput || (current.equals("0") && id != R.id.ButtonForDot) || current.endsWith("=");

        if (replaceZero) {
            if (id != R.id.ButtonForDot) current = "";
            if (current.endsWith("=")) result.setText("0");
            isNewInput = false;
        }

        if (id == R.id.ButtonFor0) toAdd = "0";
        else if (id == R.id.ButtonFor1) toAdd = "1";
        else if (id == R.id.ButtonFor2) toAdd = "2";
        else if (id == R.id.ButtonFor3) toAdd = "3";
        else if (id == R.id.ButtonFor4) toAdd = "4";
        else if (id == R.id.ButtonFor5) toAdd = "5";
        else if (id == R.id.ButtonFor6) toAdd = "6";
        else if (id == R.id.ButtonFor7) toAdd = "7";
        else if (id == R.id.ButtonFor8) toAdd = "8";
        else if (id == R.id.ButtonFor9) toAdd = "9";
        else if (id == R.id.ButtonForDot) {
            // проверка добавления точки
            int lastOp = Math.max(current.lastIndexOf('+'),
                    Math.max(current.lastIndexOf('-'),
                            Math.max(current.lastIndexOf('*'),
                                    current.lastIndexOf('/'))));
            String lastNumber = lastOp >= 0 ? current.substring(lastOp + 1) : current;
            if (!lastNumber.contains(".")) toAdd = ".";
            else toAdd = ""; // точка уже есть
            if (lastNumber.isEmpty()) toAdd = "0."; // если точка в начале числа
        }
        else if (id == R.id.ButtonPlusDiff) {
            // меняем знак последнего числа
            int lastOp = Math.max(current.lastIndexOf('+'),
                    Math.max(current.lastIndexOf('-'),
                            Math.max(current.lastIndexOf('*'),
                                    current.lastIndexOf('/'))));
            if (lastOp >= 0) {
                String before = current.substring(0, lastOp + 1);
                String lastNum = current.substring(lastOp + 1);
                if (!lastNum.startsWith("-")) lastNum = "-" + lastNum;
                else lastNum = lastNum.substring(1);
                current = before + lastNum;
            } else {
                if (!current.startsWith("-")) current = "-" + current;
                else current = current.substring(1);
            }
        }

        input.setText(current + toAdd);
    }

    // обработка нажатий на арифметические операции
    public void Operation(View view) {
        String current = input.getText().toString();

        // Если предыдущий результат уже был, начинаем новое выражение
        if (current.endsWith("=")) {
            current = result.getText().toString();
            input.setText(current);
        }

        int id = view.getId();

        // Определяем оператор
        String op = "";
        if (id == R.id.ButtonPlus) op = "+";
        else if (id == R.id.ButtonDiff) op = "-";
        else if (id == R.id.ButtonMultiply) op = "*";
        else if (id == R.id.ButtonDivide) op = "/";
        else if (id == R.id.ButtonRoot) op = "√";

        if (op.equals("√")) {
            // Корень вычисляем сразу
            try {
                double n = Double.parseDouble(current);
                double r = Math.sqrt(n);
                input.setText("√" + current + "=");
                result.setText(String.valueOf(r));
                isNewInput = true;
                operator = "";
                oldNumber = String.valueOf(r);
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка вычисления корня", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        // Если последний символ уже оператор, заменяем его
        if (!current.isEmpty()) {
            char lastChar = current.charAt(current.length() - 1);
            if (lastChar == '+' || lastChar == '-' || lastChar == '*' || lastChar == '/') {
                current = current.substring(0, current.length() - 1);
            }
        }

        input.setText(current + op);
        operator = op;
        oldNumber = current;
        isNewInput = false;
    }

    // подсчет результата
    public void ClickEqual(View view) {
        String expr = input.getText().toString();

        if (operator.isEmpty() || expr.endsWith("=")) return;

        try {
            double resultValue = 0.0;
            String[] parts = expr.split("(?=[+\\-*/])|(?<=[+\\-*/])");
            // разделяем на числа и операторы, сохраняем порядок

            double currentValue = Double.parseDouble(parts[0]);

            // вычисляем последовательно
            for (int i = 1; i < parts.length; i += 2) {
                String op = parts[i];
                double next = Double.parseDouble(parts[i + 1]);

                if (op.equals("+")) currentValue += next;
                else if (op.equals("-")) currentValue -= next;
                else if (op.equals("*")) currentValue *= next;
                else if (op.equals("/")) {
                    if (next == 0) {
                        Toast.makeText(this, "Ошибка: деление на ноль!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    currentValue /= next;
                }
            }

            resultValue = currentValue;
            input.setText(expr + "=");
            result.setText(String.valueOf(resultValue));
            oldNumber = String.valueOf(resultValue);
            operator = "";
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
        // если строка не равна 1 уменьшаем длину строки, иначе приравниваем к нулю
        String s = input.getText().toString();
        if (s.length() > 1) s = s.substring(0, s.length() - 1);
        else s = "0";
        input.setText(s);
    }

    // 1 разделить на число
    public void ClickReverse(View view) {
        //проверка деления на 0, если прошла, выполняем операцию
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
