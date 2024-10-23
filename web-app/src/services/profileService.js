import httpClient from "../configurations/httpClient";
import { API } from "../configurations/configuration";
import keycloak from "../keycloak";

const profileService = {
    createUser: (request) => httpClient.post(API.CREATE_PROFILE, request, getAuthHeader()),

    getMyProfile: () => httpClient.get(API.MY_PROFILE, getAuthHeader()),

    updateProfile: (profileId, request) => httpClient.put(API.UPDATE_PROFILE + "/" + profileId, request, getAuthHeader()),

    getAllProfile: () => httpClient.get(API.GET_ALL_PROFILE, getAuthHeader()),

    getProfile: (profileId) => httpClient.get(API.GET_PROFILE + "/" + profileId, getAuthHeader()),

    deleteProfile: (profileId) => httpClient.delete(API.DELETE_PROFILE + "/" + profileId, getAuthHeader())
}

function getAuthHeader() {
    return {
      headers: {
        Authorization: `Bearer ${keycloak.token}`,
        'Content-Type': 'application/json',
      },
    };
  }


export default profileService;