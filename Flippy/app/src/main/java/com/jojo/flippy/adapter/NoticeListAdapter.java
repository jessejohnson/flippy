package com.jojo.flippy.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jojo.flippy.adapter.Notice;
import com.jojo.flippy.app.R;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

/**
 * Created by odette on 6/11/14.
 */
public class NoticeListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Notice> noticeArrayList = new ArrayList<Notice>();

    public NoticeListAdapter(Activity a, ArrayList<Notice> list){
        this.context = (Context) a;
        this.noticeArrayList = list;
    }

    @Override
    public int getCount() {
        return noticeArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return noticeArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {

        return Long.valueOf(noticeArrayList.get(i).getId());
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.notice_list_item, viewGroup, false);
        TextView title = (TextView) v.findViewById(R.id.textViewNoticeTitle);
        TextView subtitle = (TextView) v.findViewById(R.id.textViewNoticeSubtitle);
        ImageView image = (ImageView) v.findViewById(R.id.imageViewNoticeImage);
        TextView content = (TextView) v.findViewById(R.id.textViewNoticeText);
        TextView id = (TextView) v.findViewById(R.id.textViewNoticeId);
        TextView textViewNoticeDateInfo = (TextView) v.findViewById(R.id.textViewNoticeDateInfo);

        final ImageView star = (ImageView) v.findViewById(R.id.imageViewStar);
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noticeArrayList.get(i).setStarred(!noticeArrayList.get(i).isStarred());
                if(noticeArrayList.get(i).isStarred()){
                    star.setImageResource(R.drawable.trans_star);
                } else {
                    star.setImageResource(R.drawable.trans_star_white);
                }
            }
        });

        title.setText(noticeArrayList.get(i).getTitle());
        id.setText(noticeArrayList.get(i).getId());
        textViewNoticeDateInfo.setText(noticeArrayList.get(i).getDateInfo());
        subtitle.setText(noticeArrayList.get(i).getCreatorId() + " , " + noticeArrayList.get(i).getChannelId());

        content.setText(noticeArrayList.get(i).getContent());

        //if a notice came without an image or with image
        if (String.valueOf(noticeArrayList.get(i).getImageUrl())== ""){
            image.setVisibility(View.GONE);
        }else {
          Ion.with(image)
                  .animateIn(R.anim.fade_in)
                  .load(String.valueOf(noticeArrayList.get(i).getImageUrl()));
        }

        return v;
    }
}
