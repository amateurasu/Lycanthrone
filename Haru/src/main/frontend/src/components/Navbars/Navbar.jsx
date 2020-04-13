import React from "react";
import classNames from "classnames";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";
import {AppBar, Hidden, IconButton, Toolbar} from "@material-ui/core";
import {Menu} from "@material-ui/icons";

import AdminNavbarLinks from "./AdminNavbarLinks";
import Button from "../CustomButtons/Button";

import styles from "./HeaderStyle";

const useStyles = makeStyles(styles);

export default function Header(props) {
    const classes = useStyles();

    function makeBrand() {
        let name;
        props.routes.map(prop => {
            if (window.location.href.indexOf(prop.layout + prop.path) !== -1) {
                name = prop.name;
            }
            return null;
        });
        return name;
    }

    const {color} = props;
    const appBarClasses = classNames({
        [` ${classes[color]}`]: color
    });
    return (
        <AppBar className={classes.appBar + appBarClasses}>
            <Toolbar className={classes.container}>
                <div className={classes.flex}>
                    {/* Here we create navbar brand, based on route name */}
                    <Button color="transparent" href="#" className={classes.title}>
                        {makeBrand()}
                    </Button>
                </div>
                <Hidden smDown implementation="css">
                    <AdminNavbarLinks/>
                </Hidden>
                <Hidden mdUp implementation="css">
                    <IconButton color="inherit" aria-label="open drawer" onClick={props.handleDrawerToggle}>
                        <Menu/>
                    </IconButton>
                </Hidden>
            </Toolbar>
        </AppBar>
    );
}

Header.propTypes = {
    color: PropTypes.oneOf(["primary", "info", "success", "warning", "danger"]),
    handleDrawerToggle: PropTypes.func,
    routes: PropTypes.arrayOf(PropTypes.object)
};
