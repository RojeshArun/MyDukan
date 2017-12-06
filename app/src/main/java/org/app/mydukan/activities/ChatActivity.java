package org.app.mydukan.activities;

/**
 * Created by vaibhavkumar on 09/10/17.
 */

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;

import org.app.mydukan.application.MyDukan;
import org.app.mydukan.R;
import org.app.mydukan.data.ChatData;
import org.app.mydukan.data.DatabaseHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;



public class ChatActivity extends AppCompatActivity implements AIListener{

    RecyclerView recyclerView;
    EditText editText;
    RelativeLayout addBtn;
    DatabaseReference ref;
    DatabaseReference chat_ref;
    DatabaseReference count_ref;
    FirebaseRecyclerAdapter<ChatMessage,chat_rec> adapter;
    Boolean flagFab = true;
    private MyDukan mApp;
    String uid = "";
    boolean isSubscribed = false;
    private AIService aiService;
    String welcome_msg = "Hi, my Name is RAJU,  i can Help you. ASK me PRICE of any model, i shall tell you. Type below the model name.";
    String welcome_msg_hindi= "नमस्ते, मेरा नाम राजू है, मैं आपकी सहायता कर सकता हूं। मुझे किसी भी मॉडल की कीमत पूछें, मैं आपको बताऊंगा। मॉडल नाम के नीचे टाइप करें";
    String notSubscribed = "You have exceeded your daily quota of 50 free queries. Please buy mydukan subscription plan for more queries";
    String formattedDate;
    final int[] msgCount = {0};
    DatabaseHandler sqlDb;
    int freeLimit = 1000; //Change it 50 or 100 as per requirements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_chat);
        sqlDb = new DatabaseHandler(this);
        isSubscribed =  getIntent().getBooleanExtra("IS_SUBSCRIBED", false);
        Log.e("Is Susbscribed", String.valueOf(isSubscribed));
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},1);

        mApp = (MyDukan) getApplicationContext();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        editText = (EditText)findViewById(R.id.editText);
        addBtn = (RelativeLayout)findViewById(R.id.addBtn);

        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        ref = FirebaseDatabase.getInstance().getReference();
        ref.keepSynced(true);
        chat_ref = ref.child("chat");
        count_ref = ref.child("chatCount");
        chat_ref.keepSynced(true);
        uid= mApp.getFirebaseAuth().getCurrentUser().getUid();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        formattedDate = df.format(c.getTime());

        Log.d("%%%%%ChatUID%%%%%", uid);
        ChatData entry = sqlDb.getEntry(formattedDate);
        if(entry==null){
            sqlDb.addEntry(new ChatData(formattedDate));
            Log.d("SQL Entry", "Writing to SQL");
            ChatMessage chatMessage = new ChatMessage(welcome_msg, "bot");
            chat_ref.child(uid).push().setValue(chatMessage);
            ChatMessage chatMessageHindi = new ChatMessage(welcome_msg_hindi, "bot");
            chat_ref.child(uid).push().setValue(chatMessageHindi);
        }else{
            Log.d("Entry Already There", "Skipping");
        }

        final AIConfiguration config = new AIConfiguration("0938020266a14f36a2bab54e37842b6c",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {

                String message = editText.getText().toString().trim();
                DatabaseReference tmp = count_ref.child(uid).child(formattedDate);
                tmp.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            msgCount[0] = dataSnapshot.getValue(Integer.class);
                            msgCount[0] += 1;
                            count_ref.child(uid).child(formattedDate).setValue(msgCount[0]);
                            Log.e("Updating entry", Integer.toString(msgCount[0]));
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("Failed", databaseError.getMessage());
                    }
                });
                if(msgCount[0] ==0) {
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("/chatCount/" + uid + "/" + formattedDate + "/", 1);

                    FirebaseDatabase.getInstance().getReference().updateChildren(childUpdates,
                            new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        Log.e("Chatbot firebase init:", "failure");
                                    } else {
                                        msgCount[0] += 1;
                                    }
                                }
                            });
                }
                if(msgCount[0]>=freeLimit && !isSubscribed){
                    ChatMessage chatMessage = new ChatMessage(notSubscribed, "bot");
                    chat_ref.child(uid).push().setValue(chatMessage);
                    return;
                }

                if (!message.equals("")) {
                    Log.e("Message", message);
                    ChatMessage chatMessage = new ChatMessage(message, "user");
                    chat_ref.child(uid).push().setValue(chatMessage);

                    aiRequest.setQuery(message);
                    new AsyncTask<AIRequest,Void,AIResponse>(){

                        @Override
                        protected AIResponse doInBackground(AIRequest... aiRequests) {
                            final AIRequest request = aiRequests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }
                        @Override
                        protected void onPostExecute(AIResponse response) {
                            if (response != null) {
                                Result result = response.getResult();
                                String reply = result.getFulfillment().getSpeech();
                                ChatMessage chatMessage = new ChatMessage(reply, "bot");
                                chat_ref.child(uid).push().setValue(chatMessage);
                            }
                        }
                    }.execute(aiRequest);
                }
                else {
                    aiService.startListening();
                }

                editText.setText("");

            }
        });



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = (ImageView)findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(),R.drawable.ic_send_white_24dp);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(),R.drawable.ic_mic_white_24dp);


                if (s.toString().trim().length()!=0 && flagFab){
                    ImageViewAnimatedChange(ChatActivity.this,fab_img,img);
                    flagFab=false;

                }
                else if (s.toString().trim().length()==0){
                    ImageViewAnimatedChange(ChatActivity.this,fab_img,img1);
                    flagFab=true;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Query query = chat_ref.child(uid).limitToLast(50);
        adapter = new FirebaseRecyclerAdapter<ChatMessage, chat_rec>(ChatMessage.class,R.layout.msglist,chat_rec.class,query) {
            @Override
            protected void populateViewHolder(chat_rec viewHolder, ChatMessage model, int position) {
                if (model.getMsgUser().equals("user")) {
                    viewHolder.rightText.setText(model.getMsgText());
                    viewHolder.rightText.setVisibility(View.VISIBLE);
                    viewHolder.leftText.setVisibility(View.GONE);
                    viewHolder.welcomeText.setVisibility(View.GONE);
                }else {
                    viewHolder.leftText.setText(model.getMsgText());
                    viewHolder.rightText.setVisibility(View.GONE);
                    viewHolder.leftText.setVisibility(View.VISIBLE);
                    viewHolder.welcomeText.setVisibility(View.GONE);
                }

            }
        };

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int msgCount = adapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if (lastVisiblePosition == -1 ||
                        (positionStart >= (msgCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);

                }

            }
        });

        recyclerView.setAdapter(adapter);

    }
    public void ImageViewAnimatedChange(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }

    @Override
    public void onResult(AIResponse response) {


        Result result = response.getResult();

        String message = result.getResolvedQuery();
        ChatMessage chatMessage0 = new ChatMessage(message, "user");
        chat_ref.child(uid).push().setValue(chatMessage0);


        String reply = result.getFulfillment().getSpeech();
        ChatMessage chatMessage = new ChatMessage(reply, "bot");
        chat_ref.child(uid).push().setValue(chatMessage);


    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
}
