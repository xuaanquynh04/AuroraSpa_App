package com.teamone.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teamone.models.Custom;
import com.teamone.models.Product;

import java.util.ArrayList;

public class ShareData extends ViewModel {
    private final MutableLiveData<ArrayList<Product>> _newProducts = new MutableLiveData<>();
    public LiveData<ArrayList<Product>> get_newProducts() {
        return _newProducts;
    }
    public void set_newProducts(ArrayList<Product> products) {
        _newProducts.setValue(products);
    }
    private final MutableLiveData<Boolean> _isLoadingProducts = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoadingProducts() {return _isLoadingProducts;}
    public void setIsLoadingProducts(boolean isLoading) {_isLoadingProducts.setValue(isLoading);}

    private final MutableLiveData<ArrayList<Custom>> _customs = new MutableLiveData<>();
    public LiveData<ArrayList<Custom>> getCustoms() { return _customs; }
    public void setCustoms(ArrayList<Custom> customs) { _customs.setValue(customs);}
    private final MutableLiveData<Boolean> _isLoadingCustoms = new MutableLiveData<>(false);
    public LiveData<Boolean> getIsLoadingCustoms() { return _isLoadingCustoms; }
    public void setIsLoadingCustoms(boolean isLoading) {_isLoadingCustoms.setValue(isLoading);}

    private final MutableLiveData<Product> _currentProduct = new MutableLiveData<>();
    public LiveData<Product> getCurrentProduct() {
        return _currentProduct;
    }
    public void setCurrentProduct(Product product) {
        _currentProduct.setValue(product);
    }
}
