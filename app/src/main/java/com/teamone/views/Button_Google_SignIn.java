package com.teamone.views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamone.auroraspa.R;

public class Button_Google_SignIn extends LinearLayout {
    public static final int MODE_SIGN_IN = 0;

    private int mode = MODE_SIGN_IN;
    private Fragment fragment;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<Intent> signInLauncher;
    private OnCompleteListener<AuthResult> authResultListener;

    public interface OnGoogleSignInResult {
        void onSuccess(GoogleSignInAccount account);
        void onNotRegistered();
        void onError(String message);
    }

    private OnGoogleSignInResult onGoogleSignInResult;

    public void setOnGoogleSignInResult(OnGoogleSignInResult callback) {
        this.onGoogleSignInResult = callback;
    }

    public Button_Google_SignIn(Context context) {
        super(context);
        init(context);
    }

    public Button_Google_SignIn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Button_Google_SignIn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.button_google, this, true);
        firebaseAuth = FirebaseAuth.getInstance();

        setOnClickListener(v -> {
            if (fragment != null && googleSignInClient != null) {
                signIn();
            } else {
                Log.d("ButtonGoogle", "Fragment or googleSignInClient is null");
            }
        });

        setClickable(true);
        setFocusable(true);
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(fragment.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso);

        signInLauncher = fragment.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        handleResult(result.getData());
                    }
                }
        );
    }

    public void signIn() {
        Log.d("104", "signIn");
        if (googleSignInClient != null) {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                signInLauncher.launch(signInIntent);
            });
        }
    }

    private void handleResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(com.google.android.gms.common.api.ApiException.class);
            String email = account.getEmail();
            FirebaseFirestore.getInstance()
                    .collection("customers")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        // Kiểm tra tồn tại
                        if (querySnapshot.isEmpty()) {
                            if (onGoogleSignInResult != null) onGoogleSignInResult.onNotRegistered();
                        } else {
                            // đăng nhập
                            Log.d("128", "signInWithCredential");
                            com.google.firebase.auth.AuthCredential credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(account.getIdToken(), null);
                            FirebaseAuth.getInstance().signInWithCredential(credential)
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            if (onGoogleSignInResult != null) onGoogleSignInResult.onSuccess(account);
                                            Log.d("134", "signInWithCredential:success");
                                        } else {
                                            if (onGoogleSignInResult != null) onGoogleSignInResult.onError(authTask.getException() != null ? authTask.getException().getMessage() : "Đăng nhập thất bại");
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (onGoogleSignInResult != null) onGoogleSignInResult.onError("Lỗi kiểm tra Firestore: " + e.getMessage());
                    });
        } catch (com.google.android.gms.common.api.ApiException e) {
            if (onGoogleSignInResult != null) onGoogleSignInResult.onError(e.getMessage());
        }
    }

    public void setAuthResultListener(OnCompleteListener<AuthResult> listener) {
        this.authResultListener = listener;
    }
}
