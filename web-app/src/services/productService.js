import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import keycloak from "../keycloak";

const productService = {
    createProduct: (data) => httpClient.post(API.CREATE_PRODUCT, data),

    getProduct: (id) => httpClient.get(`${API.GET_PRODUCT}/${id}`, getAuthHeader()),

    getAllProduct: () => httpClient.get(API.GET_ALL_PRODUCT, getAuthHeader()),

    deleteProduct: (id) => httpClient.delete(`${API.DELETE_PRODUCT}/${id}`, getAuthHeader()),

    updateStockQuantityProduct: (id, data) => httpClient.put(`${API.UPDATE_PRODUCT}/${id}`, data, getAuthHeader()),
}

function getAuthHeader() {
    return {
        headers: {
            Authorization: `Bearer ${keycloak.token}`
        }
    } 
}

export default productService;