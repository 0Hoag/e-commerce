import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import keycloak from "../keycloak";
import KeycloakAuthorization from "keycloak-js/authz";

const cartItemService = {
  createCartItem: (newCartItem) => httpClient.post(API.CREATE_CART_ITEM, newCartItem, getAuthHeader()),

  getAllCartItem: () => httpClient.get(API.GETALL_CART_ITEM, getAuthHeader()),

  getCartItem: (cartItemId) => httpClient.get(API.GET_CART_ITEM + "/" + cartItemId, getAuthHeader()),

  addCartItem: (profileId, cartItemId) => httpClient.post(API.ADD_CART_ITEM + "/" + profileId, { cartItemId }, getAuthHeader()),

  removeCartItem: (profileId, cartItemId) => httpClient.delete(API.REMOVE_CART_ITEM + "/" + profileId, { cartItemId }, getAuthHeader()),

  updateCartItem: (cartItemId, updateCart) => httpClient.put(API.UPDATE_CART_ITEM + "/" + cartItemId, updateCart, getAuthHeader())
}  
  
function getAuthHeader() {
  return {
    headers: {
      Authorization: `Bearer ${keycloak.token}`,
      'Content-Type': 'application/json',
    },
  };
}

export default cartItemService;
