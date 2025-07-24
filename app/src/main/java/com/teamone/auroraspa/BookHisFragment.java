package com.teamone.auroraspa;


import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamone.adapters.BookHisAdapter;
import com.teamone.auroraspa.databinding.FragmentBookHisBinding;
import com.teamone.models.Book;


import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookHisFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookHisFragment extends Fragment
        implements BookHisAdapter.OnBookViewClickListener,
        BookHisAdapter.OnBookView2ClickListener,
        BookHisAdapter.OnFeedbackClickListener,
        BookHisAdapter.OnChangeBookClickListener,
        BookHisAdapter.OnCancelBookClickListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private BookHisAdapter bookAdapter;
    private ArrayList<Book> bookList;
    private Object listViewBooks;
    private ListenerRegistration bookingListenerRegistration;
    FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();


    public BookHisFragment() {
        // Required empty public constructor
    }
    FragmentBookHisBinding binding;
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BookHisFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookHisFragment newInstance(String param1, String param2) {
        BookHisFragment fragment = new BookHisFragment();
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
        bookList = new ArrayList<>();
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showHeaderCart("LỊCH CỦA TÔI");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookHisBinding.inflate(inflater, container, false);


        listViewBooks = binding.lvBookhis;
        bookAdapter = new BookHisAdapter(getContext(), R.layout.item_book_his, bookList);
        bookAdapter.setBookViewListener(this);
        bookAdapter.setFeedbackListener(this);
        bookAdapter.setChangeBookListener(this);
        bookAdapter.setCancelBookListener(this);


        binding.lvBookhis.setAdapter(bookAdapter);


        loadBookingHistory();
        return binding.getRoot();
    }


    private void loadBookingHistory() {
//        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();
//        if(currentuser != null){
//            String customerID = currentuser.getUid();
//        }
        String customerID = customer.getUid();
        Log.d("customerID 141", customerID);
//        customerID = "5IDjX2OG0Nc0Gn4mdjkaf9sB8sa2";


        if (customerID != null) {
            Query query = db.collection("book")
                    .whereEqualTo("customerID", customerID);


            // Hủy listener cũ nếu có để tránh nhiều listener cùng chạy
            if (bookingListenerRegistration != null) {
                bookingListenerRegistration.remove();
            }
            if (bookingListenerRegistration != null) {
                bookingListenerRegistration.remove();
            }


            bookingListenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot snapshots,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w("BookHisFragment", "Listen failed.", e);
                        // Có thể hiển thị thông báo lỗi cho người dùng
                        return;
                    }
                    if (snapshots != null) {
                        bookList.clear(); // Xóa danh sách cũ trước khi thêm dữ liệu mới từ snapshot
                        for (QueryDocumentSnapshot document : snapshots) {
                            Book book = document.toObject(Book.class);
                            book.setId(document.getId()); // Quan trọng: set ID cho đối tượng Book
                            bookList.add(book);
                            Log.d("BookHisFragment", "Realtime update: Added/Modified book - " + document.getId());
                        }
                        // Thông báo cho adapter rằng dữ liệu đã thay đổi
                        if (bookAdapter != null) {
                            bookAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d("BookHisFragment", "Current data: null");
                    }
                }
            });


        }
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onBookViewClick(Book book) {
        BookViewFragment bookViewFragment = new BookViewFragment();
        Bundle bundle = new Bundle();


        bundle.putSerializable("selectedBook", book);
        Log.d("id book", book.getId());
        bookViewFragment.setArguments(bundle);


        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, bookViewFragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onBookView2Click(Book book) {
        BookViewFragment bookViewFragment = new BookViewFragment();
        Bundle bundle = new Bundle();


        bundle.putSerializable("selectedBook", book);
        bookViewFragment.setArguments(bundle);


        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, bookViewFragment)
                .addToBackStack(null)
                .commit();


    }


    @Override
    public void onFeedbackClick(Book book) {
        FeedbackFragment feedbackViewFragment = new FeedbackFragment();
        Bundle bundle = new Bundle();


        bundle.putSerializable("bookToFeedback", book);
        feedbackViewFragment.setArguments(bundle);


        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, feedbackViewFragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onChangeBookClick(Book book) {
        BookUpdateFragment bookViewFragment = new BookUpdateFragment();
        Bundle bundle = new Bundle();


        bundle.putSerializable("selectedBook", book);
        bookViewFragment.setArguments(bundle);


        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, bookViewFragment)
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void onCancelBookClick(Book book) {


        if (book != null && book.getId() != null){
            db.collection("book").document(book.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        bookList.remove(book);
                        bookAdapter.notifyDataSetChanged();
                        loadBookingHistory();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("BookHisFragment","Error deleting book",e);
                    });
        };
    }
}



