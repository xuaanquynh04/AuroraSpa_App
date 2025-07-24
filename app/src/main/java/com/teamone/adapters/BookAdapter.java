package com.teamone.adapters;




import android.app.Dialog;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;




import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.teamone.auroraspa.R;
import com.teamone.models.BookItem;
import com.teamone.models.CartItem;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;




public class BookAdapter extends BaseAdapter {
    Context context;
    ArrayList<BookItem> book;
    int lv_book_item;
    Timestamp bookDate;
    boolean isEditable;




    public BookAdapter(Context context, ArrayList<BookItem> book, int lv_book_item, boolean isEditable, Timestamp bookDate) {
        this.context = context;
        this.book = book;
        this.lv_book_item = lv_book_item;
        this.isEditable = isEditable;
        this.bookDate = bookDate;
    }




    @Override
    public int getCount() {
        return book.size();
    }




    @Override
    public Object getItem(int i) {
        return book.get(i);
    }




    @Override
    public long getItemId(int i) {
        return i;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(lv_book_item, parent, false);
            holder = new ViewHolder();
            holder.productName = convertView.findViewById(R.id.productName);
            holder.productPrice = convertView.findViewById(R.id.productPrice);
            holder.productTime = convertView.findViewById(R.id.productTime);
            holder.btnChooseTime = convertView.findViewById(R.id.btnChooseTime);
            holder.productImage = convertView.findViewById(R.id.productImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }




        BookItem bookItem = book.get(position);
        CartItem cartItem = bookItem.getItem();




        if (cartItem != null) {
            holder.productName.setText(cartItem.getProductName());
            holder.productPrice.setText(formatCurrency(cartItem.getFinalPrice()));




            // LOAD HÌNH ẢNH BẰNG GLIDE
            String imageUrl = cartItem.getProductImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.nail1) // mặc định
                        .error(R.drawable.ic_launcher_background)       // hình lỗi
                        .into(holder.productImage);
            }
        }




        // Hiển thị giờ đã chọn nếu có
        Timestamp chosenTime = bookItem.getChosenTime();
        if (chosenTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            holder.productTime.setText("Giờ bắt đầu: " + sdf.format(chosenTime.toDate()));
        } else {
            holder.productTime.setText("Giờ bắt đầu: ");
        }




        // Nếu được phép chọn giờ thì hiển thị nút
        if (isEditable) {
            holder.btnChooseTime.setVisibility(View.VISIBLE);
            holder.btnChooseTime.setOnClickListener(v -> {
                showTimeDialog(position, holder.productTime);
            });
        } else {
            holder.btnChooseTime.setVisibility(View.GONE);
        }




        return convertView;
    }
    private void showTimeDialog(int pos, TextView productTimeView) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_time);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);




        GridView gridView = dialog.findViewById(R.id.gvTimeSlots);
        List<String> timeslots = Arrays.asList(
                "08:00", "08:30", "09:00", "09:30",
                "10:00", "10:30", "11:00", "11:30",
                "12:00", "12:30", "13:00", "13:30",
                "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00", "17:30",
                "18:00", "18:30", "19:00", "19:30",
                "20:00", "20:30", "21:00", "21:30"
        );




        TimeSlotsAdapter adapter = new TimeSlotsAdapter(context, R.layout.item_time_slot, timeslots);
        gridView.setAdapter(adapter);




        gridView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTime = timeslots.get(position);
            String[] parts = selectedTime.split(":");




            if (bookDate == null) {
                Toast.makeText(context, "Vui lòng chọn ngày trước!", Toast.LENGTH_SHORT).show();
                return;
            }




            Calendar cal = Calendar.getInstance();
            cal.setTime(bookDate.toDate());
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parts[0]));
            cal.set(Calendar.MINUTE, Integer.parseInt(parts[1]));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);




            Timestamp timestamp = new Timestamp(cal.getTime());
            book.get(pos).setChosenTime(timestamp);
            adapter.setSelectedPosition(position);
            adapter.notifyDataSetChanged();
            productTimeView.setText("Giờ bắt đầu: " + selectedTime);
            gridView.postDelayed(dialog::dismiss, 100);
        });




        dialog.show();
    }
    public void setBookDate(Timestamp bookDate) {
        this.bookDate = bookDate;
        notifyDataSetChanged();
    }
    private String formatCurrency(double value) {
        return String.format("%,.0f", value).replace(",", ".");
    }
    public static class ViewHolder {
        TextView productName, productPrice, productTime;
        Button btnChooseTime;
        ImageView productImage;
    }
    public ArrayList<BookItem> getBookItems() {
        return book;
    }
}



