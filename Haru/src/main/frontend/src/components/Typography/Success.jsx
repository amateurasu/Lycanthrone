import React from "react";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";

import styles from "./TypographyStyle";

const useStyles = makeStyles(styles);

export default function Success(props) {
    const {children} = props;
    const {defaultFontStyle, successText} = useStyles();
    return (
        <div className={`${defaultFontStyle} ${successText}`}>{children}</div>
    );
}

Success.propTypes = {
    children: PropTypes.node
};
