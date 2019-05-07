package com.example.upendra.myapplication.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.drm.DrmStore;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.support.v7.app.AppCompatActivity;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.apptakk.http_request.HttpRequest;
import com.apptakk.http_request.HttpRequestTask;
import com.apptakk.http_request.HttpResponse;
import com.example.upendra.myapplication.Model.Message;
import com.example.upendra.myapplication.R;
import com.example.upendra.myapplication.Util.Config;


import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.Parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.R.id.message;
import android.support.design.widget.NavigationView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextToSpeech textToSpeech;
    private String name = "";
    SharedPreferences prefs;
    DrawerLayout drawerLayout;
    private Button btnSend;
    private FloatingActionButton fab;
    // private Button micReg;
    private EditText inputMsg;
    private ListView listViewMessages;
    ImageView imageView;
    Realm realm;
    Context context;
    RequestQueue queue;
    TextView textmsg;
    LayoutInflater inflater;
    private MessagesListAdapter mAdapter;
    private ArrayList<Message> messageList;
    public static final String TAG = "MainActivity";
    NavigationView navigationView;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        //View header = navigationView.inflateHeaderView(R.layout.nav_header_home_navigate);
        // header=navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textmsg=(TextView)findViewById(R.id.textmsg);
        textmsg.setVisibility(View.INVISIBLE);
        //imageView = header.findViewById(R.id.imageView);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView iv = new ImageView(this);
        fab = (FloatingActionButton) findViewById(R.id.fabbtn);
