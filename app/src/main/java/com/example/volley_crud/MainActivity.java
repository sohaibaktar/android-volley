package com.example.volley_crud;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText name, email, gender , status, user_id;
    Button login ,fetch, update, delete;
    TextView result_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        finder();

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = user_id.getText().toString();
                if(userID.isEmpty()){
                    Toast.makeText(MainActivity.this, "Enter Value", Toast.LENGTH_SHORT).show();
                    return;
                }
                fetchDataById(userID);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: ");
                String nametxt = name.getText().toString();
                String emailtxt = email.getText().toString();
                String gendertxt = gender.getText().toString();
                String statustxt = status.getText().toString();
                if (nametxt.isEmpty() || emailtxt.isEmpty() || gendertxt.isEmpty() || statustxt.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }
                post_data_to_volley(nametxt,emailtxt,gendertxt,statustxt);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idtxt = user_id.getText().toString();
                String nametxt = name.getText().toString();
                String emailtxt = email.getText().toString();
                String gendertxt = gender.getText().toString();
                String statustxt = status.getText().toString();
                if (idtxt.isEmpty() || nametxt.isEmpty() || emailtxt.isEmpty() || gendertxt.isEmpty() || statustxt.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter all values", Toast.LENGTH_SHORT).show();
                    return;
                }
                updateData(idtxt, nametxt, emailtxt, gendertxt, statustxt);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String idtxt = user_id.getText().toString();
                if (idtxt.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please Enter User ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                deleteData(idtxt);
            }
        });

    }

    private void deleteData(String idtxt) {
        String url = "http://192.168.0.181/android_test/delete_user.php"; // Replace with your server URL

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            Toast.makeText(MainActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
            clearFields();
        }, error -> {
            Log.d("TAG", "onErrorResponse: " + error.toString());
            Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", idtxt); // Passing the ID to delete
                return params;
            }
        };
        queue.add(request);
    }

    private void post_data_to_volley(String nametxt, String emailtxt, String gendertxt, String statustxt) {
        String url = "http://192.168.0.181/android_test/registration.php";
        Log.d("TAG", "post_data_to_volley: ");

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                name.setText("");
                email.setText("");
                gender.setText("");
                status.setText("");
                // on below line we are displaying a success toast message.
                Toast.makeText(MainActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                try {
                    // on below line we are parsing the response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);

                    // below are the strings which we
                    // extract from our json object.
                    String id = respObj.getString("id");
                    String name = respObj.getString("name");
                    String email = respObj.getString("email");
                    String gender = respObj.getString("gender");
                    String sstatus = respObj.getString("status");

                    // on below line we are setting this string s to our text view.
                    result_txt.setText("ID : "+ id+ "\n" + "Name : " + name + "\n" + "email : " + email);
                } catch (JSONException e) {
                    Log.d("TAG", "onResponse: "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: "+error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<String,String>();
                param.put("name",nametxt);
                param.put("email",emailtxt);
                param.put("gender",gendertxt);
                param.put("status",statustxt);

                return param;
            }
        };

        queue.add(request);
    }
    private void fetchDataById(String id) {
        String url = "http://192.168.0.181/android_test/fetch_user.php?id=" + id; // Replace with your server URL

        // Create a new request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // Create a GET request
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    // Parse the response into a JSON object
                    JSONObject respObj = new JSONObject(response);
                    if (respObj.getString("status").equals("success")) {
                        JSONObject userData = respObj.getJSONObject("data");

                        // Extract the user details
                        String id = userData.getString("id");
                        String user_name = userData.getString("name");
                        String user_email = userData.getString("email");
                        String user_gender = userData.getString("gender");
                        String user_status = userData.getString("status");

                        // Display the user details
                        name.setText(user_name);
                        email.setText(user_email);
                        gender.setText(user_gender);
                        status.setText(user_status);
//                        result_txt.setText("ID: " + id + "\nName: " + name + "\nEmail: " + email + "\nGender: " + gender + "\nStatus: " + status);
                    } else {
                        Toast.makeText(MainActivity.this, respObj.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the request queue
        queue.add(request);
    }

    private void updateData(String id, String nametxt, String emailtxt, String gendertxt, String statustxt) {
        String url = "http://192.168.0.181/android_test/update_user.php"; // Replace with your server URL

        // Create a new request queue
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        // Create a POST request
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                name.setText("");
                email.setText("");
                gender.setText("");
                status.setText("");
                // on below line we are displaying a success toast message.
                Toast.makeText(MainActivity.this, "Data added to API", Toast.LENGTH_SHORT).show();
                try {
                    // on below line we are parsing the response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);

                    // below are the strings which we
                    // extract from our json object.
                    String id = respObj.getString("id");
                    String name = respObj.getString("name");
                    String email = respObj.getString("email");
                    String gender = respObj.getString("gender");
                    String sstatus = respObj.getString("status");

                    // on below line we are setting this string s to our text view.
                    result_txt.setText("ID : "+ id+ "\n" + "Name : " + name + "\n" + "email : " + email+ "\n" + "gender : " + gender+ "\n" + "status : " + sstatus);
                } catch (JSONException e) {
                    Log.d("TAG", "onResponse: "+e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("TAG", "onErrorResponse: " + error.toString());
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Put the parameters in a map
                Map<String, String> param = new HashMap<>();
                param.put("id", id);
                param.put("name", nametxt);
                param.put("email", emailtxt);
                param.put("gender", gendertxt);
                param.put("status", statustxt);
                return param;
            }
        };

        // Add the request to the request queue
        queue.add(request);
    }

    private void finder() {
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        gender = (EditText) findViewById(R.id.gender);
        status = (EditText) findViewById(R.id.status);
        user_id = (EditText) findViewById(R.id.user_id);

        login = (Button) findViewById(R.id.login_btn);
        fetch = (Button) findViewById(R.id.fetch);
        update = (Button) findViewById(R.id.update);
        delete = (Button) findViewById(R.id.delete);

        result_txt = (TextView) findViewById(R.id.txt);
    }

    private void clearFields() {
        name.setText("");
        email.setText("");
        gender.setText("");
        status.setText("");
        user_id.setText("");
    }
}