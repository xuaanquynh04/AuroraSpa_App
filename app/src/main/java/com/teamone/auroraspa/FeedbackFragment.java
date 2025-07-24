package com.teamone.auroraspa;




import android.os.Bundle;




import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;




import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;




import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teamone.adapters.FeedbackItemAdapter;
import com.teamone.auroraspa.databinding.FragmentFeedbackBinding;
import com.teamone.models.Book;
import com.teamone.models.BookItem;
import com.teamone.models.Feedback;




import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;




/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackFragment extends Fragment implements FeedbackItemAdapter.OnFeedbackStatusChangeListener{




    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";




    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final String ARG_BOOK_DATA = "bookToFeedback";
    private String customerID;

    public FeedbackFragment() {
        // Required empty public constructor
    }
    private FeedbackItemAdapter feedbackItemAdapter;
    private ArrayList<BookItem> bookItemsToFeedback;
    private Book receivedBook;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    FragmentFeedbackBinding binding;
    private AlertDialog successDialog;
    private AlertDialog loadingDialog;

    private AtomicInteger successCount;
    private AtomicInteger failedCount;
    private int totalFeedbacksToSubmit;
    FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedbackFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedbackFragment newInstance(String param1, String param2) {
        FeedbackFragment fragment = new FeedbackFragment();
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
//        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();




        bookItemsToFeedback = new ArrayList<>();
        if (getArguments() != null) {
            receivedBook = (Book) getArguments().getSerializable("bookToFeedback");
            // KIỂM TRA NULL AN TOÀN HƠN
            if (receivedBook != null && receivedBook.getBookItems() != null && !receivedBook.getBookItems().isEmpty()) {
                bookItemsToFeedback.addAll(receivedBook.getBookItems());
                Log.d("FeedbackFragment", "Received book with ID: " + receivedBook.getId() + " and " + bookItemsToFeedback.size() + " book items.");
            } else {
                Log.w("FeedbackFragment", "No book or book items received for feedback. Book ID: " + (receivedBook != null ? receivedBook.getId() : "null"));
                // Có thể muốn quay lại màn hình trước hoặc hiển thị thông báo lỗi
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showHeaderCart(receivedBook.getId());
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFeedbackBinding.inflate(inflater, container, false);
        ListView listViewFeedbackItems = binding.lvFeedbacklist;
        if (bookItemsToFeedback != null && !bookItemsToFeedback.isEmpty()) {
            feedbackItemAdapter = new FeedbackItemAdapter(getContext(), R.layout.item_feedback_product, bookItemsToFeedback);
            feedbackItemAdapter.setOnFeedbackStatusChangeListener(this);
            listViewFeedbackItems.setAdapter(feedbackItemAdapter);
            listViewFeedbackItems.setVisibility(View.VISIBLE);
            binding.btnSubmit.setVisibility(View.VISIBLE);
            updateSubmitButtonState();
        } else {
            listViewFeedbackItems.setVisibility(View.GONE);
            binding.btnSubmit.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Không có dịch vụ nào để đánh giá.", Toast.LENGTH_LONG).show();
            Log.d("FeedbackFragment", "bookItemsToFeedback is empty, hiding list and button.");
        }
        customerID = customer.getUid();
        Log.d("FeedbackFragment", "Customer ID: " + customerID);
        feedbackItemAdapter.setCustomerID(customerID);
        addEvents();
        return binding.getRoot();
    }




    @Override
    public void onFeedbackStatusChanged() {
        updateSubmitButtonState();
    }


    private void updateSubmitButtonState() {
        if(feedbackItemAdapter != null){
            boolean allFeedbacked = feedbackItemAdapter.areAllItemsEnabled();
            binding.btnSubmit.setEnabled(allFeedbacked);
            if(allFeedbacked){
                binding.btnSubmit.setAlpha(1.0f);
            } else {
                binding.btnSubmit.setAlpha(0.5f);
            }
        }
    }


    private void addEvents() {
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(feedbackItemAdapter != null && feedbackItemAdapter.areAllItemsFeedbacked()){
                    Log.d("dong 194", "check");
                    submitFeedback();
                } else {
                    Toast.makeText(getContext(), "Vui lòng đánh giá tất cả dịch vụ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void submitFeedback() {
        Log.d("dong 205", "cehck");
        ArrayList<Feedback> collectedFeedbacks = feedbackItemAdapter.getCollectedFeedbacks(customerID);
        successCount = new AtomicInteger(0);
        failedCount = new AtomicInteger(0);
        totalFeedbacksToSubmit = collectedFeedbacks.size();
        Log.d("dong 210", totalFeedbacksToSubmit + "");


        if (totalFeedbacksToSubmit == 0) {
            Log.d("dong 211", String.valueOf(totalFeedbacksToSubmit));
            Toast.makeText(getContext(), "Không có đánh giá nào để gửi.", Toast.LENGTH_SHORT).show();
            return;
        }


        showLoadingDialog();


        for (Feedback feedback : collectedFeedbacks) {
            if (feedback.getProductID() == null || feedback.getProductID().isEmpty()) {
                Log.e("FeedbackFragment", "Feedback thiếu productID, bỏ qua.");
                failedCount.incrementAndGet();
                Log.d("dong 227", String.valueOf(failedCount.get()));
                checkALlFeedbackCompleted();
                continue;
            }


            // Truy vấn sản phẩm theo productID
            db.collection("products")
                    .whereEqualTo("productID", feedback.getProductID())
                    .limit(1)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            DocumentSnapshot productDoc = querySnapshot.getDocuments().get(0);
                            String docId = productDoc.getId();


                            // Dùng FieldValue.arrayUnion để thêm vào danh sách feedbacks (không ghi đè cũ)
                            db.collection("products").document(docId)
                                    .update("feedbacks", com.google.firebase.firestore.FieldValue.arrayUnion(feedback))
                                    .addOnSuccessListener(unused -> {


                                        successCount.incrementAndGet();
                                        checkALlFeedbackCompleted();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Lỗi khi thêm feedback: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e("FeedbackFragment", "Lỗi update feedbacks: ", e);
                                        failedCount.incrementAndGet();
                                        checkALlFeedbackCompleted();
                                    });


                        } else {
                            Log.e("FeedbackFragment", "Không tìm thấy sản phẩm với productID: " + feedback.getProductID());
                            failedCount.incrementAndGet();
                            checkALlFeedbackCompleted();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FeedbackFragment", "Lỗi truy vấn sản phẩm: " + feedback.getProductID(), e);
                        failedCount.incrementAndGet();
                        checkALlFeedbackCompleted();
                    });
        }
    }


    private void checkALlFeedbackCompleted() {
        int currentTotalProcessed = successCount.get() + failedCount.get();
        Log.d("dong 277", String.valueOf(successCount.get()));
        if (currentTotalProcessed == totalFeedbacksToSubmit) {
            hideLoadingDialog();
            if(successCount.get()>0){
                updateBookStatusToFeedbacked();
            } else {
                Log.d("dong 279", "fail");
                Toast.makeText(getContext(), "Không có đánh giá nào để gửi.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateBookStatusToFeedbacked() {
        if(receivedBook == null || receivedBook.getId() == null){
            Log.e("FeedbackFragment","Cannot update book status");
            return;
        }
        db.collection("book").document(receivedBook.getId())
                .update("bookStatus", "feedbacked")
                .addOnSuccessListener(aVoid -> {
                    Log.d("FeedbackFragment", "Book status updated to 'feedbacked' for Book ID: " + receivedBook.getId());
                    showSuccessDialog();


                    binding.getRoot().postDelayed(() -> {
                        if (successDialog != null && successDialog.isShowing()) {
                            successDialog.dismiss();
                        }
                        if (getParentFragmentManager() != null) {
                            getParentFragmentManager().popBackStack();
                        }
                    }, 2000);
                })
                .addOnFailureListener(e -> {
                    Log.e("FeedbackFragment", "Error updating book status to 'feedbacked' for Book ID: " + receivedBook.getId(), e);
                });
    }




    private void showLoadingDialog() {
        if (loadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_loading, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            loadingDialog = builder.create();


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
            loadingDialog = null;
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



