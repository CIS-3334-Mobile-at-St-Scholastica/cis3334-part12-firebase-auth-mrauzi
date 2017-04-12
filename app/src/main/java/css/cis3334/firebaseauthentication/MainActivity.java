package css.cis3334.firebaseauthentication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private TextView textViewStatus;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private Button buttonGoogleLogin;
    private Button buttonCreateLogin;
    private Button buttonSignOut;

    private FirebaseAuth mAuth;     // instance of Firebase Authorization
    private FirebaseAuth.AuthStateListener mAuthListener;   // instance of Firebase Authorization listener

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonGoogleLogin = (Button) findViewById(R.id.buttonGoogleLogin);
        buttonCreateLogin = (Button) findViewById(R.id.buttonCreateLogin);
        buttonSignOut = (Button) findViewById(R.id.buttonSignOut);

        /**
         * buttonLogin - button that calls the signIn() method to allow a user to log in
         */
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("CIS3334", "normal login ");  // creates a log for a normal login
                signIn(editTextEmail.getText().toString(), editTextPassword.getText().toString());  // signIn method using the user's entered email and password
                textViewStatus.setText("Status: Signed In");
            }
        });

        /**
         * buttonCreateLogin - button that calls the createAccount() method to create a new user account
         */
        buttonCreateLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("CIS3334", "Create Account ");  // creates a log for creating an account
                createAccount(editTextEmail.getText().toString(), editTextPassword.getText().toString());  // createAccount method using the user's entered email and password
                textViewStatus.setText("Status: User Created");
            }
        });

        /**
         * buttonGoogleLogin - button that calls the googleSignIn() method to allow users to sign in using Google
         */
        buttonGoogleLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("CIS3334", "Google login ");  // creates a log for logging in with Google
                googleSignIn();  // googleSignIn method using the user's Google account
            }
        });

        /**
         * buttonSignOut - button that calls the signOut() method to allow the user to sign out of his account
         */
        buttonSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Log.d("CIS3334", "Logging out - signOut ");  // creates a log for signing out
                signOut();  // signOut method to sign out of account
                textViewStatus.setText("Status: Signed Out");
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            /**
             * omAuthStateChanged() - method to check if the state of the user's account has changed
             * Needed for Firebase authentication
             * @param firebaseAuth the Firebase authorization object
             */
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    //Log.d("CIS3334", "onAuthStateChanged:signed_in:" + user.getUid());   // creates a log that the user is currently logged in
                } else {
                    // User is signed out
                    //Log.d("CIS3334", "onAuthStateChanged:signed_out");  // creates a log that the user is currently signed out
                }
                // ...
            }
        };

    }

    /**
     * onStart() - with onStart(), the user can see the activity on-screen, though it may not be in the foreground and interacting with the user.
     * Called again after if app has called onPause() or onStop()
     */
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /**
     * onStop() - when called, the app is no longer visible to the user
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * createAccount() - method to create a new user account using an email and password
     * Needed for Firebase authentication
     *
     * @param email the user's email address
     * @param password the user's password
     */
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                // listener for the authorization result task
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    // onComplete() method to check if authorization task has been completed successfully or not
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d("CIS3334", "createUserWithEmail:onComplete:" + task.isSuccessful());  // create log displaying that account was created successfully

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            /*Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                        }

                        // ...
                    }
                });

    }

    /**
     * signIn() - method to allow an already established user to login to their account using their
     * email and password
     * Needed for Firebase authentication
     *
     * @param email the user's email address
     * @param password the user's password
     */
    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                // listener for the authorization result task
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Log.d("CIS3334", "signInWithEmail:onComplete:" + task.isSuccessful());  // create log displaying that the account was signed-in to successfully

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            //Log.w("CIS3334", "signInWithEmail", task.getException());  // create log displaying that the account was not signed-in to successfully
                            /*Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();*/
                        }

                        // ...
                    }
                });

    }

    /**
     * signOut() - method to allow the user to sign out of his account
     */
    private void signOut () {
        mAuth.signOut();

    }

    /**
     * goggleSignIn() - method to allow the user to sign into his account using Google
     * Needed for Google authentication
     */
    private void googleSignIn() {

    }




}
