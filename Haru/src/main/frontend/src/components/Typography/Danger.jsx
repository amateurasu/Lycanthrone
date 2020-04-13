import React from "react";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";
import styles from "./TypographyStyle";

const useStyles = makeStyles(styles);

export default function Danger(props) {
    const {children} = props;
    const {defaultFontStyle, dangerText} = useStyles();
    return (
        <div className={`${defaultFontStyle} ${dangerText}`}>{children}</div>
    );
}

Danger.propTypes = {
    children: PropTypes.node
};
