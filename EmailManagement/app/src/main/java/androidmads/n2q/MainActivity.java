package androidmads.n2q;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import androidmads.n2q.Adapter.ViewPagerAdapterMain;
import androidmads.n2q.Database.FileManager;
import androidmads.n2q.Fragment.FragmentGmail;
import androidmads.n2q.Fragment.FragmentSms;
import androidmads.n2q.Helper.Utils;

public class MainActivity extends AppCompatActivity
                          implements GoogleApiClient.OnConnectionFailedListener {

    TabLayout tabLayoutMain;
    ViewPager viewPagerMain;
    ImageButton imgbtnSchedule, imgbtnSended, imgbtnSignIn;
    private GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 1;
    final boolean[] isCheck = {true};
    private final String FILE_NAME = Utils.FILE_NAME_GOOGLE;

    FileManager fileManager = new FileManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        init();

        if (fileManager.readData(FILE_NAME).isEmpty()){
            imgbtnSignIn.setBackgroundResource(R.drawable.ic_google_white_100);
        }

        imgbtnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fileManager.readData(FILE_NAME).isEmpty()){
                    signIn();
                } else {
                    signOut();
                }
            }
        });

        ViewPagerAdapterMain viewPagerAdapterMain =
                new ViewPagerAdapterMain(getSupportFragmentManager());

        viewPagerMain.setAdapter(viewPagerAdapterMain);
        viewPagerAdapterMain.notifyDataSetChanged();
        tabLayoutMain.setupWithViewPager(viewPagerMain);

        imgbtnSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSms fragmentSms = new FragmentSms();
                if (isCheck[0]){
                    FragmentGmail.loadDatabaseEmail("SELECT * FROM Email WHERE TrangThai = 0");
                    fragmentSms.loadDatabaseSms(getBaseContext(), "SELECT * FROM Sms WHERE TrangThai = 0");
                    imgbtnSchedule.setBackgroundResource(R.drawable.ic_clock_100);
                    imgbtnSended.setBackgroundResource(R.drawable.ic_checkmark_white_100);
                    isCheck[0] = false;
                } else {
                    FragmentGmail.loadDatabaseEmail("SELECT * FROM Email");
                    fragmentSms.loadDatabaseSms(getBaseContext(), "SELECT * FROM Sms");
                    isCheck[0] = true;
                    imgbtnSchedule.setBackgroundResource(R.drawable.ic_clock_white_100);
                }

            }
        });

        imgbtnSended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSms fragmentSms = new FragmentSms();
                if (isCheck[0]){
                    FragmentGmail.loadDatabaseEmail("SELECT * FROM Email WHERE TrangThai = 1");
                    fragmentSms.loadDatabaseSms(getBaseContext(), "SELECT * FROM Sms WHERE TrangThai = 1");
                    isCheck[0] = false;
                    imgbtnSended.setBackgroundResource(R.drawable.ic_checkmark_100);
                    imgbtnSchedule.setBackgroundResource(R.drawable.ic_clock_white_100);
                } else {
                    FragmentGmail.loadDatabaseEmail("SELECT * FROM Email");
                    fragmentSms.loadDatabaseSms(getBaseContext(), "SELECT * FROM Sms");
                    isCheck[0] = true;
                    imgbtnSended.setBackgroundResource(R.drawable.ic_checkmark_white_100);
                }
            }
        });

    }

    public void init(){
        tabLayoutMain   = (TabLayout) findViewById(R.id.tabLayoutMain);
        viewPagerMain   = (ViewPager) findViewById(R.id.viewPagerMain);
        imgbtnSchedule  = (ImageButton) findViewById(R.id.imgbtnSchedule);
        imgbtnSended    = (ImageButton) findViewById(R.id.imgbtnSended);
        imgbtnSignIn    = (ImageButton) findViewById(R.id.imgbtnSignIn);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d("" + result,"CHECKGOOGLE");
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            String address = account.getEmail().toString();

            fileManager.saveData(FILE_NAME, address);
            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
            imgbtnSignIn.setBackgroundResource(R.drawable.ic_google_100);
        } else {
            Toast.makeText(this, "Đăng nhập không thành công\n      Vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                fileManager.saveData(FILE_NAME, "");
            }
        });
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        imgbtnSignIn.setBackgroundResource(R.drawable.ic_google_white_100);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Failed", connectionResult + "");
    }
}