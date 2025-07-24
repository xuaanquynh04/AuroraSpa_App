package com.teamone.auroraspa;




import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;




import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamone.auroraspa.databinding.FragmentPayBinding;
import com.teamone.models.Book;
import com.teamone.models.BookItem;
import com.teamone.models.CartItem;

import java.util.ArrayList;


public class PayFragment extends Fragment {




    private FragmentPayBinding binding;
    private Book book;
    private ArrayList<CartItem> toDeleteCartItems ;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPayBinding.inflate(inflater, container, false);
        getBookFromArguments();
        displayBookInfo();
        addEvents();
        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) requireActivity()).showHeaderCart("THANH TOÁN");
        ((MainActivity) requireActivity()).binding.headerCart.btnCart.setVisibility(View.GONE);
    }




    private void getBookFromArguments() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            book = (Book) bundle.getSerializable("bookInfo");
        }
    }




    private void displayBookInfo() {
        if (book != null) {
            String formatted = String.format("%,.0f", book.getTotal()).replace(",", ".");
            binding.totalAmount.setText(formatted);
            binding.txtTotalPrice.setText(formatted);
        }
    }




    private void addEvents() {
        View.OnClickListener selectOnlyOne = v -> {
            binding.rbCash.setChecked(false);
            binding.rbBankTransfer.setChecked(false);
            binding.rbEwallet.setChecked(false);
            ((android.widget.RadioButton) v).setChecked(true);
        };




        binding.rbCash.setOnClickListener(selectOnlyOne);
        binding.rbBankTransfer.setOnClickListener(selectOnlyOne);
        binding.rbEwallet.setOnClickListener(selectOnlyOne);




        binding.btnPay.setOnClickListener(view -> {
            if (book == null) return;




            String paymentMethod = "";
            if (binding.rbCash.isChecked()) paymentMethod = "cash";
            else if (binding.rbBankTransfer.isChecked()) paymentMethod = "bank_transfer";
            else if (binding.rbEwallet.isChecked()) paymentMethod = "e_wallet";




            if (paymentMethod.isEmpty()) return;




            book.setPaymentMethod(paymentMethod);
            book.setBookStatus("booked");
            book.setAddTime(Timestamp.now());




            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (book.getId() == null || book.getId().isEmpty()) {
                ArrayList<BookItem> bookItems = book.getBookItems();
                toDeleteCartItems = new ArrayList<>();
                for (BookItem bookItem : bookItems) {
                    toDeleteCartItems.add(bookItem.getItem());}
//                for (CartItem cartItem : toDeleteCartItems) {
//                    Log.d("cart item", cartItem.getId());
//                }
                DocumentReference newDoc = db.collection("book").document();
                book.setId(newDoc.getId());
                newDoc.set(book)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                            if (toDeleteCartItems!=null) {
                                deleteCartItems(toDeleteCartItems);
                            }
                            requireActivity().getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.container, new HomeFragment())
                                    .commit();
                        }
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Lỗi khi đặt lịch!", Toast.LENGTH_SHORT).show()
                        );
            } else {
                db.collection("book").document(book.getId())
                        .set(book)
                        .addOnSuccessListener(unused ->
                                Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                        )
                        .addOnFailureListener(e ->
                                Toast.makeText(getContext(), "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show()
                        );
            }
        });
    }

    private void deleteCartItems(ArrayList<CartItem> toDeleteCartItems) {
        for (CartItem cartItem : toDeleteCartItems) {
            if (cartItem.getId()!=null) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("cartitem")
                        .document(cartItem.getId())
                        .delete().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("success Delete cart", cartItem.getId());
                            }
                            else
                                Log.d("delete cart", "failed");
                        });
            } else Log.d("từ đặt lịch ngay", "180");

        }
    }
}



