package com.techsignage.techsignmeetings;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Adapters.BookingAdapter;
import com.techsignage.techsignmeetings.Adapters.RoomsAdapter;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.KeyboardUtils;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.Models.HourModel;
import com.techsignage.techsignmeetings.Models.Interfaces.hourCallback;
import com.techsignage.techsignmeetings.Models.MeetingModel;
import com.techsignage.techsignmeetings.Models.ServiceResponses.AuthResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.CreateMeetingResponse;
import com.techsignage.techsignmeetings.Models.ServiceResponses.RoomsResponse;
import com.techsignage.techsignmeetings.Models.UnitModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BookActivity extends CoreActivity {

    @BindView(R.id.tv_MeetingDate)
    EditText tv_MeetingDate;
    @BindView(R.id.tv_MeetingTime)
    TextView tv_MeetingTime;
    @BindView(R.id.constraintTime)
    ConstraintLayout constraintTime;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    /*@BindView(R.id.tv_NowDate)
    TextView tv_NowDate;*/

//    @BindView(R.id.date_picker)
//    DatePicker date_picker;

    Calendar selectedDate;

   /* @BindView(R.id.tv_UnitName)
    TextView tv_UnitName;*/

//    @BindView(R.id.tv_leftbtn)
//    TextView tv_leftbtn;
//
//    @BindView(R.id.tv_rightBtn)
//    TextView tv_rightBtn;

    @BindView(R.id.book_btn)
    Button next_btn;

    @BindView(R.id.tv_MeetingTitle)
    EditText tv_MeetingTitle;

//    @BindView(R.id.lin2)
//    LinearLayout lin2;

    @BindView(R.id.progress_rel)

    RelativeLayout progress_rel;

    @BindView(R.id.rooms_spinner)
    Spinner rooms_spinner;

    LinearLayoutManager llm;

    //ProgressDialog dialog;
    //SweetAlertDialog sweetAlertDialog;
    com.techsignage.techsignmeetings.Models.Interfaces.retrofitInterface retrofitInterface;

    @BindView(R.id.back_btn)
    Button back_btn;

    Subscription subscription;
    HourModel hourModel;
    String firstHour;
    String lastHour;
    Timer tclose;

    final int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
    final int flags2 = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
    private static hourCallback callback;
    @BindView(R.id.book_list)
    RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ButterKnife.bind(this);
        progress_rel.setVisibility(View.GONE);

        tv_MeetingTitle.setImeOptions(EditorInfo.IME_ACTION_DONE);
        tv_MeetingTitle.addTextChangedListener(new TextWatcher() {
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


        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                Log.d("keyboard", "keyboard visible: " + isVisible);
                //Toast.makeText(LoginActivity.this, "keyboard visible: "+isVisible, Toast.LENGTH_SHORT).show();
                getWindow().getDecorView().setSystemUiVisibility(flags2);
//                if (isVisible)
//                {
//                    //getWindow().getDecorView().setSystemUiVisibility(flags);
//                }
//                else
//                {
//                    //getWindow().getDecorView().setSystemUiVisibility(flags2);
//                }
            }
        });

        //final String activityName = getIntent().getExtras().getString("activityName");
        //final Boolean show_spinner = activityName.equals("AllListActivity");

        callback = new hourCallback() {
            @Override
            public void selectedItem(HourModel selectedModel, final List<HourModel> lst) {
                if (selectedModel.IsEnabled && !selectedModel.IsBooked) {
                    hourModel = selectedModel;
                    for (int i = 0; i < lst.size(); i++) {
                        if (i == 0)
                            //selectedModel.StartHour = lst.get(i).StartHour;
                            firstHour = lst.get(i).StartHour;

                        if (i == lst.size() - 1)
                            //selectedModel.EndHour = lst.get(i).EndHour;
                            lastHour = lst.get(i).EndHour;
                    }
                    //selectedModel.CombinedHours = String.format("%s | %s", selectedModel.StartHour, selectedModel.EndHour);
                    //String ss = "";
                    if (Globals.lang.equals("ar")) {
                        tv_MeetingDate.setText(
                                String.format("%s",
                                        new SimpleDateFormat("dd/MM/yyyy", new Locale("ar")).format(selectedDate.getTime())
                                )
                        );
                    } else {
                        tv_MeetingDate.setText(
                                String.format("%s",
                                        new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime())
                                )
                        );
                    }
                    tv_MeetingTime.setText(

                            String.format("%s", String.format("%s | %s", firstHour, lastHour))
                    );
                }

                tv_MeetingDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker();
                    }
                });
                tv_MeetingTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker();
                    }
                });
                constraintTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDatePicker();
                    }
                });

                if (!selectedModel.IsSelected && lst.size() == 0) {
                    tv_MeetingDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()));
                }

            }
        };
        if (rooms_spinner != null) {
            rooms_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    MeetingModel meetingModel = new MeetingModel();
                    meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
                    meetingModel.UNIT_ID = ((UnitModel) rooms_spinner.getSelectedItem()).UNIT_ID;
                    meetingModel.Lang = Globals.lang;
                    structCalendar(callback, mRecyclerView, meetingModel);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
//            tv_UnitName.setText(Globals.loggedUnit.UNIT_NAME);
        }

        BookingAdapter adapter = new BookingAdapter(BookActivity.this, callback);
        for (HourModel hourModel : Globals.hours) {
            hourModel.IsSelected = false;
        }
        adapter.setLst(Globals.hours);
        mRecyclerView.setAdapter(adapter);

        llm = new LinearLayoutManager(BookActivity.this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(llm);

        final String token = Utilities.getSharedValue("token", this);
        retrofitInterface = Utilities.liveAPI(token);

        selectedDate = Calendar.getInstance();
//        tv_NowDate.setText(new SimpleDateFormat("EEEE, dd/MM/yyyy | HH:mm aaa").format(new Date()));
        tv_MeetingDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()));

        Observable<RoomsResponse> allRooms = retrofitInterface.allrooms();
        subscription = allRooms.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<RoomsResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final RoomsResponse roomsResponse) {
                        RoomsAdapter roomsAdapter = new RoomsAdapter(BookActivity.this, R.layout.spinner_item, roomsResponse.Rooms);
                        rooms_spinner.setAdapter(roomsAdapter);

                        if (rooms_spinner != null) {
                            MeetingModel meetingModel = new MeetingModel();
                            meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
                            meetingModel.UNIT_ID = roomsResponse.Rooms.get(0).UNIT_ID;
                            meetingModel.Lang = Globals.lang;
                            structCalendar(callback, mRecyclerView, meetingModel);
                        }
                    }
                });


      /*  Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        date_picker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {

                selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                tv_MeetingDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()));

                MeetingModel meetingModel = new MeetingModel();
                meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
                meetingModel.UNIT_ID = Globals.unitId;
                meetingModel.Lang = Globals.lang;
                structCalendar(callback, mRecyclerView, meetingModel);

            }
        });*/

        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                setTimer();

                for (int i = 0; i < Globals.hours.size(); i++) {
                    RecyclerView.ViewHolder v = mRecyclerView.findViewHolderForAdapterPosition(i);
                    if (v != null) {
                        RelativeLayout rel1 = (RelativeLayout) v.itemView.findViewById(R.id.rel1);
                        TextView tv_container = (TextView) rel1.findViewById(R.id.tv_container);
                        tv_container.setBackgroundResource(R.drawable.white_bg);

                        if (Globals.hours.get(i).IsSelected != null) {
                            if (Globals.hours.get(i).IsSelected) {
                                tv_container.setBackgroundResource(R.drawable.ic_check_black_24dp);
                            }
                        }

                        if (Globals.hours.get(i).IsEnabled != null) {
                            if (!Globals.hours.get(i).IsEnabled) {
                                tv_container.setBackgroundResource(R.drawable.expired);
                            }
                        }

                        if (Globals.hours.get(i).IsBooked) {
                            tv_container.setBackgroundResource(R.drawable.booked_pic);
                        }
                    }
                }
            }
        });


