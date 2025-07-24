package com.teamone.auroraspa;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.teamone.auroraspa.databinding.FragmentInfoUpBinding;
import com.teamone.auroraspa.databinding.InputTextBinding;
import com.teamone.models.Customer;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoUpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoUpFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AlertDialog successDialog;
    private AlertDialog loadingDialog;


    public InfoUpFragment() {
        // Required empty public constructor
    }
    FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db;
    private String currentUserId;


    FragmentInfoUpBinding binding;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InfoUpFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoUpFragment newInstance(String param1, String param2) {
        InfoUpFragment fragment = new InfoUpFragment();
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
        ((MainActivity) requireActivity()).showHeaderCart("CẬP NHẬT THÔNG TIN");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInfoUpBinding.inflate(inflater, container, false);
        db = FirebaseFirestore.getInstance();


        // Set lable for inputext from <include>
        binding.includeEditusername.txtLabel.setText("Tên tài khoản");
        binding.includeEditgender.txtLabel.setText("Giới tính");
        binding.includeEditphonenumb.txtLabel.setText("Số điện thoại");
        binding.includeEditemail.txtLabel.setText("Email");
        binding.includeEditemail.txtInput.setHint("Nhập email");
        binding.includeEditphonenumb.txtInput.setHint("Nhập số điện thoại");
        binding.includeEditgender.txtInput.setHint("Nhập giới tính");
        binding.includeEditusername.txtInput.setHint("Nhập tên tài khoản");


        // Set clickable to edtDOB
        binding.edtDOb.setFocusable(false);
        binding.edtDOb.setClickable(true);


        addEvents();
        loadCustomerdata();
        return binding.getRoot();
    }


    private void loadCustomerdata() {
        if (customer != null) {
            currentUserId = customer.getUid();
            Log.d("InfoUpFragment", "User logged in, using ID: " + currentUserId);
        } else {
            currentUserId = "5IDjX2OG0Nc0Gn4mdjkaf9sB8sa2"; // ID demo của bạn
            Log.d("InfoUpFragment", "No user logged in, using demo ID: " + currentUserId);
        }
        DocumentReference userRef = db.collection("customers").document(currentUserId);


        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {


                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phone = documentSnapshot.getString("phone");
                String dob = documentSnapshot.getString("dob");
                String gender = documentSnapshot.getString("gender");


                binding.txtUsername.setText(name);


                // EditText trong các include layouts
                binding.includeEditusername.txtInput.setText(name);
                binding.edtDOb.setText(dob);
                binding.includeEditgender.txtInput.setText(gender);
                binding.includeEditphonenumb.txtInput.setText(phone);
                binding.includeEditemail.txtInput.setText(email);


                Toast.makeText(getContext(), "Tải dữ liệu người dùng thành công!", Toast.LENGTH_SHORT).show();


            } else {
                Log.e("InfoUpFragment", "Dữ liệu người dùng null mặc dù tài liệu tồn tại.");
                Toast.makeText(getContext(), "Lỗi: Không thể đọc dữ liệu người dùng.", Toast.LENGTH_LONG).show();
            }
        });


    }


    private void addEvents() {
        binding.btnChangepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PwUpFragment pwupfragment = new PwUpFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, pwupfragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserdata();
            }
        });


        binding.edtDOb.setOnClickListener(new View.OnClickListener() {
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
                    binding.edtDOb.setText(formattedDate);
                    binding.edtDOb.setTextSize(12);
                });
            }
        });
    }


    private void updateUserdata() {
        showLoadingDialog();


        String newName = binding.includeEditusername.txtInput.getText().toString().trim();
        String newDob = binding.edtDOb.getText().toString().trim();
        String newPhone = binding.includeEditphonenumb.txtInput.getText().toString().trim();
        String newEmail = binding.includeEditemail.txtInput.getText().toString().trim();
        String newGender = binding.includeEditgender.txtInput.getText().toString().trim();


        Map<String,Object> updates = new HashMap<>();
        updates.put("name", newName);
        updates.put("dob", newDob);
        updates.put("phone", newPhone);
        updates.put("email", newEmail);
        updates.put("gender", newGender);


        DocumentReference userRef = db.collection("customers").document(currentUserId);
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    hideLoadingDialog(); // Ẩn loading dialog
                    showSuccessDialog(); // Hiển thị success dialog


                    // Sử dụng Handler để trì hoãn việc đóng success dialog và tải lại dữ liệu
                    binding.getRoot().postDelayed(() -> {
                        if (successDialog != null && successDialog.isShowing()) {
                            successDialog.dismiss(); // Đảm bảo đóng dialog thành công
                        }
                        loadCustomerdata(); // Tải lại dữ liệu sau khi cập nhật thành công
                    }, 2500); // 1.5 giây
                })
                .addOnFailureListener(e -> {
                    hideLoadingDialog(); // Ẩn loading dialog ngay cả khi thất bại
                    Toast.makeText(getContext(), "Lỗi khi cập nhật thông tin: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("InfoUpFragment", "Lỗi khi cập nhật thông tin: ", e);
                });
    }


    private void showLoadingDialog() {
        if (loadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // Sử dụng requireContext()
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_loading, null); // Đảm bảo dialog_loading.xml tồn tại
            builder.setView(dialogView);
            builder.setCancelable(false); // Không cho phép hủy dialog
            loadingDialog = builder.create();
            // Đặt nền trong suốt nếu muốn dialog không có nền màu mặc định
            if (loadingDialog.getWindow() != null) {
                loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            }
        }
        if (!loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }


    private void hideLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null; // Đặt về null sau khi dismiss để tạo lại lần sau nếu cần (tùy chọn)
        }
    }


    private void showSuccessDialog() {
        if (successDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_announcement, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            successDialog = builder.create();
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER);
            successDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        }
        if (!successDialog.isShowing()) {
            successDialog.show();
        }
    }


    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    c.set(Calendar.YEAR, selectedYear);
                    c.set(Calendar.MONTH, selectedMonth);
                    c.set(Calendar.DAY_OF_MONTH, selectedDay);


                    String format = "dd/MM/yyyy";
                    SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());


                    // >>> Đặt ngày đã chọn vào edtDOb TRỰC TIẾP <<<
                    binding.edtDOb.setText(sdf.format(c.getTime()));
                },
                year, month, day);
        datePickerDialog.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        if (loadingDialog != null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        if (successDialog != null && successDialog.isShowing()){
            successDialog.dismiss();
        }
    }


}

