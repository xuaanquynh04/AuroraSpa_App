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
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamone.auroraspa.R;

public class Button_Google extends LinearLayout {

    public static final int MODE_SIGN_IN = 0;
    public static final int MODE_SIGN_UP = 1;

    private int mode = MODE_SIGN_IN;
    private Fragment fragment;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<Intent> signInLauncher;
    private OnCompleteListener<AuthResult> authResultListener;

    public interface OnGoogleSignUpResult {
        void onSuccess(GoogleSignInAccount account);
        void onAlreadyExists();
        void onError(String message);
    }

    public interface OnGoogleSignInResult {
        void onSuccess(GoogleSignInAccount account);
        void onNotRegistered();
        void onError(String message);
    }

    private OnGoogleSignUpResult onGoogleSignUpResult;
    private OnGoogleSignInResult onGoogleSignInResult;

    public void setOnGoogleSignUpResult(OnGoogleSignUpResult callback) {
        this.onGoogleSignUpResult = callback;
    }

    public void setOnGoogleSignInResult(OnGoogleSignInResult callback) {
        this.onGoogleSignInResult = callback;
    }

    public Button_Google(Context context) {
        super(context);
        init(context);
    }

    public Button_Google(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Button_Google(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                Log.d("ButtonGoogle", "Fragment hoặc googleSignInClient bị null");
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
            GoogleSignInAccount account = task.getResult(ApiException.class);
            String email = account.getEmail();

            if (email == null) {
                notifyError("Không thể lấy email từ tài khoản Google.");
                return;
            }

            FirebaseFirestore.getInstance()
                    .collection("customers")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        boolean userExists = !querySnapshot.isEmpty();

                        if (mode == MODE_SIGN_UP && userExists) {
                            if (onGoogleSignUpResult != null) {
                                onGoogleSignUpResult.onAlreadyExists();
                            }
                        } else if (mode == MODE_SIGN_IN && !userExists) {
                            if (onGoogleSignInResult != null) {
                                onGoogleSignInResult.onNotRegistered();
                            }
                        } else {
                            AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                            firebaseAuth.signInWithCredential(credential)
                                    .addOnCompleteListener(authTask -> {
                                        if (authTask.isSuccessful()) {
                                            if (mode == MODE_SIGN_UP && onGoogleSignUpResult != null) {
                                                onGoogleSignUpResult.onSuccess(account);
                                            } else if (mode == MODE_SIGN_IN && onGoogleSignInResult != null) {
                                                onGoogleSignInResult.onSuccess(account);
                                            }
                                        } else {
                                            String message = authTask.getException() != null
                                                    ? authTask.getException().getMessage()
                                                    : "Xác thực Google thất bại.";
                                            notifyError(message);
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        notifyError("Lỗi khi kiểm tra email trong Firestore: " + e.getMessage());
                    });

        } catch (ApiException e) {
            notifyError("Google sign-in thất bại: " + e.getMessage());
        }
    }

    private void notifyError(String message) {
        if (mode == MODE_SIGN_IN && onGoogleSignInResult != null) {
            onGoogleSignInResult.onError(message);
        } else if (mode == MODE_SIGN_UP && onGoogleSignUpResult != null) {
            onGoogleSignUpResult.onError(message);
        }
    }

    public void setAuthResultListener(OnCompleteListener<AuthResult> listener) {
        this.authResultListener = listener;
    }
}
