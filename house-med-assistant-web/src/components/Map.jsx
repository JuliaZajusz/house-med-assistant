import React, {Component, createRef} from 'react'
// import TileLayer from "react-leaflet/lib/TileLayer";
import {Map, Marker, Popup, TileLayer} from "react-leaflet";
import 'leaflet/dist/leaflet.css'
import L from 'leaflet';
import * as _ from 'lodash';

delete L.Icon.Default.prototype._getIconUrl;

L.Icon.Default.mergeOptions({
    iconRetinaUrl: require('leaflet/dist/images/marker-icon-2x.png'),
    iconUrl: require('leaflet/dist/images/marker-icon.png'),
    shadowUrl: require('leaflet/dist/images/marker-shadow.png')
});

const outer = [[50.505, -29.09], [52.505, 29.09]]
const inner = [[49.505, -2.09], [53.505, 2.09]]

export default class MapWrapper extends Component {
    constructor(props) {
        super(props);
        this.mapRef = createRef()
    }

    state = {
        lat: 51.505,
        lng: -0.09,
        zoom: 13,
        markers: [],
        bounds: [[49.11, 15.04], [54.11, 23.04],]
    }

    onClickInner = () => {
        this.setState({bounds: inner})
    }

    onClickOuter = () => {
        this.setState({bounds: outer})
    }


    static getDerivedStateFromProps = (props, state) => {
        let markers = props.data ? props.data.places.map((place) => [place.location[1], place.location[0]]) : []
        console.log("mark", markers)
        let newBounds = state.bounds;
        if (newMarkers.length > 1) {
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
            markers: markers
        }
    }

    addMarker = (e) => {
        const {markers} = this.state
        let lat = Math.round(e.latlng.lat * 100) / 100
        let lng = Math.round(e.latlng.lng * 100) / 100
        markers.push([lat, lng])
        this.props.onAddPlaceToSalesmanSet([lng, lat])
    }

    render() {
        const startPosition = [this.state.lat, this.state.lng]
        return (
            <Map
                id="mapa"
                center={startPosition}
                zoom={this.state.zoom}
                style={{minHeight: '500px', height: '100%'}}
                onClick={this.addMarker}
                ref={this.mapRef}
                bounds={this.state.bounds}
                boundsOptions={{padding: [50, 50]}}
            >
                {/*<Rectangle*/}
                {/*    bounds={outer}*/}
                {/*    color={this.state.bounds === outer ? 'red' : 'white'}*/}
                {/*    onClick={this.onClickOuter}*/}
                {/*/>*/}
                {/*<Rectangle*/}
                {/*    bounds={inner}*/}
                {/*    color={this.state.bounds === inner ? 'red' : 'white'}*/}
                {/*    onClick={this.onClickInner}*/}
                {/*/>*/}
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
        )
    }
}
