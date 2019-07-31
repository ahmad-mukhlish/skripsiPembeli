package com.programmerbaper.skripsipembeli.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.programmerbaper.skripsipembeli.R;
import com.programmerbaper.skripsipembeli.activities.FeedBackActivity;
import com.programmerbaper.skripsipembeli.misc.CurrentActivityContext;
import com.programmerbaper.skripsipembeli.misc.NotificationID;
import com.programmerbaper.skripsipembeli.model.Pedagang;
import com.programmerbaper.skripsipembeli.model.Transaksi;
import com.programmerbaper.skripsipembeli.retrofit.api.APIClient;
import com.programmerbaper.skripsipembeli.retrofit.api.APIInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.programmerbaper.skripsipembeli.misc.Config.ID_TRANSAKSI;
import static com.programmerbaper.skripsipembeli.misc.Config.MY_PREFERENCES;
import static com.programmerbaper.skripsipembeli.misc.Config.PEDAGANG;
import static com.programmerbaper.skripsipembeli.misc.Config.TRANSAKSI;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        createNotificationChannel();
        showNotification(remoteMessage);
    }

    private void showNotification(final RemoteMessage remoteMessage) {

        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.ic_pembeli);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "123")
                .setSmallIcon(R.drawable.ic_stat_name)
                .setLargeIcon(icon)
                .setContentText(remoteMessage.getNotification().getBody())
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NotificationID.getID(), builder.build());

        if (remoteMessage.getData().get("jenis").equals("dekat")) {

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {
                    Toast.makeText(CurrentActivityContext.getActualContext(), "Pedagang Telah Mendekat", Toast.LENGTH_SHORT).show();
                }
            });

        } else if (remoteMessage.getData().get("jenis").equals("selesai")) {


            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                public void run() {

                    //flush shared preferences
                    SharedPreferences pref = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(TRANSAKSI, "");
                    editor.commit();

                    getPedagangById(remoteMessage);

                    Log.v("Cikan",remoteMessage.getData().get("id_transaksi")) ;

                }
            });


        }


    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Coba";
            String description = "CIK";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void getPedagangById(final RemoteMessage remoteMessage) {

        APIInterface apiInterface = APIClient.getApiClient().create(APIInterface.class);
        Call<Pedagang> call = apiInterface.pedagangByIDGet(Integer.parseInt(remoteMessage.getData().get("id_pedagang")));

        call.enqueue(new Callback<Pedagang>() {
            @Override
            public void onResponse(Call<Pedagang> call, Response<Pedagang> response) {

                Intent intent = new Intent(CurrentActivityContext.getActualContext(), FeedBackActivity.class);
                intent.putExtra(ID_TRANSAKSI, remoteMessage.getData().get("id_transaksi"));
                intent.putExtra(PEDAGANG, response.body());
                CurrentActivityContext.getActualContext().startActivity(intent);

            }

            @Override
            public void onFailure(Call<Pedagang> call, Throwable t) {

            }
        });



    }


}



