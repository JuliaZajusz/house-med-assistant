import React from 'react';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import SalesmanSetsList from "./SalesmanSetsList";
import connect from "react-redux/es/connect/connect";
import ActualSalesmanSet from "./ActualSalesmanSet";


const useStyles = makeStyles(theme => ({
    side_container: {
        overflow: 'hidden',
    }
}));

const mapStateToProps = (state) => {
    return {
    }
};

const mapDispatchToProps = dispatch => ({
});

export default connect(mapStateToProps, mapDispatchToProps)(function SidePanel(props) {
    const classes = useStyles();

    return (
        <Grid
            className={classes.side_container}
        >
            <ActualSalesmanSet/>
            <SalesmanSetsList/>
        </Grid>
    );
})
