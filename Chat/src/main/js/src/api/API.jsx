import axios from "axios";
import {message} from "antd";
import {getJWT, isEmptyString} from "../utils/utils";

export const host = "http://localhost:8080";
export const ws_host = "ws://localhost:8090/";
const auth_type = "Bearer";

// Set config defaults when creating the instance
const instance = axios.create({baseURL: host});

instance.interceptors.request.use(config => config, error => Promise.reject(error));

// Add a response interceptor
instance.interceptors.response.use(response => response, error => {
    //ignore ping
    if (!error.request.responseURL.endsWith("API/protected/ping")) {
        message.error(
            error.response.data.error.message
            || "Oops, The server was unable to complete your request. We will be back soon :(");
    }
    return Promise.reject(error);
});

export const API = {
    get: url => {
        const jwt = getJWT();
        const auth = !isEmptyString(jwt) ? `${auth_type} ${jwt}` : "";
        return instance.get(`${url}`, {headers: {"Authorization": auth}});
    },

    post: (url, req) => {
        const jwt = getJWT();
        const auth = !isEmptyString(jwt) ? `${auth_type} ${jwt}` : "";
        return instance.post(`${url}`, req, {headers: {"Authorization": auth}});
    },

    put: (url, req) => instance.put(`${url}`, req),

    patch: (url, req) => instance.patch(`${url}`, req),

    delete: url => instance.delete(`${url}`)
};
