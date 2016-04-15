package cmpe295b.watchdog.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import cmpe295b.watchdog.R;


public class FacebookLoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        Log.i("@@","created");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        private TextView mTextDetails;
        private CallbackManager mCallbackManager;
        private AccessTokenTracker mTokenTracker;
        private ProfileTracker mProfileTracker;
        private ImageView mProfilePicture;
        private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Harshad", "onSuccess");
                AccessToken accessToken = loginResult.getAccessToken();
                Profile profile = Profile.getCurrentProfile();
                mTextDetails.setText(constructWelcomeMessage(profile));

                startActivity(new Intent(getActivity(), BaseActivity.class));
            }


            @Override
            public void onCancel() {
                Log.d("Harshad", "onCancel");
            }

            @Override
            public void onError(FacebookException e) {
                Log.d("Harshad", "onError " + e);
            }
        };


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            FacebookSdk.sdkInitialize(getActivity().getApplicationContext());

            mCallbackManager = CallbackManager.Factory.create();
            setupTokenTracker();
            setupProfileTracker();

            mTokenTracker.startTracking();
            mProfileTracker.startTracking();

        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_login, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            setupTextDetails(view);
            setupLoginButton(view);
        }

        @Override
        public void onResume() {
            super.onResume();
            Profile profile = Profile.getCurrentProfile();
            mTextDetails.setText(constructWelcomeMessage(profile));
        }

        @Override
        public void onStop() {
            super.onStop();
            mTokenTracker.stopTracking();
            mProfileTracker.stopTracking();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            Intent i=new Intent(getActivity(), BaseActivity.class);
            //finish();
            startActivity(i);
        }

        private void setupTextDetails(View view) {
            mTextDetails = (TextView) view.findViewById(R.id.text_details);
            Button button = (Button) view.findViewById(R.id.button);
            button.setVisibility(View.GONE);
        }

        private void setupTokenTracker() {
            mTokenTracker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    Log.d("Harshad", "" + currentAccessToken);
                }
            };
        }

        private void setupProfileTracker() {
            mProfileTracker = new ProfileTracker() {
                @Override
                protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                    Log.d("Harshad", "" + currentProfile);
                    Log.d("@profile info",constructWelcomeMessage(currentProfile));
                    mTextDetails.setText(constructWelcomeMessage(currentProfile));
                }
            };
        }

        private void setupLoginButton(View view) {
            LoginButton mButtonLogin = (LoginButton) view.findViewById(R.id.login_button);
            mButtonLogin.setFragment(this);
            mButtonLogin.setReadPermissions("user_friends");
            mButtonLogin.registerCallback(mCallbackManager, mFacebookCallback);
        }

        private String constructWelcomeMessage(Profile profile) {
            StringBuffer stringBuffer = new StringBuffer();
            if (profile != null) {
                stringBuffer.append("Welcome " + profile.getName()+"profile id"+profile.getId());
            }
            return stringBuffer.toString();
        }

        public void newActivity(View view) {
            // Do something in response to button
           /* Intent i = new Intent(getActivity(), MainActivity2.class);
            this.startActivity(i);*/

        }

    }
}
