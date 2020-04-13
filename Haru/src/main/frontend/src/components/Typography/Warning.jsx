import React from "react";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";

import styles from "./TypographyStyle";

const useStyles = makeStyles(styles);

export default function Warning(props) {
    const {children} = props;
    const {defaultFontStyle, warningText} = useStyles();
    return (
        <div className={`${defaultFontStyle} ${warningText}`}>{children}</div>
    );
}

Warning.propTypes = {
    children: PropTypes.node
};
