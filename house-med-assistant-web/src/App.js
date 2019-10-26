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
import {BrowserRouter} from "react-router-dom";
import {createBrowserHistory} from 'history';
import PatientsPanel from "./components/PatientsPanel";

export const history = createBrowserHistory();

const theme = createMuiTheme({
    palette: {
        primary: purple,
        secondary: {
            main: '#f44336',
            dark: '#424242',
            light: '#fefefe',
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


    // const simpleAction = (event) => {
    // //     props.simpleAction().then((res) =>
    // //       console.log("THEN", res));
    // //     console.log("2", props);
    //     console.log(props)
    // };


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
                        {/*<Link to="/about">Home</Link>*/}
                        {/*<button onClick={(e) => simpleAction(e)}>Test redux action</button>*/}
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
                            <SidePanel/>
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

export default App;
