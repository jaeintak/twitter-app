package com.example.user.twitter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class TwitAdapter extends BaseAdapter {

    ArrayList<twit> twitList;
    StorageReference imagesRef = FirebaseStorage.getInstance().getReference().child("images");
    String userID;

    TwitAdapter() {
        twitList = new ArrayList<>();

    }

    @Override
    public int getCount() {
        return twitList.size();
    }

    @Override
    public Object getItem(int position) {
        return twitList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        final Context context = parent.getContext();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.twit, parent, false);

        }

        final twit t = twitList.get(position);


        TextView writer = convertView.findViewById(R.id.writer);
        TextView message = convertView.findViewById(R.id.message);
        ImageView profile = convertView.findViewById(R.id.profile);
        ImageView image = convertView.findViewById(R.id.image);
        ImageView like = convertView.findViewById(R.id.like);
        ImageView delete = convertView.findViewById(R.id.delete);

        String imageName = twitList.get(position).imageName;

        if(imageName != null){
            StorageReference imageRef = imagesRef.child(imageName);
            GlideApp.with(context)
                    .load(imageRef)
                    .into(image);
        }else{
            image.setImageDrawable(null);
        }
        GlideApp.with(context);


        writer.setText(twitList.get(position).writer);
        message.setText(twitList.get(position).message);
        String picUrl = twitList.get(position).picId;

        if(picUrl != null){
            GlideApp.with(context)
                    .load("https://graph.facebook.com/" + picUrl + "/picture?type=normal")
                    .circleCrop()
                    .into(profile);
            }else {
                GlideApp.with(context)
                    .load(R.drawable.empty_profile)
                    .centerCrop()
                    .into(profile);
        }

        if(t.likes != null && t.likes.get(userID) != null){
            like.setImageResource(R.drawable.like_button_liked);
        }else{
            like.setImageResource(R.drawable.like_button);
        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).deleteTwit(t.id);
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).likeUnlike(t.id, t.likes);

            }
        });

        return convertView;
    }

    void addItem(twit twit){
        twitList.add(0,twit);  //위에서부터 넣기
        notifyDataSetChanged();
    }

    void replaceItem(twit twit){
        for (int i = 0; i<twitList.size(); i++){
            if (twitList.get(i).id.equals(twit.id)){
                twitList.get(i).likes = twit.likes;
                break;
            }
        }
        notifyDataSetChanged();
    }

    void showDeleteTwit(twit twit){
        for(int i= 0; i < twitList.size(); i++) {
            if (twitList.get(i).id.equals(twit.id)) {
                twitList.remove(i);
                break;
            }
        }
        notifyDataSetChanged();
    }

}
