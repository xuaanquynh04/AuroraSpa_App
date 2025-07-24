package com.teamone.auroraspa;


import android.content.Intent;
import android.os.Bundle;


import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamone.auroraspa.databinding.FragmentSignUpBinding;
import com.teamone.auroraspa.utils.ValidationUtils;
import com.teamone.auroraspa.utils.RealTimeValidator;
import com.teamone.models.Customer;
import com.teamone.views.Button_Google;
import com.teamone.views.Input_Password;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentSignUpBinding binding;
    FirebaseFirestore db;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN =100;
    // CheckBox references
    private CheckBox cbMale, cbFemale, cbOther;


    public SignUpFragment() {
        // Required empty public constructor
    }




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        db= FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        display();
        gender();
        setupValidation();
        setupSignUpGoogle();
        addEvents();
        return binding.getRoot();
    }


    private void setupSignUpGoogle() {
        binding.btnGoogle.setFragment(this);
        binding.btnGoogle.setMode(Button_Google.MODE_SIGN_UP);
        binding.btnGoogle.setOnGoogleSignUpResult(new Button_Google.OnGoogleSignUpResult() {
            @Override
            public void onSuccess(com.google.android.gms.auth.api.signin.GoogleSignInAccount account) {
                // Đăng ký thành công, tạo dòng Firestore
                saveUserToDatabase(account);
            }
            @Override
            public void onAlreadyExists() {
                Toast.makeText(requireContext(), "Tài khoản Google đã đăng ký, vui lòng đăng nhập!", Toast.LENGTH_LONG).show();
            }
            @Override
            public void onError(String message) {
                Toast.makeText(requireContext(), "Đăng ký thất bại: " + message, Toast.LENGTH_SHORT).show();
            }
        });
        ;
    }


    private void addEvents() {
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment()).commit();
            }
        });
        binding.inputDob.txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                        .setTitleText("Chọn ngày sinh")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .setTheme(R.style.ThemeOverlay_AuroraSpa_MaterialDatePicker)
                        .build();


                picker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
                picker.addOnPositiveButtonClickListener(selection -> {
                    Date date = new Date(selection);
                    String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
                    binding.inputDob.txtDate.setText(formattedDate);
                    binding.inputDob.txtDate.setTextSize(12);
                });
            }
        });




        binding.btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnGoogle.signIn();
            }
        });




    }




    private void signUp() {
        String name = binding.inputName.txtInput.getText().toString().trim();
        String email = binding.inputEmail.txtInput.getText().toString();
        String phone = binding.inputPhone.txtInput.getText().toString().trim();
        String gender = "rong";
        if (binding.cbMale.isChecked()) {
            gender = "Nam";
        } else if (binding.cbFemale.isChecked()) {
            gender = "Nữ";
        } else if (binding.cbOther.isChecked()) {
            gender = "Khác";
        }
        final String finalGender = gender;
        String dob = binding.inputDob.txtDate.getText().toString();
        Input_Password inputPassword = binding.inputPassword;
        String pass= inputPassword.getInputText();
        String confirmPass = binding.inputConfirmPassword.getInputText();
        // Kiểm tra hợp lệ trước khi đăng ký
        if (ValidationUtils.isEmpty(name) || !ValidationUtils.isValidName(name)) {
            Toast.makeText(requireContext(), "Vui lòng nhập họ tên hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ValidationUtils.isEmpty(email) || !ValidationUtils.isValidEmail(email)) {
            Toast.makeText(requireContext(), "Vui lòng nhập email hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ValidationUtils.isEmpty(phone) || !ValidationUtils.isValidPhone(phone)) {
            Toast.makeText(requireContext(), "Vui lòng nhập số điện thoại hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ValidationUtils.isEmpty(pass) || !ValidationUtils.isValidPassword(pass)) {
            Toast.makeText(requireContext(), "Vui lòng nhập mật khẩu hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ValidationUtils.isEmpty(confirmPass) || !pass.equals(confirmPass)) {
            Toast.makeText(requireContext(), "Mật khẩu nhập lại không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("signUp", "email: " + email);
        Log.d("signUp", "pw: " + pass);
        db.collection("customers")
                .whereEqualTo("phone", phone)
                .get()
                .addOnSuccessListener(result -> {
                    if (!result.isEmpty()){
                        Toast.makeText(getContext(), "Số điện thoại đã được đăng ký, vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                    }else{
                        auth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener(requireActivity(), task -> {
                                    if (task.isSuccessful()) {
                                        // Đăng ký thành cong
                                        Log.d("signUp", "signUp: success");
                                        FirebaseUser currenCustomer = FirebaseAuth.getInstance().getCurrentUser();
                                        String uid = currenCustomer.getUid();
                                        Customer customer = new Customer(uid, name, email, phone, finalGender, dob, pass);
                                        saveUsertoDatabasenormal(customer);
                                    }Exception e = task.getException();
                                    Log.e("signUp", "Lỗi đăng ký: ", e);


                                    if (e instanceof FirebaseAuthUserCollisionException) {
                                        // Trùng email
                                        Toast.makeText(requireContext(), "Email đã được đăng ký, vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                                    } else if (e != null) {
                                        Toast.makeText(requireContext(), "Đăng ký thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    } else {
//                                        Toast.makeText(requireContext(), "Đăng ký thất bại: Lỗi không xác định", Toast.LENGTH_SHORT).show();
                                        Log.d("signup", "test");
                                    }
                                });
                    }
                });
    }
    private void saveUsertoDatabasenormal(Customer customer) {
        FirebaseFirestore.getInstance()
                .collection("customers")
                .document(customer.getId())
                .set(customer)
                .addOnSuccessListener(aVoid -> {
                    Log.d("signUp", "Lưu thông tin thành cong!");
                    com.teamone.auroraspa.utils.DialogUtils.autoShowDialogs(requireContext(), "ĐĂNG KÝ THÀNH CÔNG",R.drawable.ic_account);
                    // chuyen huong den man hinh chinh
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                    ((MainActivity) requireActivity()).replaceFragClearBackStack(new HomeFragment());
                    ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.VISIBLE);
                    ((MainActivity) requireActivity()).binding.navBar1.setTrangChuSelected();
                })
                .addOnFailureListener(e -> {
                    Log.d("signUp", "Lưu thông tin không thành công!");
                });


    }


    private void saveUserToDatabase(com.google.android.gms.auth.api.signin.GoogleSignInAccount account) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String name = account.getDisplayName() != null ? account.getDisplayName() : "";
        String email = account.getEmail() != null ? account.getEmail() : "";
        // Có thể bổ sung các trường khác nếu cần
        Customer customer = new Customer(uid, name, email, "", "", "", "");
        FirebaseFirestore.getInstance()
                .collection("customers")
                .document(uid)
                .set(customer)
                .addOnSuccessListener(aVoid -> {
                    Log.d("signUp", "Lưu thông tin thành cong!");
                    com.teamone.auroraspa.utils.DialogUtils.autoShowDialogs(requireContext(), "ĐĂNG KÝ THÀNH CÔNG",R.drawable.ic_account);
                    // chuyen huong den man hinh chinh
//                    requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
                    ((MainActivity) requireActivity()).replaceFragClearBackStack(new HomeFragment());
                    ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.VISIBLE);
                    ((MainActivity) requireActivity()).binding.navBar1.setTrangChuSelected();
                })
                .addOnFailureListener(e -> {
                    Log.d("signUp", "Lưu thông tin không thành công!");
                });
    }


    private void gender() {
        binding.cbMale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.cbFemale.setChecked(false);
                binding.cbOther.setChecked(false);
            }
        });
        binding.cbFemale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.cbMale.setChecked(false);
                binding.cbOther.setChecked(false);
            }
        });
        binding.cbOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.cbMale.setChecked(false);
                binding.cbFemale.setChecked(false);
            }
        });
    }


    private void display() {
        binding.inputName.txtLabel.setText("Họ và tên");
        binding.inputName.txtInput.setHint("Nhập họ và tên");
        binding.inputEmail.txtLabel.setText("Email");
        binding.inputEmail.txtInput.setHint("Nhập email");
        binding.inputPhone.txtLabel.setText("Số điện thoại");
        binding.inputPhone.txtInput.setHint("Nhập số điện thoại");
        binding.inputPassword.setLabel("Mật khẩu");
        binding.inputPassword.setHint("Nhập mật khẩu");
        binding.inputConfirmPassword.setLabel("Nhập lại mật khẩu");
        binding.inputConfirmPassword.setHint("Nhập lại mật khẩu");
        //
    }


    private void setupValidation() {
        // Thêm real-time validation cho tên
        RealTimeValidator.addNameValidation(
                binding.inputName.txtInput,
                binding.inputName.txtError,
                new RealTimeValidator.ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String errorMessage) {
                    }
                }
        );


        // Thêm real-time validation cho email
        RealTimeValidator.addEmailValidation(
                binding.inputEmail.txtInput,
                binding.inputEmail.txtError,
                new RealTimeValidator.ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String errorMessage) {
                    }
                }
        );


        // Thêm real-time validation cho số điện thoại
        RealTimeValidator.addPhoneValidation(
                binding.inputPhone.txtInput,
                binding.inputPhone.txtError,
                new RealTimeValidator.ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String errorMessage) {
                    }
                }
        );


        // Thêm real-time validation cho mật khẩu
        RealTimeValidator.addPasswordValidation(
                binding.inputPassword.getInputView(),
                binding.inputPassword.getErrorView(),
                new RealTimeValidator.ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String errorMessage) {
                    }
                }
        );


        // Thêm real-time validation cho nhập lại mật khẩu
        RealTimeValidator.addConfirmPasswordValidation(
                binding.inputPassword.getInputView(),
                binding.inputConfirmPassword.getInputView(),
                binding.inputConfirmPassword.getErrorView(),
                new RealTimeValidator.ValidationCallback() {
                    @Override
                    public void onValidationResult(boolean isValid, String errorMessage) {
                    }
                }
        );


    }


}



