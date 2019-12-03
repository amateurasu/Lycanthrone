import React from "react";
import PropTypes from "prop-types";
import {MemoryRouter, Route} from "react-router";
import {Link as RouterLink} from "react-router-dom";

import {
    Breadcrumbs,
    Chip,
    Collapse,
    Link,
    List,
    ListItem,
    ListItemIcon,
    ListItemText,
    Menu,
    MenuItem,
    Typography
} from "@material-ui/core";
import {emphasize, makeStyles, withStyles} from "@material-ui/core/styles";
import {Drafts, ExpandLess, ExpandMore, File, Folder, Home, Inbox, NavigateNext, Send} from "@material-ui/icons";

//region CREATE STYLED ELEMENTS
const StyledBreadcrumb = withStyles(theme => ({
    root: {
        backgroundColor: theme.palette.grey[110],
        height: theme.spacing(3),
        color: theme.palette.grey[800],
        fontWeight: theme.typography.fontWeightRegular,
        "&:hover, &:focus": {
            backgroundColor: theme.palette.grey[300]
        },
        "&:active": {
            boxShadow: theme.shadows[1],
            backgroundColor: emphasize(theme.palette.grey[300], 0.12)
        }
    }
}))(props => (
    <Chip {...props}>
        <LinkRouter/>
    </Chip>
));

const StyledMenu = withStyles({
    paper: {
        border: "1px solid #d3d4d5"
    }
})(props => (
    <Menu elevation={0} getContentAnchorEl={null}
        anchorOrigin={{vertical: "bottom", horizontal: "center"}}
        transformOrigin={{vertical: "top", horizontal: "center"}}
        {...props}/>
));

const StyledMenuItem = withStyles(theme => ({
    root: {
        "&:focus": {
            backgroundColor: theme.palette.primary.main,
            "& .MuiListItemIcon-root, & .MuiListItemText-primary": {
                color: theme.palette.common.white
            }
        }
    }
}))(MenuItem);

//endregion CREATE STYLED ELEMENTS

export function CustomizedBreadcrumbs() {

    const [anchorEl, setAnchorEl] = React.useState(null);

    const handleClick = e => {
        e.preventDefault();
        console.log(e.currentTarget);
    };

    const handleClose = () => setAnchorEl(null);

    const handleDelete = e => {
        e.preventDefault();
        console.log(e.currentTarget);
        setAnchorEl(e.currentTarget);
    };

    return (
        <React.Fragment>
            <Breadcrumbs aria-label="breadcrumb" maxItems={2} separator={<NavigateNext fontSize="small"/>}>
                <StyledBreadcrumb label="Home" deleteIcon={<ExpandMore/>} icon={<Home fontSize="small"/>}
                    onClick={handleClick} onDelete={handleDelete}/>
                <StyledBreadcrumb label="Catalog" deleteIcon={<ExpandMore/>} icon={<Folder fontSize="small"/>}
                    onClick={handleClick} onDelete={handleDelete}/>
                <StyledBreadcrumb label="Accessories" aria-controls="customized-menu" icon={<File/>}
                    onClick={handleClick} onDelete={handleDelete}/>
            </Breadcrumbs>

            <StyledMenu id="customized-menu" anchorEl={anchorEl} open={Boolean(anchorEl)}
                onClose={handleClose} keepMounted>

                <StyledMenuItem onClick={handleClose}>
                    <ListItemIcon><Send fontSize="small"/></ListItemIcon>
                    <ListItemText primary="Sent mail"/>
                </StyledMenuItem>

                <StyledMenuItem onClick={handleClose}>
                    <ListItemIcon><Drafts fontSize="small"/></ListItemIcon>
                    <ListItemText primary="Drafts"/>
                </StyledMenuItem>

                <StyledMenuItem onClick={handleClose}>
                    <ListItemIcon><Inbox fontSize="small"/></ListItemIcon>
                    <ListItemText primary="Inbox"/>
                </StyledMenuItem>
            </StyledMenu>
        </React.Fragment>
    );
}

const breadcrumbNameMap = {
    "/inbox": "Inbox",
    "/inbox/important": "Important",
    "/trash": "Trash",
    "/spam": "Spam",
    "/drafts": "Drafts"
};

function ListItemLink({to, open, ...other}) {
    const primary = breadcrumbNameMap[to];

    return (
        <li>
            <ListItem button component={RouterLink} to={to} {...other}>
                <ListItemText primary={primary}/>
                {open != null ? open ? <ExpandLess/> : <ExpandMore/> : null}
            </ListItem>
        </li>
    );
}

ListItemLink.propTypes = {
    open: PropTypes.bool,
    to: PropTypes.string.isRequired
};

const useStyles = makeStyles(theme => ({
    root: {
        display: "flex",
        flexDirection: "column",
        width: 360
    },
    lists: {
        backgroundColor: theme.palette.background.paper,
        marginTop: theme.spacing(1)
    },
    nested: {
        paddingLeft: theme.spacing(4)
    }
}));

const LinkRouter = props => <Link {...props} component={RouterLink}/>;

export function RouterBreadcrumbs() {
    const classes = useStyles();
    const [open, setOpen] = React.useState(true);

    const handleClick = () => setOpen(prevOpen => !prevOpen);

    return <MemoryRouter initialEntries={["/inbox"]} initialIndex={0}>
        <div className={classes.root}>
            <Route>
                {({location}) => {
                    const pathnames = location.pathname.split("/").filter(x => x);

                    return (
                        <Breadcrumbs aria-label="breadcrumb">
                            <LinkRouter color="inherit" to="/">Home</LinkRouter>
                            {pathnames.map((value, index) => {
                                const last = index === pathnames.length - 1;
                                const to = `/${pathnames.slice(0, index + 1).join("/")}`;

                                return last ? (
                                    <Typography color="textPrimary" key={to}>{breadcrumbNameMap[to]}</Typography>
                                ) : (
                                    <LinkRouter color="inherit" to={to} key={to}>{breadcrumbNameMap[to]}</LinkRouter>
                                );
                            })}
                        </Breadcrumbs>
                    );
                }}
            </Route>
            <nav className={classes.lists} aria-label="mailbox folders">
                <List>
                    <ListItemLink to="/inbox" open={open} onClick={handleClick}/>
                    <Collapse component="li" in={open} timeout="auto" unmountOnExit>
                        <List disablePadding>
                            <ListItemLink to="/inbox/important" className={classes.nested}/>
                        </List>
                    </Collapse>
                    <ListItemLink to="/trash"/>
                    <ListItemLink to="/spam"/>
                </List>
            </nav>
        </div>
    </MemoryRouter>;
}
