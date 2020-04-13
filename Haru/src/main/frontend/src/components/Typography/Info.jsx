import React from "react";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";

import styles from "./TypographyStyle";

const useStyles = makeStyles(styles);

export default function Info(props) {
    const {defaultFontStyle, infoText} = useStyles();
    const {children} = props;
    return (
        <div className={`${defaultFontStyle} ${infoText}`}>{children}</div>
    );
}

Info.propTypes = {
    children: PropTypes.node
};
