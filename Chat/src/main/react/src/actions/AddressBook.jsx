import {store} from "../index";
import {loadChatContainer, startNewChatSingle} from "./Chat";
import {API} from "../api/API";
import {isEmptyString} from "../utils/utils";
import deepcopy from "deepcopy";

export const ADDRESS_BOOK_FETCHED = "addressBook.ADDRESSBOOK_FETCHED";
export const ADD_FRIEND_FAIL = "addressBook.ADD_FRIEND_FAIL";
export const ADD_FRIEND = "addressBook.ADD_FRIEND";
export const ADD_FRIEND_POPUP_STATE = "addressBook.ADD_FRIEND_POPUP_STATE";
export const EMPTY = "addressBook.EMPTY";

export function loadAddressBookList() {
    return dispatch => getAddressBookList().then(result => {
        dispatch(receivedAddressBook(result));
    });
}

export function receivedAddressBook(addressbook) {
    const fetchedAddressBook = addressbook;
    let fetchedNewAddressBookList = store.getState().addressBookReducer.newAddressBookList;
    return {
        type: ADDRESS_BOOK_FETCHED,
        fetchedAddressBookList: fetchedAddressBook,
        fetchedNewAddressBookList: fetchedNewAddressBookList
    };
}

export function handleChangeAddressBook(userId) {
    return dispatch => {
        API.post(`/api/protected/sessionidbyuserid`, createGetSessionIdRequest(userId)).then(result => {
            dispatch(receivedSessionId(result, userId));
        });
    };
}

export function receivedSessionId(result, userId) {
    if (result.data.data.sessionId !== "-1") {
        store.dispatch(loadChatContainer(result.data.data.sessionId));
    } else {
        store.dispatch(startNewChatSingle(userId));
    }
    return {type: EMPTY};
}

export function addNewFriend(userName) {
    if (isEmptyString(userName)) {
        let error = "Please input username :(";
        return {type: ADD_FRIEND_FAIL, error: error};
    } else {
        return dispatch => API.post(`/api/protected/addfriend`, createAddFriendRequest(userName)).then(result => {
            dispatch(receiveAddFriendResult(result));
        });
    }
}

export function receiveAddFriendResult(result) {
    if (result.data.error) {
        let error = result.data.error.message;
        return {type: ADD_FRIEND_FAIL, error: error};
    }

    let newAddressBookList = deepcopy(store.getState().addressBookReducer.newAddressBookList);
    let newFriend = {
        name: result.data.data.item.name,
        userId: result.data.data.item.userId,
        avatar: processUsernameForAvatar(result.data.data.item.name),
        status: result.data.data.item.status,
        isOnline: result.data.data.item.online
    };

    newAddressBookList.push(newFriend);
    return {type: ADD_FRIEND, newAddressBookList: newAddressBookList};
}

export function changeStateAddFriendPopup(state) {
    return {type: ADD_FRIEND_POPUP_STATE, popupstate: state};
}

export function changeUserOnlineStatus(userId, status) {
    let fetchedAddressBook = deepcopy(store.getState().addressBookReducer.addressBookList);
    let fetchedNewAddressBook = deepcopy(store.getState().addressBookReducer.newAddressBookList);
    for (let i = 0; i < fetchedAddressBook.length; i++) {
        if (fetchedAddressBook[i].userId === userId) {
            fetchedAddressBook[i].isOnline = status;
        }
    }
    for (let j = 0; j < fetchedNewAddressBook.length; j++) {
        if (fetchedNewAddressBook[j].userId === userId) {
            fetchedNewAddressBook[j].isOnline = status;
        }
    }
    const onlineResults = [];
    const offlineResults = [];
    for (let index = 0; index < fetchedAddressBook.length; ++index) {
        if (fetchedAddressBook[index].isOnline) {
            onlineResults.push(fetchedAddressBook[index]);
        } else {
            offlineResults.push(fetchedAddressBook[index]);
        }
    }

    sortOnOff(onlineResults, offlineResults);
    fetchedAddressBook = onlineResults.concat(offlineResults);

    return {
        type: ADDRESS_BOOK_FETCHED,
        fetchedAddressBookList: fetchedAddressBook,
        fetchedNewAddressBookList: fetchedNewAddressBook
    };
}

function processUsernameForAvatar(username) {
    return username.charAt(0) + " " + username.charAt(1);
}

function sortOnOff(onlineResults, offlineResults) {
    const compare = (a, b) => {
        if (a.name < b.name) return -1;
        if (a.name > b.name) return 1;
        return 0;
    };

    onlineResults.sort(compare);
    offlineResults.sort(compare);
}

function getAddressBookList() {
    return new Promise(resolve => {
        API.get(`/api/protected/addressbook`).then(res => {
            const items = res.data.data.items;
            let results = [];
            const onlineResults = [];
            const offlineResults = [];
            for (let index = 0; index < items.length; ++index) {
                const addressbookItem = {
                    name: items[index].name,
                    userId: items[index].userId,
                    avatar: processUsernameForAvatar(items[index].name),
                    status: items[index].status,
                    isOnline: items[index].online
                };
                if (items[index].online) {
                    onlineResults.push(addressbookItem);
                } else {
                    offlineResults.push(addressbookItem);
                }
                sortOnOff(onlineResults, offlineResults);

                results = onlineResults.concat(offlineResults);
            }
            resolve(results);
        });
    });
}

function createAddFriendRequest(username) {
    return {username: username};
}

function createGetSessionIdRequest(userId) {
    return {userId: userId};
}
