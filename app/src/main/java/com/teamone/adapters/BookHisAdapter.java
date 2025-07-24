package com.teamone.adapters;


import static androidx.constraintlayout.widget.Constraints.TAG;


import static java.security.AccessController.getContext;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.app.AlertDialog;


import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.teamone.auroraspa.R;
import com.teamone.models.Book;
import com.teamone.models.BookItem;
import com.teamone.models.CartItem;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class BookHisAdapter extends BaseAdapter {
    Context context;
    int item_layout;
    ArrayList<Book> bookList;
    public interface OnBookViewClickListener {
        void onBookViewClick(Book book);
    }
    public interface OnBookView2ClickListener {
        void onBookView2Click(Book book);
    }


    public interface OnFeedbackClickListener {
        void onFeedbackClick(Book book);
    }


    public interface OnChangeBookClickListener {
        void onChangeBookClick(Book book);
    }


    public interface OnCancelBookClickListener {
        void onCancelBookClick(Book book);
    }


    private OnBookViewClickListener bookViewListener;
    private OnBookViewClickListener bookView2Listener;
    private OnFeedbackClickListener feedbackListener;
    private OnChangeBookClickListener changeBookListener;
    private OnCancelBookClickListener cancelBookListener;




    //    Constructor
    public BookHisAdapter(Context context, int item_layout, ArrayList<Book> bookList) {
        this.context = context;
        this.item_layout = item_layout;
        this.bookList = bookList;
    }


    public void setBookViewListener(OnBookViewClickListener bookViewListener) {
        this.bookViewListener = bookViewListener;
    }


    public void setBookView2Listener(OnBookViewClickListener bookView2Listener) {
        this.bookView2Listener = bookView2Listener;
    }


    public void setFeedbackListener(OnFeedbackClickListener feedbackListener) {
        this.feedbackListener = feedbackListener;
    }


    public void setChangeBookListener(OnChangeBookClickListener changeBookListener) {
        this.changeBookListener = changeBookListener;
    }


    public void setCancelBookListener(OnCancelBookClickListener cancelBookListener) {
        this.cancelBookListener = cancelBookListener;
    }


    @Override
    public int getCount() {
        return bookList.size();
    }


    @Override
    public Object getItem(int position) {
        return bookList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }


    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_book_his, null);
            holder.txtBookid = convertView.findViewById(R.id.txtBookid);
            holder.txtBookdate = convertView.findViewById(R.id.txtBookdate);
            holder.txtBookitemstarttime = convertView.findViewById(R.id.txtBookitemstarttime);
            holder.btnView = convertView.findViewById(R.id.btnView);
            holder.uncompletedbook = convertView.findViewById(R.id.uncompletedbook);
            holder.btnFeedback = convertView.findViewById(R.id.btnFeedback);
            holder.btnChangebook = convertView.findViewById(R.id.btnChangebook);
            holder.btnCancelbook = convertView.findViewById(R.id.btnCancelbook);
            holder.imvBookimage = convertView.findViewById(R.id.imvBookimage);
            holder.btnView2 = convertView.findViewById(R.id.btnView2);
            convertView.setTag(holder);


        } else{
            holder = (ViewHolder) convertView.getTag();
        }
        final Book book = bookList.get(position);
        if(book!= null){
            holder.txtBookid.setText(book.getId());
            Timestamp bookTimestamp = null;
            BookItem book1 = book.getBookItems().get(0);
            bookTimestamp = book1.getChosenTime();
//            Log.d("dong 156", String.valueOf(book.getBookItems()));
//            Log.d("dong 157", String.valueOf(book1.getChosenTime().toDate()));
            if (bookTimestamp != null){
                Date formatedDate = bookTimestamp.toDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());


                String displayDate = dateFormat.format(formatedDate);
                String displayTime = timeFormat.format(formatedDate);


                holder.txtBookdate.setText("Ngày đặt: " + displayDate);
                holder.txtBookitemstarttime.setText("Giờ bắt đầu: " + displayTime);
            }
            if(book.getBookItems() != null && !book.getBookItems().isEmpty()){
                BookItem firstBookItem = book.getBookItems().get(0);
                String productImage = firstBookItem.getItem().getProductImage();
                if(productImage != null && !productImage.isEmpty()){
                    Glide.with(context).load(productImage).into(holder.imvBookimage);
                } else{
                    Log.e("BookHisAdapter", "Product image is null or empty");
                    holder.imvBookimage.setImageResource(R.drawable.nail1);
                }
            } else {
                holder.imvBookimage.setImageResource(R.drawable.nail1);
            }


            holder.btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bookViewListener != null){
                        bookViewListener.onBookViewClick(book);
                    }
                }
            });
            holder.btnView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bookViewListener != null){
                        bookViewListener.onBookViewClick(book);
                    }
                }
            });


            // Set condition to show button
            if(holder.uncompletedbook != null && holder.btnFeedback != null) {
                String bookStatus = book.getBookStatus();


                if ("booked".equalsIgnoreCase(bookStatus)) {
                    holder.uncompletedbook.setVisibility(View.VISIBLE);
                    holder.btnFeedback.setVisibility(View.GONE);
                    holder.btnView2.setVisibility(View.GONE);
                } else if ("completed".equalsIgnoreCase(bookStatus)) {
                    holder.uncompletedbook.setVisibility(View.GONE);
                    holder.btnFeedback.setVisibility(View.VISIBLE);
                    holder.btnView.setVisibility(View.VISIBLE);
                    holder.btnView2.setVisibility(View.GONE);
                } else if ("feedbacked".equalsIgnoreCase(bookStatus)){
                    holder.btnFeedback.setVisibility(View.GONE);
                    holder.btnCancelbook.setVisibility(View.GONE);
                    holder.btnChangebook.setVisibility(View.GONE);
                    holder.btnView.setVisibility(View.GONE);
                    holder.btnView2.setVisibility(View.VISIBLE);
                }
            }


            holder.btnFeedback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(feedbackListener != null){
                        feedbackListener.onFeedbackClick(book);
                    }
                }
            });
            holder.btnChangebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(changeBookListener != null){
                        changeBookListener.onChangeBookClick(book);
                    }
                }
            });
            holder.btnCancelbook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Context dialogContext = v.getContext(); // Lấy context từ view được click


                    if (dialogContext == null) {
                        Log.e("BookHisAdapter", "Context for dialog is null in btnCancelbook.onClick");
                        return;
                    }


