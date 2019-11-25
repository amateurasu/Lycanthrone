export function setUserIdToStorage(userId) {
    sessionStorage.setItem("userId", userId);
}

export function getUserIdFromStorage() {
    return sessionStorage.getItem("userId");
}

export function setJwtToStorage(jwt) {
    sessionStorage.setItem("jwt", jwt);
}

export function getJwtFromStorage() {
    return sessionStorage.getItem("jwt");
}

export function clearStorage() {
    sessionStorage.clear();
}

export function isAuthenticated() {
    const jwt = getJwtFromStorage();
    return isEmptyString(jwt);
}

export function isEmptyString(prop) {
    return prop == null || prop === "";
}

