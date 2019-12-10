import React from "react";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";

import styles from "./TypographyStyle";

const useStyles = makeStyles(styles);

export default function Quote(props) {
    const {defaultFontStyle, quote, quoteAuthor, quoteText} = useStyles();
    const {text, author} = props;
    return (
        <blockquote className={`${defaultFontStyle} ${quote}`}>
            <p className={quoteText}>{text}</p>
            <small className={quoteAuthor}>{author}</small>
        </blockquote>
    );
}

Quote.propTypes = {
    text: PropTypes.node,
    author: PropTypes.node
};
