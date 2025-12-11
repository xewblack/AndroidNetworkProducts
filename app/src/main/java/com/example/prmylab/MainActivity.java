package com.example.prmylab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prmylab.adapters.ProductAdapter;
import com.example.prmylab.models.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private final ExecutorService networkExecutor = Executors.newFixedThreadPool(2);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ProductAdapter();
        recyclerView.setAdapter(adapter);

        loadProducts();
    }

    private void loadProducts() {
        networkExecutor.execute(() -> {
                    String json = fetchJsonFromNetwork();
                    mainHandler.post(() -> {
                                if (json != null) {
                                    List<Product> productList = parseProducts(json);
                                    if (productList != null && !productList.isEmpty()) {
                                        adapter.setProducts(productList);
                                        Log.d(TAG, "Loaded " + productList.size() + " products");
                                    } else {
                                        Toast.makeText(this, "Ошибка парсинга JSON", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Ошибка загрузки данных", Toast.LENGTH_LONG).show();
                                }
                            }
                    );
                }
        );
    }
    private String fetchJsonFromNetwork() {
        try {
            URL url = new URL("https://dummyjson.com/products");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "HTTP Error: " + conn.getResponseCode());
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                result.append(line);
            }

            br.close();
            conn.disconnect();
            return result.toString();
        }
        catch (Exception e) {
            Log.e(TAG, "Network error: " + e.getMessage());
            return null;
        }
    }
    private List<Product> parseProducts(String json) {
        List<Product> productsList = new ArrayList<>();
        try {
            JSONObject response = new JSONObject(json);
            JSONArray productsArray = response.getJSONArray("products");
            Log.d(TAG, "Found " + productsArray.length() + " products in JSON");

            for (int i = 0; i < productsArray.length(); i++) {
                JSONObject productObj = productsArray.getJSONObject(i);

                Product product = new Product();
                product.setId(productObj.getInt("id"));
                product.setTitle(productObj.getString("title"));
                product.setPrice(productObj.getDouble("price"));
                product.setThumbnail(productObj.getString("thumbnail"));
                product.setDescription(productObj.getString("description"));

                productsList.add(product);

                if (i == 0) {
                    Log.d(TAG, "Sample product: " + product);
                }
            }

            Log.d(TAG, "Successfully parsed " + productsList.size() + " products");
        } catch (JSONException e) {
            Log.e(TAG, "JSON parse error: " + e.getMessage());
            return null;
        }
        return productsList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkExecutor.shutdown();
    }
}