//        tv_leftbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               // llm.scrollToPosition(Globals.hours.size());
//                //mRecyclerView.getLayoutManager().scrollToPosition(llm.findFirstVisibleItemPosition() - 1);
//                mRecyclerView.smoothScrollToPosition(llm.findFirstVisibleItemPosition() - 3);
//            }
//        });
//
//        tv_rightBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // llm.scrollToPosition(Globals.hours.size());
//                //mRecyclerView.getLayoutManager().scrollToPosition(llm.findLastVisibleItemPosition() + 1);
//                mRecyclerView.smoothScrollToPosition(llm.findLastVisibleItemPosition() + 3);
//            }
//        });

        //final LinearLayout layout = (LinearLayout)findViewById(R.id.lin2);
//        ViewTreeObserver vto = lin2.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
//                    lin2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                } else {
//                    lin2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//                int height = lin2.getMeasuredHeight();
//                int width  = lin2.getMeasuredWidth();
//
////                if (height>100)
////                {
////                    LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,100);
////                    lin2.setLayoutParams(parms);
////
////                    //lin2.getLayoutParams().height = 100;
////                    //lin2.requestLayout();
////                }
//            }
//        });

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_MeetingTitle.setError(null);
                final String title = tv_MeetingTitle.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    tv_MeetingTitle.setError(getString(R.string.error_field_required));
                    tv_MeetingTitle.requestFocus();
                    return;
                }

                if (hourModel == null) {
                    Toast.makeText(BookActivity.this, R.string.selectrange, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rooms_spinner != null) {
                    if (rooms_spinner.getSelectedItem() == null) {
                        Toast.makeText(BookActivity.this, getResources().getString(R.string.chooseroom), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                progress_rel.setVisibility(View.VISIBLE);

                MeetingModel meetingModel = new MeetingModel();
                meetingModel.UNIT_ID = Globals.unitId;
                if (rooms_spinner != null)
                    meetingModel.UNIT_ID = ((UnitModel) rooms_spinner.getSelectedItem()).UNIT_ID;
                meetingModel.CREATE_USER = Globals.loggedUser.USER_ID;
                meetingModel.MEETING_TITLE = title;
                meetingModel.RECURRENCE_TYPE = "Single";
                //meetingModel.MEETING_TITLE =
//                meetingModel.START_DATETIME = String.format("%s %s", new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()),
//                        firstHour);
//                meetingModel.END_DATETIME = String.format("%s %s", new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()),
//                        lastHour);
                meetingModel.StartDate = String.format("%s %s", new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()),
                        firstHour);
                meetingModel.EndDate = String.format("%s %s", new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()),
                        lastHour);
                meetingModel.MEETING_STATUS_ID = "F6A33569-A955-48C2-934A-574F8589E7DC";
                Gson gson = new Gson();
                String j = gson.toJson(meetingModel);
                Observable<CreateMeetingResponse> call = retrofitInterface.savemeeting(meetingModel);
                call.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<CreateMeetingResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                progress_rel.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(CreateMeetingResponse authResponse) {
                                progress_rel.setVisibility(View.GONE);
                                if (Globals.lang.equals("ar")) {
                                    Toast.makeText(BookActivity.this, authResponse.ArabicMessage, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(BookActivity.this, authResponse.Message, Toast.LENGTH_SHORT).show();
                                }
                                Intent intent = null;
                                if (rooms_spinner == null) {
                                    intent = new Intent(BookActivity.this, AllListActivity.class);
                                } else {
                                    intent = new Intent(BookActivity.this, AllListActivity.class);
                                }
                                //Intent intent = new Intent(BookActivity.this, AllListActivity.class);
                                startActivity(intent);
                                finish();
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

//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //finish();
//                        Intent intent = new Intent(BookActivity.this, MainNewActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//
//            }
//        }, 65000);
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
//                        Intent intent = new Intent(BookActivity.this, MainNewActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//
//            }
//        }, 180000);
//
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                selectedDate = Calendar.getInstance();
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, month);
                selectedDate.set(Calendar.DAY_OF_MONTH, day);
                tv_MeetingDate.setText(new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime()));

                MeetingModel meetingModel = new MeetingModel();
                meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
                meetingModel.UNIT_ID = Globals.unitId;
                meetingModel.Lang = Globals.lang;
                structCalendar(callback, mRecyclerView, meetingModel);
            }
        };
        setTimer();
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                BookActivity.this,
                android.R.style.Theme_Holo_Light,
                mDateSetListener,
                year, month, day);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void showTimePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
