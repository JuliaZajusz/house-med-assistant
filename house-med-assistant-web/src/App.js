import React, {useState} from 'react';
import './App.css';
import SimpleExample from "./Map";
import {getSalesmanSet} from "./services/SalesmanSetService";
import Button from '@material-ui/core/Button';
import {Toolbar} from '@material-ui/core';
import Typography from "@material-ui/core/Typography";
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import {createMuiTheme} from '@material-ui/core/styles';
import purple from '@material-ui/core/colors/purple';
import ThemeProvider from "@material-ui/styles/ThemeProvider";

const theme = createMuiTheme({
    palette: {
        primary: purple,
        secondary: {
            main: '#f44336',
        },
    },
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
        flexGrow: 1
    },
    button: {
        background: 'linear-gradient(45deg, #FE6B8B 30%, #FF8E53 90%)',
        color: 'white',
        border: 0,
        borderRadius: 3,
        boxShadow: '0 3px 5px 2px rgba(255, 105, 135, .3)',
    },
    title: {
        flexGrow: 1,
    },
    side_panel: {
        padding: '8px',
        display: 'flex',
        alignItems: 'stretch'
    },
    paper: {
        flexGrow: 1,
        marginBottom: '8px',
        padding: '8px',
    }
}));

function App() {
    const classes = useStyles();

    const [state, setState] = useState(() => {
            return null
        }
    );


    const showSet = () => {
        console.log("odbywa się łądowanie")
        setState(getSalesmanSet())
    }


    return (
        <div className={classes.app}>
            <ThemeProvider theme={theme}>
                <Toolbar>
                    <Typography variant="h6" className={classes.title}>
                        house med assistant
                    </Typography>
                </Toolbar>
                <Grid container
                      direction="row"
                      justify="center"
                      className={classes.main_layout_box}
                      alignItems='stretch'
                >
                    <Grid item xs={3}
                          flexgrow={1}
                    >
                        <Grid
                            item
                            container
                            direction="column"
                            justify="center"
                            alignItems="center"
                            className={classes.side_panel}
                        >
                            <Paper className={classes.paper}>Jan Kowalski</Paper>
                            <Paper className={classes.paper}>Jan Kowalski</Paper>
                            <Paper className={classes.paper}>Jan Kowalski</Paper>
                            ala
                            <Button className={classes.button} onClick={() => showSet()}>Show set</Button>
                        </Grid>
                    </Grid>
                    <Grid item xs={9}>
                        <SimpleExample data={state}/>
                    </Grid>
                </Grid>
            </ThemeProvider>
        </div>
)
    ;
}

export default App;
