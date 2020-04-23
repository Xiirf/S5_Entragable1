package com.example.entrega1;

import android.provider.ContactsContract;
import android.util.Log;

import com.example.entrega1.entity.Viaje;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDatabaseService {
    private static String userId;
    private static FirebaseDatabaseService service;
    private static FirebaseDatabase mDataBase;

    public static FirebaseDatabaseService getServiceInstance() {
        if (service == null || mDataBase == null) {
            service = new FirebaseDatabaseService();
            mDataBase = FirebaseDatabase.getInstance();
            mDataBase.setPersistenceEnabled(true);
        }
        if (userId == null || userId.isEmpty()) {
            userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        }
        return service;
    }

    public DatabaseReference getTravel(String travelId) {
        return mDataBase.getReference("user/U123/travel/" + travelId).getRef();
    }

    public void createTravel(Viaje travel, DatabaseReference.CompletionListener completionListener) {
        mDataBase.getReference("travels/").push().setValue(travel, completionListener);
    }

    public DatabaseReference getTravels() {
        return mDataBase.getReference("travels");
    }

    public DatabaseReference getUserTravels() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return mDataBase.getReference(String.format("users/%s/travels", uid));
    }

    public void addUserTravel(String idViaje, DatabaseReference.CompletionListener completionListener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDataBase.getReference(String.format("users/%s/travels", uid)).child(idViaje).setValue(idViaje, completionListener);
    }

    public void deleteUserTravel(String idViaje, DatabaseReference.CompletionListener completionListener) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDataBase.getReference(String.format("users/%s/travels", uid)).child(idViaje).removeValue(completionListener);
    }
}
