import React from "react";
import classNames from "classnames";

import {makeStyles} from "@material-ui/core/styles";
import {ClickAwayListener, Divider, Grow, Hidden, MenuItem, MenuList, Paper, Popper} from "@material-ui/core";
import {Dashboard, Notifications, Person, Search} from "@material-ui/icons";

import CustomInput from "../CustomInput/CustomInput";
import Button from "../CustomButtons/Button";

import styles from "./HeaderLinksStyle";

const useStyles = makeStyles(styles);

export default function AdminNavbarLinks() {
    const classes = useStyles();
    const [openNotification, setOpenNotification] = React.useState(null);
    const [openProfile, setOpenProfile] = React.useState(null);
    const handleClickNotification = event => {
        if (openNotification && openNotification.contains(event.target)) {
            setOpenNotification(null);
        } else {
            setOpenNotification(event.currentTarget);
        }
    };
    const handleCloseNotification = () => setOpenNotification(null);
    const handleClickProfile = event => {
        if (openProfile && openProfile.contains(event.target)) {
            setOpenProfile(null);
        } else {
            setOpenProfile(event.currentTarget);
        }
    };
    const handleCloseProfile = () => setOpenProfile(null);
    return (
        <div>
            <div className={classes.searchWrapper}>
                <CustomInput
                    formControlProps={{
                        className: `${classes.margin} ${classes.search}`
                    }}
                    inputProps={{
                        placeholder: "Search",
                        inputProps: {"aria-label": "Search"}
                    }}/>
                <Button color="white" aria-label="edit" justIcon round>
                    <Search/>
                </Button>
            </div>

            <Button
                color={window.innerWidth > 959 ? "transparent" : "white"}
                justIcon={window.innerWidth > 959}
                simple={!(window.innerWidth > 959)}
                aria-label="Dashboard"
                className={classes.buttonLink}>

                <Dashboard className={classes.icons}/>
                <Hidden mdUp implementation="css">
                    <p className={classes.linkText}>Dashboard</p>
                </Hidden>
            </Button>

            <div className={classes.manager}>
                <Button
                    color={window.innerWidth > 959 ? "transparent" : "white"}
                    justIcon={window.innerWidth > 959}
                    simple={!(window.innerWidth > 959)}
                    aria-owns={openNotification ? "notification-menu-list-grow" : null}
                    aria-haspopup="true"
                    onClick={handleClickNotification}
                    className={classes.buttonLink}
                >
                    <Notifications className={classes.icons}/>
                    <span className={classes.notifications}>5</span>
                    <Hidden mdUp implementation="css">
                        <p onClick={handleCloseNotification} className={classes.linkText}>Notification</p>
                    </Hidden>
                </Button>
                <Popper
                    open={Boolean(openNotification)}
                    anchorEl={openNotification}
                    className={`${classNames({[classes.popperClose]: !openNotification})} ${classes.popperNav}`}
                    disablePortal
                    transition>
                    {({TransitionProps, placement}) => (
                        <Grow id="notification-menu-list-grow"
                            style={{transformOrigin: placement === "bottom" ? "center top" : "center bottom"}}
                            {...TransitionProps}>
                            <Paper>
                                <ClickAwayListener onClickAway={handleCloseNotification}>
                                    <MenuList role="menu">
                                        <MenuItem onClick={handleCloseNotification} className={classes.dropdownItem}>
                                            Mike John responded to your email
                                        </MenuItem>

                                        <MenuItem onClick={handleCloseNotification} className={classes.dropdownItem}>
                                            You have 5 new tasks
                                        </MenuItem>

                                        <MenuItem onClick={handleCloseNotification} className={classes.dropdownItem}>
                                            You{"'"}re now friend with Andrew
                                        </MenuItem>

                                        <MenuItem onClick={handleCloseNotification} className={classes.dropdownItem}>
                                            Another Notification
                                        </MenuItem>

                                        <MenuItem onClick={handleCloseNotification} className={classes.dropdownItem}>
                                            Another One
                                        </MenuItem>
                                    </MenuList>
                                </ClickAwayListener>
                            </Paper>
                        </Grow>
                    )}
                </Popper>
            </div>
            <div className={classes.manager}>
                <Button
                    color={window.innerWidth > 959 ? "transparent" : "white"}
                    justIcon={window.innerWidth > 959}
                    simple={!(window.innerWidth > 959)}
                    aria-owns={openProfile ? "profile-menu-list-grow" : null}
                    aria-haspopup="true"
                    onClick={handleClickProfile}
                    className={classes.buttonLink}>

                    <Person className={classes.icons}/>
                    <Hidden mdUp implementation="css">
                        <p className={classes.linkText}>Profile</p>
                    </Hidden>
                </Button>
                <Popper
                    open={Boolean(openProfile)}
                    anchorEl={openProfile}
                    transition
                    disablePortal
                    className={`${classNames({[classes.popperClose]: !openProfile})} ${classes.popperNav}`}>
                    {({TransitionProps, placement}) => (
                        <Grow
                            id="profile-menu-list-grow"
                            style={{transformOrigin: placement === "bottom" ? "center top" : "center bottom"}}
                            {...TransitionProps}>
                            <Paper>
                                <ClickAwayListener onClickAway={handleCloseProfile}>
                                    <MenuList role="menu">
                                        <MenuItem className={classes.dropdownItem} onClick={handleCloseProfile}>
                                            Profile
                                        </MenuItem>
                                        <MenuItem className={classes.dropdownItem} onClick={handleCloseProfile}>
                                            Settings
                                        </MenuItem>
                                        <Divider light/>
                                        <MenuItem className={classes.dropdownItem} onClick={handleCloseProfile}>
                                            Logout
                                        </MenuItem>
                                    </MenuList>
                                </ClickAwayListener>
                            </Paper>
                        </Grow>
                    )}
                </Popper>
            </div>
        </div>
    );
}