//        float x = event.getX();
//        float y = event.getY();
//        Toast.makeText(Example.this, "x=" + x + " y="+ y,
//                Toast.LENGTH_SHORT).show();
        setTimer();

        return false;
    }

    public void setTimer() {
        if (tclose != null) {
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
                        Intent intent = new Intent(BookActivity.this, AllListActivity.class);
                        //Intent intent = new Intent(BookActivity.this, AllListActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

            }
        }, 180000);
    }

    private void structCalendar(final hourCallback callback, final RecyclerView mRecyclerView
            , MeetingModel meetingModel) {
        //dialog = Utilities.showDialog(BookActivity.this);
//        MeetingModel meetingModel = new MeetingModel();
//        meetingModel.TheDate = new SimpleDateFormat("dd/MM/yyyy").format(selectedDate.getTime());
//        meetingModel.UNIT_ID = Globals.unitId;
        Observable<AuthResponse> call = retrofitInterface.roomblocks(meetingModel);
        call.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<AuthResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progress_rel.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(AuthResponse authResponse) {
//                                if (dialog.isShowing())
//                                {
//                                    dialog.hide();
//                                }
                        Globals.hours = authResponse.authElements.hours;
                        for (HourModel hourModel : Globals.hours) {
                            hourModel.IsSelected = false;
                        }
                        BookingAdapter adapter = new BookingAdapter(BookActivity.this, callback);
                        adapter.setLst(authResponse.authElements.hours);
                        mRecyclerView.setAdapter(adapter);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tclose != null)
            tclose.cancel();
    }

    public interface DataReady {
        void OnReady(int year, int month, int day);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private DataReady dataReady;

        public void setListener(DataReady dataReady) {

            this.dataReady = dataReady;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            dataReady.OnReady(year, month, day);
        }
    }

}