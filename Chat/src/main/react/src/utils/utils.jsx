export const setUserID = userId => sessionStorage.setItem("userId", userId);

export const getUserId = () => sessionStorage.getItem("userId");

export const setJWT = jwt => sessionStorage.setItem("jwt", jwt);

export const getJWT = () => sessionStorage.getItem("jwt");

export const clearStorage = () => sessionStorage.clear();

export const isEmptyString = prop => prop == null || prop === "";

export const isAuthenticated = () => isEmptyString(getJWT());
