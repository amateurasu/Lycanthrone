import React from "react";
import ReactDOM from "react-dom";

import {CustomizedBreadcrumbs, RouterBreadcrumbs} from "./material/Breadcrums";

import "typeface-roboto";

const app = (
    <React.Fragment>
        <CustomizedBreadcrumbs/>
    </React.Fragment>
);
ReactDOM.render(app, document.getElementById("root"));
