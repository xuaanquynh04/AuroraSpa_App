package com.teamone.auroraspa;


import android.icu.text.SimpleDateFormat;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.teamone.adapters.BookAdapter;
import com.teamone.auroraspa.databinding.FragmentBookViewBinding;
import com.teamone.models.Book;
import com.teamone.models.BookItem;


import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookViewFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Book book;
    FragmentBookViewBinding binding;


    public BookViewFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookViewFragment newInstance(String param1, String param2) {
        BookViewFragment fragment = new BookViewFragment();
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
        ((MainActivity) requireActivity()).showHeaderCart(book.getId());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookViewBinding.inflate(inflater, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            book = (Book) bundle.getSerializable("selectedBook");
        } else {
            Log.d("BookViewFragment", "No book data received in bundle.");
        }
        loadData();
        addEvents();


        return binding.getRoot();
    }


    private void addEvents() {
        binding.btnChange.setOnClickListener(view -> {
            BookUpdateFragment bookUpdateFragment = new BookUpdateFragment();


            // Gửi lại book sang fragment update
            Bundle bundle = new Bundle();
            bundle.putSerializable("selectedBook", book);
            bookUpdateFragment.setArguments(bundle);


            Log.d("id book", book.getId());


            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, bookUpdateFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }




    private void loadData() {
        if (book != null) {
            binding.customerName.setText(book.getCustomerName());
            binding.customerPhone.setText(book.getPhone());


            Timestamp chosenTime = book.getBookDate();
            if (chosenTime != null) {
                binding.txtSelectedDate.setText(formatTimestamp(chosenTime));
            }


            ArrayList<BookItem> bookItems = book.getBookItems();
            BookAdapter adapter = new BookAdapter(requireContext(), bookItems, R.layout.lv_book_item, false, book.getBookDate());
            binding.lvbooking.setAdapter(adapter);
            if (book.getBookStatus().equals("completed") || book.getBookStatus().equals("feedbacked")) {
                binding.btnChange.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            } else {
                binding.btnChange.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }
        }
    }




    private String formatTimestamp(Timestamp chosenTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(chosenTime.toDate());
    }
}

