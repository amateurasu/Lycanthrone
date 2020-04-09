import React from "react";
import classNames from "classnames";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";
import {Tab, Tabs} from "@material-ui/core";

import Card from "../Card/Card";
import CardBody from "../Card/CardBody";
import CardHeader from "../Card/CardHeader";

import styles from "./CustomTabsStyle";

const useStyles = makeStyles(styles);

export default function CustomTabs(props) {
    const [value, setValue] = React.useState(0);
    const handleChange = (event, value) => setValue(value);
    const classes = useStyles();
    const {headerColor, plainTabs, tabs, title, rtlActive} = props;
    const cardTitle = classNames({
        [classes.cardTitle]: true,
        [classes.cardTitleRTL]: rtlActive
    });
    return (
        <Card plain={plainTabs}>
            <CardHeader color={headerColor} plain={plainTabs}>
                {title !== undefined && <div className={cardTitle}>{title}</div>}
                <Tabs
                    value={value}
                    onChange={handleChange}
                    classes={{
                        root: classes.tabsRoot,
                        indicator: classes.displayNone,
                        scrollButtons: classes.displayNone
                    }}
                    variant="scrollable"
                    scrollButtons="auto"
                >
                    {tabs.map((prop, key) => {
                        let icon = {};
                        if (prop.tabIcon) {
                            icon = {icon: <prop.tabIcon/>};
                        }
                        return (
                            <Tab
                                classes={{
                                    root: classes.tabRootButton,
                                    selected: classes.tabSelected,
                                    wrapper: classes.tabWrapper
                                }}
                                key={key}
                                label={prop.tabName}
                                {...icon}
                            />
                        );
                    })}
                </Tabs>
            </CardHeader>
            <CardBody>
                {tabs.map((prop, key) => {
                    return key === value ? <div key={key}>{prop.tabContent}</div> : null;
                })}
            </CardBody>
        </Card>
    );
}

CustomTabs.propTypes = {
    headerColor: PropTypes.oneOf([
        "warning",
        "success",
        "danger",
        "info",
        "primary",
        "rose"
    ]),
    title: PropTypes.string,
    tabs: PropTypes.arrayOf(
        PropTypes.shape({
            tabName: PropTypes.string.isRequired,
            tabIcon: PropTypes.object,
            tabContent: PropTypes.node.isRequired
        })
    ),
    rtlActive: PropTypes.bool,
    plainTabs: PropTypes.bool
};
