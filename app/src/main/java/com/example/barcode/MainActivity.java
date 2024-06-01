package com.example.barcode;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


public class MainActivity extends AppCompatActivity {

    Button btn;


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startBarcodeScan();
            }
        });

    }

    private void startBarcodeScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String barcodeNumber = result.getContents();
                Toast.makeText(this, "Barcode Number: " + barcodeNumber, Toast.LENGTH_LONG).show();
                fetchProductName(barcodeNumber);
            }
            else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchProductName(String barcodeNumber) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Jsoup으로 웹 페이지 크롤링
                    Document doc = Jsoup.connect("https://www.google.com/search?q=" + barcodeNumber).get();
                    Log.i("Jsoup", doc.toString());
                    // 웹 페이지에서 상품명 추출
                    Element productNameElement = doc.select("your_selector").first();
                    Log.i("Product name", productNameElement.toString());
                    if (productNameElement != null) {
                        return productNameElement.text();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String productName) {
                super.onPostExecute(productName);
                if (productName != null) {
                    // 상품명을 UI에 표시
                    TextView productNameTextView = findViewById(R.id.tv_product_name);
                    productNameTextView.setText(productName);
                } else {
                    // 상품명을 찾지 못한 경우
                    Toast.makeText(getApplicationContext(), "상품명을 찾을 수 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }
}