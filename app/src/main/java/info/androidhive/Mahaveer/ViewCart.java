package info.androidhive.Mahaveer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import java.util.List;


import info.androidhive.Mahaveer.adapter.CartAdapter;

import info.androidhive.Mahaveer.model.CartList;

/**
 * Created by Akshay on 4/2/2015.
 * This Class Used to display the cart items from MENU.
 */
public class ViewCart extends Activity{
    static int flag=0;
    private static final String TAG = "";
    static String url = "";
    private ProgressDialog pDialog;
    private List<CartList> movieList = new ArrayList<CartList>();
    ListView listView;
    private CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_cart);
        ActionBar mActionBar = getActionBar();

        assert mActionBar != null;
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setBackgroundDrawable(new ColorDrawable(getResources()
                .getColor(R.color.mOrange)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources()
                    .getColor(R.color.mOrangeDark));
        }
        int actionBarTitleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        if (actionBarTitleId > 0) {
            TextView title = (TextView) findViewById(actionBarTitleId);
            if (title != null) {
                title.setTextColor(getResources()
                        .getColor(R.color.mWhite));
            }
        }
        url="http://mahaveersupermarket.com/api/rest/cart";
        listView = (ListView) findViewById(R.id.list);
        adapter = new CartAdapter(this, movieList);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        /**
         * View Cart with Volley
         */

/*

        JsonObjectRequest movieReq = new JsonObjectRequest(url,null,
                new Response.Listener<JSONObject>() {
                    public static final String TAG = "";

                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG, response.toString());
                        Log.d(TAG, "View Cart : "+response);
                        try {
                            pDialog.hide();
                            JSONObject json= (JSONObject) new JSONTokener(response.toString()).nextValue();
                            JSONObject json2 = json.getJSONObject("data");
                            JSONArray products=json2.getJSONArray("products");
                            for (int i = 0; i < products.length(); i++){
                                JSONObject prod_data = products.getJSONObject(i);
                                CartList movie=new CartList();
                                movie.setTitle(prod_data.getString("name"));
                                movie.setThumbnailUrl(prod_data.getString("thumb"));
                                movie.setRating((prod_data.getString("price")));
                                movie.setYear(prod_data.getString("total"));
                                movie.setProduct_id(prod_data.getString("key"));
                                movieList.add(movie);
                                adapter.notifyDataSetChanged();
                                movieList.add(movie);

                            }} catch (JSONException e) {
                            e.printStackTrace();
                        }


                        adapter.notifyDataSetChanged();
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();

            }

        }){
            @Override
            public Map<String, String> getHeaders()  {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Accept", "application/json");
                headers.put("X-Oc-Merchant-Id","123");
                headers.put("X-Oc-Merchant-Language","en");
                return headers;
            }
        };
        Session.getInstance().addToRequestQueue(movieReq);
*/

        /**
         * View Cart With Asynchttpclient
         */

        LoginActivity.client.addHeader("X-Oc-Session",Session.getInstance().getSession_id());
        LoginActivity.client.get(getApplicationContext(),"http://mahaveersupermarket.com/api/rest/cart", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    Log.d(TAG, "View Cart : "+response);
                    if (obj.getString("success").equals("true")) {
                        flag=1;
                        pDialog.hide();
                        JSONObject json= (JSONObject) new JSONTokener(response).nextValue();
                        JSONObject json2 = json.getJSONObject("data");
                        JSONArray products=json2.getJSONArray("products");
                        for (int i = 0; i < products.length(); i++){
                            JSONObject prod_data = products.getJSONObject(i);
                            CartList movie=new CartList();
                            movie.setTitle(prod_data.getString("name"));
                            movie.setThumbnailUrl(prod_data.getString("thumb"));
                            movie.setRating((prod_data.getString("price")));
                            movie.setYear(prod_data.getString("total"));
                            movie.setProduct_id(prod_data.getString("key"));
                            movieList.add(movie);
                            adapter.notifyDataSetChanged();
                            Session.getInstance().getRequestQueue();
                            ListView lv=(ListView) findViewById(R.id.list);
                            lv.setAdapter(adapter);

                        }

                    }
                    else
                    {
                        pDialog.hide();
                        View v;
                        Toast toast;
                        TextView text;
                        toast=Toast.makeText(getApplicationContext(), "No Items in the Cart!", Toast.LENGTH_SHORT);
                        v = toast.getView();
                        text = (TextView) v.findViewById(android.R.id.message);
                        text.setTextColor(getResources().getColor(R.color.mWhite));
                        text.setShadowLayer(0,0,0,0);
                        v.setBackgroundResource(R.color.mRed);
                        toast.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Throwable error,
                                  String content) {
                pDialog.hide();

            }
        });
    }

    public void confirm(View view)
    {   if(flag==1){
        Intent intent = new Intent(ViewCart.this, OrderConfirm.class);
        startActivity(intent);}
        else
    {
        View v;
        Toast toast;
        TextView text;
        toast=Toast.makeText(getApplicationContext(), "Please select at least one item to the cart!!", Toast.LENGTH_SHORT);
        v = toast.getView();
        text = (TextView) v.findViewById(android.R.id.message);
        text.setTextColor(getResources().getColor(R.color.mWhite));
        text.setShadowLayer(0,0,0,0);
        v.setBackgroundResource(R.color.mRed);
        toast.show();
    }
    }
    public void empty(View view){
        LoginActivity.client.delete(getApplicationContext(), "http://mahaveersupermarket.com/api/rest/cart/empty", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getString("success").equals("true")) {
                        View v;
                        Toast toast;
                        TextView text;
                        toast=Toast.makeText(getApplicationContext(), "All Items Removed!!", Toast.LENGTH_SHORT);
                        v = toast.getView();
                        text = (TextView) v.findViewById(android.R.id.message);
                        text.setTextColor(getResources().getColor(R.color.mWhite));
                        text.setShadowLayer(0,0,0,0);
                        v.setBackgroundResource(R.color.mRed);
                        toast.show();
                        adapter.notifyDataSetChanged();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
