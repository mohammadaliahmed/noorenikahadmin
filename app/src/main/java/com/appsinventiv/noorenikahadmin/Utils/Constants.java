package com.appsinventiv.noorenikahadmin.Utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constants {
    public static boolean ACCEPTED=false;
    public static boolean REQUEST_RECEIVED=false;
    public static DatabaseReference M_DATABASE= FirebaseDatabase.getInstance("https://noorenikah-default-rtdb.firebaseio.com/").getReference();
}
