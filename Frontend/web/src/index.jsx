import React from "react";
import ReactDOM from "react-dom";

import CustomizedBreadcrumbs from "./material/CustomBreadcrum";
import RouterBreadcrumbs from "./material/Breadcrum";

import "typeface-roboto";

const app = (
    <React.Fragment>
        <CustomizedBreadcrumbs/>
        <RouterBreadcrumbs/>
        {/*<Typo/>*/}
    </React.Fragment>
);
ReactDOM.render(app, document.getElementById("root"));
