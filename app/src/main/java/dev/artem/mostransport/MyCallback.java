package dev.artem.mostransport;

import com.google.firebase.database.DataSnapshot;

public interface MyCallback {
    void onCallback(DataSnapshot rootSnapshot);
}
