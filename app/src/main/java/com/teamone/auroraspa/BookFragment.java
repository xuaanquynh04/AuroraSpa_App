package com.teamone.auroraspa;




import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;




import androidx.fragment.app.Fragment;




import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamone.adapters.BookAdapter;
import com.teamone.auroraspa.databinding.FragmentBookBinding;
import com.teamone.models.Book;
import com.teamone.models.BookItem;
import com.teamone.models.CartItem;
import com.teamone.models.Customer;




import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;




public class BookFragment extends Fragment {
    private FragmentBookBinding binding;
    private ArrayList<BookItem> booking;
    private BookAdapter adapter;
    private ArrayList<CartItem> selectedItems;
    private Timestamp bookDate;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();
    Customer customerInfor = new Customer();
    private double totalAmount = 0;




    public BookFragment() {}




    public static BookFragment newInstance(String param1, String param2) {
        BookFragment fragment = new BookFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showHeaderCart("XÁC NHẬN ĐƠN HÀNG");
        ((MainActivity) requireActivity()).binding.headerCart.btnCart.setVisibility(View.GONE);
        ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBookBinding.inflate(inflater, container, false);
        checkLogin();
        if (getArguments() != null) {
            selectedItems = (ArrayList<CartItem>) getArguments().getSerializable("selectedCartItems");
        }
        loadData();
        addEvents();
        return binding.getRoot();
    }


    private void checkLogin() {
        if (customer != null) {
            String userID = customer.getUid().trim();
            Log.d("TAG", "checkLogin: " + userID);
            db.collection("customers")
                    .document(userID)  //
                    .get()
                    .addOnSuccessListener(document -> {
                        Log.d("dong 1 2 3 ", "Truy vấn document với UID: " + userID);
                        if (document.exists()) {
                            customerInfor = document.toObject(Customer.class);
                            loadData();
                            Log.d("dòng 125", "Tìm thấy document với UID: " + customerInfor.getId() +customerInfor.getName() );
                        } else {
                            Log.d("Customer", "Không tìm thấy document với UID: " + userID);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Customer", "Lỗi khi truy vấn: " + e.getMessage());
                    });
        }else{
            requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SignInFragment()).commit();
        }};


    private void addEvents() {
        binding.datePickerContainer.setOnClickListener(view -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Chọn ngày đặt lịch")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTheme(R.style.ThemeOverlay_AuroraSpa_MaterialDatePicker)
                    .build();
            picker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
            picker.addOnPositiveButtonClickListener(selection -> {
                Date date = new Date(selection);
                bookDate = new Timestamp(date);
                if (adapter != null) {
                    adapter.setBookDate(bookDate);
                }
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
                binding.txtSelectedDate.setText(formattedDate);
                binding.txtSelectedDate.setTextSize(12);
            });
        });
        binding.btnPay.setOnClickListener(view -> {
            Timestamp bookDate = getSelectedBookDate();
            if (bookDate == null) return;
            Book book = new Book(
                    customerInfor.getId(),
                    customerInfor.getName(),
                    customerInfor.getPhone(),
                    null,// addTime: sẽ set khi đặt lịch thành công
                    bookDate,
                    booking,
                    null, // paymentMethod: chưa có
                    null, // status: chưa booked
                    totalAmount
            );
            Bundle bundle = new Bundle();
            bundle.putSerializable("bookInfo", book);
            Fragment payFragment = new PayFragment();
            payFragment.setArguments(bundle);
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.container, payFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void loadData() {
        binding.customerName.setText(customerInfor.getName());
        binding.customerPhone.setText(customerInfor.getPhone());
        binding.customerEmail.setText(customerInfor.getEmail());
        booking = new ArrayList<>();
        totalAmount = 0;
        if (selectedItems != null) {
            for (CartItem item : selectedItems) {
                BookItem bookItem = new BookItem(item, null);
                booking.add(bookItem);


                if (item.getFinalPrice() != null) {
                    totalAmount += item.getFinalPrice();
                }
            }
        }


        adapter = new BookAdapter(requireContext(), booking, R.layout.lv_book_item, true, bookDate);
        binding.lvbooking.setAdapter(adapter);


        binding.totalAmount.setText(formatCurrency(totalAmount));
    }




    private Timestamp getSelectedBookDate() {
        String selectedDateStr = binding.txtSelectedDate.getText().toString().trim();
        if (selectedDateStr.isEmpty() || selectedDateStr.equals("Chọn ngày")) {
            Toast.makeText(requireContext(), "Vui lòng chọn ngày đặt lịch!", Toast.LENGTH_SHORT).show();
            return null;
        }




        try {
            Date parsedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(selectedDateStr);
            return new Timestamp(parsedDate);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
            return null;
        }
    }




    private String formatCurrency(double value) {
        return String.format("%,.0f", value).replace(",", ".");
    }
}



