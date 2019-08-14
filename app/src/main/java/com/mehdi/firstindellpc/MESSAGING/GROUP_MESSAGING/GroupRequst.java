package com.mehdi.firstindellpc.MESSAGING.GROUP_MESSAGING;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mehdi.firstindellpc.HOME.HomeActivity;
import com.mehdi.firstindellpc.PROFILE.AdapterPeople;
import com.mehdi.firstindellpc.PROFILE.ProfileActivity;
import com.mehdi.firstindellpc.PROFILE.profilData;
import com.mehdi.firstindellpc.R;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupRequst extends AppCompatActivity implements AdapterPeople.clickPrson {

    private AdapterPeople people;
    private String me;
    private ArrayList<profilData> whoAdd;
    private DatabaseReference reference;
    private ValueEventListener valueListener;
    private String usrrrr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_group_requst);

        String groupId = getIntent().getStringExtra("groupId");

        if (groupId == null) return;

        RecyclerView recyclerView = findViewById(R.id.rec_is_accept);

        people = new AdapterPeople(this, 23);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(people);

        whoAdd = new ArrayList<>();

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        reference = database.getReference().child("PROFILES").child(preferences.getString("uid", "")).child("GROUP_REQUEST").child(groupId).child("users");


         valueListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String usersStr = dataSnapshot.getValue(String.class);
                if (usersStr == null) return;
                String[] users = usersStr.split(",");
                usrrrr = usersStr;
                for (String us : users){
                    whoAdd.add(new profilData(us, null, null, null,null, null, us, null, null, null));
                }

                if (whoAdd.size() >0){
                    people.swapAdapter(whoAdd);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference.addListenerForSingleValueEvent(valueListener);



        findViewById(R.id.accept).setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {

                     if (usrrrr != null) {

                         me = preferences.getString("uid", "");
                         String[] s = usrrrr.split(",");
                         ArrayList<String> list = new ArrayList<>(Arrays.asList(s));
                         if (list.contains(me)){
                             database.getReference().child("PROFILES").child(me).child("GROUP_REQUEST").child(groupId).removeValue();
                             Intent i = new Intent(GroupRequst.this, GroupMessaging.class);
                             i.putExtra("t", "simple");
                             i.putExtra("uid", groupId);
                             startActivity(i);
                         }
                         list.add(me);
                         for (String user : list) {
                             database.getReference()
                                     .child("PROFILES").child(user).child("GROUP_MESSAGE")
                                     .child(groupId).setValue(
                                     new GroupMessage(groupId, usrrrr + "," + me, "Group", "Accept the request",
                                             "https://firebasestorage.googleapis.com/v0/b/blood-fff9b.appspot.com/o/group.png?alt=media&token=aa3c3825-0efd-401b-b936-db849a9847a2"));
                         }
                         database.getReference().child("PROFILES").child(me).child("GROUP_REQUEST").child(groupId).removeValue();
                         Intent i = new Intent(GroupRequst.this, GroupMessaging.class);
                         i.putExtra("t", "simple");
                         i.putExtra("uid", groupId);
                         startActivity(i);


                     }

                 }
             });



        findViewById(R.id.refuse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.getReference().child("PROFILES").child(preferences.getString("uid", "")).child("GROUP_REQUEST").child(groupId).removeValue();
                startActivity(new Intent(GroupRequst.this, HomeActivity.class));
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        whoAdd.clear();
        people.swapAdapter(whoAdd);
        reference.removeEventListener(valueListener);
    }


    @Override
    public void select(String uid) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("uid", uid);
        startActivity(i);
    }

    @Override
    public void add(String uid) {

    }

    @Override
    public void msg(String uid) {

    }
}
