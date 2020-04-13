import React from "react";
import PropTypes from "prop-types";
import styles from "./TypographyStyle";
import {makeStyles} from "@material-ui/core/styles";

const useStyles = makeStyles(styles);

export default function Primary(props) {
    const {defaultFontStyle, primaryText} = useStyles();
    const {children} = props;
    return (
        <div className={`${defaultFontStyle} ${primaryText}`}>
            {children}
        </div>
    );
}

Primary.propTypes = {
    children: PropTypes.node
};
