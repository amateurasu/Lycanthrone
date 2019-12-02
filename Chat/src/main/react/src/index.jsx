import React from "react";
import ReactDOM from "react-dom";
import {BrowserRouter as Router, Route} from "react-router-dom";
import {applyMiddleware, createStore} from "redux";
import thunkMiddleware from "redux-thunk";
import {Provider} from "react-redux";

import rootReducer from "./reducers";
import Portal from "./pages/Portal";
import Main from "./pages/Main";
import {API} from "./api/API";
import {clearStorage} from "./utils/utils";

import "antd/dist/antd.css";
import "./index.css";
import "react-slidedown/lib/slidedown.css";

export const store = createStore(rootReducer, applyMiddleware(thunkMiddleware));
window.store = store;

API.post(`/api/protected/ping`).then(data => {
    console.log("ping");
    console.log(data);
}, data => {
    console.log(data);
    console.log("not-ping");
    clearStorage();
});

let provider = (
    <Provider store={store}>
        <Router>
            <div style={{overflow: "hidden"}}>
                <Route exact path='/login' component={Portal}/>
                <Route exact path='/' component={Main}/>
            </div>
        </Router>
    </Provider>
);

ReactDOM.render(provider, document.getElementById("root"));
