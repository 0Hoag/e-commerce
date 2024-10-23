import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Login from "../pages/Login";
import Home from "../pages/Home";
import Authenticate from "../pages/Authenticate";
import ProfileDetail from "../pages/ProfileDetail";
import Settings from "../pages/Settings";
import Register from "../pages/Register";
import ShoppingCart from "../pages/shopping/ShoppingCart";
import CheckOut from "../pages/shopping/CheckOut";
import MyOrders from "../pages/shopping/MyOrders";
import OrderDetails from "../pages/shopping/OrderDetails";
import PaymentResult from "../pages/shopping/PaymentResult";
import Profile from "../pages/Profile";
import ProductDetail from "../pages/product/ProductDetail";

const AppRoutes = () => {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/orders" element={<MyOrders />} />
        <Route path="/payment-result" element={<PaymentResult />} />
        <Route path="/orders/:orderId" element={<OrderDetails />} />
        <Route path="/shoppingcart" element={<ShoppingCart />} />
        <Route path="/myOrders" element={<MyOrders />} />
        <Route path="/checkout" element={<CheckOut />} />
        <Route path="/authenticate" element={<Authenticate />} />
        <Route path="/profile" element={<Profile />} />
        <Route path="/settings" element={<Settings />} />
        <Route path="/product/:id" element={<ProductDetail />} />
        <Route path="/" element={<Home />} />
      </Routes>
    </Router>
  );
};

export default AppRoutes;
