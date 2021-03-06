package com.techsignage.techsignmeetings;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.KeyboardUtils;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.ServiceResponses.AuthResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;
import com.techsignage.techsignmeetings.Models.UserModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.BindView;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends CoreActivity {

    Timer tclose;

//    @BindView(R.id.container1_lin)
//    RelativeLayout container1_lin;
//
//    @BindView(R.id.container2_lin)
//    RelativeLayout container2_lin;
//
//    @BindView(R.id.container3_lin)
//    RelativeLayout container3_lin;

    @BindView(R.id.next_btn1)
    Button next_btn1;

    @BindView(R.id.back_btn)
    Button back_btn;

    @BindView(R.id.userName_txt)
    EditText userName_txt;

    @BindView(R.id.password_txt)
    EditText password_txt;

    @BindView(R.id.tv_NowDate)
    TextView tv_NowDate;

   /* @BindView(R.id.tv_UnitName)
    ImageView tv_UnitName;*/

//    @BindView(R.id.txt_holder1)
//    TextView txt_holder1;
//
//    @BindView(R.id.txt_holder2)
//    TextView txt_holder2;
//
//    @BindView(R.id.txt_holder3)
//    TextView txt_holder3;
//
//    @BindView(R.id.txt_holder4)
//    TextView txt_holder4;

    @BindView(R.id.progress_rel)
    RelativeLayout progress_rel;

    //ProgressDialog dialog;
    //SweetAlertDialog sweetAlertDialog;
    com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface retrofitInterface;

    final int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    final int flags2 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
//        tv_UnitName.setText(getString(R.string.todays));
        userName_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTimer();
            }
        });
        password_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTimer();
            }
        });

        progress_rel.setVisibility(View.GONE);
        final String activityName = getIntent().getExtras().getString("activityName");
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        /*KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener()
        {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible)
            {
                Log.d("keyboard", "keyboard visible: "+isVisible);
                //Toast.makeText(LoginActivity.this, "keyboard visible: "+isVisible, Toast.LENGTH_SHORT).show();
                //getWindow().getDecorView().getSystemUiVisibility()
                getWindow().getDecorView().setSystemUiVisibility(flags2);
                if (isVisible)
                {
                    txt_holder1.setVisibility(View.VISIBLE);
                    txt_holder2.setVisibility(View.VISIBLE);
                    txt_holder3.setVisibility(View.VISIBLE);
                    txt_holder4.setVisibility(View.VISIBLE);
                    //getWindow().getDecorView().setSystemUiVisibility(flags);
                }
                else
                {
                    txt_holder1.setVisibility(View.GONE);
                    txt_holder2.setVisibility(View.GONE);
                    txt_holder3.setVisibility(View.GONE);
                    txt_holder4.setVisibility(View.GONE);
                    //getWindow().getDecorView().setSystemUiVisibility(flags2);
                }
            }
        });*/
        tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));


//        HandlerThread ht = new HandlerThread("MySuperAwesomeHandlerThread");
//        ht.start();
//        Handler h = new Handler(ht.getLooper()) {
//            public void handleMessage(Message msg) {
//                Log.d("MyHandlerThread", "handleMessage " + msg.what + " in " + Thread.currentThread());
//            };
//        };
//        for (int i = 0; i < 5; i++) {
//            Log.d("MyHandlerThread", "sending " + i + " in " + Thread.currentThread());
//            h.sendEmptyMessageDelayed(i, 3000 + i * 1000);
//        }

        //this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final String token = Utilities.getSharedValue("token", this);
        retrofitInterface = Utilities.liveAPI(token);
       /* if (Globals.loggedUnit != null)
            tv_UnitName.setText(Globals.loggedUnit.UNIT_NAME);*/

