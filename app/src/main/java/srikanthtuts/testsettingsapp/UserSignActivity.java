package srikanthtuts.testsettingsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class UserSignActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    TextView tvDetails, tvPromoTitle, tvPromoSubTitle, tvPromoMessage;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvDetails = (TextView) findViewById(R.id.tvDetails);
        findViewById(R.id.sign_in_button).setOnClickListener(this);

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


        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnBuy).setOnClickListener(this);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.webClientID))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

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
                imgPromo.setImageResource(R.drawable.balloons_64);
            } else if (msgs[0].equalsIgnoreCase("diwali")) {
                imgPromo.setImageResource(R.drawable.ic_rocket_40_4);
            }else if (msgs[0].equalsIgnoreCase("newyear")) {
                imgPromo.setImageResource(R.drawable.balloons_32);
            }else if (msgs[0].equalsIgnoreCase("christmas")) {
                imgPromo.setImageResource(R.drawable.ic_christmas_tree_40_4);
            }else if (msgs[0].equalsIgnoreCase("kite")) {
                imgPromo.setImageResource(R.drawable.ic_kite_40_4);
            }

            tvPromoTitle.setText(msgs[1]);
            tvPromoSubTitle.setText(msgs[2]);
            tvPromoMessage.setText(msgs[3]);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
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
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFBAuthListener != null) {
            mFBAuth.removeAuthStateListener(mFBAuthListener);
        }
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
                break;
            case R.id.btnBuy:
                btnName = "Buy Click";
                Bundle buyParams = new Bundle();
                params.putString(FirebaseAnalytics.Param.ITEM_ID, "Flash Sale");
                mFBAnalytics.logEvent(FirebaseAnalytics.Event.ECOMMERCE_PURCHASE, buyParams);


        }

        mFBAnalytics.logEvent(btnName, params);
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


}
