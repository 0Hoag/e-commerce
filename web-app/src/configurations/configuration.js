export const OAuthConfig = {
  clientId: "CLIENT_ID",
  redirectUri: "REDIRECT_URI",
  authUri: "AUTH_URI",
};

export const CONFIG = {
  API_GATEWAY: "API_GATEWAY",
};

export const API = {
  //profile-service
  CREATE_PROFILE: "/profile/registration",
  MY_PROFILE: "/profile/my-profile",
  UPDATE_PROFILE: "/profile/updateProfile",
  GET_ALL_PROFILE: "/profile/getAllProfile",
  GET_PROFILE: "/profile",
  DELETE_PROFILE: "/profile/deleteProfile",


  //product-service
  CREATE_PRODUCT: "/product/registration",
  GET_PRODUCT: "/product",
  GET_ALL_PRODUCT: "/product/getAllProduct",
  DELETE_PRODUCT: "/product",
  UPDATE_PRODUCT: "/product/updateProduct",

  //user-service(CartItem-service)
  CREATE_CART_ITEM: "/profile/cartItem/registration",
  GET_CART_ITEM: "/profile/cartItem",
  GETALL_CART_ITEM: "/profile/cartItem/getAllCartItem",
  UPDATE_CART_ITEM: "/profile/cartItem/updateCartItem",
  DELETE_CART_ITEM: "/profile/cartItem",
  ADD_CART_ITEM: "/profile/cartItem/addCart",
  REMOVE_CART_ITEM: "/profile/cartItem/removeCart",

  //user-service(SelectedProduct-service)
  CREATE_SELECTED_PRODUCT: "/profile/selectedProduct/registration",
  ADD_SELECTED_PRODUCT_WITH_USER: "/profile/selectedProduct/addSelectedProductWithProfile",
  GET_SELECTED_PRODUCT: "/profile/selectedProduct", // don't have 
  GET_ALL_SELECTED_PRODUCT: "/profile/selectedProduct/getAllSelectedProduct",
  UPDATE_SELECTED_PRODUCT: "/profile/selectedProduct/updateSelectedProduct", // don't have 
  REMOVE_SELECTED_PRODUCT_WITH_USER: "/profile/selectedProduct/removeSelectedProductWithUser", // don't have 
  DELETE_SELECTED_PRODUCT_WITH_USER: "/profile/selectedProduct", // don't have 

  //user-service(Orders-service)
  CREATE_ORDERS: "/profile/orders/registration",
  GET_ORDERS: "/profile/orders/getOrders",
  GET_ALL_ORDERS: "/profile/orders/getAllOrders",
  ADD_SELECTED_PRODUCT_WITH_ORDERS_WITH_USER: "/profile/orders/addSelectedProductWithOrdersWithProfile",
  REMOVE_SELECTED_PRODUCT_WITH_ORDERS_WITH_USER: "/profile/orders/removeSelectedProductWithOrdersWithUser",
  DELETE_ORDERS: "/profile/orders/deleteOrders",
  UPDATE_ORDER: "/profile/orders/updateOrder",

  //VNPay (VNPay-service)
  CREATE_URL: "/profile/api/vnpay/create-payment",
  UPDATE_PAYMENT: "/profile/api/vnpay/payment-return",


  //KeyCloak
  LOG_OUT: "/realms/devteria/protocol/openid-connect/logout"
};

export const KEYCLOACK_CONFIG = {
  url: "http://localhost:8180",
  realm: "devteria",
  clientId: "devteria_webapp",
};