//        voiceapi("Hi");
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "The Language is not supported!");
                    } else {
                        Log.i("TTS", "Language Supported.");
                    }
                    Log.i("TTS", "Initialization success.");
                } else {
                    Toast.makeText(getApplicationContext(), "TTS Initialization failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });


//        btnSend = (Button) findViewById(R.id.btnSend);
//        btnSend.setVisibility(View.INVISIBLE);

//        inputMsg  = (EditText) findViewById(R.id.inputMsg);
//        inputMsg.setVisibility(View.INVISIBLE);
        listViewMessages = (ListView) findViewById(R.id.list_view);

        context = getApplicationContext();
        queue = Volley.newRequestQueue(context);
        inflater = getLayoutInflater();
        messageList = new ArrayList<>();

        prefs = getSharedPreferences("userId", 0);
        if (prefs.getInt("userId", 0) != 0) {
            name = prefs.getString("firstname", "")
            ;
        }

        realm = Realm.getInstance(context);

        final RealmResults<Message> results = realm.where(Message.class).findAll();
        if (results.size() > 0) {
            realm.beginTransaction();
            for (int i = 0; i < results.size(); i++) {
                messageList.add(results.get(i));
            }
            realm.commitTransaction();
        }

        mAdapter = new MessagesListAdapter(this, messageList);
        listViewMessages.setAdapter(mAdapter);


        // Button micReg=(Button)findViewById(R.id.micReg);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                miclistener();
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                sendMessage();
//            }
//        });
            }

            public void miclistener() {
                try {
                    new HttpRequestTask(
                            new HttpRequest("http://10.11.20.75:5001/", HttpRequest.GET),
                            new HttpRequest.Handler() {
                                @Override
                                public void response(HttpResponse response) {
                                    if (response.code == 200) {
                                        final String vc_response = response.body;

                                        final String url = "https://615d30cc.ngrok.io/balaapi?user_query=" + vc_response.replaceAll(" ", "%20");
                                        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        realm.beginTransaction();
                                                        Message message = realm.createObject(Message.class);
                                                        message.setSuccess(1);
                                                        message.setMessage(vc_response);
                                                        message.setSelf(true);
                                                        realm.commitTransaction();
                                                        messageList.add(message);
                                                        mAdapter.notifyDataSetChanged();
                                                        vc_response.replace(" ", "+");
                                                        Log.d("response_data", response.toString());
                                                        sendMessageToServer(vc_response.replaceAll(" ", "%20"));


                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.d("Error.Response", "Error in voice listener");
                                                    }
                                                }
                                        );
// add it to the RequestQueue

                                        queue.add(getRequest);

                                        Toast.makeText(getApplicationContext(), "voice input sent to api", Toast.LENGTH_SHORT).show();

                                        Log.d(this.getClass().toString(), "Request successful!");
                                    } else  {

                                        String vc_response = "aida could not understand your voice please speak again";
                                        Log.e(this.getClass().toString(), "Request unsuccessful: " + response);
                                        textToSpeech.speak(vc_response, TextToSpeech.QUEUE_FLUSH, null);
                                        miclistener1();

//                                        int speechStatus = textToSpeech.speak(vc_response, TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            }).execute();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Could not  for Aida", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }


    //     String voiceapirequest = "http://192.168.99.101:5001/";
//     JsonObjectRequest getRequest = new ok(Request.Method.GET, voiceapirequest, null,
//             new Response.Listener<JSONObject>()
//             {
//                 @Override
//                 public void onResponse(JSONObject response) {
//                     Log.d("txt_msg", response.toString());
//                      String voiceapiresponse = response.toString();
//                 }
//             },
//             new Response.ErrorListener()
//             {
//                 @Override
//                 public void onErrorResponse(VolleyError error) {
//                     Log.d("Error.Response","Error in voice listener");
//                 }
//             }
//     );
//
//    public void voiceapi() {
//        try {
//
//            String voiceapirequest = "https://28985bab.ngrok.io/";
////        String voiceapirequest = "http://192.168.99.101:5001/";
//            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, voiceapirequest, null,
//                    new Response.Listener<JSONObject>() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            Log.d("txt_msg", response.toString());
//                            String voiceapiresponse = response.toString();
//                        }
//                    },
//                    new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            Log.d("Error.Response", "Error in voice listener");
//                        }
//                    }
//            );
//
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "api cal " + e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//    {
//    String url = "http://10.11.20.129";
//    StringRequest voicerequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
//        @Override
//        public void onResponse(String response) {
//            //This code is executed if the server responds, whether or not the response contains data.
//            //The String 'response' contains the server's response.
//            //You can test it by printing response.substring(0,500) to the screen.
//        }
//    }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            //This code is executed if there is an error.
//        }
//    });
//    }
    public void voiceapi(String res) {
        new HttpRequestTask(
                new HttpRequest("http://192.168.99.101:5001/", HttpRequest.GET),
                new HttpRequest.Handler() {

                    public void response(HttpResponse response) {
                        if (response.code == 200) {
                            Log.d(this.getClass().toString(), "Request successful!");
                        } else {
                            Log.e(this.getClass().toString(), "Request unsuccessful: " + response);
                        }
                    }
                }).execute();
    }


    private void sendMessage() {
        String msg = inputMsg.getText().toString().trim();

        if (msg.length() == 0) {
            Toast.makeText(getApplicationContext(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!networkIsAvailable(context)) {
            Toast.makeText(getApplicationContext(), "Connect to internet", Toast.LENGTH_SHORT).show();
            return;
        }
        inputMsg.setText("");

        realm.beginTransaction();
        Message message = realm.createObject(Message.class);
        message.setSuccess(1);
        message.setChatBotName(name);
        message.setChatBotID(Integer.parseInt(Config.chatBotID));
        message.setMessage(msg);
        message.setEmotion(null);
        message.setSelf(true);
        realm.commitTransaction();

        messageList.add(message);
        mAdapter.notifyDataSetChanged();
        msg = msg.replace(" ", "+");
        sendMessageToServer(msg);
    }

    private void sendMessageToServer(final String msg) {
        String url = Config.requrl + "?user_query=" + msg;
//        String url = Config.requrl+"?Question="+msg+"&vitalKeys="+msg+"&model="+Config.model;
//        String url = Config.URL+"?apiKey="+Config.apiKey+"&message="+msg+"&chatBotID="+Config.chatBotID+"&externalID="+Config.externalID;
//        String url = Config.URL+"?apiKey="+Config.apiKey+"&message="+msg+"&chatBotID="+Config.chatBotID+"&externalID="+Config.externalID;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null && response.length() > 0) {
                            try {
//                                if(response.getInt("success")==1)
                                if (response.length() == 3) {
//                                    JSONArray jsonres = response.getJSONArray("response_data");
//                                    JSONObject s = jsonres.getJSONObject(0);

                                    realm.beginTransaction();
                                    Message message = realm.createObject(Message.class);
//                                    JSONObject m = s.optString("name");
//                                    message.setChatBotName(m.optString("chatBotName", ""));
//                                    message.setChatBotID(m.optInt("chatBotID"));
//                                    message.setMessage(s.optString("name", ""));
                                    message.setMessage(response.optString("response_data"));
//                                    message.setEmotion(m.optString("emotions",null));
                                    message.setSelf(false);
                                    realm.commitTransaction();
                                    appendMessage(message);
                                    Toast.makeText(getApplicationContext(), "setprssed after", Toast.LENGTH_SHORT).show();
                                    int speechStatus = textToSpeech.speak(message.getMessage().toString().replaceAll("[^a-zA-Z0-9. ]", " "), TextToSpeech.QUEUE_FLUSH, null);
//                                    TooltipCompat.setTooltipText( ,"Tooltip text");
                                    if(speechStatus==TextToSpeech.SUCCESS) {
                                      miclistener1();}
                                    textmsg.setVisibility(View.VISIBLE);
                                    if (speechStatus == TextToSpeech.ERROR) {
                                        Log.e("TTS", "Error in converting Text to Speech!");
                                    }
                                } else {
                                    String error = response.getString("errorMessage");
                                    Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "json parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // errors
                            Toast.makeText(getApplicationContext(), "Retry Later ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        Log.e(TAG, "Please check your network connection: " + error.getMessage() + ", code: " + networkResponse);
                        Toast.makeText(getApplicationContext(), "Network error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                        /*if (networkResponse != null)
                        {
                            // HTTP Status Code: 401 Unauthorized
                            Log.d("status", " " + networkResponse.statusCode);
                            if(networkResponse.statusCode==404)
                                Toast.makeText(context,"User Not Found.",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(context,"Try Later.",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(context,"Try Again.",Toast.LENGTH_SHORT).show();
                        }*/
                     /*   if (error.getClass().toString().equals("class com.android.volley.NoConnectionError"))
                            Toast.makeText(context,"Please connect to internet.",Toast.LENGTH_SHORT).show();
                        else if (error.getClass().toString().equals("class com.android.volley.AuthFailureError"))
                            Toast.makeText(context,"\"Request cannot be completed. Try again later.",Toast.LENGTH_SHORT).show();
                        else if (error.getClass().toString().equals("class com.android.volley.ServerError"))
                            Toast.makeText(context,"Request cannot be completed. Try again later.",Toast.LENGTH_SHORT).show();
                        else if (error.getClass().toString().equals("class com.android.volley.TimeoutError"))
                            Toast.makeText(context,"Request cannot be completed. Try again later.",Toast.LENGTH_SHORT).show();
                        else {
                            // define other errors
                            Toast.makeText(context,"error",Toast.LENGTH_SHORT).show();
                        }
                        Log.d("check", "" + error.getClass().toString());*/
                    }
                }
        );

        // disabling retry policy so that it won't make
        // multiple http calls
        int socketTimeout = 0;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        jsonObjectRequest.setRetryPolicy(policy);
        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);
//        miclistener1();
    }

    private void miclistener1() {
        try {
            new HttpRequestTask(
                    new HttpRequest("http://10.11.20.75:5001/", HttpRequest.GET),
                    new HttpRequest.Handler() {
                        @Override
                        public void response(HttpResponse response) {
                            if (response.code == 200) {
                                final String vc_response = response.body;

                                final String url = "https://615d30cc.ngrok.io/balaapi?user_query=" + vc_response.replaceAll(" ", "%20");
                                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                        new Response.Listener<JSONObject>() {
                                            @Override
                                            public void onResponse(JSONObject response) {
                                                realm.beginTransaction();
                                                Message message = realm.createObject(Message.class);
                                                message.setSuccess(1);
                                                message.setMessage(vc_response);
                                                message.setSelf(true);
                                                realm.commitTransaction();
                                                messageList.add(message);
                                                mAdapter.notifyDataSetChanged();
                                                vc_response.replace(" ", "+");
                                                Log.d("response_data", response.toString());
                                                sendMessageToServer(vc_response.replaceAll(" ", "%20"));

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d("Error.Response", "Error in voice listener");
                                            }
                                        }
                                );
// add it to the RequestQueue

                                queue.add(getRequest);

                                Toast.makeText(getApplicationContext(), "voice input sent to api", Toast.LENGTH_SHORT).show();

                                Log.d(this.getClass().toString(), "Request successful!");
                            } else {
                                String vc_response = "aida could not understand your voice please speak again";
                                Log.e(this.getClass().toString(), "Request unsuccessful: " + response);
                                textToSpeech.speak(vc_response, TextToSpeech.QUEUE_FLUSH, null);
//                                        int speechStatus = textToSpeech.speak(vc_response, TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }
                    }).execute();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not  for Aida", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Toast.makeText(getApplicationContext(), "Development in-progress", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {

            Toast.makeText(getApplicationContext(), "Development in-progress", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_slide) {

            Toast.makeText(getApplicationContext(), "Development in-progress", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {

            Toast.makeText(getApplicationContext(), "Development in-progress", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }




    public class MessagesListAdapter extends BaseAdapter {

        private Context context;
        private List<Message> messagesItems;

        public MessagesListAdapter(Context context, List<Message> navDrawerItems) {
            this.context = context;
            this.messagesItems = navDrawerItems;
        }

        @Override
        public int getCount() {
            return messagesItems.size();
        }

        @Override
        public Object getItem(int position) {
            return messagesItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Message m = messagesItems.get(position);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (messagesItems.get(position).isSelf()) {
                convertView = mInflater.inflate(R.layout.list_item_message_right,
                        null);
            } else {
                convertView = mInflater.inflate(R.layout.list_item_message_left,
                        null);
            }

            TextView lblFrom = (TextView) convertView.findViewById(R.id.lblMsgFrom);
            lblFrom.setVisibility(View.INVISIBLE);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);

            txtMsg.setText(m.getMessage());
            lblFrom.setText(m.getChatBotName());

            return convertView;
        }
    }

    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                messageList.add(m);
                mAdapter.notifyDataSetChanged();
                // Playing device's notification
                playBeep();
            }
        });
    }


    private boolean networkIsAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public void playBeep() {

        try {
            Uri notification = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
                    notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

   // public void tooltip() {
       /* ToolTipView mRedToolTipView;

        ToolTipRelativeLayout toolTipRelativeLayout  = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);

        ToolTip toolTip = new ToolTip()
                .withText("A beautiful View")
                .withColor(Color.RED)
                .withShadow()
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        mRedToolTipView = toolTipRelativeLayout .showToolTipForView(toolTip, findViewById(R.id.activity_main_redtv));
        mRedToolTipView.setOnToolTipViewClickedListener(this);*/
    }
//}
