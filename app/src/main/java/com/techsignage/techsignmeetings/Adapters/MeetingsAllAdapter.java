package com.techsignage.techsignmeetings.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.techsignage.techsignmeetings.Activities.CoreActivity;
import com.techsignage.techsignmeetings.Helpers.Globals;
import com.techsignage.techsignmeetings.Helpers.Utilities;
import com.techsignage.techsignmeetings.MeetingAttendees;
import com.techsignage.techsignmeetings.Models.UserMeetingModel;
import com.techsignage.techsignmeetings.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by heat on 3/12/2017.
 */

public class MeetingsAllAdapter extends RecyclerView.Adapter<MeetingsAllAdapter.ListViewHolder> {
    class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_meetingdate;
        //        TextView tv_timefrom;
//        TextView tv_timeto;
        TextView tv_meetingtitle;
        TextView tv_meetingorganizer;
        TextView tv_meetingroom;

        public ListViewHolder(View itemView) {
            super(itemView);

            tv_meetingdate = (TextView) itemView.findViewById(R.id.tv_meetingdate);
//            tv_timefrom = (TextView) itemView.findViewById(R.id.tv_timefrom);
//            tv_timeto = (TextView) itemView.findViewById(R.id.tv_timeto);
            tv_meetingtitle = (TextView) itemView.findViewById(R.id.tv_meetingtitle);
            tv_meetingorganizer = (TextView) itemView.findViewById(R.id.tv_meetingorganizer);
            tv_meetingroom = (TextView) itemView.findViewById(R.id.tv_meetingroom);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            UserMeetingModel userMeetingModel = dataList.get(getAdapterPosition());
//            if(userMeetingModel.meeting.ACTUAL_START_DATETIME != null)
//            {
//                Intent intent = new Intent(context, MeetingAttendees.class);
//                intent.putExtra("MEETING_ID", userMeetingModel.meeting.MEETING_ID);
//                intent.putExtra("Status", userMeetingModel.meeting.ACTUAL_START_DATETIME != null);
//                context.startActivity(intent);
//            }
//            else
//            {
//                Toast.makeText(context, "Meeting must be started", Toast.LENGTH_LONG).show();
//            }

            Intent intent = new Intent(context, MeetingAttendees.class);
            intent.putExtra("MEETING_ID", userMeetingModel.meeting.MEETING_ID);
            intent.putExtra("UNIT_NAME", userMeetingModel.unit.UNIT_NAME);
            intent.putExtra("UNIT_ID", userMeetingModel.unit.UNIT_ID);
            intent.putExtra("activityName", ((CoreActivity) context).getLocalClassName());
            //intent.putExtra("Status", userMeetingModel.meeting.ACTUAL_START_DATETIME != null);
            intent.putExtra("Status", userMeetingModel.meeting.CanCheckin);
            intent.putExtra("StatusOut", userMeetingModel.meeting.CanCheckOut);
            context.startActivity(intent);

//            Intent intent = new Intent(context, MeetingAttendees.class);
//            intent.putExtra("MEETING_ID", userMeetingModel.meeting.MEETING_ID);
//            context.startActivity(intent);
        }
    }

    int layout;
    Context context;
    List<UserMeetingModel> dataList = new ArrayList<>();
    LayoutInflater inflater;
    //Listener listener;
    //Context con;

    public MeetingsAllAdapter(Context context, int layout) {
        this.layout = layout;
        this.context = context;
        //this.con= context;
        inflater = LayoutInflater.from(context);
    }

    public void setLst(List<UserMeetingModel> lst) {
        this.dataList = lst;
    }

    public List<UserMeetingModel> getLst() {
        return this.dataList;
    }

    @Override
    public MeetingsAllAdapter.ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //View convertView = inflater.inflate(R.layout.meeting_item, parent, false);
        View convertView = inflater.inflate(layout, parent, false);
        return new MeetingsAllAdapter.ListViewHolder(convertView);
    }

    int itemHeight;

    @Override
    public void onBindViewHolder(final MeetingsAllAdapter.ListViewHolder holder, final int position) {
        try {
            Date startdate = Globals.format.parse(dataList.get(position).meeting.START_DATETIME);
            Date enddate = Globals.format.parse(dataList.get(position).meeting.END_DATETIME);
            //String MeetingDate = String.format("%s - %s", Globals.format1.format(startdate), Globals.format1.format(enddate));
            String MeetingDate = String.format("%s", Globals.format1.format(startdate));
            //String date_details = String.format("%s \n %s", Globals.format3.format(startdate), MeetingDate);
            String date_details = String.format("%s", Globals.format3.format(startdate));
            if (Globals.lang.equals("ar")) {
                String date2_details = String.format("%s", Globals.format1_ar.format(startdate));
                String date3_details = String.format("%s", Globals.format1_ar.format(enddate));
                holder.tv_meetingdate.setText(date2_details + " - " + date3_details);
            } else {
                String date2_details = String.format("%s", Globals.format1.format(startdate));
                String date3_details = String.format("%s", Globals.format1.format(enddate));
                holder.tv_meetingdate.setText(date2_details + " - " + date3_details);
            }
//            holder.tv_timefrom.setText(date2_details);
//            holder.tv_timeto.setText(date3_details);

//            holder.tv_meetingdate.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(context, MeetingAttendees.class);
//                    intent.putExtra("MEETING_ID", dataList.get(position).meeting.MEETING_ID);
//                    context.startActivity(intent);
//                }
//            });
            holder.tv_meetingtitle.setText(dataList.get(position).meeting.MEETING_TITLE);
            holder.tv_meetingorganizer.setText(String.format("%s %s", dataList.get(position).user.FIRST_NAME
                    , dataList.get(position).user.LAST_NAME));
            if (holder.tv_meetingroom != null && dataList.get(position).unit != null
                    && dataList.get(position).unit.UNIT_NAME != null) {
                holder.tv_meetingroom.setText(dataList.get(position).unit.UNIT_NAME);
            }
            Utilities.setFadeAnimation(holder.itemView);
            holder.itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            itemHeight = holder.itemView.getMeasuredHeight();
        } catch (Exception ex) {
        }

    }

    public int getItemHeight() {
        return itemHeight;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}