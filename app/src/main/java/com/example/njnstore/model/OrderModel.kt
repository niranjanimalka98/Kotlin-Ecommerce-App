package com.example.njnstore.model

public class OrderModel {
    var order_id = ""
    var customer = UserModel()
    var cart: List<CartModel> = ArrayList<CartModel>()
}