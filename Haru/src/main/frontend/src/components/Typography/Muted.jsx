import React from "react";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";

import styles from "./TypographyStyle";

const useStyles = makeStyles(styles);

export default function Muted(props) {
    const {defaultFontStyle, mutedText} = useStyles();
    const {children} = props;
    return (
        <div className={`${defaultFontStyle} ${mutedText}`}>{children}</div>
    );
}

Muted.propTypes = {
    children: PropTypes.node
};
