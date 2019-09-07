import React from 'react';
import Button from '@material-ui/core/Button';
import makeStyles from "@material-ui/core/styles/makeStyles";
import Grid from "@material-ui/core/Grid";
import Paper from "@material-ui/core/Paper";
import InputBase from "@material-ui/core/InputBase";
import SearchIcon from '@material-ui/icons/Search';
import {fade} from "@material-ui/core/styles";


const useStyles = makeStyles(theme => ({
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
}));

export default function SidePanel(props) {
    const classes = useStyles();
    //
    // const [state, setState] = useState(() => {
    //         return null
    //     }
    // );
    //

    const showSet = () => {
        props.onShowSet()
    }

    const addToSet = (place) => {
        props.onAddToSet(place)
    }


    return (
        <Grid>
            <Grid
                item
                container
                direction="column"
                justify="center"
                alignItems="center"
                className={classes.top_side_panel}
            >
                {props.salesmanSet.places.map((place) => <Paper
                    className={classes.top_paper}
                    onClick={() => addToSet(place)}
                >{place.id}</Paper>)
                }

                <Button className={classes.button} onClick={() => showSet()}>Show set</Button>
            </Grid>

            <Grid className={classes.search_panel}>
                <div className={classes.search}>
                    <div className={classes.searchIcon}>
                        <SearchIcon/>
                    </div>
                    <InputBase
                        placeholder="Szukaj zapisanych punktów..."
                        classes={{
                            root: classes.inputRoot,
                            input: classes.inputInput,
                        }}
                        inputProps={{'aria-label': 'search'}}
                    />
                </div>
            </Grid>

            <Grid
                item
                container
                direction="column"
                justify="center"
                alignItems="center"
                className={classes.side_panel}
            >
                {props.savedPlaces.filter(place => !props.salesmanSet.places.includes(place)).map((place) => <Paper
                    className={classes.paper}
                    onClick={() => addToSet(place)}
                >{place.id}</Paper>)
                }
            </Grid>

        </Grid>
    )
        ;
}
