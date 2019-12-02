import React from "react";
import PropTypes from "prop-types";
import {makeStyles, Typography} from "@material-ui/core";
import {TreeItem, TreeView} from "@material-ui/lab";

import {
    ArrowDropDown,
    ArrowRight,
    Delete,
    Forum,
    Info as InfoIcon,
    Label,
    LocalOffer,
    Mail,
    SupervisorAccount
} from "@material-ui/icons";

const useTreeItemStyles = makeStyles(theme => ({
    root: {
        color: theme.palette.text.secondary,
        maxHeight: 250,
        "&:focus > $content, &:hover > $content": {
            backgroundColor: `var(--tree-view-bg-color, ${theme.palette.grey[400]})`,
            color: "var(--tree-view-color)"
        }
    },
    content: {
        color: theme.palette.text.secondary,
        borderTopRightRadius: theme.spacing(2),
        borderBottomRightRadius: theme.spacing(2),
        paddingRight: theme.spacing(1),
        fontWeight: theme.typography.fontWeightMedium,
        "$expanded > &": {
            fontWeight: theme.typography.fontWeightRegular
        }
    },
    group: {
        marginLeft: 0,
        "& $content": {
            paddingLeft: theme.spacing(2)
        }
    },
    expanded: {},
    label: {
        fontWeight: "inherit",
        color: "inherit"
    },
    labelRoot: {
        display: "flex",
        alignItems: "center",
        padding: theme.spacing(0.5, 0)
    },
    labelIcon: {
        marginRight: theme.spacing(1)
    },
    labelText: {
        fontWeight: "inherit",
        flexGrow: 1
    }
}));

const StyledTreeItem = ({labelText, labelIcon: LabelIcon, labelInfo, color, bgColor, ...other}) => {
    const {root, content, expanded, group, label, labelIcon, labelRoot, labelText: labelTxt} = useTreeItemStyles();

    const handleClick = () => {

    };

    const itemLabel = (
        <div className={labelRoot}>
            <LabelIcon color="inherit" className={labelIcon}/>
            <Typography variant="body2" className={labelTxt}>{labelText}</Typography>
            <Typography variant="caption" color="inherit">{labelInfo}</Typography>
        </div>
    );

    return (
        <TreeItem
            label={itemLabel}
            style={{"--tree-view-color": color, "--tree-view-bg-color": bgColor}}
            classes={{root, content, expanded, group, label}}
            onClick={handleClick} {...other}/>
    );
};

StyledTreeItem.propTypes = {
    bgColor: PropTypes.string,
    color: PropTypes.string,
    labelIcon: PropTypes.elementType.isRequired,
    labelInfo: PropTypes.string,
    labelText: PropTypes.string.isRequired
};

const useStyles = makeStyles({
    root: {
        height: 264,
        flexGrow: 1,
        maxWidth: 400
    }
});

const GmailTreeView = () => {
    const {root} = useStyles();

    const handleClick = (e) => {
        console.log(e.target);
    };

    return (
        <TreeView className={root}
            defaultCollapseIcon={<ArrowDropDown/>} defaultExpandIcon={<ArrowRight/>}
            defaultEndIcon={<div style={{width: 24}}/>}>

            <StyledTreeItem nodeId="1" labelText="All Mail" labelInfo="" labelIcon={Mail} onClick={handleClick}/>
            <StyledTreeItem nodeId="2" labelText="Trash" labelInfo="" labelIcon={Delete} onClick={handleClick}/>
            <StyledTreeItem nodeId="3" labelText="Categories" labelInfo="" labelIcon={Label} onClick={handleClick}>
                <StyledTreeItem nodeId="5" labelText="Social" labelIcon={SupervisorAccount}
                    labelInfo="90" color={"#1A73E8"} bgColor={"#E8F0FE"}/>
                <StyledTreeItem nodeId="6" labelText="Updates" labelIcon={InfoIcon}
                    labelInfo="2,294" color={"#E3742F"} bgColor={"#fcefe3"}>
                    <StyledTreeItem nodeId="7" labelText="Forums" labelIcon={Forum}
                        labelInfo="3,566" color={"#A250F5"} bgColor={"#F3E8FD"}/>
                </StyledTreeItem>
                <StyledTreeItem nodeId="8" labelText="Promotions" labelIcon={LocalOffer}
                    labelInfo="733" color={"#3C8039"} bgColor={"#E6F4EA"}/>
            </StyledTreeItem>
            <StyledTreeItem nodeId="4" labelText="History" labelInfo="" labelIcon={Label} onClick={handleClick}/>
        </TreeView>
    );
};

export default GmailTreeView;
