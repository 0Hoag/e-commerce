import { getToken, removeToken, setToken } from "./localStorageService";
import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import httpKeyCloak from "../configurations/httpKeyCloak";
import qs from 'qs';
import keycloak from "../keycloak";

export const logOut = async () => {
  keycloak.logout();
};

export const isAuthenticated = () => {
  return getToken();
};
