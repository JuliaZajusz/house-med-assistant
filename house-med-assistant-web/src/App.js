import React from 'react';
import './App.css';
import MapWrapper from "./components/Map";
import {Toolbar} from '@material-ui/core';
import Typography from "@material-ui/core/Typography";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import {createMuiTheme} from '@material-ui/core/styles';
import purple from '@material-ui/core/colors/purple';
import ThemeProvider from "@material-ui/styles/ThemeProvider";
import AppBar from "@material-ui/core/AppBar";
import SidePanel from "./components/SidePanel";
import {BrowserRouter, Link} from "react-router-dom";
import {createBrowserHistory} from 'history';
import {simpleAction} from './actions/patientActions';
import PatientsPanel from "./components/PatientsPanel";
import connect from "react-redux/es/connect/connect";

export const history = createBrowserHistory();

const theme = createMuiTheme({
    palette: {
        primary: purple,
        secondary: {
            main: '#f44336',
        },
    },
    shape: {
        borderRadius: '3px',
    }
});

const useStyles = makeStyles(theme => ({
    app: {
        height: '100%',
        maxHeight: '100%',
        width: '100%',
        maxWidth: '100%',
        display: 'flex',
        flexDirection: 'column',
    },
    toolbar: {
        background: theme.palette.primary.main
    },
    main_layout_box: {
        flexGrow: 1,
        height: '100%',
        maxHeight: '100%',
    },
    title: {
        flexGrow: 1,
    },
    side_container_container: {
        height: '100%',
        maxHeight: '100%',
        overflowY: 'auto'
    }
}));

function App(props, state) {
    const classes = useStyles();


    // //TODO do usuniecia
    // const [salesmanSet, setSalesmanSet] = useState(() => {
    //     if (history.location.pathname) {
    //         let id = history.location.pathname
    //         id = id.substr(1);
    //         return {
    //             id: id.length > 0 ? id : null,
    //             places: [],
    //             path: []
    //         }
    //     }
    //     return {
    //         places: [],
    //         path: []
    //     }
    //     }
    // );

    // const [savedPlaces, setSavedPlaces] = useState(() => {
    //         return [
    //             {
    //                 "id": "dgsfs",
    //                 "location": [
    //                     3.3,
    //                     3.4
    //                 ]
    //             },
    //             {
    //                 "id": "jhgfd",
    //                 "location": [
    //                     20,
    //                     51.6
    //                 ]
    //             },
    //             {
    //                 "id": "jhu5454w",
    //                 "location": [
    //                     18,
    //                     51
    //                 ]
    //             },
    //             {
    //                 "id": "j45y3gw",
    //                 "location": [
    //                     19,
    //                     19
    //                 ]
    //             }]
    //     }
    // );

    // const loadData = () => {
    //     getAllSalesmanSets()
    //         .then(result => {
    //                 console.log(result)
    //                 let set = result.data.salesmanSets[1]
    //                 setSalesmanSet({...set, places: [...salesmanSet.places, ...set.places]})
    //             }
    //         );
    // };
    //
    // const updateSalesmanSet = () => {
    //
    //     putSalesmanSet(salesmanSet)
    //         .then(response => {
    //             repeat(response.data.updateSalesmanSet)
    //         })
    // }

    // const repeat = (set) => {
    //     history.push({
    //         pathname: `/${set.id}`
    //     })
    //     setSalesmanSet(set)
    //     upgradeSalesmanSet(set.id, 2, 20, 20)
    //         .then((response) => {
    //             //TODO jeśli stan się zmienił, doszedł nowy punkt to nie update'uj, przerwij request
    //             setSalesmanSet(response.data.upgradeSalesmanSet)
    //         })
    // }

    // const addNewSalesmanSet = () => {
    //     if (salesmanSet.id == null) {
    //         postSalesmanSet(salesmanSet)
    //             .then(response => {
    //                 repeat(response.data.newSalesmanSet);
    //             })
    //     } else {
    //         updateSalesmanSet()
    //     }
    // }
    //
    // const addToSet = (place) => {
    //     setSalesmanSet({...salesmanSet, places: [...salesmanSet.places, place]})
    // }

    const simpleAction = (event) => {
        props.simpleAction().then((res) =>
          console.log("THEN", res));
        console.log("2", props);
    };


    return (
        <div className={classes.app}>
            <BrowserRouter history={history}>
                <ThemeProvider theme={theme}>
                    <AppBar position="static">
                        <Toolbar>
                            <Typography variant="h6" className={classes.title}>
                                house med assistant
                            </Typography>
                        </Toolbar>
                        <Link to="/about">Home</Link>
                        <button onClick={(e) => simpleAction(e)}>Test redux action</button>
                    </AppBar>
                    <Grid container
                          direction="row"
                          justify="center"
                          className={classes.main_layout_box}
                          alignItems='stretch'
                    >
                        <Grid item xs={3}
                              flexgrow={1}
                              className={classes.side_container_container}
                        >
                            <SidePanel
                              // salesmanSet={salesmanSet}
                              // savedPlaces={savedPlaces}
                              // onLoadData={() => loadData()}
                              // onAddToSet={(place) => addToSet(place)}
                              // onAddNewSalesmanSet={() => addNewSalesmanSet()}
                              // onSetSalesmanSet={(salesmanSet) => setSalesmanSet(salesmanSet)}
                            />
                        </Grid>
                      <Grid item xs={6}>
                          <MapWrapper/>
                      </Grid>
                      <Grid item xs={3}
                            flexgrow={1}
                            className={classes.side_container_container}
                      >
                          <PatientsPanel/>
                      </Grid>
                    </Grid>
                </ThemeProvider>
            </BrowserRouter>
        </div>
    );
}

// const mapStateToProps = state => ({
//     ...state,
//     "ala": "makota"
// })

const mapStateToProps = (state) => {
    return {
      // ...state,
        result: state.patientReducer.result,
        "ala": "makota"
    }
};

const mapDispatchToProps = dispatch => ({
    simpleAction: () => dispatch(simpleAction())
});

export default connect(mapStateToProps, mapDispatchToProps)(App);
// export default App;
