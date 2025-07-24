package com.teamone.auroraspa;


import android.content.SharedPreferences;
import android.os.Bundle;


import androidx.fragment.app.Fragment;


import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamone.auroraspa.databinding.FragmentSignInBinding;
import com.teamone.views.Button_Google_SignIn;
import com.teamone.views.Input_Password;
import com.teamone.auroraspa.utils.ValidationUtils;
import com.teamone.auroraspa.utils.RealTimeValidator;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentSignInBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;
    SharedPreferences preferences;




    public SignInFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.GONE);
        ((MainActivity) requireActivity()).binding.headerCart.getRoot().setVisibility(View.GONE);
        ((MainActivity) requireActivity()).binding.headerNormal.getRoot().setVisibility(View.GONE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        db= FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        display();
        setupValidation();
        // Đăng nhập Google
        binding.btnGoogle.setMode(Button_Google_SignIn.MODE_SIGN_IN);
        binding.btnGoogle.setFragment(this);
        binding.btnGoogle.setOnGoogleSignInResult(new Button_Google_SignIn.OnGoogleSignInResult() {
            @Override
            public void onSuccess(GoogleSignInAccount account) {
                // Đăng nhập thành công
                Toast.makeText(requireContext(), "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                com.teamone.auroraspa.utils.DialogUtils.autoShowDialogs(requireContext(), "ĐĂNG NHẬP THÀNH CÔNG", R.drawable.ic_login);
//                requireActivity().getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.container, new HomeFragment()).commit();
                ((MainActivity) requireActivity()).replaceFragClearBackStack(new HomeFragment());
                ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.VISIBLE);
                ((MainActivity) requireActivity()).binding.navBar1.setTrangChuSelected();
            }
            @Override
            public void onNotRegistered() {
                Toast.makeText(requireContext(), "Tài khoản Google chưa được đăng ký, vui lòng đăng ký!", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), "Đăng nhập Google thất bại: " + message, Toast.LENGTH_SHORT).show();
            }
        });


        addEvents();
        return binding.getRoot();
    }


    private void addEvents() {
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignUpFragment())
                        .addToBackStack(null).commit();
            }
        });
        binding.btnGoogle.setOnClickListener(v -> binding.btnGoogle.signIn());
    }


    private void display() {
        binding.inputAccount.txtLabel.setText("Email/ số điện thoại");
        binding.inputAccount.txtInput.setHint("Nhập email hoặc số điện thoại");
        binding.inputPassword.setLabel("Mật khẩu");
        binding.inputPassword.setHint("Nhập mật khẩu");
        binding.inputAccount.txtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                preferences = requireContext().getSharedPreferences("loginSave", requireContext().MODE_PRIVATE);
                boolean isRemembered = preferences.getBoolean("remember", false);
                String savedAccount = preferences.getString("input", "");
                String savedPassword = preferences.getString("password", "");


                // So khớp input với dữ liệu đã lưu
                if (isRemembered && s.toString().equals(savedAccount)) {
                    binding.inputPassword.setText(savedPassword);
                } else {
                    binding.inputPassword.setText("");
                }
            }


            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });


    }




    private void setupValidation() {
        RealTimeValidator.addEmailOrPhoneValidation(
                binding.inputAccount.txtInput,
                binding.inputAccount.txtError,
                new RealTimeValidator.ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String errorMessage) {
                        // Không cần updateSignInButtonState nữa
                    }
                }
        );
        RealTimeValidator.addPasswordValidation(
                binding.inputPassword.getInputView(),
                binding.inputPassword.getErrorView(),
                new RealTimeValidator.ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String errorMessage) {
                        // Không cần updateSignInButtonState nữa
                    }
                }
        );
    }


    private void signIn() {
        String input = binding.inputAccount.txtInput.getText().toString().trim();
        String password = binding.inputPassword.getInputText().trim();
        // Kiểm tra hợp lệ trước khi đăng nhập
        if (ValidationUtils.isEmpty(input) || !ValidationUtils.isValidEmailOrPhone(input)) {
            Toast.makeText(requireContext(), "Vui lòng nhập email hoặc số điện thoại hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ValidationUtils.isEmpty(password) || !ValidationUtils.isValidPassword(password)) {
            Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        // Lấy thông tin khi cần
        preferences = requireContext().getSharedPreferences("loginSave", requireContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if(binding.cbSave.isChecked()){
            editor.putString("input", input);
            editor.putString("password", password);
            editor.putBoolean("remember", true);
        } else {
            editor.remove("input");
            editor.remove("password");
            editor.putBoolean("remember", false);
        }
        editor.apply();




        // Nếu nhập là email
        if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            db.collection("customers")
                    .whereEqualTo("email", input)
                    .get()
                    .addOnSuccessListener(result -> {
                        if (!result.isEmpty()) {
                            auth.signInWithEmailAndPassword(input, password)
                                    .addOnCompleteListener(requireActivity(), task -> {
                                        if (task.isSuccessful()) {
                                            Log.d("signIn", "Đăng nhập thanh cong");
                                            com.teamone.auroraspa.utils.DialogUtils.autoShowDialogs(requireContext(), "ĐĂNG NHẬP THÀNH CÔNG",R.drawable.ic_login);
                                            // chuyen huong den man hinh chinh
//                                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                                            ((MainActivity) requireActivity()).replaceFragClearBackStack(new HomeFragment());
                                            ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.VISIBLE);
                                            ((MainActivity) requireActivity()).binding.navBar1.setTrangChuSelected();
                                        } else {
                                            Exception e = task.getException();
                                            Log.e("signIn", "Lỗi đăng nhập:", e);


                                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                                Toast.makeText(requireContext(), "Mật khẩu không đúng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                                preferences.edit().clear().apply();
                                            } else {
                                                Toast.makeText(requireContext(), "Lỗi đăng nhập: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(requireContext(), "Email chưa được đăng ký!", Toast.LENGTH_SHORT).show();
                            preferences.edit().clear().apply();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Lỗi kết nối đến server: " + e.getMessage(), Toast.LENGTH_SHORT).show();


                    });
        }else {
            // Nếu là số điện thoại
            db.collection("customers")
                    .whereEqualTo("phone", input)
                    .get()
                    .addOnSuccessListener(result -> {
                        if (!result.isEmpty()) {
                            String email = result.getDocuments().get(0).getString("email");
                            auth.signInWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(requireActivity(), task -> {
                                        if (task.isSuccessful()) {
                                            com.teamone.auroraspa.utils.DialogUtils.autoShowDialogs(requireContext(), "ĐĂNG NHẬP THÀNH CÔNG",R.drawable.ic_login);
                                            Log.d("signIn", "Đăng nhập thanh cong");
//                                            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                                            ((MainActivity) requireActivity()).replaceFragClearBackStack(new HomeFragment());
                                            ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.VISIBLE);
                                            ((MainActivity) requireActivity()).binding.navBar1.setTrangChuSelected();
                                        } else {
                                            Toast.makeText(requireContext(), "Mật khẩu không đúng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                                            preferences.edit().clear().apply();
                                        }
                                    });
                        } else {
                            Toast.makeText(requireContext(), "Số điện thoại chưa được đăng ký, vui lòng đăng ký", Toast.LENGTH_LONG).show();
                            preferences.edit().clear().apply();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.d("signIn", "Lỗi truy vấn: " + e.getMessage());
                        Toast.makeText(requireContext(), "Lỗi kết nối đến server: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }




}



