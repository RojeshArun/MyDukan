package org.app.mydukan.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.app.mydukan.R;
import org.app.mydukan.adapters.AdapterListFeed;
import org.app.mydukan.adapters.CircleTransform;
import org.app.mydukan.data.Comment;
import org.app.mydukan.data.Feed;
import org.app.mydukan.utils.AppContants;
import org.app.mydukan.utils.Utils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.app.mydukan.fragments.TwoFragment.LIKE_ROOT;

/**
 * Created by Harshit Agarwal on 10/0/17.
 * Updated by shivayogi Hiremath on 10/20/17
 */

public class CommentsActivity extends AppCompatActivity {

    private static final String COMMENT_ROOT = "comments";
    RecyclerView recyclerView;
    EditText commentET;
    ImageView addCommentButton;
    ProgressBar progressBar;
    List<Comment> commentList;
    CommentsAdapter mAdapter;
    TextView emptyText;
    Feed feed;
    ImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        commentET = (EditText) findViewById(R.id.etComment);
        addCommentButton = (ImageView) findViewById(R.id.addComment);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        emptyText = (TextView) findViewById(R.id.emptyText);
        ivAvatar = (ImageView) findViewById(R.id.et_avatar);
        feed = (Feed) getIntent().getSerializableExtra(AppContants.FEED);
        initializeFeed();

        commentList = new ArrayList<>();

