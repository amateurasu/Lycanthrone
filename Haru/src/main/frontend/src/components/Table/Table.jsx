import React from "react";
import PropTypes from "prop-types";

import {makeStyles} from "@material-ui/core/styles";
import {Table, TableBody, TableCell, TableHead, TableRow} from "@material-ui/core";

import styles from "./TableStyle";

const useStyles = makeStyles(styles);

export default function CustomTable(props) {
    const classes = useStyles();
    const {tableHead, tableData, tableHeaderColor} = props;
    return (
        <div className={classes.tableResponsive}>
            <Table className={classes.table}>
                {tableHead !== undefined && (
                    <TableHead className={classes[`${tableHeaderColor}TableHeader`]}>
                        <TableRow className={classes.tableHeadRow}>
                            {tableHead.map((prop, key) => (
                                <TableCell key={key} className={`${classes.tableCell} ${classes.tableHeadCell}`}>
                                    {prop}
                                </TableCell>
                            ))}
                        </TableRow>
                    </TableHead>
                )}
                <TableBody>
                    {tableData.map((prop, key) => (
                        <TableRow key={key} className={classes.tableBodyRow}>
                            {prop.map((prop, key) =>
                                <TableCell className={classes.tableCell} key={key}>{prop}</TableCell>
                            )}
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </div>
    );
}

CustomTable.defaultProps = {tableHeaderColor: "gray"};

CustomTable.propTypes = {
    tableHeaderColor: PropTypes.oneOf(["warning", "primary", "danger", "success", "info", "rose", "gray"]),
    tableHead: PropTypes.arrayOf(PropTypes.string),
    tableData: PropTypes.arrayOf(PropTypes.arrayOf(PropTypes.string))
};
