import axios from "axios";
import { KEYCLOACK_CONFIG } from "./configuration";

const httpKeyCloak = axios.create({
    baseURL: KEYCLOACK_CONFIG.url,
    timeout: 30000,
    headers: {
      "Content-Type": "application/json",
    },
  });

export default httpKeyCloak;

