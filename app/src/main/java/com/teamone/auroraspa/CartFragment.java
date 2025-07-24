package com.teamone.auroraspa;


import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.teamone.adapters.Adapter_cart;
import com.teamone.auroraspa.databinding.FragmentCartBinding;
import com.teamone.models.CartItem;
import com.teamone.models.Customer;
import com.teamone.models.Product;
import com.teamone.viewmodels.ShareData;
import com.teamone.views.Collapsible_Header;


import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentCartBinding binding;
    ArrayList<CartItem> nailItems;
    ArrayList<CartItem> washItems;
    ArrayList<CartItem> waxItems;
    Adapter_cart adapterNail;
    Adapter_cart adapterWash;
    Adapter_cart adapterWax;
    FirebaseFirestore db =FirebaseFirestore.getInstance();
    FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();
    ;
    Customer customerInfor = new Customer();
    ShareData shareData;






    private boolean isSelectAllChanging = false;


    public CartFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
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
        ((MainActivity) requireActivity()).showHeaderCart("GIỎ HÀNG");
        ((MainActivity) requireActivity()).binding.headerCart.btnCart.setVisibility(View.GONE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater,container,false);
        checkLogin();
        display();


        nailItems = new ArrayList<>();
        washItems = new ArrayList<>();
        waxItems = new ArrayList<>();
        binding.nailContainer.setLayoutManager(new LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false));
        binding.washContainer.setLayoutManager(new LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false));
        binding.waxContainer.setLayoutManager(new LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false));
        hideNavBar();
        addEvents();

        // Inflate the layout for this fragment
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
                            loadProduct();
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
    private void display() {
        binding.nailHeader.setTitle("Bấm vào để xem dịch vụ nail đã chọn");
        binding.washHeader.setTitle("Bấm vào để xem dịch vụ gội đã chọn");
        binding.waxHeader.setTitle("Bấm vào để xem dịch vụ wax đã chọn");
    }


    private void addEvents() {
        binding.btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<CartItem> selectedNailItems = adapterNail.getCheckedItems();
                ArrayList<CartItem> selectedWashItems = adapterWash.getCheckedItems();
                ArrayList<CartItem> selectedWaxItems = adapterWax.getCheckedItems();


                ArrayList<CartItem> selectedCartItems = new ArrayList<>();
                selectedCartItems.addAll(selectedNailItems);
                selectedCartItems.addAll(selectedWashItems);
                selectedCartItems.addAll(selectedWaxItems);

                if (selectedCartItems.isEmpty()) {
                    Toast.makeText(requireContext(), "Vui lọc chọn dịch vụ", Toast.LENGTH_SHORT).show();
                    return;
                } else if (selectedNailItems.size() > 1 || selectedWashItems.size() > 1 || selectedWaxItems.size() > 1) {
                    Toast.makeText(requireContext(), "Bạn chỉ được chọn 1 dịch vụ cho mỗi nhóm!", Toast.LENGTH_SHORT).show();
                    return;
                }

                BookFragment bookFragment = new BookFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedCartItems", selectedCartItems);
                bookFragment.setArguments(bundle);


                // Mở BookFragment và truyền dữ liệu
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, bookFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }
    private void loadProduct() {
        Log.d("Bat dau load product", "Bắt đầu tải dữ liệu từ Firestore...");
        db.collection("cartitem").whereEqualTo("customerID",customerInfor.getId()).orderBy("addTime", Query.Direction.ASCENDING).get().addOnCompleteListener(task -> {
            Log.d("Customer trong cart", "Kết quả tải dữ liệu: " + customerInfor.getId());
            if (task.isSuccessful()) {
                Log.d("CartFragment", "Tải dữ liệu thành công, số documents: " + task.getResult().size());
                nailItems.clear();
                washItems.clear();
                waxItems.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    CartItem p = document.toObject(CartItem.class);
                    Log.d("dong 181", "hihi");
                    if (p != null) {
                        if(p.getProductTypeID().equals("NAIL")){
                            nailItems.add(p);
                        }else if(p.getProductTypeID().equals("WASH")){
                            washItems.add(p);
                        }else if(p.getProductTypeID().equals("WAX")){
                            waxItems.add(p);
                        };
                        Log.d("CartFragment", "Đã thêm item: " + p.getProductName()+ p.getId());
                    } else {
                        Log.d("dong 192", "p bi null");
                    }
                }
                Log.d("CartFragment", "Tổng số nail items đã tải: " + nailItems.size());
                Log.d("CartFragment", "Tổng số wash items đã tải: " + washItems.size());
                Log.d("CartFragment", "Tổng số wax items đã tải: " + waxItems.size());
                initAdapter();
            } else {
                Log.e("CartFragment", "Lỗi tải dữ liệu: " + task.getException().getMessage());}
        });
    }
    private void setcontent(){
        Log.d("CartFragment", "Thiet lap collapsible header");
        binding.nailHeader.setContentToToggle(binding.nailContainer);
        binding.washHeader.setContentToToggle(binding.washContainer);
        binding.waxHeader.setContentToToggle(binding.waxContainer);
    }
    private void initAdapter() {
        Log.d("CartFragment", "Khởi tạo adapter với nail: " + nailItems.size() + ", wash: " + washItems.size() + ", wax: " + waxItems.size() + " items");
        adapterNail = new Adapter_cart(requireContext(), R.layout.item_in_cart, nailItems);
        binding.nailContainer.setAdapter(adapterNail);
        adapterWash = new Adapter_cart(requireContext(), R.layout.item_in_cart, washItems);
        binding.washContainer.setAdapter(adapterWash);
        adapterWax = new Adapter_cart(requireContext(), R.layout.item_in_cart, waxItems);
        binding.waxContainer.setAdapter(adapterWax);
        setcontent();




        // Thiết lập listener cho adapternail
        adapterNail.setOnItemCheckedListener(new Adapter_cart.OnItemCheckedListener() {
            @Override
            public void onItemChecked(int position, boolean isChecked) {
                calculateTotalPrice();
                if (!isSelectAllChanging) updateSelectAllCheckbox();
            }
            @Override
            public void deleteItem(int position) {
                CartItem deletedItem = nailItems.get(position);
                deleteCartItem(deletedItem);
            }
            @Override
            public void editItem(int position) {
                String productID = nailItems.get(position).getProductID();
                CartItem cartItemUpdate = nailItems.get(position);
                openEditDialog(productID, cartItemUpdate);
            }
        });
        // Thiết lập listener cho adapterwash
        adapterWash.setOnItemCheckedListener(new Adapter_cart.OnItemCheckedListener() {
            @Override
            public void onItemChecked(int position, boolean isChecked) {
                calculateTotalPrice();
                if (!isSelectAllChanging) updateSelectAllCheckbox();
            }
            @Override
            public void deleteItem(int position) {
                CartItem deletedItem = washItems.get(position);
                deleteCartItem(deletedItem);
            }
            @Override
            public void editItem(int position) {
                String productID = washItems.get(position).getProductID();
                CartItem cartItemUpdate = washItems.get(position);
                openEditDialog(productID, cartItemUpdate);
            }
        });
        // thiet lâp listener cho adapterwax
        adapterWax.setOnItemCheckedListener(new Adapter_cart.OnItemCheckedListener() {
            @Override
            public void onItemChecked(int position, boolean isChecked) {
                calculateTotalPrice();
                if (!isSelectAllChanging) updateSelectAllCheckbox();
            }
            @Override
            public void deleteItem(int position) {
                CartItem deletedItem = waxItems.get(position);
                deleteCartItem(deletedItem);
            }
            @Override
            public void editItem(int position) {
                String productID = waxItems.get(position).getProductID();
                CartItem cartItemUpdate = waxItems.get(position);
                openEditDialog(productID, cartItemUpdate);
            }
        });


        setupSelectAllCheckbox();
    }

    private void openEditDialog(String productID, CartItem cartItemUpdate) {
        Log.d("cart frag dong 309", cartItemUpdate.getProductName());
        shareData = new ViewModelProvider(requireActivity()).get(ShareData.class);
        shareData.getIsLoadingProducts().observe(getViewLifecycleOwner(), isLoading ->{
            if (isLoading) {
                Log.d("ListFragment", "product is loading");
            }
            else {
                Log.d("ListFragment", "loaded");
            }
        });
        shareData.get_newProducts().observe(getViewLifecycleOwner(), products1 -> {
            for (Product p : products1) {
                Product toEditProduct;
                if (p.getProductID().equals(productID)) {
                    toEditProduct = p;
                    Log.d("cart frag dong 323", "test");
                    if (toEditProduct != null) {
                        CustomizeFragment bottomSheet = CustomizeFragment.newInstance(toEditProduct, "update", cartItemUpdate);
                        requireActivity().getSupportFragmentManager().executePendingTransactions();
                        bottomSheet.setDismissListener(() -> {
                            loadProduct();
                        });
                        bottomSheet.show(requireActivity().getSupportFragmentManager(), "CustomizeFragment");
                    } else {
                        Log.d("cart frag dong 331", "null");
                    }
            }}
        });

    }


    private void setupSelectAllCheckbox() {
        if (binding.cbSelectall.isChecked()) {
            isSelectAllChanging = true;
            if (adapterNail != null) adapterNail.selectAllItems(true);
            if (adapterWash != null) adapterWash.selectAllItems(true);
            if (adapterWax != null) adapterWax.selectAllItems(true);
            isSelectAllChanging = false;
            calculateTotalPrice();
        }
        binding.cbSelectall.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isSelectAllChanging) return;
            isSelectAllChanging = true;
            if (adapterNail != null) adapterNail.selectAllItems(isChecked);
            if (adapterWash != null) adapterWash.selectAllItems(isChecked);
            if (adapterWax != null) adapterWax.selectAllItems(isChecked);
            isSelectAllChanging = false;
            calculateTotalPrice();
        });
    }
    private void updateSelectAllCheckbox() {
        boolean allNailChecked = adapterNail != null && adapterNail.areAllItemsChecked();
        boolean allWashChecked = adapterWash != null && adapterWash.areAllItemsChecked();
        boolean allWaxChecked = adapterWax != null && adapterWax.areAllItemsChecked();
        boolean allChecked = allNailChecked && allWashChecked && allWaxChecked;
        isSelectAllChanging = true;
        binding.cbSelectall.setChecked(allChecked);
        isSelectAllChanging = false;


    }


    private void deleteCartItem(CartItem deletedItem) {
        db.collection("cartitem").document(deletedItem.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    if(deletedItem.getProductTypeID().equals("NAIL")){
                        nailItems.remove(deletedItem);
                        adapterNail.notifyDataSetChanged();
                    }else if(deletedItem.getProductTypeID().equals("WASH")){
                        washItems.remove(deletedItem);
                        adapterWash.notifyDataSetChanged();
                    }else if(deletedItem.getProductTypeID().equals("WAX")){
                        washItems.remove(deletedItem);
                        adapterWax.notifyDataSetChanged();
                    };
                    Log.d("CartFragment", "Deleted item with ID: " + deletedItem.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("CartFragment", "Error deleting item: " + e.getMessage());
                });


    }
    private void calculateTotalPrice() {
        double totalPrice = 0;
        ArrayList<CartItem> selectedNailItems = adapterNail.getCheckedItems();
        ArrayList<CartItem> selectedWashItems = adapterWash.getCheckedItems();
        ArrayList<CartItem> selectedWaxItems = adapterWax.getCheckedItems();


        for (CartItem item : selectedNailItems) {
            totalPrice += item.getFinalPrice() * item.getQuantity();
        }
        for (CartItem item: selectedWashItems) {
            totalPrice += item.getFinalPrice() * item.getQuantity();
        }
        for (CartItem item: selectedWaxItems) {
            totalPrice += item.getFinalPrice() * item.getQuantity();
        }


        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedPrice = formatter.format(totalPrice).replace(",", ".");
        binding.totalAmount.setText(formattedPrice);
    }
    private void hideNavBar() {
        ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.GONE);
    }
}

