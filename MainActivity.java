package com.example.user.twitter;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    TextView twit;
    //EditText new_message;
    FloatingActionButton send;
    ListView twitListView;
    TwitAdapter twitAdapter;

    FirebaseFirestore db;
    FirebaseStorage storage;
    StorageReference imagesRef;

    CollectionReference twitRef;

    String userName;
    String userID;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userName = getIntent().getStringExtra("user_name");
        userID = getIntent().getStringExtra("user_id");


        setContentView(R.layout.activity_main);

        twitListView = findViewById(R.id.twitList);
        //new_message = findViewById(R.id.new_message);
        send = findViewById(R.id.send);

        twitAdapter= new TwitAdapter();
        twitListView.setAdapter(twitAdapter);
        twitAdapter.userID = userID;

        db = FirebaseFirestore.getInstance();
        twitRef = db.collection("twits");

        storage = FirebaseStorage.getInstance();
        imagesRef = storage.getReference().child("images");



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewTwitActivity.class);
                startActivityForResult(intent, 100);


                //twitAdapter.addItem(new twit("탁재인", new_message.getText().toString()));

            }
        });


        twitRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()) {
                    twit twit = doc.getDocument().toObject(twit.class);
                    twit.id = doc.getDocument().getId();
                    if(doc.getType() == DocumentChange.Type.ADDED){
                        twitAdapter.addItem(twit);


                    }else if (doc.getType() == DocumentChange.Type.MODIFIED){
                        twitAdapter.replaceItem(twit);
                    }else if (doc.getType() == DocumentChange.Type.REMOVED){
                        twitAdapter.showDeleteTwit(twit);
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final String msg = data.getStringExtra("message");
        String uriString = data.getStringExtra("image_uri");

        if(uriString != null) {
            UploadTask uploadTask;

            Uri imageUri = Uri.parse(uriString);
            uploadTask = imagesRef.child(imageUri.getLastPathSegment()).putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    newTwit(userName, msg, userID, taskSnapshot.getMetadata().getName());
                }
            });
        }else{
            newTwit(userName, msg, userID, null);
        }




        //twitAdapter.addItem(new twit("탁재인",msg)) 로컬 앱에 있을 필요가 없음


    }
    public void newTwit(String writer, String message, String picId, String imageName) {
        HashMap<String, Object> twit = new HashMap<>();
        twit.put("writer", userName);
        twit.put("message", message);
        twit.put("timestamp", new Date());

        if(imageName != null)
            twit.put("imageName", imageName);

        if(userID != null)
            twit.put("picId", userID);

        twitRef.add(twit);
    }

    public void deleteTwit(String twitid) {
        twitRef.document(twitid).delete();
    }


    public void likeUnlike(String twitid, HashMap<String, Boolean> likes){
        if(likes == null) {
            likes = new HashMap<>();
        }
        if(likes.get(userID) != null){
            likes.remove(userID);
        }else {
            likes.put(userID, true);
        }
        twitRef.document(twitid).update("likes", likes);
    }




}
