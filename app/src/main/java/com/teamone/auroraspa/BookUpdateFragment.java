package com.teamone.auroraspa;




import android.icu.text.SimpleDateFormat;
import android.os.Bundle;




import androidx.fragment.app.Fragment;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;




import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamone.adapters.BookAdapter;
import com.teamone.auroraspa.databinding.FragmentBookUpdateBinding;
import com.teamone.models.Book;
import com.teamone.models.BookItem;




import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;




public class BookUpdateFragment extends Fragment {




    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    private String mParam1;
    private String mParam2;
    private Book book;
    private FragmentBookUpdateBinding binding;
    private Timestamp bookDate = null;




    public BookUpdateFragment() {}




    public static BookUpdateFragment newInstance(String param1, String param2) {
        BookUpdateFragment fragment = new BookUpdateFragment();
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
        ((MainActivity) requireActivity()).showHeaderCart("ĐỔI LỊCH");
        ((MainActivity) requireActivity()).binding.headerCart.btnCart.setVisibility(View.GONE);
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookUpdateBinding.inflate(inflater, container, false);




        Bundle bundle = getArguments();
        if (bundle != null) {
            book = (Book) bundle.getSerializable("selectedBook");
        }




        loadData();
        addEvents();




        return binding.getRoot();
    }




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
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = sdf.format(date);




                binding.txtSelectedDate.setText(formattedDate);
                binding.txtSelectedDate.setTextSize(12);
            });
        });




        binding.btnConfirm.setOnClickListener(view -> {
            if (book == null) return;




            String dateStr = binding.txtSelectedDate.getText().toString();
            if (dateStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ngày!", Toast.LENGTH_SHORT).show();
                return;
            }




            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(dateStr);
                bookDate = new Timestamp(date); // Cập nhật lại từ text cho chắc chắn




                // Set trước khi push lên Firestore
                book.setBookDate(bookDate);


                // Lấy lại danh sách bookItems đã được chọn giờ từ adapter
                if (binding.lvbooking.getAdapter() instanceof BookAdapter) {
                    BookAdapter adapter = (BookAdapter) binding.lvbooking.getAdapter();
                    ArrayList<BookItem> updatedItems = new ArrayList<>();
                    updatedItems.addAll(adapter.getBookItems());
                    for (BookItem item : adapter.getBookItems()) {
                        Log.d("item time", item.getChosenTime().toDate().toString());
                    }
//                    for (BookItem item : adapter.getBookItems()) {
//                        Log.d("bookdate", String.valueOf(bookDate.toDate()));
//                        item.setChosenTime(bookDate);
//                        updatedItems.add(item);
//                    }
                    book.setBookItems(updatedItems);
                }




                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("book").document(book.getId())
                        .set(book)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                        });




            } catch (Exception e) {
                Toast.makeText(getContext(), "Định dạng ngày không hợp lệ!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }




    private void loadData() {
        if (book != null) {
            binding.customerName.setText(book.getCustomerName());
            binding.customerPhone.setText(book.getPhone());




            Timestamp chosenTime = book.getBookDate();
            if (chosenTime != null) {
                bookDate = chosenTime;
                binding.txtSelectedDate.setText(formatTimestamp(chosenTime));
            }




            ArrayList<BookItem> bookItems = book.getBookItems();
            BookAdapter adapter = new BookAdapter(requireContext(), bookItems, R.layout.lv_book_item, true, bookDate);
            binding.lvbooking.setAdapter(adapter);
        }
    }




    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }
}



