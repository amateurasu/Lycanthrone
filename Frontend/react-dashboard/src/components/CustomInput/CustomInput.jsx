import React from "react";
import classNames from "classnames";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";
import {FormControl, InputLabel, Input} from "@material-ui/core";

import {Clear, Check} from "@material-ui/icons";

import styles from "./CustomInputStyle";

const useStyles = makeStyles(styles);

export default function CustomInput(props) {
    const classes = useStyles();
    const {
        formControlProps,
        labelText,
        id,
        labelProps,
        inputProps,
        error,
        success
    } = props;

    const labelClasses = classNames({
        [` ${classes.labelRootError}`]: error,
        [` ${classes.labelRootSuccess}`]: success && !error
    });
    const underlineClasses = classNames({
        [classes.underlineError]: error,
        [classes.underlineSuccess]: success && !error,
        [classes.underline]: true
    });
    const marginTop = classNames({
        [classes.marginTop]: labelText === undefined
    });
    return (
        <FormControl
            {...formControlProps}
            className={`${formControlProps.className} ${classes.formControl}`}
        >
            {labelText !== undefined && (
                <InputLabel htmlFor={id} className={classes.labelRoot + labelClasses} {...labelProps}>
                    {labelText}
                </InputLabel>
            )}
            <Input
                id={id}
                classes={{
                    root: marginTop,
                    disabled: classes.disabled,
                    underline: underlineClasses
                }}
                {...inputProps}/>
            {error ? (
                <Clear className={`${classes.feedback} ${classes.labelRootError}`}/>
            ) : success && (
                <Check className={`${classes.feedback} ${classes.labelRootSuccess}`}/>
            )}
        </FormControl>
    );
}

CustomInput.propTypes = {
    labelText: PropTypes.node,
    labelProps: PropTypes.object,
    id: PropTypes.string,
    inputProps: PropTypes.object,
    formControlProps: PropTypes.object,
    error: PropTypes.bool,
    success: PropTypes.bool
};
