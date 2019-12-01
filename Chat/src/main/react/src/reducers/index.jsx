import {combineReducers} from "redux";
import chatReducer from "./Chat";
import addressBookReducer from "./AddressBook";
import userReducer from "./User";

const appReducer = combineReducers({chatReducer, addressBookReducer, userReducer});

const rootReducer = (state, action) => {
    if (action.type === "USER_LOGOUT") {
        state = undefined;
    }
    return appReducer(state, action);
};

export default rootReducer;
