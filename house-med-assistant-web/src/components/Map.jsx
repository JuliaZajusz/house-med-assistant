import React, {Component, createRef} from 'react'
import {Map, Marker, Polyline, Popup, TileLayer} from "react-leaflet";
import 'leaflet/dist/leaflet.css'
import L from 'leaflet';
import * as _ from 'lodash';
import {withStyles} from "@material-ui/styles";
import connect from "react-redux/es/connect/connect";
import {addPatientToSalesmanSet} from "../actions/salesmanSetActions";

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});

const outer = [[50.505, -29.09], [52.505, 29.09]]
const inner = [[49.505, -2.09], [53.505, 2.09]]

const classes = theme => ({
    legend: {
        // height: '200px',
        width: '100px',
        background: 'rgba(0,0,0,.5)',
        position: 'absolute',
        right: 0,
        top: 0,
        zIndex: 10000,
        padding: '8px',
        // visibility: 'visible',
        // opacity: 1,
    },
    legend_hidden: {
        // background: 'green',
        // visibility: 'hidden',
        opacity: 0,
        // transition: ' visibility 0s, opacity 5s linear',
    }
})

class MapWrapper extends Component {
    constructor(props) {
        super(props);
        this.mapRef = createRef()
    }

    state = {
        lat: 51.505,
        lng: -0.09,
        zoom: 13,
        markers: [],
        bounds: [[49.11, 15.04], [54.11, 23.04]],
        path: [],
        selectedPathIndex: 0,
        paths: []
    }


    onClickInner = () => {
        this.setState({bounds: inner})
    }

    onClickOuter = () => {
        this.setState({bounds: outer})
    }


    static getDerivedStateFromProps = (props, state) => {
        console.log("state", state)
        // console.log("props", props)
        let markers = props.data ? props.data.places.map((place) => [place.coordinate.location[1], place.coordinate.location[0]]) : []
        let path = (props.data && props.data.paths && props.data.paths[state.selectedPathIndex]) ? props.data.paths[state.selectedPathIndex].places.map((place) => [place.location[1], place.location[0]]) : []
        let paths = (props.data && props.data.paths) ?
            props.data.paths
                .map(path => {
                        let places = path.places
                            .map((place) =>
                                [place.location[1], place.location[0]]
                            )
                        return {...path, places: places, value: Math.round(path.value * 100) / 100}
                    }
                )
            : []

        console.log("paths", paths)
        // console.log("mark", markers)
        let newBounds = state.bounds;
        if (markers.length > 1) {
            let south = _.minBy(markers, (o) => {
                return o[0]
            })
            let west = _.minBy(markers, (o) => {
                return o[1]
            })
            let north = _.maxBy(markers, (o) => {
                return o[0]
            })
            let east = _.maxBy(markers, (o) => {
                return o[1]
            })
            newBounds = [[south, west], [north, east]]
        }

        return {
            ...state,
            bounds: newBounds,
            markers: markers,
            path: path,
            selectedPathIndex: 0,
            paths: paths
        }
    }

    addMarker = (e) => {
        const {markers} = this.state
        let lat = Math.round(e.latlng.lat * 100) / 100
        let lng = Math.round(e.latlng.lng * 100) / 100
        markers.push([lat, lng])
        this.props.addCoordinateToSalesmanSet([lng, lat])
    }

    polyline = [[51.505, -0.09], [51.51, -0.1], [51.51, -0.12]]

    render() {
        const startPosition = [this.state.lat, this.state.lng]
        const {classes} = this.props;
        return (
            <div style={{height: '100%', position: 'relative'}}>
                <div className={this.state.paths.length > 0
                    ? classes.legend
                    : classes.legend + " " + classes.legend_hidden
                }
                >
                    {this.state.paths &&
                    this.state.paths.map((path, idx) => {
                        return <div key={"path-" + idx}
                                    style={{cursor: 'pointer'}}
                                    onClick={() => {
                            console.log("USTAWIAM INDEX NA ", idx)
                            this.setState({...this.state, selectedPathIndex: idx, path: path})
                        }}>
                            {idx}: {path.value}
                        </div>
                    })
                    }
                </div>
                <Map
                    id="mapa"
                    center={startPosition}
                    zoom={this.state.zoom}
                    style={{height: '100%'}}
                    onClick={this.addMarker}
                    ref={this.mapRef}
                    bounds={this.state.bounds}
                    boundsOptions={{padding: [50, 50]}}
                >
                    {this.state.path.length > 1 &&
                    // this.state.path.map((pathPoint, idx) =>{
                    //     if(this.state.path[idx+1])
                    //     return <Polyline color="lime" positions={this.state.path} />})
                    <Polyline color="lime" positions={this.state.path}/>
                    }
                    {this.state.path.length > 1 &&
                    <Polyline color="pink"
                              positions={[this.state.path[this.state.path.length - 1], this.state.path[0]]}/>
                    }
                    <TileLayer
                        attribution="&amp;copy <a href=&quot;http://osm.org/copyright&quot;>OpenStreetMap</a> contributors"
                        url="https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png"
                    />
                    {this.state.markers.map((markerPosition, idx) =>
                        <Marker key={`marker-${idx}`} position={markerPosition} onClick={() => console.log("a")}>
                            <Popup key={`popup-${idx}`}>
                            <span>
                                A pretty CSS3 popup. <br/>
                                Easily customizable.<br/>
                                {markerPosition[0]}, {markerPosition[1]}
                            </span>
                            </Popup>
                        </Marker>
                    )}
                </Map>
            </div>
        )
    }
}

const mapStateToProps = (state) => {
    return {
        data: state.salesmanSetReducer.mapSalesmanSet,
    }
};

const mapDispatchToProps = dispatch => ({
    addCoordinateToSalesmanSet: (coordinates) => dispatch(addPatientToSalesmanSet(coordinates))
});

export default connect(mapStateToProps, mapDispatchToProps)(withStyles(classes)(MapWrapper));
