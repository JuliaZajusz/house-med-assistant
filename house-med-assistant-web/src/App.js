import React, {useState} from 'react';
import './App.css';
import SimpleExample from "./Map";
import {getSalesmanSet} from "./services/SalesmanSetService";
import {Toolbar} from '@material-ui/core';
import Typography from "@material-ui/core/Typography";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import {createMuiTheme} from '@material-ui/core/styles';
import purple from '@material-ui/core/colors/purple';
import ThemeProvider from "@material-ui/styles/ThemeProvider";
import AppBar from "@material-ui/core/AppBar";
import SidePanel from "./SidePanel";

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

function App() {
    const classes = useStyles();

    const [salesmanSet, setSalesmanSet] = useState(() => {
        return {places: []}
        }
    );

    const [currencies, setCurrencies] = useState(() => {
            return {}
        }
    );

    const [savedPlaces, setSavedPlaces] = useState(() => {
            return [
                {
                    "id": "dgsfs",
                    "location": [
                        3.3,
                        3.4
                    ]
                },
                {
                    "id": "jhgfd",
                    "location": [
                        20,
                        51.6
                    ]
                },
                {
                    "id": "jhu5454w",
                    "location": [
                        18,
                        51
                    ]
                },
                {
                    "id": "j45y3gw",
                    "location": [
                        19,
                        19
                    ]
                }]
        }
    );

    function loadData() {
        //  const { loading, error, data } = useQuery(gql`
        //     {
        //         rates(currency: "USD") {
        //             currency
        //             rate
        //         }
        //     }
        // `);
        //
        // setCurrencies(data)

    }

    const showSet = () => {
        console.log("odbywa się łądowanie")
        let set = getSalesmanSet()
        setSalesmanSet({...salesmanSet, places: [...salesmanSet.places, ...set.places]})
    }

    const addToSet = (place) => {
        setSalesmanSet({...salesmanSet, places: [...salesmanSet.places, place]})
    }


    return (
        <div className={classes.app}>
            <ThemeProvider theme={theme}>
                <AppBar position="static">
                    <Toolbar
                        // className={classes.toolbar}
                    >
                    <Typography variant="h6" className={classes.title}>
                        house med assistant
                    </Typography>
                </Toolbar>
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
                            salesmanSet={salesmanSet}
                            savedPlaces={savedPlaces}
                            onLoadData={() => loadData()}
                            onShowSet={() => showSet()}
                            onAddToSet={(place) => addToSet(place)}
                        />
                    </Grid>
                    <Grid item xs={9}>
                        <SimpleExample data={salesmanSet}/>
                    </Grid>
                </Grid>
            </ThemeProvider>
        </div>
)
    ;
}

export default App;