        mAdapter = new CommentsAdapter(this, commentList, feed);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        getComments();

        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });

        commentET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                addCommentButton.setVisibility(s.toString().trim().isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Glide.with(this)
                .load(user.getPhotoUrl())
                .placeholder(R.drawable.profile_circle)
                .centerCrop()
                .transform(new CircleTransform(ivAvatar.getContext()))
                .override(50, 50)
                .into(ivAvatar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
             onBackPressed();
            return true;
        }

            return super.onOptionsItemSelected(item);
    }

    private void initializeFeed() {
        AdapterListFeed.MyViewHolder holder = new AdapterListFeed.MyViewHolder(findViewById(R.id.cardView), new AdapterListFeed.OnClickItemFeed() {
            @Override
            public void onClickItemFeed(int position, View view) {
                switch (view.getId()) {
                    case R.id.tv_contentLink:  // HyperLink click
                        String textHyper = feed.getText();
                        if (textHyper.isEmpty() || textHyper == null) {
                            return;
                        } else {
                            Intent intent = new Intent(CommentsActivity.this, WebViewActivity.class);
                            intent.putExtra(AppContants.VIEW_PROFILE, (Serializable) feed);
                            startActivity(intent);
                        }
                        break;
                    case R.id.comment:
                        commentET.requestFocus();
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(commentET, InputMethodManager.SHOW_IMPLICIT);
                        break;
                    case R.id.like:
                        toggleLike();

                }
            }
        });

        holder.currFeed = feed;
        holder.setTvName(feed.getName());
        holder.setTvContent(feed.getText());
        holder.setTvTime(feed.getTime());
        holder.setIvAvatar(feed.getPhotoAvatar());
        holder.setIvContent(feed.getPhotoFeed());
        if (feed.getLink() != null) {
            holder.setTvLink(feed.getLink());
        }
        holder.getLikes(feed);

        holder.commentTV.setText("Comment");
    }

    private void toggleLike() {
        final DatabaseReference referenceLike = FirebaseDatabase.getInstance().getReference().child(LIKE_ROOT + "/" + feed.getIdFeed());
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        referenceLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    referenceLike.child(user.getUid()).setValue(true);
                    return;
                }
                if (dataSnapshot.hasChild(user.getUid())) {
                    referenceLike.child(user.getUid()).removeValue();
                } else {
                    referenceLike.child(user.getUid()).setValue(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void addComment() {
        String commentText = commentET.getText().toString();
        commentText = commentText.trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "Please enter a text", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(COMMENT_ROOT);
        String key = databaseReference.push().getKey();

        Comment comment = new Comment();
        comment.setUserInfo(new Comment.UserInfo(user.getUid(), user.getDisplayName(),
                user.getPhotoUrl() == null ? "default_uri" : user.getPhotoUrl().toString()));
        comment.setText(commentText);
        comment.setTime(MyProfileActivity.getCurrentTimeStamp());
        comment.setId(key);

        databaseReference.child(feed.getIdFeed()).child(key).setValue(comment);

        commentET.setText("");
        //todo show progress bar and handle offline

    }

    private void getComments() {

        final DatabaseReference referenceFollow = FirebaseDatabase.getInstance().getReference().child(COMMENT_ROOT + "/" + feed.getIdFeed());
        referenceFollow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                if (dataSnapshot != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //for(DataSnapshot commentSnapshot: snapshot.getChildren())
                        commentList.add(snapshot.getValue(Comment.class));
                    }
                }
                toggleLoading();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void toggleLoading() {
        progressBar.setVisibility(View.GONE);
        emptyText.setVisibility(commentList.isEmpty() ? View.VISIBLE : View.GONE);

    }

    class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

        Feed feed;
        LayoutInflater inflater;
        Context context;
        List<Comment> commentList;

        public CommentsAdapter(Context context, List<Comment> commentList, Feed feed) {
            this.context = context;
            this.commentList = commentList;
            inflater = LayoutInflater.from(context);
            this.feed = feed;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(inflater.inflate(R.layout.item_comment, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            Comment comment = commentList.get(position);
            holder.setProfileImage(comment.getUserInfo().getPhotoUrl());
            holder.setProfileName(comment.getUserInfo().getName());
            holder.setTime(comment.getTime());
            holder.setComment(comment.getText());
            holder.setEditAndDelete(comment.getUserInfo().getuId(), feed.getIdUser());
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView profileImage;
            TextView profileName, time, comment, edit, delete;

            public MyViewHolder(View itemView) {
                super(itemView);
                profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
                profileName = (TextView) itemView.findViewById(R.id.profileName);
                time = (TextView) itemView.findViewById(R.id.time);
                comment = (TextView) itemView.findViewById(R.id.comment);
                edit = (TextView) itemView.findViewById(R.id.edit);
                delete = (TextView) itemView.findViewById(R.id.delete);

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editComment(getLayoutPosition());


                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int pos = getLayoutPosition();
                        new AlertDialog.Builder(context)
                                .setTitle("Delete comment")
                                .setMessage("Are you sure you want to delete the comment?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String commentId = commentList.get(pos).getId();
                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                .child(COMMENT_ROOT + "/" + feed.getIdFeed() + "/" + commentId);
                                        databaseReference.removeValue();
                                    }
                                })
                                .setNegativeButton("Cancel", null).show();
                    }
                });

            }

            private void editComment(final int pos) {
                final EditText input = new EditText(context);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                input.setText(commentList.get(pos).getText());

                new AlertDialog.Builder(context)
                        .setTitle("Edit comment")
                        .setView(input)
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newComment = input.getText().toString().trim();
                                if (newComment.isEmpty()) {
                                    Toast.makeText(context, "Cannot update to an empty comment", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String commentId = commentList.get(pos).getId();
                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                        .child(COMMENT_ROOT + "/" + feed.getIdFeed() + "/" + commentId);

                                Comment comment = commentList.get(pos);
                                comment.setText(newComment);
                                databaseReference.setValue(comment);
                            }
                        })
                        .setNegativeButton("Cancel", null).show();

            }

            public void setProfileImage(String url) {

                Glide.with(profileImage.getContext())
                        .load(url.equals("default_uri") ?
                                R.drawable.profile_circle :
                                url)
                        .placeholder(R.drawable.profile_circle)
                        .centerCrop()
                        .transform(new CircleTransform(profileImage.getContext()))
                        .override(50, 50)
                        .into(profileImage);
            }

            public void setProfileName(String name) {
                profileName.setText(name == null ? "" : name);
            }

            public void setTime(String timeString) {
                try {
                    time.setText(Utils.getDisplayTimeText(timeString));
                } catch (ParseException e) {
                    e.printStackTrace();
                    time.setText(timeString);
                }
            }

            public void setComment(String text) {
                comment.setText(text == null ? "" : text);
            }

            public void setEditAndDelete(String commentUser, String feedUser) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                delete.setVisibility((user.getUid().equalsIgnoreCase(feedUser) || user.getUid().equalsIgnoreCase(commentUser)) ?
                        View.VISIBLE : View.GONE);
                edit.setVisibility(user.getUid().equalsIgnoreCase(commentUser) ? View.VISIBLE : View.GONE);
            }
        }
    }
}