//                    AlertDialog.Builder builder = new AlertDialog.Builder(dialogContext);
//                    LayoutInflater inflater = LayoutInflater.from(dialogContext); // Lấy LayoutInflater từ context
//                    View dialogView = inflater.inflate(R.layout.dialog_confirm, null); // Sử dụng layout của bạn
//                    builder.setView(dialogView);
//                    // builder.setCancelable(true); // Mặc định là true
//
//
//                    final AlertDialog dialog = builder.create();

                    Dialog dialog = new Dialog(dialogContext);
                    dialog.setContentView(R.layout.dialog_confirm);


                    // Tìm các nút trong layout của dialog
                    Button btnConfirm = dialog.findViewById(R.id.btnConfirm);
                    Button btnCancelDialog = dialog.findViewById(R.id.btnCancel);


                    if (btnConfirm != null) {
                        btnConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(cancelBookListener != null){
                                    cancelBookListener.onCancelBookClick(book);
                                } else {
                                    Log.e("BookHisAdapter", "cancelBookListener is null in dialog_confirm.xml. Check ID.");
                                    return;


                                }
                                dialog.dismiss(); // Đóng dialog
                            }
                        });
                    } else {
                        Log.e("BookHisAdapter", "btnConfirm is null in dialog_confirm.xml. Check ID.");
                    }


                    if (btnCancelDialog != null) {
                        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Hành động khi người dùng nhấn "KHÔNG"
                                Log.d("BookHisAdapter", "User cancelled the dialog for book ID: " + book.getId());
                                dialog.dismiss(); // Chỉ đóng dialog
                            }
                        });
                    } else {
                        Log.w("BookHisAdapter", "btnCancelDialog is not found in dialog_confirm.xml. This might be intentional.");
                    }
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });




        } else {
            holder.txtBookid.setText("Mã lịch: Không xác định");
            holder.txtBookdate.setText("Ngày đặt: N/A");
            holder.txtBookitemstarttime.setText("Giờ bắt đầu: N/A");
            holder.imvBookimage.setImageResource(R.drawable.nail1);

            if (holder.uncompletedbook != null) holder.uncompletedbook.setVisibility(View.GONE);
            if (holder.btnFeedback != null) holder.btnFeedback.setVisibility(View.GONE);
            if (holder.btnView2 != null) holder.btnView2.setVisibility(View.GONE);
            if (holder.btnChangebook != null) holder.btnChangebook.setVisibility(View.GONE);
            if (holder.btnCancelbook != null) holder.btnCancelbook.setVisibility(View.GONE);
            holder.btnView.setVisibility(View.GONE);
        }




        return convertView;
    }


    private static class ViewHolder {
        TextView txtBookid, txtBookdate, txtBookitemstarttime;
        Button btnView, btnView2, btnFeedback, btnChangebook, btnCancelbook;
        LinearLayout uncompletedbook;
        ImageView imvBookimage;
    }
}



