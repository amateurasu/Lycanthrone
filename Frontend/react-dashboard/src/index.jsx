import React from "react";
import ReactDOM from "react-dom";
import {createBrowserHistory} from "history";
import {Redirect, Route, Router, Switch} from "react-router-dom";

import Admin from "./layouts/Admin";
import RTL from "./layouts/RTL";

const hist = createBrowserHistory();

const router = (
    <Router history={hist}>
        <Switch>
            <Route path="/admin" component={Admin}/>
            <Route path="/rtl" component={RTL}/>
            <Redirect from="/" to="/admin/dashboard"/>
        </Switch>
    </Router>
);

ReactDOM.render(router, document.getElementById("root"));
