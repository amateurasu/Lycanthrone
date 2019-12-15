import {API} from "../api/API";
import {message} from "antd";

export const CHANGE_TAB = "portal.CHANGE_TAB";
export const REGISTER_SUCCEEDED = "user.REGISTER_SUCCEEDED";
export const REGISTER_FAILED = "user.REGISTER_FAILED";
export const USER_PROFILE = "user.USER_PROFILE";
export const CHANGE_STATUS = "user.CHANGE_STATUS";

const callRegisterApi = user => new Promise(resolve => {
    API.post(`/api/public/user`, user).then(res => resolve(res));
});

const createChangeStatusRequest = status => ({status});

export const changeTab = activeTabKey => ({type: CHANGE_TAB, activeTabKey});

export const registerSucceeded = user => {
    message.success("Register successfully. You can proceed to login with your account :)");
    return {type: REGISTER_SUCCEEDED, user};
};

export function register(user) {
    return dispatch => callRegisterApi(user).then(result => {
        dispatch(registerSucceeded(result.data));
    });
}

export const receivedUserProfile = result => {
    const {stt, userName, fullName} = result.data.data;
    let status = (result.data.data.status !== "") ? stt : "You are online";
    return {
        type: USER_PROFILE,
        userName,
        userFullName: fullName,
        userStatus: status
    };
};

export function getProfile() {
    return dispatch => API.get(`/api/protected/user`).then(res => {
        dispatch(receivedUserProfile(res));
    });
}

export function changeUserStatus(status) {
    let userStatus = (status !== "") ? status : "You are online";
    API.post(`/api/protected/status`, createChangeStatusRequest(status));
    return {type: CHANGE_STATUS, userStatus};
}

export const logout = () => ({type: "USER_LOGOUT"});