//        RelativeLayout view = (RelativeLayout)findViewById(R.id.loginmain);
//        view.setOnTouchListener(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View view,MotionEvent event) {
//
//                tclose = new Timer();
//                tclose.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                //finish();
//                                Intent intent = new Intent(LoginActivity.this, MainNewActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//
//                    }
//                }, 10000);
//
//                return true;
//            }
//
//        });


        next_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userName_txt.setError(null);
                password_txt.setError(null);
                final String userName = userName_txt.getText().toString();
                final String password = password_txt.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    userName_txt.setError(getString(R.string.error_field_required));
                    userName_txt.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    password_txt.setError(getString(R.string.error_field_required));
                    password_txt.requestFocus();
                    return;
                }

                //sweetAlertDialog = Utilities.showProgressPrettyDialog(LoginActivity.this, getResources().getString(R.string.processing));
                //sweetAlertDialog.show();
                progress_rel.setVisibility(View.VISIBLE);

                getWindow().getDecorView().setSystemUiVisibility(Globals.flags2);
                UserModel userModel = new UserModel();
                userModel.USERNAME = userName_txt.getText().toString();
                userModel.PASSWORD = password_txt.getText().toString();
                userModel.UnitId = Globals.unitId;
                userModel.Lang = Globals.lang;
                Observable<AuthResponse> call = retrofitInterface.allowedrooms(userModel);
                call.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<AuthResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable error) {
                                error.printStackTrace();
                                if (error instanceof HttpException) {
                                    // We had non-2XX http error
                                    Toast.makeText(LoginActivity.this,
                                            LoginActivity.this.getString(R.string.error_network_timeout),
                                            Toast.LENGTH_LONG).show();
                                }
                                if (error instanceof IOException) {
                                    // A network or conversion error happened
                                    Toast.makeText(LoginActivity.this,
                                            LoginActivity.this.getString(R.string.error_network_timeout),
                                            Toast.LENGTH_LONG).show();
                                }
                                progress_rel.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(AuthResponse authResponse) {
                                progress_rel.setVisibility(View.GONE);

                                if (!authResponse.ResponseStatus)
                                {
                                    if (Globals.lang.equals("ar"))
                                    {
                                        Toast.makeText(getApplicationContext(), authResponse.ArabicMessage, Toast.LENGTH_LONG).show();
                                    }
                                    else
                                    {
                                        Toast.makeText(getApplicationContext(), authResponse.Message, Toast.LENGTH_LONG).show();
                                    }
                                    return;
                                }

                                Globals.hours = authResponse.authElements.hours;
                                Globals.loggedUser = authResponse.authElements.loggeduser;
                                Boolean loggedCheck = false;
                                for (UnitModel unitModel : authResponse.authElements.rooms)
                                {
                                    if(unitModel.UNIT_ID.toLowerCase().equals(Globals.unitId.toLowerCase()))
                                    {
                                        Globals.loggedUnit = unitModel;
                                        loggedCheck = true;
                                        break;
                                    }
                                }
                                if (activityName.equals("MainActivity"))
                                {
                                    if (loggedCheck)
                                    {
                                        //Toast.makeText(LoginActivity.this, authResponse.Message, Toast.LENGTH_SHORT).show();
                                        if (Globals.lang.equals("ar"))
                                        {
                                            Toast.makeText(getApplicationContext(), authResponse.ArabicMessage, Toast.LENGTH_LONG).show();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), authResponse.Message, Toast.LENGTH_LONG).show();
                                        }
                                        Intent intent = new Intent(LoginActivity.this, BookActivity.class);
                                        intent.putExtra("activityName", activityName);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, R.string.nopermission, Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else
                                {
                                    Intent intent = new Intent(LoginActivity.this, BookActivity.class);
                                    intent.putExtra("activityName", activityName);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        setTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tclose != null)
            tclose.cancel();
    }

//    @Override
//    public void onClick(View v) {
//
//        tclose = new Timer();
//        tclose.schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //finish();
//                        Intent intent = new Intent(LoginActivity.this, MainNewActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//
//            }
//        }, 10000);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
//        float x = event.getX();
//        float y = event.getY();9
//        Toast.makeText(Example.this, "x=" + x + " y="+ y,
//                Toast.LENGTH_SHORT).show();
        setTimer();

        return false;
    }

    private void setTimer()
    {
        if (tclose != null)
        {
            //Toast.makeText(getApplicationContext(), "aloh", Toast.LENGTH_SHORT).show();
            tclose.cancel();
        }
        tclose = new Timer();
        tclose.schedule(new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //finish();
                        Intent intent = new Intent(LoginActivity.this, AllListActivity.class);
                        //Intent intent = new Intent(LoginActivity.this, MainNewActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }, 180000);
    }

    //
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//            setLandscape();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//            setPortrait();
//        }
//
//
//
//        // Checks whether a hardware keyboard is available
//        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
//            Toast.makeText(this, "keyboard visible", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_YES) {
//            Toast.makeText(this, "keyboard hidden", Toast.LENGTH_SHORT).show();
//        }
//    }

//    void setLandscape()
//    {
//        container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .18f));
//        container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .64f));
//        container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .18f));
//    }
//
//    void setPortrait()
//    {
//        container1_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
//        container2_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .70f));
//        container3_lin.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, .15f));
//    }
}
