package com.mehdi.firstindellpc.PROFILE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mehdi.firstindellpc.HOME.HomeActivity;
import com.mehdi.firstindellpc.MESSAGING.CHATROOM.RoomChatActivity;
import com.mehdi.firstindellpc.MESSAGING.GROUP_MESSAGING.GroupMessaging;
import com.mehdi.firstindellpc.R;

import java.util.ArrayList;

public class Search extends AppCompatActivity implements AdapterPeople.clickPrson {

    private EditText searchBox;
    private AdapterPeople people;
    private ArrayList<profilData> whoAdd;
    private String GroupId = "";

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, HomeActivity.class));
    }


    private LinearLayout empty;
    private String myId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        empty = findViewById(R.id.em);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        myId = preferences.getString("uid", null);

        findViewById(R.id.back_sear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Search.this, HomeActivity.class));
            }
        });


        ImageView searchIcon = findViewById(R.id.search_icon);
        searchBox = findViewById(R.id.search_box);
        RecyclerView recyclerView = findViewById(R.id.rec_search);

        int t = getIntent().getIntExtra("type", 0);
        if (t == 0){
            people = new AdapterPeople(this, 0);
        }else if (t == 1){
            GroupId = getIntent().getStringExtra("gId");
            people = new AdapterPeople(this, 1);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(people);

        whoAdd = new ArrayList<>();

        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchBox.getText().toString().length() > 0){
                    search(searchBox.getText().toString().toLowerCase());
                }

            }
        });



    }

    public void search(String search){



            DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

            Query ser = mFirebaseDatabaseReference.child("PROFILES").orderByChild("name")
                    .startAt(search)
                    .endAt(search+"\uf8ff");


            ser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    whoAdd.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        profilData user = postSnapshot.getValue(profilData.class);
                        if (user == null) return;
                        if (!(user.getUid().equals(myId))){
                            whoAdd.add(user);
                        }
                    }
                    /*

                    if (whoAdd.size() > 0){
                        empty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }else {
                        empty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                     */
                    people.swapAdapter(whoAdd);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }});

        }



    @Override
    public void select(String uid) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("uid", uid);
        startActivity(i);
    }

    @Override
    public void add(String uid) {
        Intent i = new Intent(this, GroupMessaging.class);
        i.putExtra("uid", uid);
        i.putExtra("t", "addone");
        i.putExtra("gId2", GroupId);
        startActivity(i);
    }

    @Override
    public void msg(String uid) {
        Intent i = new Intent(this, RoomChatActivity.class);
        i.putExtra("uid", uid);
        startActivity(i);
    }
}
