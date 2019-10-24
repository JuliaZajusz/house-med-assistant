import React from 'react';
import Button from '@material-ui/core/Button';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import {fade} from "@material-ui/core/styles";
import Fab from "@material-ui/core/Fab";
import SalesmanSetsList from "./SalesmanSetsList";
import connect from "react-redux/es/connect/connect";
import {addNewSalesmanSet, setSalesmanSet} from "../actions/salesmanSetActions";
import Delete from '@material-ui/icons/Delete';
import ButtonGroup from "@material-ui/core/ButtonGroup";


const useStyles = makeStyles(theme => ({
    side_container: {
        overflow: 'hidden',
    },
    button: {
        background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
        color: 'white',
        border: 0,
        borderRadius: 3,
        boxShadow: '0 3px 5px 2px rgba(255, 105, 135, .3)',
    },
    top_side_panel: {
        padding: '8px',
        alignItems: 'stretch',
        background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
    },
    side_panel: {
        paddingBottom: '1px',
        alignItems: 'stretch',
    },
    top_paper: {
        marginBottom: '8px',
        padding: '8px',
    },
    paper: {
        padding: '8px',
        borderRadius: 0,
    },
    search_panel: {
        padding: '8px',
        background: theme.palette.primary.main
    },
    search: {
        position: 'relative',
        borderRadius: theme.shape.borderRadius,
        backgroundColor: fade(theme.palette.common.white, 0.15),
        '&:hover': {
            backgroundColor: fade(theme.palette.common.white, 0.25),
        },
        marginLeft: 0,
        width: '100%',
        [theme.breakpoints.up('sm')]: {
            marginLeft: theme.spacing(1),
            width: 'auto',
        },
        border: '1px solid grey',
    },
    searchIcon: {
        width: theme.spacing(7),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    },
    inputRoot: {
        color: 'inherit',
    },
    inputInput: {
        padding: theme.spacing(1, 1, 1, 7),
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('md')]: {
            width: 200,
        },
    },
    flexWrapNowrap: {
        flexWrap: 'nowrap',
    }
}));

const mapStateToProps = (state) => {
    return {
        mapSalesmanSet: state.salesmanSetReducer.mapSalesmanSet,
    }
};

const mapDispatchToProps = dispatch => ({
    addNewSalesmanSet: () => dispatch(addNewSalesmanSet()),
    setSalesmanSet: () => dispatch(setSalesmanSet()),
});

export default connect(mapStateToProps, mapDispatchToProps)(function SidePanel(props) {
    const classes = useStyles();

    const addNewSalesmanSet = () => {
        props.addNewSalesmanSet()
    }
    //
    // const addToSet = (place) => {
    //     props.onAddToSet(place)
    // }
    //
    // const loadData = () => {
    //     props.onLoadData()
    // }

    return (
        <Grid
            className={classes.side_container}
        >
            <Grid
                item
                container
                direction="column"
                justify="center"
                alignItems="center"
                className={classes.top_side_panel}
            >
                <Grid container
                      direction="row"
                      justify="flex-start"
                      className={classes.flexWrapNowrap}
                  // flexWrap="nowrap"
                      alignItems='stretch'
                >
                    <Grid item>
                        <ButtonGroup size="small" aria-label="small outlined button group">
                            <Button onClick={() => props.setSalesmanSet()}>Wyczyść</Button>
                            {/*<Button>Two</Button>*/}
                            {/*<Button>Three</Button>*/}
                        </ButtonGroup>
                    </Grid>
                </Grid>
                {props.mapSalesmanSet && props.mapSalesmanSet.places && props.mapSalesmanSet.places.map((place) =>
                  <Paper
                    key={place.id}
                    className={classes.top_paper}
                    // onClick={() => addToSet(place)}
                >
                      <Grid container
                            direction="row"
                            justify="flex-start"
                            className={classes.flexWrapNowrap}
                        // flexWrap="nowrap"
                            alignItems='stretch'
                      >
                          <div style={{fontSize: '10px', color: 'grey'}}>
                              {place.id}
                          </div>
                          <Delete fontSize="small"/>
                      </Grid>
                    {place.name && <div>
                        {place.name}
                    </div>}
                    <div>
                        {place.location[0]}, {place.location[1]}
                    </div>

                </Paper>)
                }
                <Fab
                    variant="extended"
                    size="small"
                    color="primary"
                    aria-label="add"
                    className={classes.margin}
                    onClick={() => addNewSalesmanSet()}
                >
                    Wylicz trasę
                </Fab>
            </Grid>
            <SalesmanSetsList/>
        </Grid>
    );
})
