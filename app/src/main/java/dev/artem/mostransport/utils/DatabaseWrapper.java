package dev.artem.mostransport.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;


import java.util.ArrayList;
import java.util.HashMap;

import dev.artem.mostransport.MyCallback;
import dev.artem.mostransport.models.User;

public class DatabaseWrapper {
    private FirebaseDatabase fd = FirebaseDatabase.getInstance();
    private DatabaseReference root = fd.getReference();

    public boolean sameUsernameExists(DataSnapshot rootSnapshot, String username) {
        boolean sameUsernameExists = false;

        for (DataSnapshot userSnapshot : rootSnapshot.child("Users").getChildren()) {
            if (userSnapshot.child("name").getValue(String.class).equals(username)) {
                sameUsernameExists = true;
            }
        }
        return sameUsernameExists;
    }

    public User getUserById(DataSnapshot rootSnapshot, String userId) {
        return rootSnapshot.child("Users").child(userId).getValue(User.class);
    }

    public void getRootAsSnapshot(MyCallback myCallback) {
        root.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot rootSnapshot) {
                myCallback.onCallback(rootSnapshot);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Log.e(this.getClass().getSimpleName(), "onCancelled1", error.toException());
            }
        });
    }

    public String getUserId(DataSnapshot rootSnapshot, String username) {
        for (DataSnapshot userSnapshot : rootSnapshot.child("Users").getChildren()) {
            User user = userSnapshot.getValue(User.class);
            if (user.getName().equals(username)) {
                return userSnapshot.getKey();
            }
        }
        return null;
    }

    public DatabaseReference getUsersRef() {
        return FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static User getCurrentUser(DataSnapshot rootSnapshot) {
        return rootSnapshot.child("Users").child(getCurrentUserId()).getValue(User.class);
    }

    private boolean arrayListsAreSame(ArrayList<?> arrayList1, ArrayList<?> arrayList2) {
        return arrayList1.containsAll(arrayList2) && arrayList2.containsAll(arrayList1);
    }
}
