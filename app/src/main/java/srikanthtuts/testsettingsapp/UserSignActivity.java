package srikanthtuts.testsettingsapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fastaccess.datetimepicker.TimePickerFragmentDialog;
import com.fastaccess.datetimepicker.callback.TimePickerCallback;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class UserSignActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener, TimePickerCallback {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    TextView tvDetails, tvPromoTitle, tvPromoSubTitle, tvPromoMessage, tvFromTime, tvToTime;
    ImageView imgPromo;
    CardView cvPromo;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mFBAuth;
    private FirebaseAuth.AuthStateListener mFBAuthListener;
    private long MINIMUM_SESSION_DURATION = 5000;
    private long PROMO_CACHE_DURATION = 1800;

    private final String CONFIG_PROMO_ENABLED = "promo_enabled";
    private final String CONFIG_PROMO_MESSAGE = "promo_message";

    //Firebase Values
    private FirebaseAnalytics mFBAnalytics;
    private FirebaseRemoteConfig mFBConfig;

    private FirebaseUser mFirebaseUser;
    //private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabase;
    Customers customers;

    Button btnDate, btnTime;
    String timeType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvDetails = (TextView) findViewById(R.id.tvDetails);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        tvFromTime = (TextView) findViewById(R.id.tvFromTime);
        tvToTime = (TextView) findViewById(R.id.tvToTime);

        //Firebase Analytics
        mFBAnalytics = FirebaseAnalytics.getInstance(this);
        mFBAnalytics.setMinimumSessionDuration(MINIMUM_SESSION_DURATION);

        //Firebase Remote Config
        mFBConfig = FirebaseRemoteConfig.getInstance();
        //Add Remote Config Settings
        //Config fetches are normally limited to 5 per hour.
        // This enables many more requests to facilitate testing
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFBConfig.setConfigSettings(configSettings);
        //Set Default Settings
        mFBConfig.setDefaults(R.xml.config_params);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnBuy).setOnClickListener(this);

        findViewById(R.id.btnDate).setOnClickListener(this);
        findViewById(R.id.btnTime).setOnClickListener(this);

        customers = new Customers();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClientID))
                .requestEmail()
                .build();

        // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(AppIndex.API).build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());


        //Firebase
        mFBAuth = FirebaseAuth.getInstance();

        mFBAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fbUser = firebaseAuth.getCurrentUser();
                if (fbUser != null) {
                    // User is signed in
                    Log.d("FB", "onAuthStateChanged:signed_in:" + fbUser.getUid());

                } else {
                    // User is signed out
                    Log.d("FB", "onAuthStateChanged:signed_out");
                }
            }
        };


        checkPromoEnabled();
    }


    private void checkPromoEnabled() {
        //If in developer mode cacheExpiration is set to 0 so that each fetch will get details from Server
        if (mFBConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            PROMO_CACHE_DURATION = 0;
        }

        mFBConfig.fetch(PROMO_CACHE_DURATION)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.i("FBConfig", "Promo check was successful");
                            mFBConfig.activateFetched();
                        } else {
                            Log.i("FBConfig", "Promo check failed");
                        }

                        //showPromoButton();
                        showPromoCard();
                    }
                });
    }

    private void showPromoButton() {
        boolean showBtn = false;
        String promoMsg = "";
        Spanned htmlAsSpanned;

        showBtn = mFBConfig.getBoolean(CONFIG_PROMO_ENABLED);
        promoMsg = mFBConfig.getString(CONFIG_PROMO_MESSAGE);
        htmlAsSpanned = Html.fromHtml(mFBConfig.getString(CONFIG_PROMO_MESSAGE));

        Button btn = (Button) findViewById(R.id.btnPromo);
        btn.setVisibility(showBtn ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.btnPromo).setOnClickListener(this);
        btn.setText(htmlAsSpanned);
        tvDetails.setText(htmlAsSpanned);
    }

    private void showPromoCard() {
        // "imgDefault|Happy Independence Day!!|Heavy discount for App at Independence Day sale!|30% discount! Now App only for  ₹0.99"
        //Diwali|Happy Diwali!!|Heavy discount for Rovr at this festival of lights |90% discount! Now Rovr only for  ₹10
        //NewYear|Happy New Year 2017!!|Start the new year with a positive way |Rovr only for  ₹10 (95% off)
        //Christmas|Merry Christmas!!|Heavy discount for Rovr at this Christmas |90% discount! Now Rovr only for  ₹10
        //Diwali|Happy Diwali!!|Heavy discount for Rovr at this festival of lights |90% discount! Now Rovr only for  ₹10

        cvPromo = (CardView) findViewById(R.id.cvPromo);
        tvPromoTitle = (TextView) findViewById(R.id.tvPromoTitle);
        tvPromoSubTitle = (TextView) findViewById(R.id.tvPromoSubTitle);
        tvPromoMessage = (TextView) findViewById(R.id.tvPromoMessage);
        imgPromo = (ImageView) findViewById(R.id.imgVwPromo);

        boolean showCard = mFBConfig.getBoolean(CONFIG_PROMO_ENABLED);
        Log.d("PromoMsg", mFBConfig.getString(CONFIG_PROMO_MESSAGE));
        String promoMsg = mFBConfig.getString(CONFIG_PROMO_MESSAGE);
        String[] msgs = promoMsg.split("\\|");
        Log.d("PromoMsg [1]", msgs[1].toString());
        if (msgs.length > 0) {


            cvPromo.setVisibility(showCard ? View.VISIBLE : View.INVISIBLE);

            if (msgs[0].equalsIgnoreCase("default")) {
                imgPromo.setImageResource(R.drawable.ic_balloons);
            } else if (msgs[0].equalsIgnoreCase("diwali")) {
                imgPromo.setImageResource(R.drawable.ic_fireworks);
            } else if (msgs[0].equalsIgnoreCase("newyear")) {
                imgPromo.setImageResource(R.drawable.ic_balloons);
            } else if (msgs[0].equalsIgnoreCase("christmas")) {
                imgPromo.setImageResource(R.drawable.ic_sale); //ic_christmas_tree_40_4
            } else if (msgs[0].equalsIgnoreCase("kite")) {
                imgPromo.setImageResource(R.drawable.ic_kite_40_4);
            }

            tvPromoTitle.setText(msgs[1]);
            tvPromoSubTitle.setText(msgs[2]);
            tvPromoMessage.setText(msgs[3]);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.connect();
        mFBAuth.addAuthStateListener(mFBAuthListener);
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {

            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(mGoogleApiClient, getIndexApiAction());
    }

    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(mGoogleApiClient, getIndexApiAction());
        if (mFBAuthListener != null) {
            mFBAuth.removeAuthStateListener(mFBAuthListener);
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mGoogleApiClient.disconnect();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading..");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    //Sign In
    private void signIn() {
        Intent signIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Log.i("FBTuts Data", data.getDataString());

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            tvDetails.setText(account.getDisplayName() + ", " + account.getEmail());
            customers.setUserid(account.getId());
            customers.setPassword(account.getEmail());
            customers.setCxname(account.getDisplayName());
            customers.setAddress(account.getPhotoUrl().toString());
            customers.setPhone(account.getIdToken());
            firebaseAuthWithGoogle(account);
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("FBTuts", "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFBAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d("FBTuts", "signInWithCredential:onComplete:" + task.isSuccessful());


                if (!task.isSuccessful()) {
                    Log.d("FBTuts", "signInWithCredential:Failed:" + task.getException());
                    Toast.makeText(UserSignActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        Bundle params = new Bundle();
        params.putInt("ButtonID", v.getId());
        String btnName = "ButtonName";

        switch (v.getId()) {
            case R.id.sign_in_button:
                btnName = "SignIn Click";
                signIn();
                break;
            case R.id.btnSave:
                btnName = "Save Click";
                //sendAnalytics();
                saveData();
                break;
            case R.id.btnBuy:
                btnName = "Buy Click";
                Bundle buyParams = new Bundle();
                params.putString(FirebaseAnalytics.Param.ITEM_ID, "Flash Sale");
                mFBAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, buyParams);
                addToCalendar();
            case R.id.btnPromo:
                //saveData();
                addToCalendar();
                break;
            case R.id.btnTime:
                /*TimePickerFragmentDialog.newInstance(true).show(getSupportFragmentManager(), "ToTime");
                timeType = "ToTime";*/
                break;
            case R.id.btnDate:
                TimePickerFragmentDialog.newInstance(true).show(getSupportFragmentManager(), "FromTime");
                timeType = "FromTime";
                break;
        }

        mFBAnalytics.logEvent(btnName, params);
    }

    public void addToCalendar(){
        long calID = 1;
        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016,12,13);
        //beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        //endTime.set(2012, 9, 14, 8, 45);
        beginTime.set(2016,12,13);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Task 1.2");
        values.put(CalendarContract.Events.DESCRIPTION, "This is 1.2 task");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        //values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles");
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.WRITE_CALENDAR )
                == PackageManager.PERMISSION_GRANTED ) {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            // get the event ID that is the last element in the Uri
            long eventID = Long.parseLong(uri.getLastPathSegment());
        }


    }

    private void saveData() {
        Toast.makeText(getApplicationContext(), "User : " + customers.getUserid(), Toast.LENGTH_LONG).show();
        mDatabase.child("users").child(customers.getUserid()).setValue(customers);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bundle params = new Bundle();
        params.putString(FirebaseAnalytics.Param.ITEM_ID, "Flash Sale");
        mFBAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, params);

    }

    private void sendAnalytics() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onTimeSet(long timeOnly, long dateWithTime) {
        //Toast.makeText(UserSignActivity.this, "Time :  " + String.valueOf(timeOnly), Toast.LENGTH_LONG).show();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date dt = new Date(timeOnly);
        String val = sdf.format(dt);
        if (timeType.equals("ToTime")) {
            //tvToTime.setText(String.valueOf(timeOnly));
            tvToTime.setText(val);
        } else if (timeType.equals("FromTime")) {
            //tvFromTime.setText(String.valueOf(timeOnly));
            tvFromTime.setText(val);
        }
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("UserSign Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
