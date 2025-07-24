package com.teamone.auroraspa;


import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.teamone.auroraspa.databinding.FragmentMyAccBinding;
import com.teamone.models.Product;


import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DocumentReference db;
    FirebaseFirestore database =FirebaseFirestore.getInstance();
    FirebaseUser customer = FirebaseAuth.getInstance().getCurrentUser();
    String customerID;


    public MyAccFragment() {
        // Required empty public constructor
    }
    FragmentMyAccBinding binding;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyAccFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccFragment newInstance(String param1, String param2) {
        MyAccFragment fragment = new MyAccFragment();
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
        ((MainActivity) requireActivity()).showOnlyNavBar(1);
        ((MainActivity) requireActivity()).showHeaderCart("QUẢN LÝ TÀI KHOẢN");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        binding = FragmentMyAccBinding.inflate(inflater, container, false);
        loadCustomerdata();
        // Set click for buttons
        binding.imvUserava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoUpFragment infoupfragement = new InfoUpFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, infoupfragement);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        binding.btnNailcollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NailColFragment nailcolfragment = new NailColFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, nailcolfragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
//        binding.btnPointhis.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PointHisFragment pointhisfragment = new PointHisFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, pointhisfragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
        binding.btnBookhis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookHisFragment bookhisfragment = new BookHisFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, bookhisfragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        binding.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        binding.btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                ((MainActivity) requireActivity()).replaceFragClearBackStack(new HomeFragment());
                ((MainActivity) requireActivity()).binding.navBar1.setVisibility(View.VISIBLE);
                ((MainActivity) requireActivity()).binding.navBar1.setTrangChuSelected();
            }
        });


        // Show top 3 recent book


        return binding.getRoot();
    }
    private void loadCustomerdata() {
        if (customer != null) {
            customerID = customer.getUid();
            Log.d("InfoUpFragment", "User logged in, using ID: " + customerID);
        } else {
            customerID = "5IDjX2OG0Nc0Gn4mdjkaf9sB8sa2"; // ID demo của bạn
            Log.d("InfoUpFragment", "No user logged in, using demo ID: " + customerID);
        }
        DocumentReference userRef = database.collection("customers").document(customerID);


        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {


                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phone = documentSnapshot.getString("phone");
                String dob = documentSnapshot.getString("dob");
                String gender = documentSnapshot.getString("gender");


                binding.txtUsername.setText(name);



                Toast.makeText(getContext(), "Tải dữ liệu người dùng thành công!", Toast.LENGTH_SHORT).show();


            } else {
                Log.e("InfoUpFragment", "Dữ liệu người dùng null mặc dù tài liệu tồn tại.");
                Toast.makeText(getContext(), "Lỗi: Không thể đọc dữ liệu người dùng.", Toast.LENGTH_LONG).show();
            }
        });


    }

}

