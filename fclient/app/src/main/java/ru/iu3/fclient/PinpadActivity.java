package ru.iu3.fclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;

public class PinpadActivity extends AppCompatActivity {

    TextView tvPin;
    String pin = "";
    final int MAX_KEYS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad);

        tvPin = findViewById(R.id.txtPin);

        findViewById(R.id.btnOK).setOnClickListener((View) -> {
            Intent it = new Intent();
            it.putExtra("pin", pin);
            setResult(RESULT_OK, it);
            finish();
        });

        findViewById(R.id.btnReset).setOnClickListener((View) -> {
            pin = "";
            tvPin.setText("");
        });

        // Обработка txtAmount
        TextView ta = findViewById(R.id.txtAmount);

        String amt = String.valueOf(getIntent().getStringExtra("amount"));
        Long f = Long.valueOf(amt);
        DecimalFormat df = new DecimalFormat("#,###,###,##0.00");
        String s = df.format(f);

        ta.setText("Сумма: " + s);

        // Обработка txtPtc
        TextView tp = findViewById(R.id.txtPtc);
        int pts = getIntent().getIntExtra("ptc", 0);
        if (pts == 2) {
            tp.setText("Осталось две попытки");
        } else if (pts == 1) {
            tp.setText("Осталась одна попытка");
        }
    }


    //Обработчики кнопок ввода цифр
    public void keyClick(View v)
    {
        String key = ((TextView)v).getText().toString();
        int sz = pin.length();
        if (sz < 4)
        {
            pin += key;
            tvPin.setText("****".substring(3 - sz));
        }
    }
}