package com.teamone.auroraspa;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;
import com.teamone.auroraspa.databinding.FragmentHomeBinding;
import com.teamone.auroraspa.databinding.FragmentRecNailBinding;
import com.teamone.viewmodels.ShareData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecNailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecNailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FragmentRecNailBinding binding;
    ShareData shareData;

    public RecNailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecNailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecNailFragment newInstance(String param1, String param2) {
        RecNailFragment fragment = new RecNailFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecNailBinding.inflate(inflater, container, false);
        binding.datePickerContainer.setOnClickListener(view -> {
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Chọn ngày đặt lịch")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTheme(R.style.ThemeOverlay_AuroraSpa_MaterialDatePicker)
                    .build();
            picker.show(requireActivity().getSupportFragmentManager(), "DATE_PICKER");
            picker.addOnPositiveButtonClickListener(selection -> {
                Date date = new Date(selection);
                String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
                binding.txtSelectedDate.setText(formattedDate);
                binding.txtSelectedDate.setTextSize(12);
            });
        });
        binding.btnSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.txtSelectedDate.getText().toString().isEmpty()) {
                    Toast.makeText(requireActivity(), "Vui lòng chọn ngày sinh", Toast.LENGTH_SHORT).show();
                } else {
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    ResultNailFragment resultNailFragment = ResultNailFragment.newInstance("", "");
                    fragmentTransaction.replace(R.id.container, resultNailFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }
        });
        return binding.getRoot();
    }
}