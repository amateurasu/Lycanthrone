import React from "react";
import ReactDOM from "react-dom";
import {createBrowserHistory} from "history";
import {Redirect, Route, Router, Switch} from "react-router-dom";

import Admin from "./layouts/Admin";

const hist = createBrowserHistory();

const router = (
    <Router history={hist}>
        <Switch>
            <Route path="/admin" component={Admin}/>
            <Redirect from="/" to="/admin/dashboard"/>
        </Switch>
    </Router>
);

ReactDOM.render(router, document.getElementById("root"));
